package com.example.medivh.mario_cover;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class Activity_one extends AppCompatActivity {


//  Флажок
    private boolean rendererSet = false;
    // Для сетевого взаимодействия


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Убираем полосу названия приложения
        getSupportActionBar().hide();
        // Проверка ориентации и задание горизонтальной ориентации
        Configuration configuration = getResources().getConfiguration();
        if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Делай переварот мэн",
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //https://vk.com/doc18053558_440093360 - 2 файл с авторами
        new ReadInBackground("https://vk.com/doc18053558_440093360").execute();

        setContentView(R.layout.activity_one_land);
    }

    public void startgame(View view) {

        Intent intent = new Intent(this, GameMonitor.class);
        startActivity(intent);

    }

    public void Info(View view) {
        String info = "";
        info = ReadInBackground.getInfo();
        Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }

}
