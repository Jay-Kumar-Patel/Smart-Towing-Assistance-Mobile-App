package com.example.scan_the_tow;

import static android.Manifest.permission.CAMERA;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class New_Tow_Vehicle extends AppCompatActivity {

    EditText time,date,area,fine;
    public static EditText noplate;
    Button scan,sendmessage;
    FirebaseDatabase db;
    DatabaseReference ref;
    String t,d,a,f,nop;
    FirebaseAuth auth;
    ImageView imgnoplate;
    Bitmap bitmap;
    StringBuilder stringBuilder;
    static final int Request_capture_code = 1;
    static final int Request_loaction_code = 2;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tow_vehicle);

        time = (EditText) findViewById(R.id.edtime);
        date = (EditText) findViewById(R.id.eddate);
        noplate = (EditText) findViewById(R.id.ednoplate);
        area = (EditText) findViewById(R.id.edarea);
        fine = (EditText) findViewById(R.id.edpayfine);
        scan = (Button) findViewById(R.id.btnscan);
        sendmessage = (Button) findViewById(R.id.btnsendmessage);
        imgnoplate = (ImageView) findViewById(R.id.img);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        if (ContextCompat.checkSelfPermission(New_Tow_Vehicle.this, CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(New_Tow_Vehicle.this, new String[]{CAMERA},Request_capture_code);
        }

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(New_Tow_Vehicle.this);
                getLastLocation();
            }
        });

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpledate = new SimpleDateFormat("EEEE,dd-MMM-yyyy");
        d = simpledate.format(calendar.getTime());
        SimpleDateFormat simpletime = new SimpleDateFormat("hh-mm-ss a");
        t = simpletime.format(calendar.getTime());
        date.setText(d);
        time.setText(t);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploaddata();
            }

            private void uploaddata(){
                a = area.getText().toString().trim();
                f = fine.getText().toString().trim();
                d = date.getText().toString().trim();
                t = time.getText().toString().trim();
                nop = noplate.getText().toString().trim();

                ref = FirebaseDatabase.getInstance().getReference("Vehicles");
                ref.child(nop).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){
                                Towing_Data td = new Towing_Data(d,t,a,f,nop);
                                ref = db.getReference("Tow_Vehicles_Data");
                                DataSnapshot dataSnapshot = task.getResult();
                                String mobile = String.valueOf(dataSnapshot.child("mobile").getValue()).toString();
                                ref.child(auth.getCurrentUser().getUid()).child(nop).setValue(td)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    sendMessage(mobile);
                                                }
                                                else{
                                                    Toast.makeText(getApplicationContext(), "Data Not Uploaded..!!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Number Plate Does Not Exist..!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
            private void sendMessage(String mobile) {
                if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                    try{
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(mobile,null,"Hello, Sanjay Patel your vehicle is tow from RTO Circle,Subhas Bridge, Ahmedabad Payment Link : https://rzp.io/i/3aQbmg",null,null);
                        Toast.makeText(getApplicationContext(), "SMS Sended..!!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(New_Tow_Vehicle.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to sent Message..!!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},100);
                }
            }
        });
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                Geocoder geocoder = new Geocoder(New_Tow_Vehicle.this, Locale.getDefault());
                                List<Address> addressList = null;
                                try {
                                    addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    String s1 = addressList.get(0).getLocality();
                                    String s2 = addressList.get(0).getAdminArea();
                                    String s3 = addressList.get(0).getPostalCode();
                                    area.setText(s1+" "+s2+" "+s3);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
        else{
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(New_Tow_Vehicle.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_loaction_code);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Request_loaction_code){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(getApplicationContext(), "Please Provide Required Permissions..!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(New_Tow_Vehicle.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resulturi = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resulturi);
                    getTextFromImage(bitmap);
                    imgnoplate.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void getTextFromImage(Bitmap bitmap){
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()){
            Toast.makeText(getApplicationContext(), "Some Error Occured..!!", Toast.LENGTH_SHORT).show();
        }
        else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            stringBuilder = new StringBuilder();
            for (int i=0;i<textBlockSparseArray.size();i++){
                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append("\n");
            }
            noplate.setText(stringBuilder.toString());
        }
    }
}