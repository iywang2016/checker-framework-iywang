package org.checkerframework.checker.sqltainting;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.source.SuppressWarningsPrefix;

/**
 * A type-checker plug-in for the SQL Tainting type system qualifier that finds (and verifies the
 * absence of) SQL injection bugs in embedded query values.
 *
 * <p>It verifies that only SQL-safe embedded query values are trusted and that user input is
 * sanitized before use.
 *
 * @checker_framework.manual #sqltainting-checker SQL Tainting Checker
 */
@SuppressWarningsPrefix({"sqlsanitized", "sqltainting"})
public class SqlTaintingChecker extends BaseTypeChecker {}
