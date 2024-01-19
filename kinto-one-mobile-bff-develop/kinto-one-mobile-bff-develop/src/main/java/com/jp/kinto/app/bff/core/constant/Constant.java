package com.jp.kinto.app.bff.core.constant;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public interface Constant {
  String API_URL_PREFIX = "/api";
  String SALES_CHANNEL = "web";
  String GOKU_API_VERSION = "v1";
  String EXTERNAL = "external";
  Integer INTERNAL_ERROR_CODE = 999;
  String APP_VERSION_PREFIX = "KintoOneApp:A:";
  String JPN_KINTO_ID_AUTH = "jpn_kinto_id_auth";
  Integer ZIP_CODE_SIZE = 7;
  String NUMBER_REGULAR = "-?\\d+(\\.\\d+)?";
  String BZ4X_CAR_MODEL_NAME = "bZ4X";
  List<String> CAR_NAME_INCLUDES_MS_KEY_LIST = Arrays.asList("ms", "myr");
  String GR = "gr";
  String MS = "ms";
  String TERM_1 = "/inc/terms/toyota/index.html";
  String TERM_2 = "/inc/terms/toyota_b/index.html";
  String TERM_3 = "/inc/terms/lexus/index.html";
  String TERM_4 = "/inc/terms/lexus_b/index.html";
  String TERM_5 = "/inc/terms/gr/index.html";
  String TERM_6 = "/inc/terms/gr_b/index.html";

  String GUEST_NAME = "Guest";

  String GR_SPORT_CAR_TYPE = "GR/GR SPORT";

  List<String> TOYOTA_CAR_TYPE =
      Arrays.asList("コンパクト", "セダン", "ワゴン", "SUV", "ミニバン", GR_SPORT_CAR_TYPE);
  LinkedHashMap<String, String> TOYOTA_CAR_TYPE_EXCHANGE =
      new LinkedHashMap<>() {
        {
          put("コンパクト", "コンパクト");
          put("セダン", "セダン");
          put("ワゴン", "ワゴン");
          put("SUV", "SUV");
          put("ミニバン", "ミニバン");
          put("クーペ", GR_SPORT_CAR_TYPE);
        }
      };

  enum MemberType implements SystemValue {
    /** 個人 */
    Personal("MEM-", 1),
    /** 法人 */
    Corporate("'COR-'-", 2);

    private String prefix;
    private Integer sv;

    MemberType(String prefix, Integer sv) {
      this.prefix = prefix;
      this.sv = sv;
    }

    @Override
    public int getSv() {
      return sv;
    }

    public static MemberType fromString(String type) {
      if (type == null) {
        return null;
      }
      return switch (type.toUpperCase()) {
        case "PERSONAL" -> Personal;
        case "CORPORATE" -> Corporate;
        default -> null;
      };
    }
  }

  enum SettingMasterKey {
    /** OCR */
    OCR_TOKEN
  }

  enum DeviceKindSv implements SystemValue {

    /** Android . */
    Android(1, "A"),
    /** IOS . */
    Ios(2, "I");

    private int sv;
    private String type;

    private DeviceKindSv(int sv, String type) {
      this.sv = sv;
      this.type = type;
    }

    @Override
    public int getSv() {
      return this.sv;
    }

    public String getType() {
      return this.type;
    }

    public static DeviceKindSv fromType(String type) {
      return switch (type) {
        case "A" -> Android;
        case "I" -> Ios;
        default -> null;
      };
    }
  }

  enum BrandSv implements SystemValue {
    /** TOYOTA */
    Toyota(1, "T01", "トヨタ"),
    /** LEXUS */
    Lexus(2, "L01", "レクサス");

    private int sv;
    private String code;
    private String prefix;

    private BrandSv(int sv, String code, String prefix) {
      this.sv = sv;
      this.code = code;
      this.prefix = prefix;
    }

    @Override
    public int getSv() {
      return this.sv;
    }

    public String getCode() {
      return code;
    }

    public String getPrefix() {
      return prefix;
    }

    public static String getPrefixByCode(String code) {
      return Arrays.stream(BrandSv.values())
          .filter(brand -> brand.getCode().equals(code))
          .findFirst()
          .map(BrandSv::getPrefix)
          .orElse(BrandSv.Toyota.getPrefix());
    }

    public static boolean isValidByCode(String code) {
      return Arrays.stream(BrandSv.values()).anyMatch(brand -> brand.getCode().equals(code));
    }
  }

  enum PlanSv implements SystemValue {
    /** PLAN_A */
    PlanA(0, "PLAN_A"),
    /** PLAN_B */
    PlanB(1, "PLAN_B");

    private int sv;
    private String code;

    private PlanSv(int sv, String code) {
      this.sv = sv;
      this.code = code;
    }

    @Override
    public int getSv() {
      return this.sv;
    }

    public String getCode() {
      return code;
    }

    public static PlanSv fromString(String code) {
      if (code == null) {
        return null;
      }
      return switch (code.toUpperCase()) {
        case "PLAN_A" -> PlanA;
        case "PLAN_B" -> PlanB;
        default -> null;
      };
    }

    public static boolean isValidByCode(String code) {
      return Arrays.stream(PlanSv.values()).anyMatch(brand -> brand.getCode().equals(code));
    }
  }

  /** ContractMonthSv 契約月数 */
  enum ContractMonthSv implements SystemValue {
    /** 3年 */
    ContractMonth36(36, "3年"),
    /** 5年 */
    ContractMonth60(60, "5年"),
    /** 7年 */
    ContractMonth84(84, "7年");

    private int sv;
    private String status;

    private ContractMonthSv(int sv, String status) {
      this.sv = sv;
      this.status = status;
    }

    @Override
    public int getSv() {
      return sv;
    }

    /**
     * IDのstatusを取得する
     *
     * @return status
     */
    public String getStatus() {
      return status;
    }
  }

  enum Flag implements SystemValue {

    /** NO . */
    NO(0),
    /** Yes . */
    YES(1);

    private int sv;

    private Flag(int sv) {
      this.sv = sv;
    }

    @Override
    public int getSv() {
      return this.sv;
    }
  }

  /** NotificationKindSv 通知種別 */
  enum NotificationKindSv implements SystemValue {
    /** 契約ステータス変更 */
    ContractStatusNtc(1);

    private int sv;

    private NotificationKindSv(int sv) {
      this.sv = sv;
    }

    @Override
    public int getSv() {
      return sv;
    }
  }

  /** 単独オプショングループコード */
  enum SingleGroupCode implements SystemValue {
    IEXOPT(0, "IEXOPT", "内外装向上"), // 内外装向上
    COMOPT(1, "COMOPT", "快適・利便性向上"), // 快適・利便性向上
    COLDAR(2, "COLDAR", "寒冷地仕様・冬タイヤ"); // 寒冷地対応

    private int sv;

    private String code;

    private String name;

    private SingleGroupCode(int sv, String code, String name) {
      this.sv = sv;
      this.code = code;
      this.name = name;
    }

    @Override
    public int getSv() {
      return this.sv;
    }

    public String getCode() {
      return code;
    }

    public String getName() {
      return name;
    }

    public static boolean isValidByCode(String code) {
      return Arrays.stream(SingleGroupCode.values())
          .anyMatch(brand -> brand.getCode().equals(code));
    }
  }

  /** コネクティッド */
  enum TConnect implements SystemValue {
    NONE(0, "NONE"), // なし
    FULL_COVERAGE(1, "FULL_COVERAGE"), // 100%付帯
    SELECTIVE(2, "SELECTIVE");

    private int sv;

    private String code;

    private TConnect(int sv, String code) {
      this.sv = sv;
      this.code = code;
    }

    @Override
    public int getSv() {
      return this.sv;
    }

    public String getCode() {
      return code;
    }

    public static boolean isValidByCode(String code) {
      return Arrays.stream(TConnect.values()).anyMatch(brand -> brand.getCode().equals(code));
    }
  }

  /** 規約一覧 */
  enum TermSv implements SystemValue {
    APPLICATION_PROCEDURE(0), // 申込手続き
    DEALER(1), // 取扱い販売店
    MAINTENANCE(2), // メンテナンス
    PAYMENT_METHOD(3), // 支払方法
    APPLICATION_FEE_PAYMENT(4), // 申込金支払
    MONTHLY_FEE(5), // 月額使用料
    CANCELLATION_APPLICATION(6), // 解約時における申込金扱い
    DEADLINE(7), // 納期
    GARAGE_ARRANGEMENT(8), // 車庫手配
    PROHIBITED_MATTER_A(9), // 禁止事項（１）
    PROHIBITED_MATTER_B(10), // 禁止事項（２）
    PERSONAL_INFORMATION(11), // 個人情報
    ANTISOCIAL_CLAUSE(12), // 反社条項
    MIDTERM_CANCELLATION(13), // 中途解約
    SPECIFIC_CAR(14), // 特定の車種
    TERM(15); // 規約同意

    private int sv;

    private TermSv(int sv) {
      this.sv = sv;
    }

    @Override
    public int getSv() {
      return this.sv;
    }
  }
}
