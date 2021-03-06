package com.project2022.emotiondiary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ShowDiary extends AppCompatActivity {

    Toolbar toolbar;

    ImageView weather_img, bead;
    TextView date_display, content;

    private ViewPager2 sliderViewPager;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    ArrayList<Uri> imgArray = new ArrayList<>();

    String docid, id;
    Boolean actionBarView;

    Integer pictures;

    FloatingActionButton floatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diary);

        id = ((Info)this.getApplication()).getId();

        //????????? ?????????
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//?????? ????????? ???????????????.
        actionBar.setDisplayHomeAsUpEnabled(true); //?????? ?????? ??????

        weather_img=findViewById(R.id.weather_img);
        bead=findViewById(R.id.beads_img);

        content=findViewById(R.id.content);
        date_display=findViewById(R.id.date_display);

        sliderViewPager=findViewById(R.id.sliderViewPager);

        Intent intent = getIntent();
        docid = intent.getStringExtra("docid");

        actionBarView = intent.getBooleanExtra("actionbar",true);

        //docid??? ???????????? ?????? ????????????
        DocumentReference docRef = db.collection("diary").document(docid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                        //Diary diary = document.toObject(Diary.class);

                        //?????? ????????????
                        printBead(document.get("beads").toString().split(",").length, document.get("beads").toString());

                        //?????? ????????????
                        date_display.setText(document.get("date").toString());

                        //?????? ?????? ????????????
                        content.setText(document.get("content").toString());

                        //?????? ????????????
                        switch (document.get("weather").toString()){
                            case "??????":
                                weather_img.setImageResource(R.drawable.sunny);
                                break;
                            case "??????":
                                weather_img.setImageResource(R.drawable.cloudy);
                                break;
                            case "???":
                                weather_img.setImageResource(R.drawable.rainy);
                                break;
                            case "???":
                                weather_img.setImageResource(R.drawable.snowy);
                                break;
                        }

                        //????????? ????????????
                        pictures = Integer.valueOf(document.get("pictures").toString());

                        if (pictures !=0) {
                            ArrayList<Uri> uriArray = new ArrayList<>();
                            StorageReference storageRef = storage.getReference();
                            StorageReference pathRef = storageRef.child("diary");
                            if (pathRef == null) {
                                Toast.makeText(getApplicationContext(), "???????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < pictures; i++) {
                                    storageRef.child("diary/" + docid + "_" + i + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Got the download URL for 'users/me/profile.png'
                                            uriArray.add(uri);
                                            //????????? ????????? viewpager2??? ??????
                                            sliderViewPager.setOffscreenPageLimit(1);
                                            sliderViewPager.setAdapter(new ImageSliderAdapter(getApplicationContext(), uriArray));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                        }
                                    });
                                }
                            }
                        }else {
                            sliderViewPager.setVisibility(View.GONE);
                        }

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "?????? ?????? ???????????? ??????", Toast.LENGTH_SHORT).show();
            }
        });

        //????????? ??????
        floatBtn = findViewById(R.id.floatingActionButton);

        floatBtn.setOnClickListener(view -> {
            Intent fBtnIntent = new Intent(getApplicationContext(),Comment.class);
            fBtnIntent.putExtra("docid",docid);
            startActivity(fBtnIntent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(actionBarView){
            getMenuInflater().inflate(R.menu.menu_show, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.edit_btn:
                //?????? ??????
                Intent intent = new Intent(getApplicationContext(), EditDiary.class);
                intent.putExtra("docid", docid);
                startActivity(intent);

                return true;
            case R.id.delete_btn:
                //?????? ??????
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowDiary.this);
                builder.setTitle("?????? ????????????");
                builder.setMessage("????????? ???????????? ????????? ??? ????????????.\n?????????????????????????");
                builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.collection("diary").document(docid).delete();
                        delete_img(docid, pictures);
                        Intent intent = new Intent(getApplicationContext(), MyRoom.class);
                        intent.putExtra("email",id);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("?????????", null);
                builder.setNeutralButton("??????", null);
                builder.create().show();
                return true;
            case R.id.show_btn:
                //?????? ?????? ?????? ????????????
                Intent intent2 = new Intent(getApplicationContext(),ShareList.class);
                intent2.putExtra("show",true);
                intent2.putExtra("docid",docid);
                startActivity(intent2);
            case android.R.id.home:
                //select back button
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //?????? ????????? ????????????
    void printBead(Integer beads_count, String topArray){
        // ?????? 1?????? ???????????? ???
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
        // ?????? 2??? ???????????? ???
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
        // ?????? 3??? ???????????? ???
        else if(beads_count==3){
            if (topArray.contains("angry")) {
                if(topArray.contains("anxiety")){
                    if(topArray.contains("emb"))
                        bead.setImageResource(R.drawable.emb_angry_anxiety); //??????+??????+??????
                    else if(topArray.contains("happy"))
                        bead.setImageResource(R.drawable.happy_angry_anxiety); //??????+??????+??????
                    else if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.angry_anxiety_hurt); //??????+??????+??????
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.angery_sad_anxiety); //??????+??????+??????
                }
                else if(topArray.contains("emb")){
                    if(topArray.contains("happy"))
                        bead.setImageResource(R.drawable.happy_emb_anxiety); //??????+??????+??????
                    else if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.emb_angry_hurt); //??????+??????+??????
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.angry_sad_emb); //??????+??????+??????
                }
                else if(topArray.contains("happy")) {
                    if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.happy_angry_hurt); //??????+??????+??????
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.angry_sad_happy); //??????+??????+??????
                }
                else if(topArray.contains("hurt"))
                    if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.angry_hurt_sad); //??????+??????+??????
            }
            else if (topArray.contains("anxiety")) {
                if(topArray.contains("emb")){
                    if(topArray.contains("happy"))
                        bead.setImageResource(R.drawable.happy_emb_anxiety); //??????+??????+??????
                    else if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.emb_anxiety_hurt); // ??????+??????+??????
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.emb_anxiety_sad); //??????+??????+??????
                }
                else if(topArray.contains("happy")){
                    if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.happy_anxiety_hurt); // ??????+??????+??????
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.happy_anxiety_sad); // ??????+??????+??????
                }
                else if(topArray.contains("hurt") && topArray.contains("sad"))
                    bead.setImageResource(R.drawable.anxiety_hurt_sad); //??????+??????+??????
            }
            else if (topArray.contains("emb")) {
                if(topArray.contains("happy")){
                    if(topArray.contains("hurt"))
                        bead.setImageResource(R.drawable.happy_emb_hurt); //??????+??????+??????
                    else if(topArray.contains("sad"))
                        bead.setImageResource(R.drawable.happy_emb_sad); //??????+??????+??????
                }
                else if(topArray.contains("hurt") && topArray.contains("sad"))
                    bead.setImageResource(R.drawable.emb_hurt_sad); //??????+??????+??????
            }
            else if (topArray.contains("happy")) {
                if(topArray.contains("hurt") && topArray.contains("sad"))
                    bead.setImageResource(R.drawable.happy_hurt_sad); //??????+??????+??????
            }
        }
    }

    //????????? ?????????????????? ?????????????????? ????????????
    private ArrayList<Uri> getFireBaseImage(String docid, Integer pictures){
        //https://art-coding3.tistory.com/38
        ArrayList<Uri> uriArray = new ArrayList<>();
        StorageReference storageRef = storage.getReference();
        StorageReference pathRef = storageRef.child("diary");
        if (pathRef == null){
            Toast.makeText(getApplicationContext(), "???????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
        }
        else {
            for (int i=0;i<pictures;i++){
                storageRef.child("diary/"+docid+"_"+i+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        uriArray.add(uri);
                        Toast.makeText(getApplicationContext(), imgArray.toString(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        }
        return uriArray;
    }

    //storage??? ????????? ??????
    private void delete_img(String docid, Integer size) {
        for (int i=0;i<size;i++){
            storage.getReference().child("diary/" + docid + "_" + i + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "?????? ??????", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
