package com.example.splunklite.svc;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.splunklite.model.AlertDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class Notifier {
  private final HttpClient client = HttpClient.newHttpClient();
  private final ObjectMapper om = new ObjectMapper();
  private final JavaMailSender mailSender;

  public Notifier(JavaMailSender mailSender){ this.mailSender = mailSender; }

  public void send(AlertDefinition def, long hits) {
    try {
      if (def.getNotify()==null) return;
      // if notify has url -> webhook
      if (def.getNotify().getUrl()!=null){
        String json = om.writeValueAsString(Map.of("alert", def.getName(), "hits", hits));
        HttpRequest req = HttpRequest.newBuilder()
          .uri(URI.create(def.getNotify().getUrl()))
          .POST(HttpRequest.BodyPublishers.ofString(json))
          .header("Content-Type", "application/json")
          .build();
        client.send(req, HttpResponse.BodyHandlers.discarding());
      }
      // if notify has comma-separated emails -> send mail
      if (def.getNotify().getTo()!=null && !def.getNotify().getTo().isBlank()){
        String[] to = def.getNotify().getTo().split(",");
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(trimArray(to));
        msg.setSubject("Alert: " + def.getName());
        msg.setText("Alert fired: " + def.getName() + " - hits=" + hits);
        mailSender.send(msg);
      }
    } catch (Exception ignored) { }
  }

  private String[] trimArray(String[] arr){
    return java.util.Arrays.stream(arr).map(String::trim).filter(s->!s.isEmpty()).toArray(String[]::new);
  }
}
