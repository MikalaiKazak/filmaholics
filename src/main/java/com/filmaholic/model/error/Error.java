package com.filmaholic.model.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Error {

  @JsonProperty("time")
  public String timestamp;
  public String error;
  public Integer status;
  public String path;
}
