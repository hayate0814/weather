package com.jp.kinto.app.bff.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.kinto.app.bff.core.constant.SystemValue;

public class Casts {

  /**
   * 指定されたobjをBooleanに変換.
   *
   * @param obj 変換対象
   * @return Boolean
   */
  public static Boolean toBoolean(Object obj) {
    if (obj == null) {
      return false;
    }
    String value = obj.toString().trim();
    return "true".equalsIgnoreCase(value)
        || "1".equals(value)
        || "yes".equalsIgnoreCase(value)
        || "ok".equalsIgnoreCase(value);
  }

  /**
   * case to SV
   *
   * @param enumClass SVクラス
   * @param value sv値
   * @return Enum
   */
  public static <T extends Enum<T>> T toSvEnum(Class<T> enumClass, Integer value) {
    if (value != null) {
      T[] list = enumClass.getEnumConstants();
      for (T sv : list) {
        if (((SystemValue) sv).eq(value)) {
          return sv;
        }
      }
    }
    return null;
  }

  /**
   * 指定したObjectをjson文字列に変更する
   *
   * @param objectMapper json変換用{@link ObjectMapper}
   * @param obj 変換対象Object
   * @return json文字列
   */
  public static String toJson(ObjectMapper objectMapper, Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to convert json-text", e);
    }
  }

  /**
   * 指定したjson文字列をObjectに変更する
   *
   * @param json json文字列
   * @param valueTypeRef 変換対象Object
   * @param <T> 変換対象Object
   * @return Object
   */
  public static <T> T jsonToObject(String json, TypeReference<T> valueTypeRef) {
    try {
      return new ObjectMapper().readValue(json, valueTypeRef);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert json-object", e);
    }
  }

  /**
   * 指定されたobjをStringに変換.
   *
   * @param obj 変換対象
   * @return String
   */
  public static String toString(Object obj) {
    return toString(obj, null);
  }

  /**
   * 指定されたobjをStringに変換.
   *
   * @param obj 変換対象
   * @param defaultValue NULL時、使う値
   * @return String
   */
  public static String toString(Object obj, String defaultValue) {
    return obj == null ? defaultValue : obj.toString();
  }
}
