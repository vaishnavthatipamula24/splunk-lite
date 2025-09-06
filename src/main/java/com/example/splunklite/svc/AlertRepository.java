package com.example.splunklite.svc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.example.splunklite.model.AlertDefinition;

@Repository
public class AlertRepository {
  private final Map<String, AlertDefinition> store = new ConcurrentHashMap<>();
  public AlertDefinition save(AlertDefinition d){
    if (d.getId()==null) d.setId(UUID.randomUUID().toString());
    store.put(d.getId(), d);
    return d;
  }
  public List<AlertDefinition> all(){
     return new ArrayList<>(store.values());
     }
}
