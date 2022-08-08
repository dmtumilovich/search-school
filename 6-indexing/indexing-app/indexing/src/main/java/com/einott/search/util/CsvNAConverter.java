package com.einott.search.util;

import com.einott.search.domain.Flight;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class CsvNAConverter extends AbstractBeanField<Flight, Object> {

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (s.equals("NA")) {
            return -1;
        } else {
            return Integer.parseInt(s);
        }
    }
    
}
