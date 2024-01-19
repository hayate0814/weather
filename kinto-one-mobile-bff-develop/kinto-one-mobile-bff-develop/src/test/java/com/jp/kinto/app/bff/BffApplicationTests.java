package com.jp.kinto.app.bff;

import com.jp.kinto.app.bff.core.message.Msg;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BffApplicationTests {

  @Test
  void contextLoads() {
    System.out.println("msg:" + Msg.format("Hi {}. My name is {}.", "Alice", "Bob"));
    System.out.println("msg:" + Msg.stringLength.args("パスワード", 12));
  }
}
