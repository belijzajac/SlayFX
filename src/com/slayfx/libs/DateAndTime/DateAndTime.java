package com.slayfx.libs.DateAndTime;

public class DateAndTime implements ControlDateAndTime{
    private int years, months, days, hours, minutes, seconds;

    @Override
    public int compareTo(DateAndTime date) {
        if(years - date.years == 0) {
            if(months - date.months == 0){
                if(days - date.days == 0){
                    if(hours - date.hours == 0){
                        if(minutes - date.minutes == 0){
                            if(seconds - date.seconds == 0){
                                return 0;
                            }
                            return seconds - date.seconds;
                        }
                        return minutes - date.minutes;
                    }
                    return hours - date.hours;
                }
                return days - date.days;
            }
            return months - date.months;
        }
        return years - date.years;

        }

    @Override
    public void addYears(int y) {
        years += y;
    }

    @Override
    public void addMonths(int m) {
        months += m;
        checkMonths();
    }

    @Override
    public void addDays(int d) {
        days += d;
        checkDays();
    }

    @Override
    public void addHours(int h) {
        hours += h;
        checkHours();
    }

    @Override
    public void addMinutes(int m) {
        minutes += m;
        checkMinutes();
    }

    @Override
    public void addSeconds(int s) {
        seconds += s;
        checkSeconds();
    }

    @Override
    public void subYears(int y) {
        years -= y;
    }

    @Override
    public void subMonths(int m) {
        months -= m;
        checkMonths();
    }

    @Override
    public void subDays(int d) {
        days -= d;
        checkDays();
    }

    @Override
    public void subHours(int h) {
        hours -= h;
        checkHours();
    }

    @Override
    public void subMinutes(int m) {
        minutes -= m;
        checkMinutes();
    }

    @Override
    public void subSeconds(int s) {
        seconds -= s;
        checkSeconds();
    }

    @Override
    public void setYears(int y) {
        years = y;
    }

    @Override
    public void setMonths(int m) {
        months = m;
        checkMonths();
    }

    @Override
    public void setDays(int d) {
        days = d;
        checkDays();
    }

    @Override
    public void setHours(int h) {
        hours = h;
        checkHours();
    }

    @Override
    public void setMinutes(int m) {
        minutes = m;
        checkMinutes();
    }

    @Override
    public void setSeconds(int s) {
        seconds = s;
        checkSeconds();
    }

    @Override
    public String getYears() {
        return String.valueOf(years);
    }

    @Override
    public String getMonths() {
        return String.valueOf(months);
    }

    @Override
    public String getDays() {
        return String.valueOf(days);
    }

    @Override
    public String getHours() {
        return String.valueOf(hours);
    }

    @Override
    public String getMinutes() {
        if(minutes < 10)
            return "0" + String.valueOf(minutes);
        return String.valueOf(minutes);
    }

    @Override
    public String getSeconds() {
        if(seconds < 10)
            return "0" + String.valueOf(seconds);
        return String.valueOf(seconds);
    }

    @Override
    public String getDate() {
        return "Year:" + getYears() + " month:" + getMonths() + " day:" + getDays() + "\n" + "Hour:" + getHours() + " minute:" + getMinutes() + " second:" + getSeconds() + "\n";
    }

    private void checkMonths(){
        if(months > 12){
            years += months/12;
            months = months % 12;
        }

        else if(months < 1){
            years += months/12 - 1;
            months = 12 + months % 12;
        }
    }

    private void checkDays(){
        checkMonths();
        switch (months) {
            case 1: //January
            case 3: //March
            case 5: //May
            case 7: //July
            case 8: //August
            case 10: //October
            case 12: //December
                if (days > 31){
                    months++;
                    days -= 31;
                    checkDays();
                }

                break;


            case 4: //April
            case 6: //June
            case 9: //September
            case 11: //November
                if (days > 30){
                    months++;
                    days -= 30;
                    checkDays();
                }
                break;

            case 2: //Feburary
                if(years % 4 == 0){
                    if (days > 29){
                        months++;
                        days -= 29;
                        checkDays();
                    }
                }
                else {
                    if (days > 28) {
                        months++;
                        days -= 28;
                        checkDays();
                    }
                }
                break;
        }

        switch (months) {
            case 1: //January
            case 2: //Feburary
            case 4: //April
            case 6: //June
            case 9: //September
            case 8: //August
            case 11: //November

                if(days < 1){
                    months--;
                    days += 31;
                    checkDays();
                }
                break;


            case 5: //May
            case 7: //July
            case 10: //October
            case 12: //December
                if(days < 1){
                    months--;
                    days += 30;
                    checkDays();
            }
                break;



            case 3: //March
                if(years % 4 == 0){
                    if(days < 1){
                        months--;
                        days += 29;
                        checkDays();
                    }

                }
                else{
                    if(days < 1){
                        months--;
                        days += 28;
                        checkDays();
                    }
                }
        }
    }
    private void checkHours(){
        if(hours > 24){
            days += hours/24;
            hours = hours % 24;
            checkDays();
        }

        else if(hours < 0){
            days += hours/24 - 1;
            hours = 24 + hours % 24;
            checkDays();
        }
    }

    private void checkMinutes(){
        if(minutes > 60){
            hours += minutes/60;
            minutes = minutes % 60;
            checkHours();
        }

        else if(minutes < 0){
            hours += minutes/60 - 1;
            minutes = 60 + minutes % 60;
            checkHours();
        }
    }

    private void checkSeconds(){
        if(seconds > 60){
            minutes += seconds/60;
            seconds = seconds % 60;
            checkMinutes();
        }

        else if(seconds < 0){
            minutes += seconds/60 - 1;
            seconds = 60 + seconds % 60;
            checkMinutes();
        }
    }
}


