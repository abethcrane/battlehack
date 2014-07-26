package com.battlehack.smallsolution;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class Purchased extends Activity {
    // Initializing variables
    EditText inputName;
    EditText inputEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchased);
        TextView tv = (TextView) findViewById(R.id.Message);
        Intent i = getIntent();
        tv.setText(String.format(getString(R.string.purchased_title), i.getStringExtra("name"), i.getStringExtra("code")));
    }
}
