package com.jp.kinto.app.bff.service;

import com.jp.kinto.app.bff.core.exception.BadRequestException;
import com.jp.kinto.app.bff.core.exception.BadRequestException.ErrorCode;
import com.jp.kinto.app.bff.core.exception.GokuApiMessageException;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.CoreGokuApi;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.contractRegist.ContractRegistReq;
import com.jp.kinto.app.bff.model.contractRegist.ContractRegistRequest;
import com.jp.kinto.app.bff.model.contractRegist.ContractRegistResponse;
import com.jp.kinto.app.bff.repository.LoginHistoryRepository;
import com.jp.kinto.app.bff.repository.MemberContractRespository;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.LoginHistory;
import com.jp.kinto.app.bff.repository.entity.MemberContract;
import com.jp.kinto.app.bff.utils.Asserts;
import com.jp.kinto.app.bff.utils.Casts;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * 契約申込 ContractRegistService
 *
 * @author YE-FOFU
 */
@Service
@Slf4j
public class ContractRegistService {

  @Autowired private CoreGokuApi coreGokuApi;
  @Autowired private MemberContractRespository memberContractRespository;
  @Autowired private UserDeviceRepository userDeviceRepository;
  @Autowired private LoginHistoryRepository loginHistoryRepository;

  @Transactional
  public Mono<ContractRegistResponse> contractRegist(AuthUser authUser, ContractRegistReq request) {

    log.info("contractRegist---START---");
    // Validation 基本情報チェック
    validationCheck(request.getContractRegistRequest());
    LocalDateTime now = LocalDateTime.now();

    return coreGokuApi
        .registContract(request, authUser.getJpnKintoIdAuth())
        .doOnError(
            GokuApiMessageException.class,
            e -> {
              if (HttpStatus.NOT_FOUND.equals(e.getRemoteHttpStatus())) {
                throw new BadRequestException(
                    ErrorCode.VehicleDataExpired, Msg.VehicleAndDealerDataExpired, e);
              } else {
                throw e;
              }
            })
        .flatMap(
            data ->
                loginHistoryRepository
                    .findOneByUserAndMemberId(
                        authUser.getUserId(), data.getContract().getKintoCoreMemberId())
                    .flatMap(
                        existingLoginHistory -> {
                          existingLoginHistory.setLoginDatetime(LocalDateTime.now());
                          return loginHistoryRepository.save(existingLoginHistory);
                        })
                    .switchIfEmpty(
                        loginHistoryRepository.save(
                            new LoginHistory()
                                .setUserId(authUser.getUserId())
                                .setMemberId(data.getContract().getKintoCoreMemberId())
                                .setLoginDatetime(LocalDateTime.now())))
                    .then(
                        userDeviceRepository
                            .findById(authUser.getUserId())
                            .flatMap(
                                userDevice -> {
                                  userDevice.setMemberId(data.getContract().getKintoCoreMemberId());
                                  userDevice.setUpdateDatetime(now);
                                  return userDeviceRepository.save(userDevice);
                                })
                            .then(
                                Mono.defer(
                                    () -> {
                                      MemberContract memberContract = new MemberContract();
                                      memberContract.setContractId(
                                          Casts.toString(data.getContract().getId()));
                                      memberContract.setApplicationId(
                                          data.getApplication().getId());
                                      memberContract.setMemberId(
                                          data.getContract().getKintoCoreMemberId());
                                      memberContract.setOldContractId(
                                          data.getContract().getOldContractId());
                                      memberContract.setCreateDatetime(now);
                                      memberContract.setUpdateDatetime(now);
                                      return memberContractRespository
                                          .save(memberContract)
                                          .thenReturn(data);
                                    }))))
        .map(
            data -> {
              ContractRegistResponse response = new ContractRegistResponse();
              response.setContractId(data.getContract().getId());
              response.setMemberId(data.getContract().getKintoCoreMemberId());
              return response;
            });
  }

  /**
   * Validationチェック
   *
   * @param request チェック対象
   */
  private void validationCheck(ContractRegistRequest request) {
    Asserts.notEmptyText(
        request.getMemberType(), () -> new MessageException(Msg.required.args("メンバータイプ")));
    Asserts.notEmptyText(
        request.getUserName(), () -> new MessageException(Msg.required.args("メールアドレス")));
    Asserts.notNull(
        request.getContractCar(), () -> new MessageException(Msg.required.args("車契約情報")));
  }
}
