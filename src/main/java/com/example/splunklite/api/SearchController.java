package com.example.splunklite.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.splunklite.model.SearchRequest;
import com.example.splunklite.model.SearchResponse;
import com.example.splunklite.svc.Searcher;

@RestController
@RequestMapping("/api/search")
public class SearchController {
  private final Searcher searcher;
  public SearchController(Searcher s){
     this.searcher = s;
     }

  @PostMapping
  public SearchResponse search(@RequestBody SearchRequest req) {
    return searcher.run(req);
  }
}
