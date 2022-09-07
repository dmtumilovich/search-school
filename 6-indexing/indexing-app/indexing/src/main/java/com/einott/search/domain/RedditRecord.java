package com.einott.search.domain;

import java.time.LocalDateTime;

import com.einott.search.util.CsvLongConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;

import lombok.Data;

@Data
public class RedditRecord {
    @CsvBindByName
    private String title;
    @CsvBindByName
    private Integer score;
    @CsvBindByName
    private String id;
    @CsvBindByName
    private String url;
    @CsvBindByName(column = "comms_num")
    private Integer commsNum;
    @CsvCustomBindByName(converter = CsvLongConverter.class)
    private Long created;
    @CsvBindByName
    private String body;
    @CsvBindByName
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;
}
