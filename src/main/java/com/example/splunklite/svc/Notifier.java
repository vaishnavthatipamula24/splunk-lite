package com.example.splunklite.svc;

import com.example.splunklite.model.AlertDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class Notifier {
  private final HttpClient client = HttpClient.newHttpClient();
  private final ObjectMapper om = new ObjectMapper();

  public void send(AlertDefinition def, long hits) {
    try {
      if (def.getNotify()==null || def.getNotify().getUrl()==null) return;
      String json = om.writeValueAsString(Map.of("alert", def.getName(), "hits", hits));
      HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create(def.getNotify().getUrl()))
        .POST(HttpRequest.BodyPublishers.ofString(json))
        .header("Content-Type", "application/json")
        .build();
      client.send(req, HttpResponse.BodyHandlers.discarding());
    } catch (Exception ignored) {}
  }
}
