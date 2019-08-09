package utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Represents a food item
 */
public class FoodItem {

    private Long id;
    private String name;
    private String note;
    private String dateAdded;
    private String expiryDate;
    private final Date dateExpiration;

    public FoodItem(String name, String note, String dateAdded, String expiryDate) {
        this.name = name;
        this.note = note;
        this.dateAdded = dateAdded;
        this.expiryDate = expiryDate;
        this.dateExpiration = getDateFromString(expiryDate);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateExpiration() { return dateExpiration; }

    // --Commented out by Inspection (08-Aug-19 22:58):public void setDateExpiration(Date dateExpiration) { this.dateExpiration = dateExpiration; }

    // Converts String into date
    public Date getDateFromString(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            Log.v("Exception", e.getLocalizedMessage());
            return null;
        }
    }

    // Returns days between item expiration date and today's date
    public long daysUntilExpiration() {
        Calendar calDate = new GregorianCalendar();
        // reset hour, minutes, seconds and millis
        calDate.set(Calendar.HOUR_OF_DAY, 0);
        calDate.set(Calendar.MINUTE, 0);
        calDate.set(Calendar.SECOND, 0);
        calDate.set(Calendar.MILLISECOND, 0);
        Date currentDate = calDate.getTime();
        long diffInMilliseconds = this.dateExpiration.getTime() - currentDate.getTime();
        return TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS);
    }

}
