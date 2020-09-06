package in.astudentzone.mentzyassignment.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.astudentzone.mentzyassignment.HomePage;
import in.astudentzone.mentzyassignment.R;

public class OTPverification extends AppCompatActivity {
    private TextView numbertext;
    private Button verify;
    private EditText otp;
    private Bundle extras;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private ProgressDialog progressDialog,progressDialog2;
    private String verificationCode;
    private SmsVerifyCatcher smsVerifyCatcher;

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_pverification);

        //find view by ID
        numbertext = findViewById(R.id.otpnumbertext);
        verify = findViewById(R.id.phonenumberbutton);
        otp = findViewById(R.id.et_otp);


        //get number from activity login
        extras = getIntent().getExtras();
        final String phonenumbertemp =  extras.getString("number");
        final String phonenumber = "+91" + phonenumbertemp;

        //set progress dialog
        progressDialog = new ProgressDialog(OTPverification.this);
        progressDialog2 = new ProgressDialog(OTPverification.this);
        progressDialog.setCancelable(false);
        progressDialog2.setCancelable(false);
        progressDialog.setMessage("Please Wait...!!");
        progressDialog2.setMessage("Verifying...!!");

        //set text
        numbertext.setText(phonenumbertemp);

        //get OTP
        GetOtp(phonenumber);

        //text watcher
        otp.addTextChangedListener(verifyvisibility);

        //on click listner
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String OTP = otp.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,OTP);
                signup(credential,phonenumber);
            }
        });

        //get otp automatically
        smsVerifyCatcher = new SmsVerifyCatcher(OTPverification.this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                otp.setText(code);//set code in edit text
            }
        });
    }

    private void GetOtp(String phonenumber) {

        //callback
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //Callback function called


            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(OTPverification.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) { //Manual Otp Verification
                super.onCodeSent(s, forceResendingToken);
                progressDialog.dismiss();
                verificationCode = s;
                Toast.makeText(OTPverification.this, "Code sent", Toast.LENGTH_SHORT).show();
            }
        };

        //Otp verfication
        progressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,                     // Phone number to verify
                60,                           // Timeout duration
                TimeUnit.SECONDS,                // Unit of timeout
                OTPverification.this,        // Activity (for callback binding)
                mCallback);                      // OnVerificationStateChangedCallbacks


    }

    //sms code reader
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    //Sign Up method called
    private void signup(PhoneAuthCredential credential, final String phonenumber) {
        progressDialog2.show();
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithCredential(credential).addOnCompleteListener(OTPverification.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final String uid = auth.getCurrentUser().getUid().toString();
                    final DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("ALL_USERS").child(uid);
                    mref.child("Other_Details").child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                //it means user already registered
                                Toast.makeText(OTPverification.this,"Welcome "+ phonenumber.toString(),Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(OTPverification.this, HomePage.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                progressDialog2.dismiss();
                                startActivity(intent);
                            }
                            else
                            {

                                //new user
                                mref.child("mobile").setValue(phonenumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Intent intent = new Intent(OTPverification.this,ProfilePic.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            progressDialog2.dismiss();
                                            startActivity(intent);
                                        }
                                       else
                                        {
                                           progressDialog2.dismiss();
                                            Toast.makeText(OTPverification.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                                       }
                                   }
                                });
                           }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    Toast.makeText(OTPverification.this, "Incorrect OTP", Toast.LENGTH_LONG).show();
                    progressDialog2.dismiss();
                }
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
            if (s.length() < 6) {
                verify.setAlpha((float) 0.4);
                verify.setEnabled(false);
                verify.setText("ENTER OTP");
            } else {
                verify.setText("VERIFY");
                verify.setAlpha(1);
                verify.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }


    };
}