package com.filmaholic.config;

import info.movito.themoviedbapi.TmdbApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TmdbApiConfig {

  @Value("${tmdb.api.key}")
  private String apiKey;

  public TmdbApi getTmdbApi() {
    return new TmdbApi(apiKey);
  }
}
