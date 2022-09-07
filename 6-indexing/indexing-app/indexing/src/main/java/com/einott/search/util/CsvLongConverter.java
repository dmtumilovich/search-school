package com.einott.search.util;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class CsvLongConverter<T, I> extends AbstractBeanField <T, I> {

    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Double.valueOf(value).longValue();
        } catch (NumberFormatException e) {
            throw new CsvDataTypeMismatchException(e.getMessage());
        }
    }
    
}
