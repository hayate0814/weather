package com.jp.kinto.app.bff.model.ocr;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class OcrResponse {
  private String ocrToken;
}
