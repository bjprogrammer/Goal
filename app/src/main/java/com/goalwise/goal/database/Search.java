package com.goalwise.goal.database;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search")
public class Search {
//    public Integer getId() {
//        return Id;
//    }
//
//    public void setId(Integer id) {
//        Id = id;
//    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

//    @NonNull
//    @PrimaryKey(autoGenerate = true)
//    private Integer Id;


    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "search")
    private String keyword;

}
