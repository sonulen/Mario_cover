package com.example.medivh.mario_cover;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class GameMonitor extends AppCompatActivity {

    //  Переменая с областью отрисовки
    GLSurfaceView glSurfaceView = null;
    Renderer ourRender = null;
    Button ControlButton = null;
    // Обработчик событий
    eventsHandler handler;
    final int STATUS_NONE = 0;
    final int STATUS_OFF = 9;
    final int STATUS_START = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        final MediaPlayer clicksound = MediaPlayer.create(this, R.raw.smb_jump_small);

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
            handler = new eventsHandler(this,(Button) findViewById(R.id.finalbutton));
            handler.handlerRules.sendEmptyMessage(STATUS_NONE);
            // Если прошло проверку на версию то запускаем наш класс отрисовки Renderer()
            glSurfaceView.setEGLContextClientVersion(2);
            ourRender = new Renderer(this, handler);
            glSurfaceView.setRenderer(ourRender);

        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show(); return;
        }
    }

    protected void onDestroy() {
        handler.handlerRules.sendEmptyMessage(STATUS_OFF);
        super.onDestroy();
    }

    protected void onStop() {
        handler.handlerRules.sendEmptyMessage(STATUS_OFF);
        super.onStop();
    }

    protected void onPause() {
        handler.handlerRules.sendEmptyMessage(STATUS_OFF);
        super.onPause();
    }

    protected void onResume() {
        handler.handlerRules.sendEmptyMessage(STATUS_OFF);
        super.onResume();
    }

    protected void onSaveInstanceState(Bundle outState) {
        handler.handlerRules.sendEmptyMessage(STATUS_OFF);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onUserLeaveHint() {
        handler.handlerRules.sendEmptyMessage(STATUS_OFF);
        super.onUserLeaveHint();
    }

    @Override
    public void onBackPressed() {
        handler.handlerRules.sendEmptyMessage(STATUS_OFF);
         super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                handler.handlerRules.sendEmptyMessage(STATUS_OFF);
                return true;
            case KeyEvent.KEYCODE_SEARCH:
                handler.handlerRules.sendEmptyMessage(STATUS_OFF);
                return true;
            case KeyEvent.KEYCODE_BACK:
                handler.handlerRules.sendEmptyMessage(STATUS_OFF);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void moveMariomove(View view) {
        Button button = (Button) findViewById(R.id.button_move);
        button.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch( View b , MotionEvent theMotion ) {
                switch ( theMotion.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        ourRender.Move();
                        break;
                    case MotionEvent.ACTION_UP:
                        ourRender.Stop();
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
                        ourRender.MoveBack();
                        break;
                    case MotionEvent.ACTION_UP:
                        ourRender.Stop();
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
                        ourRender.MoveUp();
                        break;
                    case MotionEvent.ACTION_UP:
                        ourRender.StopUp();
                        break;
                }
                return true;
            }
        });
    }


    public void RestartUp(View view) {
        Intent intent = new Intent(this, GameMonitor.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        ControlButton = (Button) findViewById(R.id.finalbutton);
        ControlButton.setVisibility(View.INVISIBLE);
    }



// Konec
}


