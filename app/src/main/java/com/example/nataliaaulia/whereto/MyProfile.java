package com.example.nataliaaulia.whereto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyProfile extends AppCompatActivity {

    private EditText mFirstNameField, mLastNameField, mEmailField, mPasswordField;
    private TextView mBadgeField;
    private ImageView mProfileImg;
    private Button mSave;
    private String userId;
    private String mFirstName, mLastName, mEmail, mPassword, mProfileImageUrl, mBadge;
    private Uri resultUri;
    //private FirebaseAuth mAuth;
    private DatabaseReference mData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);

        mFirstNameField = (EditText) findViewById(R.id.firstname);
        mLastNameField = (EditText) findViewById(R.id.lastname);
        mEmailField = (EditText) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.password);
        mBadgeField = (TextView) findViewById(R.id.badge);
        mProfileImg = (ImageView) findViewById(R.id.profileimg);
        mSave = (Button) findViewById(R.id.save);
//        userId = mAuth.getCurrentUser().getUid();

        mData = FirebaseDatabase.getInstance().getReference().child("Users").child("Volunteers");
        getUserInformation();

        mProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
                Intent intent = new Intent(MyProfile.this, HomePage.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void getUserInformation() {
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("profileImageUrl")!=null) {
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImg);
                    }

                    if (map.get("firstName")!=null) {
                        mFirstName = map.get("firstName").toString();
                        mFirstNameField.setText(mFirstName);
                    }

                    if (map.get("lastName")!=null) {
                        mLastName = map.get("phone").toString();
                        mLastNameField.setText(mLastName);
                    }

                    if (map.get("email")!=null) {
                        mEmail = map.get("email").toString();
                        mEmailField.setText(mEmail);
                    }

                    if (map.get("password")!=null) {
                        mPassword = map.get("password").toString();
                        mPasswordField.setText(mPassword);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {
        mFirstName = mFirstNameField.getText().toString();
        mLastName = mLastNameField.getText().toString();
        mEmail = mEmailField.getText().toString();
        mPassword = mPasswordField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("firstName", mFirstName);
        userInfo.put("lastName", mLastName);
        userInfo.put("email", mEmail);
        userInfo.put("password", mPassword);
        mData.updateChildren(userInfo);

        if(resultUri != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, boas);
            byte[] data = boas.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while(!urlTask.isSuccessful()) {
                        Uri downloadUrl = urlTask.getResult();
                        Map newImage = new HashMap();
                        newImage.put("profileImageUrl", downloadUrl);
                        mData.updateChildren(newImage);
                    }
                    finish();
                }
            });
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImg.setImageURI(resultUri);
        }
    }
}

