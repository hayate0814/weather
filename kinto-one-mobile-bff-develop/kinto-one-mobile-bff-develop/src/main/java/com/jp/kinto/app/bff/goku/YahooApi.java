package com.jp.kinto.app.bff.goku;

import com.jp.kinto.app.bff.core.properties.YahooProperties;
import com.jp.kinto.app.bff.goku.client.YahooClient;
import com.jp.kinto.app.bff.goku.response.YahooRes;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** YahooPI */
@Component
public class YahooApi {

  private YahooClient yahooClient;
  private YahooProperties yahooProperties;

  public YahooApi(Function<String, WebClient> webClientFun, YahooProperties yahooProperties) {
    this.yahooProperties = yahooProperties;
    this.yahooClient = new YahooClient(webClientFun.apply(yahooProperties.getUrl()));
  }

  public Mono<List<YahooRes>> fetchAddress(String keyWord, int page, List<YahooRes> responseList) {
    String url = "?appid=" + yahooProperties.getKey();
    url += "&query=" + keyWord;
    url += "&page=" + page;
    url += "&output=json";
    url += "&detail=simple";
    url += "&al=3";
    url += "&ar=eq";
    url += "&results=100";
    url += "&sort=address";
    url += "&_=" + System.currentTimeMillis();

    var currerntPage = new int[] {page};
    String finalUrl = url;
    return yahooClient
        .call(YahooRes.class, client -> client.get().uri(finalUrl))
        .flatMap(
            response -> {
              int count = response.getResultInfo().getCount();
              if (count != 0) {
                responseList.add(response);
              }
              if (count == 100 && currerntPage[0] < 2) {
                return fetchAddress(keyWord, (++currerntPage[0]), responseList);
              } else {
                return Mono.just(responseList);
              }
            });
  }
}
