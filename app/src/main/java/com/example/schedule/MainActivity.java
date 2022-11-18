package com.example.schedule;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    Button btnLogIn;
    TextView txtErrorLogIn;
    public String user;

    public class LogInListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            //Hämta användare från databasen
            //Kolla om användare finns

            //Hämta användare från bankID
            user = "200201010337";
            user = "199905063641";
            if(user != null) {
                txtErrorLogIn.setVisibility(View.INVISIBLE);
                Intent homeAct = new Intent(MainActivity.this, HomeActivity.class);
                homeAct.putExtra("id", user);
                startActivity(homeAct);
                finish();
            }else{
                txtErrorLogIn.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogIn = findViewById(R.id.buttonLogIn);
        txtErrorLogIn = findViewById(R.id.textViewErrorLogin);

        btnLogIn.setOnClickListener(new LogInListener());
    }
}
