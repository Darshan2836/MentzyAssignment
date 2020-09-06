package in.astudentzone.mentzyassignment.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import in.astudentzone.mentzyassignment.R;

public class ProfilePic extends AppCompatActivity {

    private Button add;
    private TextView skip;
    private final static int GALLERY_CODE = 1;
    private Uri resultUri;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic);

        skip = findViewById(R.id.skipbutton);
        add = findViewById(R.id.addphotobutton);



        //Auth instance
        mAuth = FirebaseAuth.getInstance();


        //Database conectivity
        userData = FirebaseDatabase.getInstance().getReference("ALL_USERS");


        //reference
        storageReference = FirebaseStorage.getInstance().getReference();

        //Progress Dialog created
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..!!");
        progressDialog.setCancelable(false);

        //skip button on click
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfilePic.this,NewUserForm.class);
                startActivity(intent);
            }
        });

        //add button on click
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,GALLERY_CODE);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Uri mImageuri = data.getData();
            CropImage.activity(mImageuri)
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(ProfilePic.this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog.show();
                resultUri = result.getUri();
                final StorageReference filepath = storageReference.child("USER_PROFILE").child(resultUri.getLastPathSegment());
                filepath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Uri result = uri;

                                        //save user data to database
                                        FirebaseUser currentuser = mAuth.getCurrentUser();                      //currentUser object created
                                        String uid = currentuser.getUid();

                                        userData.child(uid).child("profilepic").setValue(result.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful())
                                           {
                                               Intent intent = new Intent(ProfilePic.this,NewUserForm.class);
                                               startActivity(intent);
                                               progressDialog.dismiss();
                                               finish();
                                               Toast.makeText(ProfilePic.this,"Profile Photo Added",Toast.LENGTH_LONG).show();
                                           }
                                            }
                                        });


                                    }
                                });

                            }
                        });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                progressDialog.dismiss();
                Toast.makeText(ProfilePic.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }
}