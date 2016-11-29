package com.example.medivh.mario_cover;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Activity_one extends AppCompatActivity {

//  Переменая с областью отрисовки
    GLSurfaceView glSurfaceView;
//  Флажок
    private boolean rendererSet = false;


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
        setContentView(R.layout.activity_one_land);
    }

    public void startgame(View view) {
        // Получаем область
        glSurfaceView = new GLSurfaceView(this);
        // Проверяем поддерживается ли OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (supportsEs2) {
            // Если прошло проверку на версию то запускаем наш класс отрисовки Renderer()
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new Renderer(this));
            setContentView(glSurfaceView);


            LinearLayout ll = new LinearLayout(this);
            Button b = new Button(this);
            b.setText("RUN! TUTU");
            ll.addView(b);
            ll.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            this.addContentView(ll,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            b.setOnTouchListener(new View.OnTouchListener(){
                public boolean onTouch( View b , MotionEvent theMotion ) {
                    switch ( theMotion.getAction() ) {
                        case MotionEvent.ACTION_DOWN:
                            Renderer.Move();
                            break;
                        case MotionEvent.ACTION_UP:
                            Renderer.Stop();
                            break;
                    }
                    return true;
                }
            });
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show(); return;
        }
    }
}
