package in.astudentzone.mentzyassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import in.astudentzone.mentzyassignment.Login.EditForm;

public class HomePage extends AppCompatActivity {

    private LinearLayout signout;
    private ProgressDialog progressDialog,progressDialog1;
    private FirebaseAuth mAuth;
    private ImageView imageView;
    private DatabaseReference mref;
    private TextView name,emailid,phone,age,profession,hobbies,city;
    private Button editProfile;
    private String namet,emailidt,phonet,aget,professiont,hobbiest,cityt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        signout = findViewById(R.id.linearlayoutsignout);
        imageView = findViewById(R.id.roundedImageView);
        name = findViewById(R.id.name);
        emailid = findViewById(R.id.emailaddress);
        phone = findViewById(R.id.phonenumber);
        age = findViewById(R.id.age);
        profession = findViewById(R.id.profession);
        hobbies = findViewById(R.id.hobbies);
        city = findViewById(R.id.city);
        editProfile = findViewById(R.id.editprofilebutton);

        //mauth instance
        mAuth = FirebaseAuth.getInstance();

        //progress dialog
        progressDialog = new ProgressDialog(HomePage.this);
        progressDialog.setMessage("Loading...!");
        progressDialog.setCancelable(false);

        progressDialog1 = new ProgressDialog(HomePage.this);
        progressDialog1.setMessage("Signing Out...!");
        progressDialog1.setCancelable(false);



        //edit profile on click listner
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Intent intent = new Intent(HomePage.this, EditForm.class);
             intent.putExtra("name",namet);
                intent.putExtra("emailid",emailidt);
                intent.putExtra("age",aget);
                intent.putExtra("city",cityt);
                intent.putExtra("profession",professiont);
                intent.putExtra("hobbies",hobbiest);
             startActivity(intent);
            }
        });



        //string uid
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid().toString();

        //database reference
        mref = FirebaseDatabase.getInstance().getReference().child("ALL_USERS").child(uid);


        //get image and no
        progressDialog.show();
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("profilepic").exists()) {
                    imageView.setEnabled(false);
                    String imageuri = snapshot.child("profilepic").getValue().toString();
                    Picasso.get().load(imageuri).resize(100, 100).into(imageView);
                }
                String no = snapshot.child("mobile").getValue().toString();
                phone.setText(no);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mref.child("Other_Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    namet =snapshot.child("name").getValue().toString();
                    emailidt = snapshot.child("emailid").getValue().toString();
                    aget = snapshot.child("age").getValue().toString();
                    professiont = snapshot.child("profession").getValue().toString();
                    hobbiest = snapshot.child("hobbies").getValue().toString();
                    cityt = snapshot.child("city").getValue().toString();

                    name.setText(namet);
                    emailid.setText(emailidt);
                    age.setText(aget);
                    profession.setText(professiont);
                    hobbies.setText(hobbiest);
                    city.setText(cityt);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //sigout onclicklistner
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog1.show();
                mAuth.signOut();
                Intent intent = new Intent(HomePage.this,MainActivity.class);
                progressDialog1.dismiss();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                Toast.makeText(HomePage.this, "Signed Out", Toast.LENGTH_LONG).show();
            }
        });
    }
}