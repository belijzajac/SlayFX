package com.slayfx.libs.DateAndTime;

public class BuildDataAndTime implements DateAndTimeBuilder {
    private DateAndTime date;

    public BuildDataAndTime() {
        date = new DateAndTime();
    }

    @Override
    public DateAndTimeBuilder buildYears() {
        date.setYears(2018);
        return this;
    }

    @Override
    public DateAndTimeBuilder buildMonths() {
        date.setMonths(10);
        return this;
    }

    @Override
    public DateAndTimeBuilder buildDays() {
        date.setDays(25);
        return this;
    }

    @Override
    public DateAndTimeBuilder buildHours() {
        date.setHours(16);
        return this;
    }

    @Override
    public DateAndTimeBuilder buildMinutes() {
        date.setMinutes(1);
        return this;
    }

    @Override
    public DateAndTimeBuilder buildSeconds() {
        date.setSeconds(0);
        return this;
    }

    @Override
    public DateAndTime buildDate() {
        return date;
    }
}
