package in.astudentzone.mentzyassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import in.astudentzone.mentzyassignment.Login.NewUserForm;
import in.astudentzone.mentzyassignment.Login.OTPverification;

public class MainActivity extends AppCompatActivity {

    private EditText phonenumber;
    private Button button;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null)
        {
            progressDialog.show();
            DatabaseReference  mref = FirebaseDatabase.getInstance().getReference().child("ALL_USERS").child(currentUser.getUid()).child("Other_Details");
            mref.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        startActivity(new Intent(MainActivity.this,HomePage.class));
                    }
                    else
                    {
                        startActivity(new Intent(MainActivity.this, NewUserForm.class));
                    }
                    progressDialog.dismiss();
                    finish();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find view by ID
        button  = findViewById(R.id.phonenumberbutton);
        phonenumber = findViewById(R.id.phonenumber);

        //mAuth
        mAuth =FirebaseAuth.getInstance();


        //progr
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please Wait..!!");
        progressDialog.setCancelable(false);


        //text watcher
        phonenumber.addTextChangedListener(verifyvisibility);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OTPverification.class);
                intent.putExtra("number",phonenumber.getText().toString());
                startActivity(intent);
                //  Toast.makeText(MainActivity.this,"hurray",Toast.LENGTH_LONG).show();
            }
        });

    }


    //text watcher fuction
    private TextWatcher verifyvisibility = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() < 10) {
                button.setAlpha((float) 0.4);
                button.setEnabled(false);
                button.setText("ENTER PHONE NUMBER");
            } else {
                button.setText("CONTINUE");
                button.setAlpha(1);
                button.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }


    };
}