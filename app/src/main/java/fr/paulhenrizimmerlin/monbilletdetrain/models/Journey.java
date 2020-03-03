package fr.paulhenrizimmerlin.monbilletdetrain.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "journey_table")
public class Journey {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String departure;
    private String arrival;
    private Date date;
    private float currentPrice;
    private float limitPrice;

    @Ignore
    public Journey(String departure, String arrival, Date date) {
        this.departure = departure;
        this.arrival = arrival;
        this.date = date;
    }

    public Journey() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(float currentPrice) {
        this.currentPrice = currentPrice;
    }

    public float getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(float limitPrice) {
        this.limitPrice = limitPrice;
    }

    public void setHours(String hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours.substring(0, 2)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(hours.substring(3, 5)));
        this.date = calendar.getTime();
    }

    @Override
    public String toString() {
        return "Journey{" +
                "departure='" + departure + '\'' +
                ", arrival='" + arrival + '\'' +
                ", date=" + date +
                ", currentPrice=" + currentPrice +
                ", limitPrice=" + limitPrice +
                '}';
    }
}
