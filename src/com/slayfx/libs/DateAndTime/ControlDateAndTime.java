package com.slayfx.libs.DateAndTime;

public interface ControlDateAndTime extends Comparable<DateAndTime> {
    int compareTo(DateAndTime date);

    void addYears(int y);
    void addMonths(int m);
    void addDays(int d);
    void addHours(int h);
    void addMinutes(int m);
    void addSeconds(int s);

    void subYears(int y);
    void subMonths(int m);
    void subDays(int d);
    void subHours(int h);
    void subMinutes(int m);
    void subSeconds(int s);

    void setYears(int y);
    void setMonths(int m);
    void setDays(int d);
    void setHours(int h);
    void setMinutes(int m);
    void setSeconds(int s);

    String getYears();
    String getMonths();
    String getDays();
    String getHours();
    String getMinutes();
    String getSeconds();
    String getDate();
}
