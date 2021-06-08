package com.example.agroproject.model;

import androidx.core.util.Pair;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;
import java.util.TimeZone;

public class DatePicker {
    private MaterialDatePicker.Builder<Pair<Long, Long>> builder;

    private CalendarConstraints.Builder calendarConstraintsBuilder;

    private  MaterialDatePicker materialDatePicker;

    /**
     * Instantiates a new DatePicker object
     *
     */
    public DatePicker(){
        builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("SELECT A DATE");
    }

    /**
     * Initialize calendar components
     */
    public void Init(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        Long today = MaterialDatePicker.todayInUtcMilliseconds();
        calendar.setTimeInMillis(today);

        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        Long december = calendar.getTimeInMillis();

        calendarConstraintsBuilder = new CalendarConstraints.Builder();
        calendarConstraintsBuilder.setEnd(december);
        calendarConstraintsBuilder.setValidator(DateValidatorPointBackward.before(today));

        builder.setCalendarConstraints(calendarConstraintsBuilder.build());
        materialDatePicker = builder.build();
    }


    public MaterialDatePicker getMaterialDatePicker(){
        return  materialDatePicker;
    }
}
