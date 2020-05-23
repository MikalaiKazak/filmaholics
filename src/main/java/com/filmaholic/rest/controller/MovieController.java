package com.filmaholic.rest.controller;

import info.movito.themoviedbapi.model.core.MovieResultsPage;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("api/v1/movies")
public class MovieController {


  @GetMapping("/recommend/{userId}")
  public MovieResultsPage getRecommendations(@PathVariable("userId") String userId) {
    return null;
  }
}
