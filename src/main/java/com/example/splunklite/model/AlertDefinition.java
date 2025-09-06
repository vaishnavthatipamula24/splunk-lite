package com.example.splunklite.model;

public class AlertDefinition {
  private String id;
  private String name;
  private SearchRequest query;
  private Threshold threshold;
  private Notify notify;

  public static class Notify {
    private String type; // webhook
    private String url;
  private String to; // comma-separated emails
    public String getType(){return type;} public void setType(String t){this.type=t;}
    public String getUrl(){return url;} public void setUrl(String u){this.url=u;}
  public String getTo(){return to;} public void setTo(String t){this.to=t;}
  }

  public String getId(){return id;} public void setId(String id){this.id=id;}
  public String getName(){return name;} public void setName(String n){this.name=n;}
  public SearchRequest getQuery(){return query;} public void setQuery(SearchRequest q){this.query=q;}
  public Threshold getThreshold(){return threshold;} public void setThreshold(Threshold t){this.threshold=t;}
  public Notify getNotify(){return notify;} public void setNotify(Notify n){this.notify=n;}
}
