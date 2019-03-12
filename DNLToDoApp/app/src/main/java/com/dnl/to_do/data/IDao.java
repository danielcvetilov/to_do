package com.dnl.to_do.data;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import java.util.List;

public interface IDao<T> {
    @Insert
    void insert(List<T> records);

    @Update
    void update(List<T> record);

    @Delete
    void delete(T record);
}
