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

public class SignIn extends AppCompatActivity {

    ProgressBar progressBar;
    EditText email,password;
    Button login,signup;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        progressBar = (ProgressBar) findViewById(R.id.signinprogressbar);
        email = (EditText) findViewById(R.id.inemail);
        password = (EditText) findViewById(R.id.inpassword);
        login = (Button) findViewById(R.id.btnlogin);
        signup = (Button) findViewById(R.id.btnsignupredirect);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                String e = email.getText().toString().trim();
                String p = password.getText().toString().trim();

                if (e.length() == 0 || p.length() == 0){
                    Toast.makeText(getApplicationContext(), "Please enter email/password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

                if (e.length() != 0 && p.length() != 0){
                    auth.signInWithEmailAndPassword(e, p)
                            .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        progressBar.setVisibility(View.INVISIBLE);
                                        email.setText(" ");
                                        password.setText(" ");
                                        Intent i = new Intent(SignIn.this,MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                    else{
                                        progressBar.setVisibility(View.INVISIBLE);
                                        email.setText(" ");
                                        password.setText(" ");
                                        Toast.makeText(getApplicationContext(), "Invalid Email/Password..!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignIn.this,SignUp.class);
                startActivity(i);
            }
        });
    }
}