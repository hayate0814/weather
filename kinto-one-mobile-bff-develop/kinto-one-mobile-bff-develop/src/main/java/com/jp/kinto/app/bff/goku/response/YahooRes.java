package com.jp.kinto.app.bff.goku.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class YahooRes {

  @JsonProperty("ResultInfo")
  private ResultInfo resultInfo;

  @JsonProperty("Feature")
  private List<Feature> feature;

  @Data
  public static class ResultInfo {
    @JsonProperty("Count")
    private int count;

    @JsonProperty("Total")
    private int total;

    @JsonProperty("Status")
    private int status;
  }

  @Data
  public static class Feature {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Geometry")
    private Geometry geometry;
  }

  @Data
  public static class Geometry {
    @JsonProperty("Coordinates")
    private String coordinates;

    public double getLat() {
      String[] coordinates = this.coordinates.split(",");
      if (coordinates.length >= 2) {
        return Double.parseDouble(coordinates[1]);
      } else {
        return 0.0;
      }
    }

    public double getLon() {
      String[] coordinates = this.coordinates.split(",");
      if (coordinates.length >= 2) {
        return Double.parseDouble(coordinates[0]);
      } else {
        return 0.0;
      }
    }
  }
}
