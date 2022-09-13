package com.project2022.emotiondiary;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.CalendarView;

public class CalendarDialog extends Dialog {

    private CalendarView calendarView;

    public CalendarDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.activity_calendar_dialog);

        calendarView = findViewById(R.id.calendar);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                Log.i("디버깅", "선택한 날짜는 " + i + "년 " + (i1 + 1) + "월 " + i2 + "일");
            }
        });

    }
}