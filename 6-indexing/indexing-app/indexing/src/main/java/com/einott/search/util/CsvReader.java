package com.einott.search.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Stream;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class CsvReader {
    
    public static <T> Stream<T> readCsvLazy(String csvFileName, Class<T> pojoClass) {
        CsvToBean<T> csvToBean;
        try {
            csvToBean = new CsvToBeanBuilder<T>(new FileReader(csvFileName)).withType(pojoClass).build();
        } catch (Exception e) {
            throw new CsvReaderException(e);
        }
        return csvToBean.stream();
    }

}
