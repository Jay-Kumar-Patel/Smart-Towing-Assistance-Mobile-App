package com.example.scan_the_tow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    ProgressBar progressBar;
    EditText username,email,password,comfpassword;
    Button signup;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressBar = (ProgressBar) findViewById(R.id.signupprogressbar);
        username= (EditText) findViewById(R.id.upusername);
        email = (EditText) findViewById(R.id.upemail);
        password = (EditText) findViewById(R.id.uppassword);
        comfpassword = (EditText) findViewById(R.id.confirmpassword);
        signup = (Button) findViewById(R.id.btnsignup);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                String e = email.getText().toString().trim();
                String p = password.getText().toString().trim();
                String cp = comfpassword.getText().toString().trim();
                String un = username.getText().toString().trim();

                if (e.length()==0 || p.length()==0 || cp.length()==0 || un.length()==0){
                    Toast.makeText(getApplicationContext(), "Please Enter all the details..", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                if (p.matches(cp) && p.length() >= 8){
                    auth.createUserWithEmailAndPassword(e, p)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        UserData user = new UserData(un,e,p);
                                        ref = db.getReference("Users");
                                        ref.child(auth.getCurrentUser().getUid()).setValue(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            email.setText(" ");
                                                            password.setText(" ");
                                                            comfpassword.setText(" ");
                                                            username.setText(" ");
                                                            Intent i = new Intent(SignUp.this,MainActivity.class);
                                                            startActivity(i);
                                                            Toast.makeText(getApplicationContext(), "Registered Successfully..!!", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            Toast.makeText(getApplicationContext(), "User Data Uploading Problem..!!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    else{
                                        progressBar.setVisibility(View.INVISIBLE);
                                        email.setText(" ");
                                        password.setText(" ");
                                        comfpassword.setText(" ");
                                        username.setText(" ");
                                        Toast.makeText(getApplicationContext(), "Process Error..!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}