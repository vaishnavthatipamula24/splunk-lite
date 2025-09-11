package com.example.splunklite.svc;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.splunklite.model.AlertDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class Notifier {
  private final HttpClient client = HttpClient.newHttpClient();
  private final ObjectMapper om = new ObjectMapper();
  private final EmailService emailService;

  public Notifier(EmailService emailService){ this.emailService = emailService; }

  public void send(AlertDefinition def, long hits) {
    try {
      if (def.getNotify()==null) return;

      // webhook if present
      if (def.getNotify().getUrl()!=null){
        String json = om.writeValueAsString(Map.of("alert", def.getName(), "hits", hits));
        HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(def.getNotify().getUrl()))
          .POST(HttpRequest.BodyPublishers.ofString(json))
          .header("Content-Type", "application/json")
          .build();
        client.send(req, HttpResponse.BodyHandlers.discarding());
      }

      // mail: use EmailService
      if (def.getNotify().getTo()!=null && !def.getNotify().getTo().isBlank()){
        String[] to = def.getNotify().getTo().split(",");
        String subject = "Alert: " + def.getName();
        String text = "Alert fired: " + def.getName() + " - hits=" + hits;
        for (String t : trimArray(to)){
          emailService.sendEmail(t, subject, text);
        }
      }
    } catch (Exception ignored) { }
  }

  private String[] trimArray(String[] arr){
    return java.util.Arrays.stream(arr).map(String::trim).filter(s->!s.isEmpty()).toArray(String[]::new);
  }
}
