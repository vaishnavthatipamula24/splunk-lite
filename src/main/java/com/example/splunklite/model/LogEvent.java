package com.example.splunklite.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogEvent {
  private String source;
  private String env;
  private String level;
  private String message;
  private Instant ts;
  private Map<String, Object> fields = new HashMap<>();

  @JsonAnySetter public void put(String k, Object v){ fields.put(k,v); }
  @JsonAnyGetter public Map<String,Object> fields(){ return fields; }

  public String getSource(){return source;} public void setSource(String s){this.source=s;}
  public String getEnv(){return env;} public void setEnv(String s){this.env=s;}
  public String getLevel(){return level;} public void setLevel(String s){this.level=s;}
  public String getMessage(){return message;} public void setMessage(String s){this.message=s;}
  public Instant getTs(){return ts;} public void setTs(Instant t){this.ts=t;}
}
