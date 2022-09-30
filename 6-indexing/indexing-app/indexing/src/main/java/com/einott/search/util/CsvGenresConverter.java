package com.einott.search.util;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.util.List;

public class CsvGenresConverter<T, I> extends AbstractBeanField<T, I> {

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return List.of(value.split("\\|"));
        } catch (Throwable t) {
            throw new CsvDataTypeMismatchException(t.getMessage());
        }
    }
}
