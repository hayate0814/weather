package com.jp.kinto.app.bff.core.message;

import org.slf4j.helpers.MessageFormatter;

public interface Msg {

  interface MsgFormat {
    String args(Object... args);
  }

  String VehicleDataExpired = "お見積り途中の車両情報は変更されました。お手数をお掛けして申し訳ありませんが、お見積りは再度作成をお願いいたします";
  String LoginMistake = "ログインできませんでした。メールアドレス/パスワードをご確認ください";
  String OcrGetTokenMistake = "OCRが一時的にご利用いただけませんので、手動で入力してください";

  String NotLoginUser = "ログインしてから再度検索をお願い致します";
  String CorporationUser = "法人会員様は本アプリをご利用いただけません";
  String NotificationNotExist = "該当通知情報は存在しません。再度検索をお願い致します";
  String SimulationNotExist = "該当シミュレーションは存在しません。再度検索をお願い致します";
  String SimulationDataExpired = "該当シミュレーションが更新されましたので、再度検索をお願い致します";
  String YahooSearchNotExist = "該当地名は存在しません。再度検索をお願い致します。";
  String VehicleAndDealerDataExpired =
      "お見積り途中の車両情報または販売店情報は変更されました。お手数をお掛けして申し訳ありませんが、お見積りは再度作成をお願いいたします";

  String CarNotExist =
      """
      KINTO ONEをご検討いただきありがとうございます。
      申し訳ございませんが、お取り扱い期間や予定数の終了により、ご選択いただいた車はお申し込みいただけません。""";

  String badRequest = "リクエストのパラメータは正しくありません";
  MsgFormat required = (label) -> format("{}は必須項目です", label);
  MsgFormat invalidValue = (label) -> format("{}の値が正しくありません", label);
  MsgFormat stringLength = (params) -> format("{}は{}桁で入力してください", params);

  static String format(String pattern, Object... args) {
    return MessageFormatter.arrayFormat(pattern, args).getMessage();
  }
}
