package com.jp.kinto.app.bff.simulation;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.MonthlyPriceRes;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.auth.AuthUser.Role;
import com.jp.kinto.app.bff.repository.SimulationRepository;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.Simulation;
import com.jp.kinto.app.bff.repository.entity.UserDevice;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/simulations/{simulationId}")
public class SimulationDetailTest extends ApiTest {

  @MockBean private SimulationRepository simulationRepository;
  @MockBean private UserDeviceRepository userDeviceRepository;
  @MockBean private CarMasterGokuApi carMasterGokuApi;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("AuthUserのメンバIDが存在の正常系")
  void success_01() {
    Simulation simulation = new Simulation();
    simulation.setSimulationId(1);
    simulation.setSimulationDatetime(LocalDateTime.now());
    simulation.setUpdateDatetime(LocalDateTime.now());
    simulation.setSimulationJson(
        """
            {"selectedCarSetting":{"desiredNumberPlate1":"567","desiredNumberPlate2":"","planType":"PLAN_A","contractMonths":84,"carModelId":"CAR-0000001199","gradeId":"GRD-0000005483","outerPlateColorId":"OPC-0000036356","interiorColorId":"ITC-0000007101","packageId":"PKG-0000021343","packageExclusiveEquipmentIds":["PED-0000009465","PED-0000009466"],"packageEquipmentIds":["PED-0000008201"],"singleOptionIds":["SOD-0000010444","SOD-0000010445","SOD-0000012355","SOD-0000010447","SOD-0000010449","SOD-0000010450","SOD-0000010456"],"bonusAdditionAmount":50000},"personal":{"selectedDealer":{"dealerCode":"036010AI","toyotaDealer":{"lat":123.33,"lon":12444.33,"carModelEnglishName":"roomy"},"lexusDealer":{"storeName":"kawaguti"}},"survey":{"ownedCarBrand":"TOYOTA","ownedCarCondition":"USED"},"lastName":"筋斗","firstName":"太郎","lastNameKana":"キント","firstNameKana":"タロウ","birthday":"1980-06-03","gender":"MALE","postalCode":"450-0002","prefecture":"愛知県","city":"名古屋市中村区","street":"名駅５","streetKana":"メイエキ５","addressOther":"それ以降の住所５","useLocation":{"postalCode":"335-0026","prefecture":"埼玉県","city":"戸田市","street":"新曽南","addressOther":"使用場所確認","cityKana":"トダシ","streetKana":"ニイゾミナミ","selectedReason":"UNACCOMPANIED_POSTING"},"email":"minghuan.zhang@nextlogy.co.jp","phoneNumber":"03-4441-1235","mobilePhoneNumber":"080-4442-1235","preferredContactPhoneNumber":"03-4443-1235","preferredContactTimezones":["ALL"],"residenceType":"FAMILY_OWNED","residenceYears":"UNDER_1_YEAR","isPrimaryIncomeProvider":"Yes","hasHousingLoan":"Yes","hasSpouse":"Yes","numberOfHousemates":1,"numberOfChildren":1,"annualIncome":500,"usingPurpose":"OTHERS","campaignCode":"8812345","office":{"job":"SELF_EMPLOYED","workplaceBusinessStructure":"LIMITED","workplaceBusinessStructureAffix":"BEFORE","workplaceName":"ＫＩＮＴＯ","workplaceNameKana":"キント","workplaceDepartment":"開発部","workplacePosition":"OFFICER","workplacePostalCode":"450-0002","workplacePrefecture":"愛知県","workplaceCity":"名古屋市中村区","workplaceStreet":"名駅６","workplaceStreetKana":"メイエキ６","workplaceAddressOther":"勤務それ以外６","workplacePhoneNumber":"03-5555-1234","workplaceHeadOfficeLocation":"愛知県","workplaceContinuousYears":"UNDER_5_YEARS","workplaceNumberOfEmployees":"UNDER_50"}}}
            """);
    simulation.setTitleJson(
        """
            {"carModelName":"ルーミー","gradeName":"X GAS 1.0L 2WD(5人)","carImageUrl":"https://kinto-jp.com/s3resource/carmodel/ROM005X07.png"}
           """);

    Mockito.when(simulationRepository.findByIdAndGuestUserId(any(), any()))
        .thenReturn(Mono.just(simulation));

    MonthlyPriceRes monthlyPriceRes =
        parseJsonFile(
            INPUT_ROOT + "/simulation/success_monthly_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getMonthlyPrice(any())).thenReturn(Mono.just(monthlyPriceRes));

    webClient
        .get()
        .uri("/api/simulations/1")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).memberId("aaa").role(Role.User).build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("AuthUserのメンバIDが存在しないの正常系")
  void success_02() {
    UserDevice userDevice = new UserDevice();
    userDevice.setMemberId("aaa");
    Mockito.when(userDeviceRepository.findById(1)).thenReturn(Mono.just(userDevice));
    Simulation simulation = new Simulation();
    simulation.setSimulationId(1);
    simulation.setSimulationDatetime(LocalDateTime.now());
    simulation.setUpdateDatetime(LocalDateTime.now());
    simulation.setSimulationJson(
        """
            {"selectedCarSetting":{"desiredNumberPlate1":"567","desiredNumberPlate2":"","planType":"PLAN_A","contractMonths":84,"carModelId":"CAR-0000001199","gradeId":"GRD-0000005483","outerPlateColorId":"OPC-0000036356","interiorColorId":"ITC-0000007101","packageId":"PKG-0000021343","packageExclusiveEquipmentIds":["PED-0000009465","PED-0000009466"],"packageEquipmentIds":["PED-0000008201"],"singleOptionIds":["SOD-0000010444","SOD-0000010445","SOD-0000012355","SOD-0000010447","SOD-0000010449","SOD-0000010450","SOD-0000010456"],"bonusAdditionAmount":50000},"personal":{"selectedDealer":{"dealerCode":"036010AI","toyotaDealer":{"lat":123.33,"lon":12444.33,"carModelEnglishName":"roomy"},"lexusDealer":{"storeName":"kawaguti"}},"survey":{"ownedCarBrand":"TOYOTA","ownedCarCondition":"USED"},"lastName":"筋斗","firstName":"太郎","lastNameKana":"キント","firstNameKana":"タロウ","birthday":"1980-06-03","gender":"MALE","postalCode":"450-0002","prefecture":"愛知県","city":"名古屋市中村区","street":"名駅５","streetKana":"メイエキ５","addressOther":"それ以降の住所５","useLocation":{"postalCode":"335-0026","prefecture":"埼玉県","city":"戸田市","street":"新曽南","addressOther":"使用場所確認","cityKana":"トダシ","streetKana":"ニイゾミナミ","selectedReason":"UNACCOMPANIED_POSTING"},"email":"minghuan.zhang@nextlogy.co.jp","phoneNumber":"03-4441-1235","mobilePhoneNumber":"080-4442-1235","preferredContactPhoneNumber":"03-4443-1235","preferredContactTimezones":["ALL"],"residenceType":"FAMILY_OWNED","residenceYears":"UNDER_1_YEAR","isPrimaryIncomeProvider":"Yes","hasHousingLoan":"Yes","hasSpouse":"Yes","numberOfHousemates":1,"numberOfChildren":1,"annualIncome":500,"usingPurpose":"OTHERS","campaignCode":"8812345","office":{"job":"SELF_EMPLOYED","workplaceBusinessStructure":"LIMITED","workplaceBusinessStructureAffix":"BEFORE","workplaceName":"ＫＩＮＴＯ","workplaceNameKana":"キント","workplaceDepartment":"開発部","workplacePosition":"OFFICER","workplacePostalCode":"450-0002","workplacePrefecture":"愛知県","workplaceCity":"名古屋市中村区","workplaceStreet":"名駅６","workplaceStreetKana":"メイエキ６","workplaceAddressOther":"勤務それ以外６","workplacePhoneNumber":"03-5555-1234","workplaceHeadOfficeLocation":"愛知県","workplaceContinuousYears":"UNDER_5_YEARS","workplaceNumberOfEmployees":"UNDER_50"}}}
            """);
    simulation.setTitleJson(
        """
            {"carModelName":"ルーミー","gradeName":"X GAS 1.0L 2WD(5人)","carImageUrl":"https://kinto-jp.com/s3resource/carmodel/ROM005X07.png"}
            """);

    Mockito.when(simulationRepository.findByIdAndGuestUserId(any(), any()))
        .thenReturn(Mono.just(simulation));

    MonthlyPriceRes monthlyPriceRes =
        parseJsonFile(
            INPUT_ROOT + "/simulation/success_monthly_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getMonthlyPrice(any())).thenReturn(Mono.just(monthlyPriceRes));

    webClient
        .get()
        .uri("/api/simulations/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("メンバIDが存在しない場合")
  void success_03() {
    UserDevice userDevice = new UserDevice();
    Mockito.when(userDeviceRepository.findById(1)).thenReturn(Mono.just(userDevice));
    Simulation simulation = new Simulation();
    simulation.setSimulationId(1);
    simulation.setSimulationDatetime(LocalDateTime.now());
    simulation.setUpdateDatetime(LocalDateTime.now());
    simulation.setSimulationJson(
        """
            {"selectedCarSetting":{"desiredNumberPlate1":"567","desiredNumberPlate2":"","planType":"PLAN_A","contractMonths":84,"carModelId":"CAR-0000001199","gradeId":"GRD-0000005483","outerPlateColorId":"OPC-0000036356","interiorColorId":"ITC-0000007101","packageId":"PKG-0000021343","packageExclusiveEquipmentIds":["PED-0000009465","PED-0000009466"],"packageEquipmentIds":["PED-0000008201"],"singleOptionIds":["SOD-0000010444","SOD-0000010445","SOD-0000012355","SOD-0000010447","SOD-0000010449","SOD-0000010450","SOD-0000010456"],"bonusAdditionAmount":50000},"personal":{"selectedDealer":{"dealerCode":"036010AI","toyotaDealer":{"lat":123.33,"lon":12444.33,"carModelEnglishName":"roomy"},"lexusDealer":{"storeName":"kawaguti"}},"survey":{"ownedCarBrand":"TOYOTA","ownedCarCondition":"USED"},"lastName":"筋斗","firstName":"太郎","lastNameKana":"キント","firstNameKana":"タロウ","birthday":"1980-06-03","gender":"MALE","postalCode":"450-0002","prefecture":"愛知県","city":"名古屋市中村区","street":"名駅５","streetKana":"メイエキ５","addressOther":"それ以降の住所５","useLocation":{"postalCode":"335-0026","prefecture":"埼玉県","city":"戸田市","street":"新曽南","addressOther":"使用場所確認","cityKana":"トダシ","streetKana":"ニイゾミナミ","selectedReason":"UNACCOMPANIED_POSTING"},"email":"minghuan.zhang@nextlogy.co.jp","phoneNumber":"03-4441-1235","mobilePhoneNumber":"080-4442-1235","preferredContactPhoneNumber":"03-4443-1235","preferredContactTimezones":["ALL"],"residenceType":"FAMILY_OWNED","residenceYears":"UNDER_1_YEAR","isPrimaryIncomeProvider":"Yes","hasHousingLoan":"Yes","hasSpouse":"Yes","numberOfHousemates":1,"numberOfChildren":1,"annualIncome":500,"usingPurpose":"OTHERS","campaignCode":"8812345","office":{"job":"SELF_EMPLOYED","workplaceBusinessStructure":"LIMITED","workplaceBusinessStructureAffix":"BEFORE","workplaceName":"ＫＩＮＴＯ","workplaceNameKana":"キント","workplaceDepartment":"開発部","workplacePosition":"OFFICER","workplacePostalCode":"450-0002","workplacePrefecture":"愛知県","workplaceCity":"名古屋市中村区","workplaceStreet":"名駅６","workplaceStreetKana":"メイエキ６","workplaceAddressOther":"勤務それ以外６","workplacePhoneNumber":"03-5555-1234","workplaceHeadOfficeLocation":"愛知県","workplaceContinuousYears":"UNDER_5_YEARS","workplaceNumberOfEmployees":"UNDER_50"}}}
            """);
    simulation.setTitleJson(
        """
            {"carModelName":"ルーミー","gradeName":"X GAS 1.0L 2WD(5人)","carImageUrl":"https://kinto-jp.com/s3resource/carmodel/ROM005X07.png"}
            """);

    Mockito.when(
            simulationRepository.findByIdAndGuestUserId(any(Integer.class), any(Integer.class)))
        .thenReturn(Mono.just(simulation));

    MonthlyPriceRes monthlyPriceRes =
        parseJsonFile(
            INPUT_ROOT + "/simulation/success_monthly_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getMonthlyPrice(any())).thenReturn(Mono.just(monthlyPriceRes));

    webClient
        .get()
        .uri("/api/simulations/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("メンバIDにデータ取得失敗")
  void failure_01() {
    Mockito.when(simulationRepository.findByIdAndGuestUserId(any(), any()))
        .thenReturn(Mono.empty());
    webClient
        .get()
        .uri("/api/simulations/1")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).memberId("aaa").role(Role.User).build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json(
            """
            {
              "code": 2,
              "message": "該当シミュレーションは存在しません。再度検索をお願い致します"
            }
            """);
  }

  @Test
  @DisplayName("ユーザIDにデータ取得失敗")
  void failure_02() {
    UserDevice userDevice = new UserDevice();
    Mockito.when(userDeviceRepository.findById(any(Integer.class)))
        .thenReturn(Mono.just(userDevice));
    Mockito.when(simulationRepository.findByIdAndGuestUserId(any(), any()))
        .thenReturn(Mono.empty());
    webClient
        .get()
        .uri("/api/simulations/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json(
            """
            {
              "code": 2,
              "message": "該当シミュレーションは存在しません。再度検索をお願い致します"
            }
            """);
  }
}
