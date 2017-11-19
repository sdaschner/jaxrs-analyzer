package com.sebastian_daschner.jaxrs_analyzer.analysis.javadoc;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.ClassComment;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MemberParameterTag;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MethodComment;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier.ofNonStatic;
import static com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier.ofStatic;

/**
 * @author Sebastian Daschner
 */
public class JavaDocParserVisitor extends VoidVisitorAdapter<Void> {

    private String packageName;
    private String className;
    private final Map<MethodIdentifier, MethodComment> methodComments;
    private final Map<String, ClassComment> classComments = new HashMap<>();

    public JavaDocParserVisitor(Map<MethodIdentifier, MethodComment> methodComments) {
        this.methodComments = methodComments;
    }

    @Override
    public void visit(PackageDeclaration packageDeclaration, Void arg) {
        packageName = packageDeclaration.getNameAsString();
        super.visit(packageDeclaration, arg);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration classOrInterface, Void arg) {
        className = calculateClassName(classOrInterface);

        classOrInterface.getComment()
                .filter(Comment::isJavadocComment)
                .map(this::toJavaDoc)
                .ifPresent(this::recordClassComment);

        super.visit(classOrInterface, arg);
    }

    private Javadoc toJavaDoc(Comment comment) {
        return comment.asJavadocComment().parse();
    }

    private boolean isDeprecated(Javadoc javadoc) {
        return javadoc.getBlockTags().stream().anyMatch(t -> t.getType() == JavadocBlockTag.Type.DEPRECATED);
    }

    private String calculateClassName(ClassOrInterfaceDeclaration classOrInterface) {
        if (StringUtils.isBlank(packageName))
            return classOrInterface.getNameAsString();
        return packageName.replace('.', '/') + "/" + classOrInterface.getNameAsString();
    }

    private void recordClassComment(Javadoc javadoc) {
        String comment = javadoc.getDescription().toText();
        classComments.put(className, new ClassComment(comment, isDeprecated(javadoc)));
    }

    @Override
    public void visit(FieldDeclaration field, Void arg) {
        field.getComment()
                .filter(Comment::isJavadocComment)
                .map(this::toJavaDoc)
                .ifPresent(c -> classComments.get(className).getFieldComments().add(createFieldComment(c, field)));
        super.visit(field, arg);
    }

    private MemberParameterTag createFieldComment(Javadoc javadoc, FieldDeclaration field) {
        return createMemberParamTag(javadoc.getDescription(), field.getAnnotations().stream());
    }

    @Override
    public void visit(MethodDeclaration method, Void arg) {
        method.getComment()
                .filter(Comment::isJavadocComment)
                .map(this::toJavaDoc)
                .ifPresent(c -> recordMethodComment(c, method));
        super.visit(method, arg);
    }

    private void recordMethodComment(Javadoc javadoc, MethodDeclaration method) {
        MethodIdentifier identifier = calculateMethodIdentifier(method);
        String comment = javadoc.getDescription().toText();
        List<MemberParameterTag> tags = createMethodParameterTags(javadoc, method);
        methodComments.put(identifier, new MethodComment(comment, tags, classComments.get(className), isDeprecated(javadoc)));
    }

    private List<MemberParameterTag> createMethodParameterTags(Javadoc javadoc, MethodDeclaration method) {
        return javadoc.getBlockTags().stream()
                .filter(t -> t.getType() == JavadocBlockTag.Type.PARAM || t.getTagName().equalsIgnoreCase("response"))
                .map(t -> createMethodParameterTag(t, method))
                .collect(Collectors.toList());
    }

    private MemberParameterTag createMethodParameterTag(JavadocBlockTag tag, MethodDeclaration method) {
        Stream<AnnotationExpr> annotations = method.getParameterByName(tag.getName().orElse(null))
                .map(Parameter::getAnnotations)
                .map(NodeList::stream)
                .orElseGet(Stream::empty);

        return createMemberParamTag(tag, annotations);
    }

    private MemberParameterTag createMemberParamTag(JavadocDescription javadocDescription, Stream<AnnotationExpr> annotationStream) {
        Map<String, String> annotations = annotationStream.collect(Collectors.toMap(a -> a.getName().getIdentifier(), a -> a.asSingleMemberAnnotationExpr().getMemberValue().asStringLiteralExpr().asString()));
        return new MemberParameterTag(javadocDescription.toText(),"", annotations);
    }

    private MemberParameterTag createMemberParamTag(JavadocBlockTag tag, Stream<AnnotationExpr> annotationStream) {
        Map<String, String> annotations = annotationStream.collect(Collectors.toMap(a -> a.getName().getIdentifier(), a -> a.asSingleMemberAnnotationExpr().getMemberValue().asStringLiteralExpr().asString()));
        return new MemberParameterTag(tag.getContent().toText(),tag.getTagName(), annotations);
    }

    /**
     * <b>Note:</b> This will not return the actual identifier but only the simple names of the types (return type &amp; parameter types).
     * Doing a full type resolving with all imports adds too much complexity at this point.
     * This is a best-effort approach.
     */
    private MethodIdentifier calculateMethodIdentifier(MethodDeclaration method) {
        String[] parameters = method.getParameters().stream().map(p -> p.getType().asString()).toArray(String[]::new);
        String returnType = method.getType().asString();

        if (method.isStatic()) {
            return ofStatic(className, method.getNameAsString(), returnType, parameters);
        }
        return ofNonStatic(className, method.getNameAsString(), returnType, parameters);
    }

}
