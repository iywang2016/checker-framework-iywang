package org.checkerframework.checker.test.junit;

import java.io.File;
import java.util.List;
import org.checkerframework.checker.sqlquerytainting.SqlQueryTaintingChecker;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

public class SqlQueryTaintingTest extends CheckerFrameworkPerDirectoryTest {

  /**
   * Create a TaintingTest.
   *
   * @param testFiles the files containing test code, which will be type-checked
   */
  public SqlQueryTaintingTest(List<File> testFiles) {
    super(testFiles, SqlQueryTaintingChecker.class, "sqlquerytainting");
  }

  @Parameters
  public static String[] getTestDirs() {
    return new String[] {"sqlquerytainting", "all-systems"};
  }
}