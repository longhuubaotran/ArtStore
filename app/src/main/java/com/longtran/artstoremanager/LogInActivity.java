package com.longtran.artstoremanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {
    EditText usernameEdit, passwordEdit;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        checkUsernamePassword();
    }

    private void initView() {
        usernameEdit = findViewById(R.id.username_edit_text);
        passwordEdit = findViewById(R.id.password_edit_text);
        loginBtn = findViewById(R.id.login_btn);
    }

    private void checkUsernamePassword(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password;
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                if ((username.equals("admin"))&&(password.equals("admin"))){
                    Intent intent = new Intent(getApplicationContext(),AdminActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Incorrect username or password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
