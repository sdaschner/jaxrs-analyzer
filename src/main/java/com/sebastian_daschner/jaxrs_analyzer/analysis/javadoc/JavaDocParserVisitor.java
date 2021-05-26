package com.sebastian_daschner.jaxrs_analyzer.analysis.javadoc;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.ClassComment;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MemberParameterTag;
import com.sebastian_daschner.jaxrs_analyzer.model.javadoc.MethodComment;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;
import com.sebastian_daschner.jaxrs_analyzer.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier.ofNonStatic;
import static com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier.ofStatic;

/**
 * @author Sebastian Daschner
 */
public class JavaDocParserVisitor extends VoidVisitorAdapter<Void> {

    private String packageName;
    private String typeName;
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
        recordTypeInfo(classOrInterface);

        super.visit(classOrInterface, arg);
    }

    @Override
    public void visit(EnumDeclaration enumDeclaration, Void arg) {
        recordTypeInfo(enumDeclaration);

        super.visit(enumDeclaration, arg);
    }

    private void recordTypeInfo(TypeDeclaration typeDeclaration) {
        typeName = calculateTypeName(typeDeclaration);

        typeDeclaration.getComment()
                .filter(Comment::isJavadocComment)
                .map(this::toJavaDoc)
                .ifPresent(this::recordTypeComment);
    }

    private Javadoc toJavaDoc(Comment comment) {
        return comment.asJavadocComment().parse();
    }

    private boolean isDeprecated(Javadoc javadoc) {
        return javadoc.getBlockTags().stream().anyMatch(t -> t.getType() == JavadocBlockTag.Type.DEPRECATED);
    }

    private String calculateTypeName(TypeDeclaration type) {
        if (StringUtils.isBlank(packageName))
            return type.getNameAsString();
        return packageName.replace('.', '/') + "/" + type.getNameAsString();
    }

    private void recordTypeComment(Javadoc javadoc) {
        String comment = javadoc.getDescription().toText();
        Map<Integer, String> responseComments = createResponseComments(javadoc);
        classComments.put(typeName, new ClassComment(comment, responseComments, isDeprecated(javadoc)));
    }

    @Override
    public void visit(FieldDeclaration field, Void arg) {
        field.getComment()
                .filter(Comment::isJavadocComment)
                .map(this::toJavaDoc)
                .ifPresent(c -> createFieldComment(c, field));
        super.visit(field, arg);
    }

    private void createFieldComment(Javadoc javadoc, FieldDeclaration field) {
        ClassComment classComment = classComments.get(typeName);
        if (classComment == null) {
            classComment = new ClassComment();
            classComments.put(typeName, classComment);
        }
        classComment.getFieldComments().add(createMemberParamTag(javadoc.getDescription(), field.getAnnotations().stream()));
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
        Map<Integer, String> responseComments = createResponseComments(javadoc);
        methodComments.put(identifier, new MethodComment(comment, tags, responseComments, classComments.get(typeName), isDeprecated(javadoc)));
    }

    private List<MemberParameterTag> createMethodParameterTags(Javadoc javadoc, MethodDeclaration method) {
        return javadoc.getBlockTags().stream()
                .filter(t -> t.getType() == JavadocBlockTag.Type.PARAM)
                .map(t -> createMethodParameterTag(t, method))
                .collect(Collectors.toList());
    }

    private MemberParameterTag createMethodParameterTag(JavadocBlockTag tag, MethodDeclaration method) {
        Stream<AnnotationExpr> annotations = method.getParameterByName(tag.getName().orElse(null))
                .map(Parameter::getAnnotations)
                .map(NodeList::stream)
                .orElseGet(Stream::empty);

        return createMemberParamTag(tag.getContent(), annotations);
    }

    private MemberParameterTag createMemberParamTag(JavadocDescription javadocDescription, Stream<AnnotationExpr> annotationStream) {
        Map<String, String> annotations = annotationStream
                .filter(Expression::isSingleMemberAnnotationExpr)
                .collect(Collectors.toMap(a -> a.getName().getIdentifier(),
                        this::createMemberParamValue));
        return new MemberParameterTag(javadocDescription.toText(), annotations);
    }

    private String createMemberParamValue(AnnotationExpr a) {
        Expression memberValue = a.asSingleMemberAnnotationExpr().getMemberValue();
        if (memberValue.getClass().isAssignableFrom(StringLiteralExpr.class))
            return memberValue.asStringLiteralExpr().asString();

        if (memberValue.getClass().isAssignableFrom(NameExpr.class))
            return memberValue.asNameExpr().getNameAsString();

        throw new IllegalArgumentException(String.format("Javadoc param type (%s) not supported.", memberValue.toString()));
    }

    private Map<Integer, String> createResponseComments(Javadoc javadoc) {
        return javadoc.getBlockTags().stream()
                .filter(t -> ResponseCommentExtractor.RESPONSE_TAG_NAME.equalsIgnoreCase(t.getTagName()))
                .map(t -> t.getContent().toText())
                .map(ResponseCommentExtractor::extract)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    /**
     * <b>Note:</b> This will not return the actual identifier but only the simple names of the types (return type &amp; parameter types).
     * Doing a full type resolving with all imports adds too much complexity at this point.
     * This is a best-effort approach.
     */
    private MethodIdentifier calculateMethodIdentifier(MethodDeclaration method) {
        String[] parameters = method.getParameters().stream()
                .map(p -> p.getType().asString())
                .map(p -> p.replace('.', '/'))
                .toArray(String[]::new);
        String returnType = method.getType().asString().replace('.', '/');

        if (method.isStatic()) {
            return ofStatic(typeName, method.getNameAsString(), returnType, parameters);
        }
        return ofNonStatic(typeName, method.getNameAsString(), returnType, parameters);
    }

}
