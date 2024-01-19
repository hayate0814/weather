package com.jp.kinto.app.bff.goku;

import com.jp.kinto.app.bff.core.properties.OCRProperties;
import com.jp.kinto.app.bff.goku.client.CloudOcrClient;
import com.jp.kinto.app.bff.goku.response.OcrAccessRes;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CloudOcrApi {
  private CloudOcrClient cloudOcrClient;
  private OCRProperties ocrProperties;

  public CloudOcrApi(OCRProperties ocrProperties, Function<String, WebClient> webClientFun) {
    this.ocrProperties = ocrProperties;
    this.cloudOcrClient = new CloudOcrClient(webClientFun.apply(ocrProperties.getUrl()));
  }

  public Mono<OcrAccessRes> getAccess() {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("user", ocrProperties.getUser());
    formData.add("pass", ocrProperties.getPass());

    return cloudOcrClient.call(
        OcrAccessRes.class,
        client ->
            client
                .post()
                .uri("/aient/access")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(formData));
  }
}
