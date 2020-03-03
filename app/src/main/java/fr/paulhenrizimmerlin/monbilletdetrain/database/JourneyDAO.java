package fr.paulhenrizimmerlin.monbilletdetrain.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.paulhenrizimmerlin.monbilletdetrain.models.Journey;

@Dao
public interface JourneyDAO {

    @Query("SELECT * FROM journey_table ORDER BY id")
    List<Journey> loadAllJourneys();

    @Insert
    void insertJourney(Journey journey);

    @Update
    void updateJourney(Journey journey);

    @Delete
    void delete(Journey journey);

    @Query("DELETE FROM journey_table WHERE 1 = 1")
    void deleteAll();

    @Query("SELECT * FROM journey_table WHERE id = :id")
    Journey loadJourneyById(int id);
}