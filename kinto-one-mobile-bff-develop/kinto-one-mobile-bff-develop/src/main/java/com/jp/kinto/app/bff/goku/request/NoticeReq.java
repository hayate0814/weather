package com.jp.kinto.app.bff.goku.request;

import lombok.Data;

@Data
public class NoticeReq {
  private String oldContractId;
  private String memberId;
  private String title;
  private String body;
}
