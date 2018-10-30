package com.example.nataliaaulia.whereto;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class CustService extends AppCompatActivity {

    private Button mSend, mBack;
    private EditText mEmail, mPhone;
    private String yourStringPhone = "(+1)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_service);

        mSend = (Button) findViewById(R.id.send);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CustService.this, "Message sent.", Toast.LENGTH_SHORT).show();
            }
        });

        mEmail = (EditText) findViewById(R.id.email);
        mPhone = (EditText) findViewById(R.id.phone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPhone.setText(PhoneNumberUtils.formatNumber(yourStringPhone, Locale.getDefault().getCountry()));
        } else {
            mPhone.setText(PhoneNumberUtils.formatNumber(yourStringPhone)); //Deprecated method
        }

        mBack = (Button) findViewById(R.id.back);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustService.this, HomePage.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}
