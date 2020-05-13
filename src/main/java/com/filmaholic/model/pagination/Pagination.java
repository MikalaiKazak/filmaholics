package com.filmaholic.model.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {

  Integer page;

  Integer size;

  String sort;

  Sort.Direction direction;
}
