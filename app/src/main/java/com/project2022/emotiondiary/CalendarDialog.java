package com.project2022.emotiondiary;

import static java.lang.Integer.parseInt;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.util.Collections;
import java.util.Objects;

public class CalendarDialog extends Dialog {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MaterialCalendarView calendarView;

    private LinearLayout cal_show_diary;
    private ImageView bead;
    private TextView content,day;

    public CalendarDialog(@NonNull Context context, String email) {
        super(context);
        setContentView(R.layout.activity_calendar_dialog);

        calendarView = findViewById(R.id.calendar);
        cal_show_diary = findViewById(R.id.cal_show_diary);
        bead = findViewById(R.id.cal_beads);
        content = findViewById(R.id.cal_content);
        day=findViewById(R.id.cal_date);

        calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getContext().getResources().getTextArray(R.array.custom_months)));
        calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getContext().getResources().getTextArray(R.array.custom_weekdays)));
        // 오늘 날짜 표시
        calendarView.setSelectedDate(CalendarDay.today());
        // 일기 작성 날짜 점 표시
        db.collection("diary")
                .whereEqualTo("writer_id", email)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                    String docid = ds.getId();
                    DocumentReference docRef = db.collection("diary").document(docid);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    try{
                                        int year,month,date;
                                        year = Integer.parseInt(document.get("date").toString().trim().substring(0,4));
                                        month = Integer.parseInt(document.get("date").toString().trim().substring(6,8));
                                        date = Integer.parseInt(document.get("date").toString().trim().substring(10,12));
                                        Log.i("일기작성날짜확인", "year:"+year+"month:"+month+"date:"+date);
                                        calendarView.addDecorator(new EventDecorator(Collections
                                                .singleton(CalendarDay.from(year,month,date)), Color.parseColor("#FF9CDD")));
                                    }
                                    catch (NumberFormatException e){
                                        //에러 처리
                                    }
                                    catch (NullPointerException e){
                                        //에러 처리
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            String mm, dd;

            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Log.i("디버깅", "선택한 날짜는 " + date);
                int i = date.getYear();
                int i1 = date.getMonth();
                int i2 = date.getDay();

                if (i1 + 1 < 10) {
                    mm = "0" + i1;
                } else {
                    mm = Integer.toString(i1);
                }
                if (i2 < 10) {
                    dd = "0" + i2;
                } else {
                    dd = Integer.toString(i2);
                }
                Log.i("디버깅", "선택한 날짜는 " + i + "년 " + mm + "월 " + dd + "일");

                db.collection("diary")
                        .whereEqualTo("date", i + "년 " + mm + "월 " + dd + "일")
                        .whereEqualTo("writer_id", email)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                    String docid = ds.getId();
                                    Log.i("디버깅", docid);

                                    //docid에 해당하는 일기 불러오기
                                    DocumentReference docRef = db.collection("diary").document(docid);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                                                    //Diary diary = document.toObject(Diary.class);

                                                    //일기 내용 불러오기
                                                    printBead(document.get("beads").toString().split(",").length, document.get("beads").toString());
                                                    content.setText(document.get("content").toString());
                                                    day.setText(document.get("date").toString());
                                                }
                                            }
                                        }
                                    });
                                    calendarView.setVisibility(View.GONE);
                                    cal_show_diary.setVisibility(View.VISIBLE);
                                    bead.setVisibility(View.VISIBLE);
                                    day.setVisibility(View.VISIBLE);
                                    content.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });
    }
                            //구슬 이미지 불러오기
                            void printBead(Integer beads_count, String topArray){
                                // 감정 1개만 추출됐을 때
                                if(beads_count==1){
                                    if (topArray.contains("angry")) {
                                        bead.setImageResource(R.drawable.angry);
                                    }
                                    else if (topArray.contains("happy")) {
                                        bead.setImageResource(R.drawable.happy);
                                    }
                                    else if (topArray.contains("anxiety")) {
                                        bead.setImageResource(R.drawable.anxiety);
                                    }
                                    else if (topArray.contains("sad")) {
                                        bead.setImageResource(R.drawable.sad);
                                    }
                                }
                                // 감정 2개 추출됐을 때
                                else if(beads_count==2){
                                    if (topArray.contains("angry")) {
                                        if(topArray.contains("happy"))
                                            bead.setImageResource(R.drawable.angry_happy);
                                        else if(topArray.contains("anxiety"))
                                            bead.setImageResource(R.drawable.angry_anxiety);
                                        else if(topArray.contains("sad"))
                                            bead.setImageResource(R.drawable.angry_sad);
                                    }
                                    else if (topArray.contains("happy")) {
                                        if(topArray.contains("anxiety"))
                                            bead.setImageResource(R.drawable.anxiety_happy);
                                        else if(topArray.contains("sad"))
                                            bead.setImageResource(R.drawable.sad_happy);
                                    }
                                    else if (topArray.contains("anxiety") && topArray.contains("sad")) {
                                        bead.setImageResource(R.drawable.sad_anxiety);
                                    }
                                }
                                // 감정 3개 추출됐을 때
                                else if(beads_count==3){
                                    if (topArray.contains("angry")) {
                                        if(topArray.contains("happy")) {
                                            if(topArray.contains("anxiety"))
                                                bead.setImageResource(R.drawable.happy_angry_anxiety); //분노+기쁨+상처
                                            else if(topArray.contains("sad"))
                                                bead.setImageResource(R.drawable.angry_sad_happy); //분노+기쁨+슬픔
                                        }
                                        else if(topArray.contains("anxiety"))
                                            if(topArray.contains("sad"))
                                                bead.setImageResource(R.drawable.angry_anxiety_sad); //분노+상처+슬픔
                                    }
                                    else if (topArray.contains("happy")) {
                                        if(topArray.contains("anxiety") && topArray.contains("sad"))
                                            bead.setImageResource(R.drawable.happy_anxiety_sad); //기쁨+상처+슬픔
                                    }
                                }
                            }
}