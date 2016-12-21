package com.example.medivh.mario_cover;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GameMonitor extends AppCompatActivity {

    //  Переменая с областью отрисовки
    GLSurfaceView glSurfaceView;
    // Флаг событий
    static int EventIden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        // Убираем полосу названия приложения
        getSupportActionBar().hide();
        // Получаем область из нашего layout-land
        glSurfaceView = (GLSurfaceView) findViewById(R.id.glSurfaceViewID);
        // Проверяем поддерживается ли OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (supportsEs2) {
            // Если прошло проверку на версию то запускаем наш класс отрисовки Renderer()
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new Renderer(this));

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            //int width = size.x;
            //int height = size.y;
            //Renderer.TakeSize(width,height);
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show(); return;
        }
    }

    public void moveMariomove(View view) {
        Button button = (Button) findViewById(R.id.button_move);
        button.setOnTouchListener(new View.OnTouchListener(){
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
    }

    public void moveBackMove(View view) {
        Button button = (Button) findViewById(R.id.button_move_back);
        button.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch( View b , MotionEvent theMotion ) {
                switch ( theMotion.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        Renderer.MoveBack();
                        break;
                    case MotionEvent.ACTION_UP:
                        Renderer.Stop();
                        break;
                }
                return true;
            }
        });
    }

    public void JumpMove(View view) {
        Button button = (Button) findViewById(R.id.JumpMario);
        button.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch( View b , MotionEvent theMotion ) {
                switch ( theMotion.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        Renderer.MoveUp();
                        break;
                    case MotionEvent.ACTION_UP:
                        Renderer.StopUp();
                        break;
                }
                return true;
            }
        });
    }

    public void getFlags(int flag) {


    }


    public void RestartUp(View view) {

    }



// Konec
}


