package com.example.splunklite.svc;

import com.example.splunklite.model.AlertDefinition;
import com.example.splunklite.model.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertScheduler {
  private final AlertRepository repo;
  private final Searcher searcher;
  private final Notifier notifier;

  public AlertScheduler(AlertRepository r, Searcher s, Notifier n,
                        @Value("${app.alert-interval-seconds:60}") long interval) {
    this.repo = r; this.searcher = s; this.notifier = n;
  }

  @Scheduled(fixedDelayString = "${app.alert-interval-seconds:60}000")
  public void evaluate(){
    List<AlertDefinition> defs = repo.all();
    for (AlertDefinition d : defs) {
      try {
        SearchResponse res = searcher.run(d.getQuery());
        long hits = res.getHits()==null?0:res.getHits().size();
        boolean fire = switch (d.getThreshold().getOperator()) {
          case ">=" -> hits >= d.getThreshold().getValue();
          case ">"  -> hits >  d.getThreshold().getValue();
          case "<"  -> hits <  d.getThreshold().getValue();
          case "<=" -> hits <= d.getThreshold().getValue();
          default   -> false;
        };
        if (fire) notifier.send(d, hits);
      } catch (Exception ignored) {}
    }
  }
}
