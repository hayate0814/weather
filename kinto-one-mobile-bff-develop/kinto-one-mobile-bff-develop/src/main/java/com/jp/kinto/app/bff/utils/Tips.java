package com.jp.kinto.app.bff.utils;

import com.jp.kinto.app.bff.goku.request.MonthlyPriceReq;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

public class Tips {

  /**
   * 指定配列の最大値を取得
   *
   * @param arr 配列
   * @return 最大値
   */
  public static Integer max(Integer[] arr) {
    if (arr == null || arr.length == 0) {
      throw new RuntimeException("max値の比較するには、引数が空の配列とすることができません");
    }
    int max = arr[0];
    for (int i = 1; i < arr.length; i++) {
      max = Math.max(max, arr[i]);
    }
    return max;
  }

  /**
   * 指定requestを、APIコール時のクエリパラメータに変換する <br>
   * Object　⇒　MultiValueMap
   *
   * @param request　Object
   * @return MultiValueMap
   */
  public static MultiValueMap<String, String> toUriParamMap(Object request) {
    var clazz = request.getClass();
    List<Object[]> getFieldMethods = getGetMethods(clazz);
    var map = new HashMap<String, List<String>>();
    getFieldMethods.forEach(
        getInfo -> {
          var field = (Field) getInfo[0];
          var method = (Method) getInfo[1];
          var value = getFieldValue(request, method);
          if (value != null) {
            map.put(field.getName(), value);
          }
        });
    return new MultiValueMapAdapter<>(map);
  }

  /**
   * 指定文字列をbase64にエンコードする
   *
   * @param originalString エンコード対象文字列
   * @return encodedString
   */
  public static String stringToBase64(String originalString) {
    try {
      String encodedString = Base64.getEncoder().encodeToString(originalString.getBytes());
      return encodedString;
    } catch (Exception e) {
      throw new RuntimeException("Failed to encode " + originalString, e);
    }
  }

  private static final Map<Class<?>, List<Object[]>> declaredGetMethodsCache =
      new ConcurrentReferenceHashMap<>();

  private static List<Object[]> getGetMethods(Class<?> clazz) {
    List<Object[]> getMethodFieldInfo = declaredGetMethodsCache.get(clazz);
    if (getMethodFieldInfo == null) {
      synchronized (clazz) {
        getMethodFieldInfo = declaredGetMethodsCache.get(clazz);
        if (getMethodFieldInfo == null) {
          Field[] fields = clazz.getDeclaredFields();
          getMethodFieldInfo =
              Arrays.stream(fields)
                  .map(
                      f -> {
                        var fieldName = f.getName();
                        Method getMethod = null;
                        try {
                          getMethod =
                              clazz.getDeclaredMethod(
                                  "get"
                                      + fieldName.substring(0, 1).toUpperCase()
                                      + fieldName.substring(1));
                        } catch (NoSuchMethodException e) {
                          //  do nothing
                        }
                        return new Object[] {f, getMethod};
                      })
                  .filter(info -> info[1] != null)
                  .toList();
          declaredGetMethodsCache.put(clazz, getMethodFieldInfo);
        }
      }
    }
    return getMethodFieldInfo;
  }

  private static List<String> getFieldValue(Object obj, Method f) {
    try {
      Object value = f.invoke(obj);
      if (value == null) {
        return null;
      }
      if (Checks.isArray(value)) {
        int length = Array.getLength(value);
        var list = new ArrayList<String>(length);
        for (int i = 0; i < length; i++) {
          list.add(String.valueOf(Array.get(value, i)));
        }
        return list;
      }
      if (value instanceof Collection<?> col) {
        return col.stream().filter(java.util.Objects::nonNull).map(String::valueOf).toList();
      }
      return Collections.singletonList(String.valueOf(value));
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to invoke the method. target class:"
              + obj.getClass().getName()
              + ", method:"
              + f.getName(),
          e);
    }
  }

  // for test
  public static void main(String[] args) {
    //    System.out.println(max(new Integer[] {36, 60, 84}));
    MonthlyPriceReq monthlyPriceReq =
        MonthlyPriceReq.builder()
            .carModelId("CAR-0000001196")
            .contractMonths(100000)
            .bonusAdditionAmount(150000)
            .gradeId("GRD-0000005656")
            .packageExclusiveEquipmentIds(new String[] {"PED-0000009454"})
            .singleOptionIds(new String[] {"SOD-0000010778", "SOD-0000010779", "SOD-0000010786"})
            //        .singleOptionIds(
            //            Arrays.stream(new String[] {"SOD-0000010778", "SOD-0000010779",
            // "SOD-0000010786"}).toList())
            .build();

    var uriParamMap = toUriParamMap(monthlyPriceReq);
    System.out.println("uriParamMap:" + uriParamMap);
  }
}
