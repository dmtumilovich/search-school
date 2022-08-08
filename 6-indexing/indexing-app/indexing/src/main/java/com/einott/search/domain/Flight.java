package com.einott.search.domain;

import lombok.Data;

@Data
public class Flight {

    private String year;
    private String month;
    private String dayOfMonth;
    private String dayOfWeek;
    private String depTime;
    private String crsDepTime;
    private String arrTime;
    private String crsArrTime;
    private String uniqueCarrier;
    private String flightNum;
    private String tailNum;
    private String actualElapseTime;
    private String crsElapsedTime;
    private String airTime;
    private String arrDelay;
    private String depDelay;
    private String origin;
    private String dest;
    private String distance;
    private String taxiIn;
    private String taxiOut;
    private String cancelled; // TODO boolean
    private String cancellationCode;
    private String diverted; // TODO boolean
    private String carrierDelay;
    private String weatherDelay;
    private String nasDelay;
    private String securityDelay;
    private String lateAircraftDelay;
    
}
