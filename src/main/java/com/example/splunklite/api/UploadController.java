package com.example.splunklite.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.splunklite.model.LogEvent;
import com.example.splunklite.svc.Indexer;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class UploadController {
  private final Indexer indexer;
  private final ObjectMapper om = new ObjectMapper();

  public UploadController(Indexer indexer){ this.indexer = indexer; }

  @PostMapping("/upload")
  public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws Exception {
    if (file == null || file.isEmpty()) return ResponseEntity.badRequest().body("file required");
    List<String> errors = new ArrayList<>();
    int count = 0;
    try (BufferedReader r = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))){
      String line;
      while ((line = r.readLine()) != null){
        line = line.trim();
        if (line.isEmpty()) continue;
        try {
          // try JSON first
          LogEvent e = om.readValue(line, LogEvent.class);
          // if ts is null but line contains a numeric ts field in root, let Jackson map it via fields; leave as-is
          indexer.put(e);
          count++;
        } catch (Exception je){
          // try simple CSV/TSV: source,env,level,message,ts (ts optional)
          try {
            String[] parts = line.split("[\t,|;]");
            LogEvent e = new LogEvent();
            if (parts.length>0) e.setSource(parts[0].trim());
            if (parts.length>1) e.setEnv(parts[1].trim());
            if (parts.length>2) e.setLevel(parts[2].trim());
            if (parts.length>3) e.setMessage(parts[3].trim());
            if (parts.length>4){
              try { long t = Long.parseLong(parts[4].trim()); e.setTs(Instant.ofEpochMilli(t)); } catch(NumberFormatException x) { /* ignore */ }
            }
            indexer.put(e);
            count++;
          } catch(Exception ex){
            errors.add("line failed: " + line.substring(0, Math.min(120, line.length())) + " -> " + ex.getMessage());
          }
        }
      }
    }
    return ResponseEntity.ok().body(java.util.Map.of("count", count, "errors", errors));
  }
}
