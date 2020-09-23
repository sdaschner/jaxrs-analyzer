package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.ApplicationPathAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.ConsumesAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.PathAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.annotation.ProducesAnnotationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.*;

import javax.ws.rs.*;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.isAnnotationPresent;
import static org.objectweb.asm.Opcodes.*;

/**
 * @author Sebastian Daschner
 */
public class JAXRSClassVisitor extends ClassVisitor {

    private static final Class<? extends Annotation>[] RELEVANT_METHOD_ANNOTATIONS = new Class[]{Path.class, GET.class, PUT.class, POST.class, DELETE.class, OPTIONS.class, HEAD.class};

    private final ClassResult classResult;

    public JAXRSClassVisitor(final ClassResult classResult) {
        super(ASM7);
        this.classResult = classResult;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        classResult.setOriginalClass(name);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        switch (desc) {
            case Types.PATH:
                return new PathAnnotationVisitor(classResult);
            case Types.APPLICATION_PATH:
                return new ApplicationPathAnnotationVisitor(classResult);
            case Types.CONSUMES:
                return new ConsumesAnnotationVisitor(classResult);
            case Types.PRODUCES:
                return new ProducesAnnotationVisitor(classResult);
            case Types.DEPRECATED:
                classResult.setDeprecated(true);
                break;
        }
        return null;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if ((access & ACC_STATIC) == 0)
            return new JAXRSFieldVisitor(classResult, desc, signature);
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final boolean legalModifiers = ((access & ACC_SYNTHETIC) | (access & ACC_STATIC) | (access & ACC_NATIVE)) == 0;
        final String methodSignature = signature == null ? desc : signature;
        final MethodIdentifier identifier = MethodIdentifier.of(classResult.getOriginalClass(), name, methodSignature, false);

        if (legalModifiers && !"<init>".equals(name)) {
            final MethodResult methodResult = new MethodResult();
            if (hasJAXRSAnnotations(classResult.getOriginalClass(), name, methodSignature))
                return new JAXRSMethodVisitor(identifier, classResult, methodResult, true);
            else {
                final Method annotatedSuperMethod = searchAnnotatedSuperMethod(classResult.getOriginalClass(), name, methodSignature);
                if (annotatedSuperMethod != null) {
                    try {
                        return new JAXRSMethodVisitor(identifier, classResult, methodResult, false);
                    } finally {
                        classResult.getMethods().stream().filter(m -> m.equals(methodResult)).findAny().ifPresent(m -> visitJAXRSSuperMethod(annotatedSuperMethod, m));
                    }
                }
            }
        }
        return null;
    }

    private static boolean hasJAXRSAnnotations(final String className, final String methodName, final String signature) {
        final Method method = JavaUtils.findMethod(className, methodName, signature);
        return method != null && hasJAXRSAnnotations(method);
    }

    private static Method searchAnnotatedSuperMethod(final String className, final String methodName, final String methodSignature) {
        final List<Class<?>> superTypes = determineSuperTypes(className);
        return superTypes.stream().map(c -> {
            final Method superAnnotatedMethod = JavaUtils.findMethod(c, methodName, methodSignature);
            if (superAnnotatedMethod != null && hasJAXRSAnnotations(superAnnotatedMethod))
                return superAnnotatedMethod;
            return null;
        }).filter(Objects::nonNull).findAny().orElse(null);
    }

    private static List<Class<?>> determineSuperTypes(final String className) {
        final Class<?> loadedClass = JavaUtils.loadClassFromName(className);
        if (loadedClass == null)
            return Collections.emptyList();

        final List<Class<?>> superClasses = new ArrayList<>();
        final Queue<Class<?>> classesToCheck = new LinkedBlockingQueue<>();
        Class<?> currentClass = loadedClass;

        do {
            if (currentClass.getSuperclass() != null && Object.class != currentClass.getSuperclass())
                classesToCheck.add(currentClass.getSuperclass());

            Stream.of(currentClass.getInterfaces()).forEach(classesToCheck::add);

            if (currentClass != loadedClass)
                superClasses.add(currentClass);

        } while ((currentClass = classesToCheck.poll()) != null);

        return superClasses;
    }

    private static boolean hasJAXRSAnnotations(final Method method) {
        for (final Object annotation : method.getDeclaredAnnotations()) {
            // TODO test both
            if (Stream.of(RELEVANT_METHOD_ANNOTATIONS).map(a -> JavaUtils.getAnnotation(method, a))
                    .filter(Objects::nonNull).anyMatch(a -> a.getClass().isAssignableFrom(annotation.getClass())))
                return true;

            if (isAnnotationPresent(annotation.getClass(), HttpMethod.class))
                return true;
        }
        return false;
    }

    private void visitJAXRSSuperMethod(Method method, MethodResult methodResult) {
        try {

            final ClassReader classReader = new ContextClassReader(method.getDeclaringClass().getCanonicalName());
            final ClassVisitor visitor = new JAXRSAnnotatedSuperMethodClassVisitor(methodResult, method);

            classReader.accept(visitor, ClassReader.EXPAND_FRAMES);
        } catch (IOException e) {
            LogProvider.error("Could not analyze JAX-RS super annotated method " + method);
            LogProvider.debug(e);
        }
    }

}

