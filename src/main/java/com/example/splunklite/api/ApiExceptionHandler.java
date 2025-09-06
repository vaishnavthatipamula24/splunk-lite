package com.example.splunklite.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String,Object>> handle(Exception ex){
    Map<String,Object> body = new LinkedHashMap<>();
    body.put("error", ex.getClass().getSimpleName());
    body.put("message", ex.getMessage());
    // include a short stack snippet for debugging (first element)
    StackTraceElement[] st = ex.getStackTrace();
    if (st != null && st.length>0) body.put("at", st[0].toString());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
