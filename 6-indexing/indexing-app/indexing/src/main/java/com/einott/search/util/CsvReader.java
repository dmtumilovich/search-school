package com.einott.search.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class CsvReader {

    public static <K, V> Map<K, List<V>> readCsvIndexed(String csvFileName, Class<V> pojoClass, Function<V, K> indexMapper) {
        return readCsvLazy(csvFileName, pojoClass)
                .collect(Collectors.groupingBy(indexMapper));
    }

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
