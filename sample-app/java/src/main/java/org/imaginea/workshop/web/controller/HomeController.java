package org.imaginea.workshop.web.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;


@RestController
@RequestMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
@ApiIgnore
public class HomeController {

  @GetMapping(path = "/health")
  public ResponseEntity<?> health() {
    return ResponseEntity.ok().build();
  }

}
