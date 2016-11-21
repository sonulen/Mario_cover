package com.example.medivh.mario_cover;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
/**
 * Created by Medivh on 21.11.2016.
 */

public class Renderer implements GLSurfaceView.Renderer{

//  При создании Surface View ( поверхность отображения ) отчищаем к базовому цвету "Красный"
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

//  При смене ориентации и начальной загрузки указываем начало т.(0,0) и длину и высоту отрисовки
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
    }

//  При  отрисовке/перерисовке кадра очищаем экранный буфер, задающийся флажком GL_COLOR_BUFFER_BIT, и 
//  заполняем его цветом, заданным последним вызовом glClearColor(), т.е., красным.
    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);
    }
}