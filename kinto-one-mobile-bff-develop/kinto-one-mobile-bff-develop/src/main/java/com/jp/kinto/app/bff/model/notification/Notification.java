package com.jp.kinto.app.bff.model.notification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Notification {
  private Integer notificationId;
  private String title;
  private String createDateTime;
  private Integer readFlag;
}
