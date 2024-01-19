package com.jp.kinto.app.bff.core.constant;

public interface SystemValue {

  /**
   * Get System Value.
   *
   * @return int
   */
  int getSv();

  /**
   * Get name.
   *
   * @return String
   */
  String name();

  /**
   * 等価比較.
   *
   * @param sv Short
   * @return boolean
   */
  default boolean eq(Integer sv) {
    return sv != null && getSv() == sv.intValue();
  }

  /**
   * 等価比較.
   *
   * @param name String
   * @return boolean
   */
  default boolean eq(String name) {
    return name != null && name.equals(this.name());
  }
}
