package com.example.splunklite.svc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.example.splunklite.model.SearchRequest;
import com.example.splunklite.model.SearchResponse;
import com.example.splunklite.util.TimeUtil;

@Service
public class Searcher {
  private final JdbcTemplate jdbc;
  public Searcher(JdbcTemplate jdbc){ this.jdbc = jdbc; }

  public SearchResponse run(SearchRequest req) {
    long from = TimeUtil.parse(req.getFrom()==null?"now-24h":req.getFrom()).toEpochMilli();
    long to   = TimeUtil.parse(req.getTo()==null?"now":req.getTo()).toEpochMilli();
    int size = Math.max(1, req.getSize());

    List<Map<String,Object>> rows;
    if (req.getQuery()!=null && !req.getQuery().isBlank()) {
      String q = req.getQuery();
      rows = jdbc.queryForList(
        "SELECT l.*, MATCH(message,source,env,level) AGAINST (? IN BOOLEAN MODE) AS score FROM logs l WHERE l.ts BETWEEN ? AND ? AND MATCH(message,source,env,level) AGAINST (? IN BOOLEAN MODE) ORDER BY score DESC LIMIT ?",
        q, from, to, q, size
      );
    } else {
      rows = jdbc.queryForList(
        "SELECT * FROM logs WHERE ts BETWEEN ? AND ? ORDER BY ts DESC LIMIT ?",
        from, to, size
      );
    }

    List<SearchResponse.Hit> hits = new ArrayList<>();
    for (Map<String,Object> r : rows) {
      Map<String,Object> src = new LinkedHashMap<>(r);
      src.remove("id");
      hits.add(new SearchResponse.Hit(String.valueOf(r.get("id")), "sqlite", src));
    }
    SearchResponse resp = new SearchResponse();
    resp.setHits(hits);
    return resp;
  }
}
