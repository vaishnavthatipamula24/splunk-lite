package com.example.splunklite.api;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.splunklite.model.LogEvent;
import com.example.splunklite.svc.Indexer;

@RestController
@RequestMapping("/api/logs")
public class LogController {
  
  private final Indexer indexer;
  public LogController(Indexer i){ 
    this.indexer = i; 
  }

  @PostMapping
  public ResponseEntity<?> ingest(@RequestBody LogEvent e) throws Exception {
    if (e.getTs()==null) e.setTs(Instant.now());
    indexer.put(e);
    return ResponseEntity.accepted().build();
  }
}
