package com.einott.search.domain;

import lombok.Data;

@Data
public class Flight {

    private int year;
    private int month;
    private int dayOfMonth;
    private int dayOfWeek;
    private int depTime;
    private int crsDepTime;
    private int arrTime;
    private int crsArrTime;
    private String uniqueCarrier;
    private String flightNum;
    private String tailNum;
    private int actualElapseTime;
    private int crsElapsedTime;
    private int airTime;
    private int arrDelay;
    private int depDelay;
    private String origin;
    private String dest;
    private int distance;
    private int taxiIn;
    private int taxiOut;
    private int cancelled; // TODO boolean
    private String cancellationCode;
    private int diverted; // TODO boolean
    private String carrierDelay;
    private String weatherDelay;
    private String nasDelay;
    private String securityDelay;
    private String lateAircraftDelay;
    
}
