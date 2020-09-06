package in.astudentzone.mentzyassignment.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import in.astudentzone.mentzyassignment.HomePage;
import in.astudentzone.mentzyassignment.R;

public class EditForm extends AppCompatActivity {

    private ImageView imageView;
    private DatabaseReference mref;
    private FirebaseAuth auth;
    private EditText name, emailid, age, city, profession, hobbies;
    private Button submit;
    private Bundle extras;
    private Uri resultUri;
    private final static int GALLERY_CODE = 1;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private DatabaseReference userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_form);

        //findview by id
        imageView = findViewById(R.id.roundedImageView);
        name = findViewById(R.id.name);
        emailid = findViewById(R.id.emailaddress);
        age = findViewById(R.id.age);
        city = findViewById(R.id.city);
        profession = findViewById(R.id.profession);
        hobbies = findViewById(R.id.hobbies);
        submit = findViewById(R.id.submitbuttonform);

        //text watcher
        name.addTextChangedListener(verifyvisibility);
        emailid.addTextChangedListener(verifyvisibility);
        age.addTextChangedListener(verifyvisibility);


        //Database conectivity
        userData = FirebaseDatabase.getInstance().getReference("ALL_USERS");


        //reference
        storageReference = FirebaseStorage.getInstance().getReference();

        //Progress Dialog created
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..!!");
        progressDialog.setCancelable(false);


        //get
        extras = getIntent().getExtras();
        final String namet =  extras.getString("name");
        final String emailt =  extras.getString("emailid");
        final String aget =  extras.getString("age");
        final String proft =  extras.getString("profession");
        final String cityt =  extras.getString("city");
        final String hobbiet =  extras.getString("hobbies");

        name.setText(namet);
        emailid.setText(emailt);
        age.setText(aget);
        profession.setText(proft);
        city.setText(cityt);
        hobbies.setText(hobbiet);


        //string uid
        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid().toString();

        //database reference
        mref = FirebaseDatabase.getInstance().getReference().child("ALL_USERS").child(uid);


        //get image
        mref.child("profilepic").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imageuri = snapshot.getValue().toString();
                    Picasso.get().load(imageuri).resize(100, 100).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //submit set on click
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String citytxt = city.getText().toString();
                String profftxt = profession.getText().toString();
                String hobbiestxt = hobbies.getText().toString();
                if (citytxt.isEmpty()) {
                    citytxt = "NA";
                }
                if (profftxt.isEmpty()) {
                    profftxt = "NA";
                }
                if (hobbiestxt.isEmpty()) {
                    hobbiestxt = "NA";
                }
                UserInfo userInfo = new UserInfo(name.getText().toString(), emailid.getText().toString(), age.getText().toString(), citytxt, profftxt, hobbiestxt);
                mref.child("Other_Details").setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(EditForm.this, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(EditForm.this, "Someting went wrong", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });



        //image vie on click listner
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,GALLERY_CODE);
            }
        });
    }

    private TextWatcher verifyvisibility = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String nametxt = name.getText().toString();
            String emailtxt = emailid.getText().toString();
            String agetxt = age.getText().toString();

            if (nametxt.isEmpty() || emailtxt.isEmpty() || agetxt.isEmpty()) {
                submit.setEnabled(false);
                submit.setText("ENTER DETAILS");
                submit.setAlpha((float) 0.5);
            } else {
                submit.setEnabled(true);
                submit.setText("APPLY CHANGES");
                submit.setAlpha(1);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Uri mImageuri = data.getData();
            CropImage.activity(mImageuri)
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(EditForm.this);

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
                                        FirebaseUser currentuser = auth.getCurrentUser();                      //currentUser object created
                                        String uid = currentuser.getUid().toString();

                                        userData.child(uid).child("profilepic").setValue(result.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(EditForm.this,"Profile Photo Added",Toast.LENGTH_LONG).show();
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
                Toast.makeText(EditForm.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }
    }
}