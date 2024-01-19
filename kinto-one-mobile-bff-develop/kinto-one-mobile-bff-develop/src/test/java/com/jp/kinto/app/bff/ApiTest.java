package com.jp.kinto.app.bff;

import static com.jp.kinto.app.bff.core.constant.Constant.API_URL_PREFIX;
import static com.jp.kinto.app.bff.core.constant.Constant.GOKU_API_VERSION;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.exception.GokuApiMessageException;
import com.jp.kinto.app.bff.core.properties.AppProperties;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.auth.AuthUser.Role;
import com.jp.kinto.app.bff.utils.Asserts;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.ResourceUtils;

/** APIテストのベースClass */
public abstract class ApiTest {
  private static final Logger logger = LoggerFactory.getLogger(ApiTest.class);
  protected static final String INPUT_ROOT = "/input-data";
  protected WebTestClient webClient;

  @Autowired private ApplicationContext context;

  @Autowired private AppProperties appProperties;
  @Autowired private JwtTokenProvider jwtTokenProvider;
  @Autowired private ObjectMapper objectMapper;

  /** setup */
  @BeforeEach
  void setup() {
    webClient =
        WebTestClient.bindToApplicationContext(context)
            .apply(SecurityMockServerConfigurers.springSecurity())
            .configureClient()
            .defaultHeaders(
                httpHeaders -> {
                  httpHeaders.set(
                      "Authorization",
                      "Bearer "
                          + jwtTokenProvider.createToken(
                              AuthUser.builder().userId(1).role(Role.User).build()));
                  httpHeaders.set(
                      "Version",
                      Constant.APP_VERSION_PREFIX + appProperties.getLatestVersionAndroid());
                })
            .responseTimeout(Duration.ofMinutes(1))
            .build();
    MockitoAnnotations.openMocks(this);
  }

  /** APIの結果を出力する */
  protected static WebTestClient.ResponseSpec.ResponseSpecConsumer printResponse =
      spec -> {
        spec.returnResult(Map.class)
            .consumeWith(
                resp ->
                    logger.info(
                        "API[{}] status:{} response:{}",
                        resp.getUrl(),
                        resp.getStatus(),
                        new String(
                            Optional.ofNullable(resp.getResponseBodyContent())
                                .orElse("".getBytes()))));
      };

  /**
   * テスト用リソースファイルの読込み
   *
   * @param fileClassPath ファイルのパス（classpathをベースとするpath）
   * @return byte[]
   */
  protected byte[] file(String fileClassPath) {
    Asserts.notNull(fileClassPath, "fileClassPath");
    String path = fileClassPath.charAt(0) == '/' ? fileClassPath.substring(1) : fileClassPath;
    try {
      return Files.readAllBytes(ResourceUtils.getFile("classpath:" + path).toPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * jsonTextをobjectにparseする
   *
   * @return T
   */
  protected <T> T parseJson(String jsonText, Class<T> clazz) {
    try {
      return objectMapper.readValue(jsonText, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  protected <T> T parseJsonFile(String fileClassPath, Class<T> clazz) {
    try {
      return objectMapper.readValue(file(fileClassPath), clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected <T> T parseJsonFile(String fileClassPath, TypeReference<T> typeReference) {
    try {
      return objectMapper.readValue(file(fileClassPath), typeReference);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * GokuApiMessageExceptionのインスタンスをMockする（マスタAPIをコールする際に発生する例外）
   *
   * @param remoteHttpStatus HttpStatus
   * @param remoteErrorCode remoteErrorCode
   * @return GokuApiMessageException
   */
  protected GokuApiMessageException mockGokuApiMessageExceptionWhenMasterApiCall(
      HttpStatus remoteHttpStatus, String remoteErrorCode) {
    return mockGokuApiMessageExceptionWhenMasterApiCall(
        API_URL_PREFIX + "/price/" + GOKU_API_VERSION, remoteHttpStatus, remoteErrorCode);
  }

  protected GokuApiMessageException mockGokuApiMessageExceptionWhenMasterApiCall(
      String remoteUrl, HttpStatus remoteHttpStatus, String remoteErrorCode) {
    MockClientHttpRequest request = null;
    try {
      request = new MockClientHttpRequest(HttpMethod.GET, new URI(remoteUrl));
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
    return new GokuApiMessageException(
        remoteHttpStatus.value(),
        Optional.of(request),
        new HashMap<>() {
          {
            put("errorCode", remoteErrorCode);
            put("message", "xxxx");
          }
        });
  }
}
