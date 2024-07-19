package org.checkerframework.checker.sqlquotes;

import com.sun.source.tree.BinaryTree;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.sqlquotes.qual.SqlEvenQuotes;
import org.checkerframework.checker.sqlquotes.qual.SqlOddQuotes;
import org.checkerframework.checker.sqlquotes.qual.SqlQuotesUnknown;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationMirrorSet;
import org.checkerframework.javacutil.TreeUtils;

/** Annotated type factory for the SQL Quotes Checker. */
public class SqlQuotesAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

  /** The {@code @}{@link SqlEvenQuotes} annotation mirror. */
  private final AnnotationMirror SQL_EVEN_QUOTES;

  /** The {@code @}{@link SqlOddQuotes} annotation mirror. */
  private final AnnotationMirror SQL_ODD_QUOTES;

  /** The {@code @}{@link SqlQuotesUnknown} annotation mirror. */
  private final AnnotationMirror SQL_QUOTES_UNKNOWN;

  /** A singleton set containing the {@code @}{@link SqlEvenQuotes} annotation mirror. */
  private final AnnotationMirrorSet setOfSqlEvenQuotes;

  /**
   * Creates a {@link SqlQuotesAnnotatedTypeFactory}.
   *
   * @param checker the SQL tainting checker
   */
  public SqlQuotesAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(checker);
    this.SQL_EVEN_QUOTES = AnnotationBuilder.fromClass(getElementUtils(), SqlEvenQuotes.class);
    this.SQL_ODD_QUOTES = AnnotationBuilder.fromClass(getElementUtils(), SqlOddQuotes.class);
    this.SQL_QUOTES_UNKNOWN =
        AnnotationBuilder.fromClass(getElementUtils(), SqlQuotesUnknown.class);
    this.setOfSqlEvenQuotes = AnnotationMirrorSet.singleton(SQL_EVEN_QUOTES);
    postInit();
  }

  @Override
  protected Set<AnnotationMirror> getEnumConstructorQualifiers() {
    return setOfSqlEvenQuotes;
  }

  @Override
  public TreeAnnotator createTreeAnnotator() {
    return new ListTreeAnnotator(
        super.createTreeAnnotator(),
        new SqlQuotesAnnotatedTypeFactory.SqlQuotesTreeAnnotator(this));
  }

  private class SqlQuotesTreeAnnotator extends TreeAnnotator {
    public SqlQuotesTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
      super(atypeFactory);
    }

    @Override
    public Void visitBinary(BinaryTree tree, AnnotatedTypeMirror type) {
      if (TreeUtils.isStringConcatenation(tree)) {
        AnnotatedTypeMirror leftType = getAnnotatedType(tree.getLeftOperand());
        AnnotatedTypeMirror rightType = getAnnotatedType(tree.getRightOperand());

        if (leftType.hasPrimaryAnnotation(SQL_QUOTES_UNKNOWN)
            || rightType.hasPrimaryAnnotation(SQL_QUOTES_UNKNOWN)) {
          type.replaceAnnotation(SQL_QUOTES_UNKNOWN);
          return null;
        }

        int leftParity = leftType.hasPrimaryAnnotation(SQL_ODD_QUOTES) ? 1 : 0;
        int rightParity = rightType.hasPrimaryAnnotation(SQL_ODD_QUOTES) ? 1 : 0;

        int parity = leftParity + rightParity;
        if (parity == 0 || parity == 2) {
          type.replaceAnnotation(SQL_EVEN_QUOTES);
        } else {
          type.replaceAnnotation(SQL_ODD_QUOTES);
        }
      }

      return null;
    }
  }
}
