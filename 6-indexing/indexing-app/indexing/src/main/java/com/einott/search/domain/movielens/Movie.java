package com.einott.search.domain.movielens;

import com.einott.search.util.CsvGenresConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Data;

import java.util.List;

@Data
public class Movie {
    @CsvBindByName
    private Long movieId;
    @CsvBindByName
    private String title;
    @CsvCustomBindByName(converter = CsvGenresConverter.class)
    private List<String> genres;
}
