package com.jp.kinto.app.bff.model.notification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDetail {
  private String title;
  private String createDateTime;
  private String content;
}
