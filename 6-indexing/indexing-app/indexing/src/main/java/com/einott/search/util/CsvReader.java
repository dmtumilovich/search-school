package com.einott.search.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.stream.Stream;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class CsvReader {
    
    public static <T> Stream<T> readCsvLazy(String csvFileName, Class<T> pojoClass) {
        try {
            return readCsvLazy(new FileReader(csvFileName), pojoClass);
        } catch (FileNotFoundException e) {
            throw new CsvReaderException(e);
        }
    }

    public static <T> Stream<T> readCsvLazy(Reader csvFile, Class<T> pojoClass) {
        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvFile).withType(pojoClass).build();
        return csvToBean.stream();
    }

}
