package com.example.splunklite.model;

public class Threshold {
  private String operator; // >=, >, <, <=
  private long value;
  public String getOperator(){return operator;} public void setOperator(String o){this.operator=o;}
  public long getValue(){return value;} public void setValue(long v){this.value=v;}
}
