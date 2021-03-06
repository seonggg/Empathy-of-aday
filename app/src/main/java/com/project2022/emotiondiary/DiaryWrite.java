package com.project2022.emotiondiary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DiaryWrite extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;

    private final String TAG = this.getClass().getSimpleName();

    Toolbar toolbar;

    ImageView imageview;
    ImageButton weatherB;
    EditText editText;

    LinearLayout recyclerCase;
    private ViewPager2 sliderViewPager;

    String weather="??????";
    String docid;
    String nick, id;

    String strDate;

    ArrayList<Uri> uriList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_write);

        mAuth=FirebaseAuth.getInstance();
        String user_uid = mAuth.getCurrentUser().getUid();
        Toast.makeText(getApplicationContext(), user_uid, Toast.LENGTH_LONG).show();
        id = ((Info)this.getApplication()).getId();
        nick = ((Info)this.getApplication()).getNick();

        //????????? ?????????
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//?????? ????????? ???????????????.
        actionBar.setDisplayHomeAsUpEnabled(true); //?????? ?????? ??????

        editText = findViewById(R.id.editText);

        //?????? ??????
        TextView date = findViewById(R.id.date_display);
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy??? MM??? dd???", Locale.getDefault());
        strDate = dateFormat.format(currentTime);
        date.setText((strDate));

        //?????? ???????????????
        weatherB = findViewById(R.id.weather_button);
        weatherB.setOnClickListener(this::OnClickHandler);

        //????????? ??????
        imageview = findViewById(R.id.photoView);
        recyclerCase = findViewById(R.id.recyclerCase);
        sliderViewPager=findViewById(R.id.sliderViewPager);

        //????????? ????????? ?????????
        recyclerCase.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


            launcher.launch(intent);

        });
    }

    //????????? ????????????
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Intent intent = result.getData();

                        if(intent == null){   // ?????? ???????????? ???????????? ?????? ??????
                            Toast.makeText(getApplicationContext(), "???????????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
                        }
                        else{   // ???????????? ???????????? ????????? ??????
                            if(intent.getClipData() == null){     // ???????????? ????????? ????????? ??????
                                Log.e("single choice: ", String.valueOf(intent.getData()));
                                Uri imageUri = intent.getData();
                                uriList.add(imageUri);
                            }
                            else{      // ???????????? ????????? ????????? ??????
                                ClipData clipData = intent.getClipData();
                                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                                if(clipData.getItemCount() > 5){   // ????????? ???????????? 5??? ????????? ??????
                                    Toast.makeText(getApplicationContext(), "????????? 5????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                                }
                                else{   // ????????? ???????????? 5??? ????????? ??????
                                    Log.e(TAG, "multiple choice");

                                    for (int i = 0; i < clipData.getItemCount(); i++){
                                        Uri imageUri = clipData.getItemAt(i).getUri();  // ????????? ??????????????? uri??? ????????????.
                                        try {
                                            uriList.add(imageUri);  //uri??? list??? ?????????.

                                        } catch (Exception e) {
                                            Log.e(TAG, "File select error", e);
                                        }
                                    }
                                }
                            }

                            //????????? ????????? viewpager2??? ??????
                            imageview.setVisibility(View.GONE);
                            sliderViewPager.setVisibility(View.VISIBLE);

                            sliderViewPager.setOffscreenPageLimit(1);
                            sliderViewPager.setAdapter(new ImageSliderAdapter(getApplicationContext(), uriList));

                        }
                    }
                }
            });


    //?????? ?????? ??????????????? ?????????
    public void OnClickHandler(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("?????? ??????");

        builder.setItems(R.array.LAN, (dialog, pos) -> {
            String[] items = getResources().getStringArray(R.array.LAN);
            if(items[pos].equals("??????")) {
                weatherB.setImageResource(R.drawable.sunny);
                weather="??????";
            }
            if(items[pos].equals("??????")) {
                weatherB.setImageResource(R.drawable.cloudy);
                weather="??????";
            }
            if(items[pos].equals("???")) {
                weatherB.setImageResource(R.drawable.rainy);
                weather="???";
            }
            if(items[pos].equals("???")) {
                weatherB.setImageResource(R.drawable.snowy);
                weather="???";
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //storage??? ?????? ??????
    private void uploadImg(Uri file, int num){
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("diary/"+docid+"_"+num+".jpg");
        UploadTask uploadTask = riversRef.putFile(file);

        try{
            InputStream in = getContentResolver().openInputStream(file);
            Bitmap img = BitmapFactory.decodeStream(in);
            in.close();
            imageview.setImageBitmap(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DiaryWrite.this, "?????? ????????? ??????",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(DiaryWrite.this, "?????? ????????? ??????",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //?????? ?????? ?????? ??????
            case R.id.add_btn:
                //?????? ??????
                if (editText.getText().toString().length() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "????????? ???????????????", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //firebase??? ??????
                    Long datetime = System.currentTimeMillis();
                    Timestamp timestamp = new Timestamp(datetime);
                    Integer pictures = uriList.size();
                    Diary data = new Diary(id, nick, editText.getText().toString(), timestamp, strDate, weather, pictures);
                    data.toMap();
                    Log.i("firebase_diary", data.toString());

                    db.collection("diary")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                                    //?????????????????? storage??? ????????? ??????
                                    docid = documentReference.getId();
                                    for (int i = 0; i <= uriList.size() - 1; i++) {
                                        uploadImg(uriList.get(i), i);
                                    }

                                    // ?????? ?????? ???????????? ??????
                                    Intent intent = new Intent(getApplicationContext(), BeadsMaking.class);
                                    intent.putExtra("docid", docid);
                                    intent.putExtra("content",editText.getText().toString());
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }
                return true;
            case android.R.id.home:
                //select back button
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
