/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.lib;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import simple.lib.mime.ParseException;

/**
 *
 */
public class Date implements Comparable<Date> {    
    private final long time;
    
    private long nano;

    public Date() {
        this.time = now();
        this.nano = nano();
    }

    public Date(long i) {
        if ((this.time = i) <= 0)
            throw new NullPointerException();
    }
    
    public Date(Calendar calendar) {
        this(calendar.getTime());
    }
    
    public Date(java.util.Date date) {
        time = date.getTime();
    }
    
    public Date addYear(int plus) {   
        Calendar calendar = getCalendar();
        calendar.add(Calendar.YEAR, plus);
        return new Date(calendar);
    }
    
    public Date setYear(int plus) {   
        Calendar calendar = getCalendar();
        calendar.set(Calendar.YEAR, plus);
        return new Date(calendar);
    }
    
    public Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date());
        return calendar;
    }
    
    public int getYear() {
        return getCalendar().get(Calendar.YEAR);
    }
    
    public long getTime() {
        return time;
    }

    public int compareTo() {
        return compareTo(new Date());
    }
    
    public int compareTo(Date d) {
        return (int) (d.time - time);
    }
    
    public boolean equals(long d) {
        return time == d;
    }
    
    public boolean equals(Date d) {
        return time == d.time;
    }

    public String toString() {
        return toString("dd/MM/yyyy HH:mm:ss");
    }

    public String toString(String format) {
        return toString(format, Locale.getDefault());
    }

    public String toString(String format, Locale locale) {
        return toString(new SimpleDateFormat(format, locale));
    }
    
    public String toString(SimpleDateFormat format) {
        return format.format(new java.util.Date(this.time));
    }
    
    public java.util.Date date() {
        return new java.util.Date(time);
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static long nano() {
        return System.nanoTime();
    }
    
    /** Lazy Date Parser */
    
    private final static List<SimpleDateFormat> 
        formats = new Listes<SimpleDateFormat>();
    
    static {        
        // FTPDate Formats
        addFormat("MMM dd hh:mm", Locale.US);
        addFormat("MMM dd yyyy", Locale.US);
        // NNTPDate Formats
        // default : Wed, 27 Feb 2013 10:18:45 +0100
        addFormat("dd MMM yyyy hh:mm:ss", Locale.US);
        addFormat("dd MMM yyyy hh:mm:ss Z", Locale.US);
        addFormat("EEE, dd MMM yyyy hh:mm", Locale.US);
        addFormat("EEE, dd MMM yyyy hh:mm Z", Locale.US);
        addFormat("EEE, dd MMM yyyy hh:mm:ss", Locale.US);
        addFormat("EEE, dd MMM yyyy hh:mm:ss Z", Locale.US);        
    }

    public static void addFormat(String format, Locale locale) {
        formats.add(new SimpleDateFormat(format, locale));
    }
    
    public static Date parse(String value) {
        for (SimpleDateFormat format : formats) try {
            return new Date(format.parse(value));
        } catch (NumberFormatException cause) {
            //
        } catch (Throwable cause) {
            //
        }
        
        throw new ParseException(value);
    }

}
