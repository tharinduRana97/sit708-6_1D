package com.example.learnerapp.model;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class InterestsConverter {

    @TypeConverter
    public String fromList(List<String> list) {
        return list != null ? String.join(",", list) : null;
    }

    @TypeConverter
    public List<String> fromString(String value) {
        return value != null ? Arrays.asList(value.split(",")) : null;
    }
}
