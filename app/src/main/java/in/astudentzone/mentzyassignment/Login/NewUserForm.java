package in.astudentzone.mentzyassignment.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import in.astudentzone.mentzyassignment.HomePage;
import in.astudentzone.mentzyassignment.R;

public class NewUserForm extends AppCompatActivity {
    private ImageView imageView;
    private DatabaseReference mref;
    private FirebaseAuth auth;
    private EditText name, emailid, age, city, profession, hobbies;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_form);

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


        //string uid
        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid().toString();

        //database reference
        mref = FirebaseDatabase.getInstance().getReference().child("ALL_USERS").child(uid);


        //get image
        mref.child("profilepic").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    imageView.setEnabled(false);
                    String imageuri = snapshot.getValue().toString();
                    Picasso.get().load(imageuri).resize(100, 100).into(imageView);
                } else {
                    imageView.setEnabled(true);
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
                            Intent intent = new Intent(NewUserForm.this, HomePage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(NewUserForm.this, "Someting went wrong", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });



        //image vie on click listner
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewUserForm.this, ProfilePic.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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
                submit.setText("CONTINUE");
                submit.setAlpha(1);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    };
}