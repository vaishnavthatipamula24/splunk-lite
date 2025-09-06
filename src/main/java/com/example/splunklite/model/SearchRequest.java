package com.example.splunklite.model;

public class SearchRequest {
  private String from;  // ISO8601 or now-15m
  private String to;    // ISO8601 or now
  private String query; // FTS5 query string
  private int size = 100;
  public String getFrom(){return from;} public void setFrom(String s){this.from=s;}
  public String getTo(){return to;} public void setTo(String s){this.to=s;}
  public String getQuery(){return query;} public void setQuery(String q){this.query=q;}
  public int getSize(){return size;} public void setSize(int s){this.size=s;}
}
