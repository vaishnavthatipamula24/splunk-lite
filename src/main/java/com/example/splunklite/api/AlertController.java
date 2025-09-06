package com.example.splunklite.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.splunklite.model.AlertDefinition;
import com.example.splunklite.svc.AlertRepository;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
  private final AlertRepository repo;
  public AlertController(AlertRepository r){
     this.repo = r; 
    }

  @PostMapping public AlertDefinition create(@RequestBody AlertDefinition d){ 
    return repo.save(d);
   }
   
  @GetMapping public List<AlertDefinition> list(){ 
    return repo.all();
  }
}
