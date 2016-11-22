package com.example.alexander.firstopengl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class activity_one extends AppCompatActivity {
    //  Переменая с областью отрисовки
    private GLSurfaceView glSurfaceView;
    //  Флажок для управления
    private boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Получаем область
        glSurfaceView = new GLSurfaceView(this);
        // Проверяем поддерживается ли OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (supportsEs2) {
            // Если прошло проверку на версию то запускаем наш класс отрисовки Renderer()
            glSurfaceView.setRenderer(new renderer_one()); rendererSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show(); return;
        }
        setContentView(glSurfaceView);
    }
}