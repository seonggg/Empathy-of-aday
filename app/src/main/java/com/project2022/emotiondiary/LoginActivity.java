package com.project2022.emotiondiary;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    long backKeyPressingTime = 0;
    Button email;
    TextView signUp;
    private FirebaseAuth mAuth=null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // FirebaseAuth 객체의 공유 인스턴스를 가져옴
        mAuth = FirebaseAuth.getInstance();

        signInButton = findViewById(R.id.signInButton);

        onStart();

        email = findViewById(R.id.email_login_btn);
        email.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,EmailLogin.class);
            startActivity(intent);
        });

        signUp = findViewById(R.id.signup_btn);
        signUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,SignUp.class);
            startActivity(intent);
        });

        // 구글 로그인을 앱에 통합
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(view -> signIn());

    }

    private void signIn() {
        //로그인 통합 페이지 넘김
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //구글을 통해 돌아온다.
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    //사용자가 정상적으로 로그인하면 GoogleSignInAccount 객체에서 ID토큰 가져와서
    //Firebase 사용자 인증 정보로 교환하고 해당 정보를 사용해 Firebase 인증
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("success check: ","google login success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("success check: ","google login fail");
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            CollectionReference colRef = db.collection("user");
            colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(mAuth.getCurrentUser().getEmail().equals(document.get("user_email"))){
                            check=1;
                        }
                    }
                    if(check==1){
                        Intent intent = new Intent(getApplicationContext(),MyRoom.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(),GoogleSignUp.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // 활동을 초기화할 때 사용자가 현재 로그인되어 있는지 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(LoginActivity.this,MyRoom.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        //뒤로가기 막기
        //super.onBackPressed();

        // 뒤로가기 연속 두 번 클릭 시 종료
        if(System.currentTimeMillis()>backKeyPressingTime + 2500){
            backKeyPressingTime = System.currentTimeMillis();
            Toast toast = Toast.makeText(getApplicationContext(), "뒤로가기 두 번 누르면 종료",Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if(System.currentTimeMillis()<=backKeyPressingTime + 2500){
            finishAffinity();
        }
    }
}