package com.goalwise.goal.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public abstract class SearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Search search);


    @Query("SELECT * FROM search")
    public abstract List<Search> getSearchList();

//    @Transaction
//    public void deleteAndInsert(Search search){
//        delete();
//        insert(search);
//    }
//
//    @Query("DELETE FROM search")
//    public abstract void delete();
}

