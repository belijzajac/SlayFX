package com.slayfx.libs.DateAndTime;

public interface DateAndTimeBuilder {
    DateAndTimeBuilder buildYears();
    DateAndTimeBuilder buildMonths();
    DateAndTimeBuilder buildDays();
    DateAndTimeBuilder buildHours();
    DateAndTimeBuilder buildMinutes();
    DateAndTimeBuilder buildSeconds();
    DateAndTime buildDate();
}
