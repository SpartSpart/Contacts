package com.spart.spart.contacts;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {
    Button saveBtn;
    EditText fileName,
             nasFolder,
             nasLogin,
             nasPassword,
             eMail;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        saveBtn = findViewById(R.id.save_Btn);
        fileName = findViewById(R.id.file_name);
        nasFolder= findViewById(R.id.nas_folder);
        nasLogin= findViewById(R.id.nas_login);
        nasPassword= findViewById(R.id.nas_password);
        eMail= findViewById(R.id.email);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            fileName.setText(sharedPreferences.getString("filename", ""));
            nasFolder.setText(sharedPreferences.getString("nasfolder", ""));
            nasLogin.setText(sharedPreferences.getString("naslogin", ""));
            nasPassword.setText(sharedPreferences.getString("naspassword", ""));
            eMail.setText(sharedPreferences.getString("email", ""));
        }catch (Exception ignore){}

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringFileName,
                       stringNasFolder,
                       stringNasLogin,
                       stringNasPassword,
                       stringEmail;
                sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                stringFileName = fileName.getText().toString();
                stringNasFolder = nasFolder.getText().toString();
                stringNasLogin = nasLogin.getText().toString();
                stringNasPassword = nasPassword.getText().toString();
                stringEmail = eMail.getText().toString();
                editor.putString("filename",stringFileName);
                editor.putString("nasfolder",stringNasFolder);
                editor.putString("naslogin",stringNasLogin);
                editor.putString("naspassword",stringNasPassword);
                editor.putString("email",stringEmail);
                editor.commit();
                finishactivity();
            }
        });
    }

   void finishactivity(){
        this.finish();
    }
}
