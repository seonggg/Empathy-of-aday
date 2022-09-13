package com.project2022.emotiondiary;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.CalendarView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class CalendarDialog extends Dialog {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private CalendarView calendarView;

    public CalendarDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.activity_calendar_dialog);

        calendarView = findViewById(R.id.calendar);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            String mm,dd;
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                if (i1+1<10){
                    i1=i1+1;
                    mm= "0" + i1;
                } else{
                    mm=Integer.toString(i1+1);
                }
                if (i2<10){
                    dd= "0" + i2;
                } else{
                    dd=Integer.toString(i2);
                }
                Log.i("디버깅", "선택한 날짜는 " + i + "년 " + mm + "월 " + dd + "일");

                db.collection("diary")
                        .whereEqualTo("date", i + "년 " + mm + "월 " + dd + "일")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                    String docid = ds.getId();
                                    Intent intent = new Intent(context, ShowDiary.class);
                                    intent.putExtra("docid", docid);
                                    context.startActivity(intent);
                                }
                            }
                        });

            }
        });
    }
}