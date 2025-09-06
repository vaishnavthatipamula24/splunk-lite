package com.example.splunklite.model;

import java.util.List;
import java.util.Map;

public class SearchResponse {
  public record Hit(String id, String index, Map<String,Object> source) {}
  private List<Hit> hits;
  public List<Hit> getHits(){
    return hits;
  }
  public void setHits(List<Hit> h){
    this.hits=h;
  }
}
