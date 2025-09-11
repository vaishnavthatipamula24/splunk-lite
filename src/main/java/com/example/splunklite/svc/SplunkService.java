package com.example.splunklite.svc;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.splunklite.model.LogEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SplunkService {
  private final HttpClient client = HttpClient.newHttpClient();
  private final ObjectMapper om = new ObjectMapper();
  private final String hecUrl;
  private final String hecToken;

  public SplunkService(@Value("${splunk.hec.url:}") String hecUrl,
                       @Value("${splunk.hec.token:}") String hecToken) {
    this.hecUrl = hecUrl;
    this.hecToken = hecToken;
  }

  public void sendEvent(LogEvent e){
    try {
      if (hecUrl==null || hecUrl.isBlank() || hecToken==null || hecToken.isBlank()) return;
      Map<String,Object> payload = new HashMap<>();
      payload.put("time", ((e.getTs()==null)? Instant.now().toEpochMilli()/1000 : e.getTs().toEpochMilli()/1000));
      Map<String,Object> event = new HashMap<>();
      event.put("source", e.getSource());
      event.put("env", e.getEnv());
      event.put("level", e.getLevel());
      event.put("message", e.getMessage());
      event.put("extra", e.fields());
      payload.put("event", event);
      String json = om.writeValueAsString(payload);
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(hecUrl))
        .header("Authorization", "Splunk " + hecToken)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(json))
        .build();
      client.sendAsync(req, HttpResponse.BodyHandlers.discarding());
    } catch (Exception ignored) { }
  }
}
