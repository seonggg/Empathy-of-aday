package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

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
import android.view.MenuItem;
import android.view.View;
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
import java.util.ArrayList;

public class EditDiary extends AppCompatActivity {

    Toolbar toolbar;
    TextView date_display, edit_img;
    ImageButton weather_button;
    ImageView imageview;
    ViewPager2 sliderViewPager;
    EditText editText;

    ArrayList<Uri> uriArray = new ArrayList<>();
    ArrayList<Uri> newUri = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    String weather, docid, strDate;

    Integer img_change = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_diary);

        //????????? ?????????
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//?????? ????????? ???????????????.
        actionBar.setDisplayHomeAsUpEnabled(true); //?????? ?????? ??????

        date_display = findViewById(R.id.date_display);
        weather_button = findViewById(R.id.weather_button);
        sliderViewPager = findViewById(R.id.sliderViewPager);
        editText = findViewById(R.id.editText);
        edit_img = findViewById(R.id.edit_img);
        //????????? ??????
        imageview = findViewById(R.id.photoView);
        sliderViewPager = findViewById(R.id.sliderViewPager);

        Intent intent = getIntent();
        docid = intent.getStringExtra("docid");

        weather_button.setOnClickListener(this::OnClickHandler);

        //docid??? ???????????? ?????? ????????????
        DocumentReference docRef = db.collection("diary").document(docid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());

                        //?????? ????????????
                        strDate = document.get("date").toString();
                        date_display.setText(strDate);

                        //?????? ?????? ????????????
                        editText.setText(document.get("content").toString());

                        //?????? ????????????
                        switch (document.get("weather").toString()) {
                            case "??????":
                                weather_button.setImageResource(R.drawable.sunny);
                                weather = "??????";
                                break;
                            case "??????":
                                weather_button.setImageResource(R.drawable.cloudy);
                                weather = "??????";
                                break;
                            case "???":
                                weather_button.setImageResource(R.drawable.rainy);
                                weather = "???";
                                break;
                            case "???":
                                weather_button.setImageResource(R.drawable.snowy);
                                weather = "???";
                                break;
                        }

                        //????????? ????????????
                        Integer pictures_origin = Integer.valueOf(document.get("pictures").toString());
                        StorageReference storageRef = storage.getReference();
                        StorageReference pathRef = storageRef.child("diary");
                        if (pathRef == null) {
                            Toast.makeText(getApplicationContext(), "???????????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < pictures_origin; i++) {
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

                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        //????????? ????????? ?????????
        edit_img.setOnClickListener(v -> {

            img_change++;

            Intent intent_img = new Intent(Intent.ACTION_PICK);
            intent_img.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent_img.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent_img.setAction(Intent.ACTION_GET_CONTENT);
            intent_img.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            launcher.launch(intent_img);

        });
    }

    //????????? ????????????
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();

                        if (intent == null) {   // ?????? ???????????? ???????????? ?????? ??????
                            Toast.makeText(getApplicationContext(), "???????????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
                            imageview.setVisibility(View.VISIBLE);
                            sliderViewPager.setVisibility(View.GONE);
                        } else {   // ???????????? ???????????? ????????? ??????
                            if (intent.getClipData() == null) {     // ???????????? ????????? ????????? ??????
                                Log.e("single choice: ", String.valueOf(intent.getData()));
                                Uri imageUri = intent.getData();
                                newUri.add(imageUri);
                            } else {      // ???????????? ????????? ????????? ??????
                                ClipData clipData = intent.getClipData();
                                Log.e("clipData", String.valueOf(clipData.getItemCount()));

                                if (clipData.getItemCount() > 5) {   // ????????? ???????????? 5??? ????????? ??????
                                    Toast.makeText(getApplicationContext(), "????????? 5????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                                } else {   // ????????? ???????????? 5??? ????????? ??????
                                    Log.e(TAG, "multiple choice");

                                    for (int i = 0; i < clipData.getItemCount(); i++) {
                                        Uri imageUri = clipData.getItemAt(i).getUri();  // ????????? ??????????????? uri??? ????????????.
                                        try {
                                            newUri.add(imageUri);  //uri??? list??? ?????????.

                                        } catch (Exception e) {
                                            Log.e(TAG, "File select error", e);
                                        }
                                    }
                                }
                            }

                            sliderViewPager.setOffscreenPageLimit(1);
                            sliderViewPager.setAdapter(new ImageSliderAdapter(getApplicationContext(), newUri));

                        }
                    }
                }
            });

    //?????? ?????? ??????????????? ?????????
    public void OnClickHandler(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("?????? ??????");

        builder.setItems(R.array.LAN, (dialog, pos) -> {
            String[] items = getResources().getStringArray(R.array.LAN);
            if (items[pos].equals("??????")) {
                weather_button.setImageResource(R.drawable.sunny);
                weather = "??????";
            }
            if (items[pos].equals("??????")) {
                weather_button.setImageResource(R.drawable.cloudy);
                weather = "??????";
            }
            if (items[pos].equals("???")) {
                weather_button.setImageResource(R.drawable.rainy);
                weather = "???";
            }
            if (items[pos].equals("???")) {
                weather_button.setImageResource(R.drawable.snowy);
                weather = "???";
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //storage??? ?????? ??????
    private void uploadImg(Uri file, int num) {
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("diary/" + docid + "_" + num + ".jpg");
        UploadTask uploadTask = riversRef.putFile(file);

        try {
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
                Toast.makeText(EditDiary.this, "?????? ????????? ??????", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditDiary.this, "?????? ????????? ??????", Toast.LENGTH_SHORT).show();
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
                    Integer pictures = uriArray.size();
                    //firebase??? ??????
                    //?????????????????? storage ??? ????????? ??????
                    if (img_change != 0) {
                        delete_img(docid, pictures);
                        //?????????????????? storage??? ????????? ??????
                        for (int i = 0; i <newUri.size(); i++) {
                            uploadImg(newUri.get(i), i);
                        }
                        pictures = newUri.size();
                    }

                    db.collection("diary").document(docid).update("content", editText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                    db.collection("diary").document(docid).update("weather", weather).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                    db.collection("diary").document(docid).update("pictures", pictures).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                    // ?????? ?????? ??????
                    Intent intent = new Intent(getApplicationContext(), ShowDiary.class);
                    intent.putExtra("docid", docid);
                    startActivity(intent);

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

    //storage??? ????????? ??????
    private void delete_img(String docid, Integer size) {
        for (int i = 0; i < size; i++) {
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