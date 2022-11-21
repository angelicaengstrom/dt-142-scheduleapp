package com.example.schedule;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    Button btnLogIn;
    public String user;
    TextInputLayout textInputLayout;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER = "user";

    public class LogInListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            user = textInputLayout.getEditText().getText().toString();
            if(user.contains("199905063641")) {
                saveData();
                Intent homeAct = new Intent(MainActivity.this, HomeActivity.class);
                homeAct.putExtra("id", user);
                startActivity(homeAct);
                finish();
            }else {
                textInputLayout.setError(getString(R.string.nogot_gick_snett));
                textInputLayout.setErrorEnabled(true);
            }
        }
    }

    public class InputListener implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() > 0 && charSequence.length() != 12) {
                textInputLayout.setError(getString(R.string.fel_format));
                textInputLayout.setErrorEnabled(true);
                btnLogIn.setEnabled(false);
            } else {
                textInputLayout.setErrorEnabled(false);
                btnLogIn.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogIn = findViewById(R.id.buttonLogIn);
        loadData();

        btnLogIn.setEnabled(false);
        btnLogIn.setOnClickListener(new LogInListener());

        textInputLayout = (TextInputLayout) findViewById(R.id.ssn_text_input_layout);
        textInputLayout.getEditText().addTextChangedListener(new InputListener());
    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USER, user);

        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        user = sharedPreferences.getString(USER, null);
        //FÖR ATT ÅTERSTÄLLA
        /*SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER, null);
        editor.apply();*/
        if(user != null){
            Intent homeAct = new Intent(MainActivity.this, HomeActivity.class);
            homeAct.putExtra("id", user);
            startActivity(homeAct);
            finish();
        }
    }
}
