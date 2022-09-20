package com.project2022.emotiondiary;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Calendar;

public class CalendarDialog extends Dialog {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

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
//                                        Intent intent = new Intent(context, ShowDiary.class);
//                                        intent.putExtra("docid", docid);
//                                        context.startActivity(intent);
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
                                    else if (topArray.contains("anxiety")) {
                                        bead.setImageResource(R.drawable.anxiety);
                                    }
                                    else if (topArray.contains("emb")) {
                                        bead.setImageResource(R.drawable.emb);
                                    }
                                    else if (topArray.contains("happy")) {
                                        bead.setImageResource(R.drawable.happy);
                                    }
                                    else if (topArray.contains("hurt")) {
                                        bead.setImageResource(R.drawable.hurt);
                                    }
                                    else if (topArray.contains("sad")) {
                                        bead.setImageResource(R.drawable.sad);
                                    }
                                }
                                // 감정 2개 추출됐을 때
                                else if(beads_count==2){
                                    if (topArray.contains("angry")) {
                                        if(topArray.contains("anxiety"))
                                            bead.setImageResource(R.drawable.angry_anxiety);
                                        else if(topArray.contains("emb"))
                                            bead.setImageResource(R.drawable.angry_emb);
                                        else if(topArray.contains("happy"))
                                            bead.setImageResource(R.drawable.angry_happy);
                                        else if(topArray.contains("hurt"))
                                            bead.setImageResource(R.drawable.angry_hurt);
                                        else if(topArray.contains("sad"))
                                            bead.setImageResource(R.drawable.angry_sad);
                                    }
                                    else if (topArray.contains("anxiety")) {
                                        if(topArray.contains("emb"))
                                            bead.setImageResource(R.drawable.anxiety_emb);
                                        else if(topArray.contains("happy"))
                                            bead.setImageResource(R.drawable.anxiety_happy);
                                        else if(topArray.contains("hurt"))
                                            bead.setImageResource(R.drawable.anxiety_hurt);
                                        else if(topArray.contains("sad"))
                                            bead.setImageResource(R.drawable.sad_anxiety);
                                    }
                                    else if (topArray.contains("emb")) {
                                        if(topArray.contains("happy"))
                                            bead.setImageResource(R.drawable.emb_happy);
                                        else if(topArray.contains("hurt"))
                                            bead.setImageResource(R.drawable.hurt_emb);
                                        else if(topArray.contains("sad"))
                                            bead.setImageResource(R.drawable.sad_emb);
                                    }
                                    else if (topArray.contains("happy")) {
                                        if(topArray.contains("hurt"))
                                            bead.setImageResource(R.drawable.hurt_happy);
                                        else if(topArray.contains("sad"))
                                            bead.setImageResource(R.drawable.sad_happy);
                                    }
                                    else if (topArray.contains("hurt") && topArray.contains("sad")) {
                                        bead.setImageResource(R.drawable.sad_hurt);
                                    }
                                }
                                // 감정 3개 추출됐을 때
                                else if(beads_count==3){
                                    if (topArray.contains("angry")) {
                                        if(topArray.contains("anxiety")){
                                            if(topArray.contains("emb"))
                                                bead.setImageResource(R.drawable.emb_angry_anxiety); //분노+불안+당황
                                            else if(topArray.contains("happy"))
                                                bead.setImageResource(R.drawable.happy_angry_anxiety); //분노+불안+기쁨
                                            else if(topArray.contains("hurt"))
                                                bead.setImageResource(R.drawable.angry_anxiety_hurt); //분노+불안+상처
                                            else if(topArray.contains("sad"))
                                                bead.setImageResource(R.drawable.angery_sad_anxiety); //분노+불안+슬픔
                                        }
                                        else if(topArray.contains("emb")){
                                            if(topArray.contains("happy"))
                                                bead.setImageResource(R.drawable.happy_emb_anxiety); //분노+당황+기쁨
                                            else if(topArray.contains("hurt"))
                                                bead.setImageResource(R.drawable.emb_angry_hurt); //분노+당황+상처
                                            else if(topArray.contains("sad"))
                                                bead.setImageResource(R.drawable.angry_sad_emb); //분노+당황+슬픔
                                        }
                                        else if(topArray.contains("happy")) {
                                            if(topArray.contains("hurt"))
                                                bead.setImageResource(R.drawable.happy_angry_hurt); //분노+기쁨+상처
                                            else if(topArray.contains("sad"))
                                                bead.setImageResource(R.drawable.angry_sad_happy); //분노+기쁨+슬픔
                                        }
                                        else if(topArray.contains("hurt"))
                                            if(topArray.contains("sad"))
                                                bead.setImageResource(R.drawable.angry_hurt_sad); //분노+상처+슬픔
                                    }
                                    else if (topArray.contains("anxiety")) {
                                        if(topArray.contains("emb")){
                                            if(topArray.contains("happy"))
                                                bead.setImageResource(R.drawable.happy_emb_anxiety); //불안+당황+기쁨
                                            else if(topArray.contains("hurt"))
                                                bead.setImageResource(R.drawable.emb_anxiety_hurt); // 불안+당황+상처
                                            else if(topArray.contains("sad"))
                                                bead.setImageResource(R.drawable.emb_anxiety_sad); //불안+당황+슬픔
                                        }
                                        else if(topArray.contains("happy")){
                                            if(topArray.contains("hurt"))
                                                bead.setImageResource(R.drawable.happy_anxiety_hurt); // 불안+기쁨+상처
                                            else if(topArray.contains("sad"))
                                                bead.setImageResource(R.drawable.happy_anxiety_sad); // 불안+기쁨+슬픔
                                        }
                                        else if(topArray.contains("hurt") && topArray.contains("sad"))
                                            bead.setImageResource(R.drawable.anxiety_hurt_sad); //불안+상처+슬픔
                                    }
                                    else if (topArray.contains("emb")) {
                                        if(topArray.contains("happy")){
                                            if(topArray.contains("hurt"))
                                                bead.setImageResource(R.drawable.happy_emb_hurt); //당황+기쁨+상처
                                            else if(topArray.contains("sad"))
                                                bead.setImageResource(R.drawable.happy_emb_sad); //당황+기쁨+슬픔
                                        }
                                        else if(topArray.contains("hurt") && topArray.contains("sad"))
                                            bead.setImageResource(R.drawable.emb_hurt_sad); //당황+상처+슬픔
                                    }
                                    else if (topArray.contains("happy")) {
                                        if(topArray.contains("hurt") && topArray.contains("sad"))
                                            bead.setImageResource(R.drawable.happy_hurt_sad); //기쁨+상처+슬픔
                                    }
                                }
                            }
}