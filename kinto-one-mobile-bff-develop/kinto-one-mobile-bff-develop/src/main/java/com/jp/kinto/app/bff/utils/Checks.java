package com.jp.kinto.app.bff.utils;

public class Checks {
  /**
   * NotNullでない場合、actionを実行する
   *
   * @param <T>
   * @param obj チェック対象
   * @param action Consumer
   */
  public static <T> void ifNotNull(T obj, Runnable action) {
    if (obj != null) {
      action.run();
    }
  }

  /**
   * 指定したObjectは配列かどうか示す
   *
   * @param obj Object
   * @return Boolean
   */
  public static boolean isArray(Object obj) {
    return (obj != null && obj.getClass().isArray());
  }

  /**
   * 指定したObjectはNumberかどうか示す
   *
   * @param cs String
   * @return Boolean
   */
  public static boolean isNumeric(final CharSequence cs) {
    if (isEmpty(cs)) {
      return false;
    }
    final int sz = cs.length();
    for (int i = 0; i < sz; i++) {
      if (!Character.isDigit(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  private static boolean isEmpty(final CharSequence cs) {
    return cs == null || cs.length() == 0;
  }
}
