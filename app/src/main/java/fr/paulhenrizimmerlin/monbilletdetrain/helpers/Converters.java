package fr.paulhenrizimmerlin.monbilletdetrain.helpers;

import androidx.room.TypeConverter;

import java.util.Date;

// Needed to convert date for database
public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}