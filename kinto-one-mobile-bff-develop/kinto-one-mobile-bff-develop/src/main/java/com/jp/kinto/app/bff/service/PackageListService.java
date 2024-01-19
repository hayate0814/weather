package com.jp.kinto.app.bff.service;

import static com.jp.kinto.app.bff.core.constant.Constant.SingleGroupCode.*;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.PackageRes.PackageEquipmentRes;
import com.jp.kinto.app.bff.goku.response.PackageRes.PackageExclusiveEquipmentRes;
import com.jp.kinto.app.bff.model.packageData.Package;
import com.jp.kinto.app.bff.model.packageData.Package.PackageEquipment;
import com.jp.kinto.app.bff.model.packageData.Package.PackageExclusiveEquipment;
import com.jp.kinto.app.bff.model.packageData.Package.SingleOption;
import com.jp.kinto.app.bff.utils.Asserts;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PackageListService {
  @Autowired private CarMasterGokuApi gokuApi;

  public Mono<List<Package>> getPackageList(
      Integer contractMonths, String gradeId, String planType) {
    log.debug("PackageList取得---START---");
    Asserts.notNull(contractMonths, () -> new MessageException(Msg.required.args("契約月数")));
    Asserts.notEmptyText(gradeId, () -> new MessageException(Msg.required.args("グレードID")));
    Asserts.notEmptyText(planType, () -> new MessageException(Msg.required.args("契約プラン")));
    Asserts.assertTrue(
        Constant.PlanSv.isValidByCode(planType),
        () -> new MessageException(Msg.invalidValue.args("契約プラン")));
    Asserts.isValidSystemValue(
        Constant.ContractMonthSv.class,
        contractMonths,
        () -> new MessageException(Msg.invalidValue.args("契約月数")));

    return gokuApi
        .getPackageList(contractMonths, gradeId, planType)
        .map(
            e -> {
              Asserts.assertTrue(e != null && e.getData() != null, "パッケージデータが取得失敗しました");
              return e.getData().stream()
                  .map(
                      f -> {
                        Asserts.assertTrue(
                            !Objects.isNull(f.getPackageExclusiveEquipments())
                                && !Objects.isNull(f.getPackageEquipments())
                                && !Objects.isNull(f.getSingleOptions()),
                            "パッケージデータが取得失敗しました");
                        Package packages = new Package();
                        BeanUtils.copyProperties(f, packages);

                        packages.setPackageExclusiveEquipments(
                            f.getPackageExclusiveEquipments().stream()
                                .map(
                                    g -> {
                                      PackageExclusiveEquipment packageExclusiveEquipment =
                                          new PackageExclusiveEquipment();
                                      BeanUtils.copyProperties(g, packageExclusiveEquipment);
                                      return packageExclusiveEquipment;
                                    })
                                .toList());

                        packages.setPackageEquipments(
                            f.getPackageEquipments().stream()
                                .map(
                                    g -> {
                                      PackageEquipment packageEquipment = new PackageEquipment();
                                      BeanUtils.copyProperties(g, packageEquipment);
                                      return packageEquipment;
                                    })
                                .toList());

                        List<SingleOption> compotList = new ArrayList<>();
                        List<SingleOption> iexoptList = new ArrayList<>();
                        List<SingleOption> coldarList = new ArrayList<>();
                        packages.setSingleOptions(
                            f.getSingleOptions().stream()
                                .map(
                                    h -> {
                                      SingleOption singleOption = new SingleOption();
                                      BeanUtils.copyProperties(h, singleOption);
                                      if (IEXOPT.getCode().equals(singleOption.getGroupCode())) {
                                        singleOption.setGroupName(IEXOPT.getName());
                                        iexoptList.add(singleOption);
                                      } else if (COMOPT
                                          .getCode()
                                          .equals(singleOption.getGroupCode())) {
                                        singleOption.setGroupName(COMOPT.getName());
                                        compotList.add(singleOption);
                                      } else {
                                        singleOption.setGroupName(COLDAR.getName());
                                        coldarList.add(singleOption);
                                      }
                                      return singleOption;
                                    })
                                .toList());
                        iexoptList.addAll(compotList);
                        iexoptList.addAll(coldarList);
                        packages.setSingleOptions(iexoptList);
                        List<String> displayNames = new ArrayList<>();
                        displayNames.addAll(
                            f.getPackageExclusiveEquipments().stream()
                                .map(PackageExclusiveEquipmentRes::getGroupName)
                                .distinct()
                                .toList());
                        displayNames.addAll(
                            f.getPackageEquipments().stream()
                                .map(PackageEquipmentRes::getAbbreviation)
                                .distinct()
                                .toList());
                        packages.setDisplayNames(
                            displayNames.stream().map(h -> "・" + h).collect(Collectors.toList()));
                        return packages;
                      })
                  .toList();
            });
  }
}
