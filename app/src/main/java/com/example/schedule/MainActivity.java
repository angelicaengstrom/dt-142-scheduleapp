package com.example.schedule;

import androidx.annotation.NonNull;
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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    Button btnLogIn;
    public String ssn;
    TextInputLayout textInputLayout;
    SharedPreferences sharedPreferences;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER = "user";

    public class LogInListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            ssn = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();
            getUser(ssn);
        }
    }

    public class InputListener implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() > 0 && charSequence.length() != 12) {
                showErrorMessage(getString(R.string.fel_format));
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
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        if(oldUser()){
            startHomeActivity();
        }else {
            btnLogIn = findViewById(R.id.buttonLogIn);
            btnLogIn.setEnabled(false);
            btnLogIn.setOnClickListener(new LogInListener());

            textInputLayout = (TextInputLayout) findViewById(R.id.ssn_text_input_layout);
            Objects.requireNonNull(textInputLayout.getEditText()).addTextChangedListener(new InputListener());
        }
    }

    public void saveData(){
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USER, ssn);
        editor.apply();
    }

    public boolean oldUser(){
        ssn = sharedPreferences.getString(USER, null);

        /*
        ÅTERSTÄLLNING
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER, null);
        editor.apply();*/

        return ssn != null;
    }

    public void startHomeActivity(){
        Intent homeAct = new Intent(MainActivity.this, HomeActivity.class);
        homeAct.putExtra("id", ssn);
        startActivity(homeAct);
        finish();
    }

    void getUser(String id){
        /*String samuel = "10.82.231.15";
        samuel = "89.233.229.182";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + samuel + ":8080/antons-skafferi-db-1.0-SNAPSHOT/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();*/

        Retrofitter<EmployeeAPI> retrofitter = new Retrofitter<>();

        //EmployeeAPI employeeAPI = retrofit.create(EmployeeAPI.class);

        EmployeeAPI employeeAPI = retrofitter.create(EmployeeAPI.class);
        Call<List<Employee>> call = employeeAPI.getEmployeeWithId(id);

        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(@NonNull Call<List<Employee>> call, @NonNull Response<List<Employee>> response) {
                if(!response.isSuccessful()) {
                    showErrorMessage(getString(R.string.nogot_gick_snett));
                    return;
                }
                List<Employee> employee = response.body();
                if(Objects.requireNonNull(employee).size() == 1) {
                    saveData();
                    startHomeActivity();
                }else{
                    showErrorMessage(getString(R.string.server_fail));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Employee>> call, @NonNull Throwable t) {
                showErrorMessage(getString(R.string.server_fail));
            }
        });
    }

    void showErrorMessage(String errorMessage){
        textInputLayout.setError(errorMessage);
        textInputLayout.setErrorEnabled(true);
    }
}
