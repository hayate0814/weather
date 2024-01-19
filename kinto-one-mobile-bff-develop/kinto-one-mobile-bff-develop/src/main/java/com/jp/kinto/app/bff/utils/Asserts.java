package com.jp.kinto.app.bff.utils;

import static com.jp.kinto.app.bff.core.message.Msg.format;

import com.jp.kinto.app.bff.core.exception.AssertFailException;
import com.jp.kinto.app.bff.core.exception.BffException;
import io.jsonwebtoken.lang.Collections;
import io.jsonwebtoken.lang.Objects;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.function.Supplier;
import org.springframework.util.StringUtils;

public class Asserts {

  /**
   * true断言
   *
   * @param expression 断言チェック対象の判定式
   * @param message 判定false：メッセージ
   */
  public static void assertTrue(boolean expression, String message) {
    if (!expression) {
      throw new AssertFailException(message);
    }
  }

  /**
   * true断言
   *
   * @param expression 断言チェック対象の判定式
   * @param exceptionSupplier BffException
   */
  public static void assertTrue(
      boolean expression, Supplier<? extends BffException> exceptionSupplier) {
    if (!expression) {
      throw exceptionSupplier.get();
    }
  }

  /**
   * notNull断言
   *
   * @param value 断言チェック対象の値
   * @param name 断言チェック対象のパラメータ名
   */
  public static void notNull(Object value, String name) {
    if (value == null) {
      throw new AssertFailException(format("[{}] may not be null", name));
    }
  }

  /**
   * notNull断言
   *
   * @param value 断言チェック対象の値
   * @param exceptionSupplier BffException
   */
  public static void notNull(Object value, Supplier<? extends BffException> exceptionSupplier) {
    if (value == null) {
      throw exceptionSupplier.get();
    }
  }

  /**
   * notEmptyText断言
   *
   * @param value 断言チェック対象の値
   * @param name 断言チェック対象のパラメータ名
   */
  public static void notEmptyText(String value, String name) {
    if (!StringUtils.hasLength(value)) {
      throw new AssertFailException(
          format("[{}] must have text; it must not be null, empty, or blank", name));
    }
  }

  /**
   * notEmptyText断言
   *
   * @param value 断言チェック対象の値
   * @param exceptionSupplier BffException
   */
  public static void notEmptyText(
      String value, Supplier<? extends BffException> exceptionSupplier) {
    if (!StringUtils.hasLength(value)) {
      throw exceptionSupplier.get();
    }
  }

  /**
   * notEmpty断言
   *
   * @param value 断言チェック対象の値
   * @param name 断言チェック対象のパラメータ名
   */
  public static void notEmpty(Collection<?> value, String name) {
    if (Collections.isEmpty(value)) {
      throw new AssertFailException(
          format("[{}] must have text; it must not be null, empty, or blank", name));
    }
  }

  /**
   * notEmpty断言
   *
   * @param value 断言チェック対象の値
   * @param exceptionSupplier BffException
   */
  public static void notEmpty(
      Collection<?> value, Supplier<? extends BffException> exceptionSupplier) {
    if (Collections.isEmpty(value)) {
      throw exceptionSupplier.get();
    }
  }

  /**
   * hasLength断言
   *
   * @param value 断言チェック対象の値
   * @param name 断言チェック対象のパラメータ名
   */
  public static void hasLength(Object value, String name) {
    if (!Objects.isArray(value) || Array.getLength(value) == 0) {
      throw new AssertFailException(
          format("[{}] must not be empty: it must contain at least 1 element", name));
    }
  }

  /**
   * hasLength断言
   *
   * @param value 断言チェック対象の値
   * @param exceptionSupplier BffException
   */
  public static void hasLength(Object value, Supplier<? extends BffException> exceptionSupplier) {
    if (!Objects.isArray(value) || Array.getLength(value) == 0) {
      throw exceptionSupplier.get();
    }
  }

  /**
   * isValidSystemValue断言
   *
   * @param enumClass 断言チェック対象のSVクラス
   * @param value 断言チェック対象の値
   * @param name 断言チェック対象のパラメータ名
   * @param <T> SystemValue
   */
  public static <T extends Enum<T>> void isValidSystemValue(
      Class<T> enumClass, Integer value, String name) {
    if (Casts.toSvEnum(enumClass, value) == null) {
      throw new AssertFailException(
          format("[{}={}] is not valid value of [enum:{}]", name, value, enumClass.getName()));
    }
  }

  public static <T extends Enum<T>> void isValidSystemValue(
      Class<T> enumClass, Integer value, Supplier<? extends BffException> exceptionSupplier) {
    if (Casts.toSvEnum(enumClass, value) == null) {
      throw exceptionSupplier.get();
    }
  }
}
