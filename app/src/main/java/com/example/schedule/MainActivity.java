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

import com.example.schedule.json.Employee;
import com.example.schedule.json.EmployeeAPI;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
            connect(user);
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
        /*
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER, null);
        editor.apply();*/
        if(user != null){
            Intent homeAct = new Intent(MainActivity.this, HomeActivity.class);
            homeAct.putExtra("id", user);
            startActivity(homeAct);
            finish();
        }
    }

    void connect(String id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.82.231.15:8080/antons-skafferi-db-1.0-SNAPSHOT/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EmployeeAPI employeeAPI = retrofit.create(EmployeeAPI.class);
        Call<List<Employee>> call = employeeAPI.getEmployeeWithId(id);
        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if(!response.isSuccessful()) {
                    textInputLayout.setError(getString(R.string.nogot_gick_snett));
                    textInputLayout.setErrorEnabled(true);
                    return;
                }
                List<Employee> employee = response.body();
                if(!employee.isEmpty()) {
                    saveData();
                    Intent homeAct = new Intent(MainActivity.this, HomeActivity.class);
                    homeAct.putExtra("id", user);
                    startActivity(homeAct);
                    finish();
                }else{
                    textInputLayout.setError(getString(R.string.nogot_gick_snett));
                    textInputLayout.setErrorEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                textInputLayout.setError(getString(R.string.server_fail));
                textInputLayout.setErrorEnabled(true);
            }
        });
    }
}
