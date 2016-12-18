package com.example.medivh.mario_cover;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;

/**
 * Created by Medivh on 21.11.2016.
 */

public class Renderer implements GLSurfaceView.Renderer {

    private Context context;
    // Создаем матрицу проекций относительно камеры. почему 16?!
    // Создаем матрицу модели
    private float[] mModelMatrix = new float[16];
    //


    // Размеры экрана
    private static float screenWidth = 2.7f;
    private static float screenHeight = 1.5f;
    private static int lengthmap = 0;
    private static int successSlots = 0;
    // Создаем переменные для сдвигов
    private float x=0;
    private float xEarth ;
    private float y;
//
    static public int flag;
// NOVOE
    private FloatBuffer marioData;
    private int textureMario;
    private int textureSky;
    private int textureSea;
    private int textureEarth;
    private float[] mMatrix = new float[16];
    private int uMatrixLocation;
    private int programId;

    private int aPositionLocation;
    private int aTextureLocation;
    private int uTextureUnitLocation;

    private final static int POSITION_COUNT = 3;
    private static final int TEXTURE_COUNT = 2;
    private static final int STRIDE = (POSITION_COUNT
            + TEXTURE_COUNT) * 4;




    private float[] mProjectionMatrixNEW = new float[16];

    private float[] mMatrixNEW = new float[16];
    // Создаем матрицы вида
    private float[] mViewMatrixNEW = new float[16];
    // Создаем матрицу модели
    private float[] mModelMatrixNEW = new float[16];


//  Передаем контекст, создаем Рендер
    public Renderer(Context context) {
        this.context = context;
    }
    
//  При создании Surface View ( поверхность отображения ) отчищаем к базовому цвету "Красный"
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Задаем начальный цвет и очищаем бэкграунд
        GLES20.glClearColor(0f, 1f, 1f, 1.0f);

//      NOVOE
        createAndUseProgram();
        getLocations();
        prepareData();
        bindData();
        createViewMatrix();
    }

//  При смене ориентации и начальной загрузки указываем начало т.(-0,0) и длину и высоту отрисовки и задаем матрицу проекций
    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        // Начальная точка отображения
        GLES20.glViewport(0, 0, (width), height);
//        ПОСТАВЬ -40 ЧТО ЗА ХУЙНЯ
//        NOVOE
        createProjectionMatrix(width, height);
        bindMatrix();
    }

//  При  отрисовке/перерисовке кадра очищаем экранный буфер, задающийся флажком GL_COLOR_BUFFER_BIT, и 
//  заполняем его цветом, заданным последним вызовом glClearColor(), т.е., красным.
    @Override
    public void onDrawFrame(GL10 gl10) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        Matrix.setIdentityM(mModelMatrixNEW, 0);
        bindMatrix();

        glBindTexture(GL_TEXTURE_2D, textureSky);
        glDrawArrays(GL_TRIANGLE_STRIP, 4, 4);

        glBindTexture(GL_TEXTURE_2D, textureSea);
        glDrawArrays(GL_TRIANGLE_STRIP, 8, 4);




        if ( x < (4*screenWidth/5))
        {
            glBindTexture(GL_TEXTURE_2D, textureEarth);

            for ( int i = 0; i < successSlots; i++ ) {

                glDrawArrays(GL_TRIANGLE_STRIP, (12+(i*4)), 4);
            }
            Matrix.setIdentityM(mModelMatrix, 0);
            setModelMatrix();
            bindMatrix();
            glBindTexture(GL_TEXTURE_2D, textureMario);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        }
        else {
            Matrix.setIdentityM(mModelMatrix, 0);
            glBindTexture(GL_TEXTURE_2D, textureMario);
            setModelMatrix();
            bindMatrix();
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

            glBindTexture(GL_TEXTURE_2D, textureEarth);

            Matrix.setIdentityM(mModelMatrix, 0);
            setModelMatrixEarth();
            bindMatrix();

            for ( int i = 0; i < successSlots; i++ ) {

                glDrawArrays(GL_TRIANGLE_STRIP, (12+(i*4)), 4);
            }
        }

    }

// NOVOE
    private void createProjectionMatrix(int width, int height) {
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;
        Matrix.frustumM(mProjectionMatrixNEW, 0, left, right, bottom, top, near, far);
    }

    private void bindMatrix() {
        Matrix.multiplyMM(mMatrixNEW, 0, mViewMatrixNEW, 0, mModelMatrixNEW, 0);
        Matrix.multiplyMM(mMatrixNEW, 0, mProjectionMatrixNEW, 0, mMatrixNEW, 0);
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrixNEW, 0);
    }

    private void createAndUseProgram() {
        int vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(programId);
    }

    private void getLocations() {
        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        aTextureLocation = glGetAttribLocation(programId, "a_Texture");
        uTextureUnitLocation = glGetUniformLocation(programId, "u_TextureUnit");
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
    }

    private void prepareData() {

        float[] map = {
                // где 0
                0,0,1,1,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
        };
        successSlots = 0;
        for ( int i = 0; i < map.length; i++ )
        {
            if (map[i] != 0) successSlots += 1;
        }
        lengthmap = map.length;
        float[] mariocoord = new float[((successSlots+1)*20)+60];

//        final String LOG_TAG = "SuccessSlots - ";
//        Log.d(LOG_TAG, Float.toString(successSlots));
        successSlots = 0;

        float[] mariocoordMISSCLICK = {
                // Mario
                -screenWidth, 0f, 0f,   0.46f, 0.002f,       // 0 1 2 3 4
                -screenWidth, -(screenHeight/3), 0.0f,  0.46f, 0.257f, // 5 6 7 8 9
                -(screenWidth*4/5), 0, 0.0f,   0.634f, 0.002f, // 10 11 12 13 14
                -(screenWidth*4/5), -(screenHeight/3), 0.0f,  0.634f, 0.257f, // 15 16 17 18 19
                // Sky
                -screenWidth, screenHeight, 0,   0, 0, // 20 21 22 23 24
                -screenWidth, -(screenHeight/3), 0,      0, 0.49f, // 25 26 27 28 29
                (screenWidth),  screenHeight, 0,   0.49f, 0,  // 30 31 32 33 34
                (screenWidth), -(screenHeight/3), 0,       0.49f, 0.49f, // 35 36 37 38 39
                // Sea
                -screenWidth, -screenHeight, 0,      0.50f, 0, // 40 41 42 43 44
                -screenWidth, -(screenHeight/3), 0,       0.50f, 0.49f, // 45 46 47 48 49
                (screenWidth), -screenHeight, 0,      1, 0, // 50 51 52 53 54
                (screenWidth), -(screenHeight/3), 0,        1, 0.49f, // 55 56 57 58 59
        };

        for ( int i = 0; i < 60; i ++)
        {
            mariocoord[i]=mariocoordMISSCLICK[i];
        }
        // переменная что бы нарисовать верхнюю половину и потом вернуться к началу координат и нарисовать нижнюю
        int halfCompleted = 0;
        // Высота относительно 00 где мы рисуем левл ( верхняя грянь)
        float nowHeight = 0;
        for ( int i = 0; i < lengthmap ; i ++ )
        {
            if (map[i] != 0) {

                //Log.d("В цикле +++++", Float.toString(successSlots));

                if (i >= lengthmap / 2) {
                    halfCompleted = i - lengthmap / 2;
                    // Высота относительно 00 где мы рисуем левл ( верхняя грянь)
                    nowHeight = -screenHeight/3;
                } else {
                    halfCompleted = i;
                }
                mariocoord[60 + (successSlots * 20)] = (-screenWidth + (screenWidth / 5) * halfCompleted);
                mariocoord[61 + (successSlots * 20)] = nowHeight;
                mariocoord[62 + (successSlots * 20)] = 0;
                mariocoord[63 + (successSlots * 20)] = 0.0230547f;
                mariocoord[64 + (successSlots * 20)] = 0.644927536f;

                mariocoord[65 + (successSlots * 20)] = (-screenWidth + (screenWidth / 5) * halfCompleted);
                mariocoord[66 + (successSlots * 20)] = nowHeight-screenHeight/3;
                mariocoord[67 + (successSlots * 20)] = 0;
                mariocoord[68 + (successSlots * 20)] = 0.0230547f;
                mariocoord[69 + (successSlots * 20)] = 0.75724637f;

                mariocoord[70 + (successSlots * 20)] = (-screenWidth * 4 / 5 + (screenWidth / 5) * halfCompleted);
                mariocoord[71 + (successSlots * 20)] = nowHeight;
                mariocoord[72 + (successSlots * 20)] = 0;
                mariocoord[73 + (successSlots * 20)] = 0.1123919f;
                mariocoord[74 + (successSlots * 20)] = 0.644927536f;

                mariocoord[75 + (successSlots * 20)] = (-screenWidth * 4 / 5 + (screenWidth / 5) * halfCompleted);
                mariocoord[76 + (successSlots * 20)] = nowHeight-screenHeight/3;
                mariocoord[77 + (successSlots * 20)] = 0;
                mariocoord[78 + (successSlots * 20)] = 0.1123919f;
                mariocoord[79 + (successSlots * 20)] = 0.75724637f;

                successSlots = successSlots + 1;
            }
        }

        marioData = ByteBuffer
                .allocateDirect(mariocoord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        marioData.put(mariocoord);
        textureMario = TextureUtils.loadTexture(context, R.drawable.texture);
        textureSky = TextureUtils.loadTexture(context, R.drawable.texture2);
        textureSea = TextureUtils.loadTexture(context, R.drawable.texture2);
        textureEarth = TextureUtils.loadTexture(context, R.drawable.texture);
    }



    private void bindData() {
        // координаты вершин
        marioData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GL_FLOAT,
                false, STRIDE,  marioData);
        glEnableVertexAttribArray(aPositionLocation);


        // координаты текстур
        marioData.position(POSITION_COUNT);
        glVertexAttribPointer(aTextureLocation, TEXTURE_COUNT, GL_FLOAT,
                false, STRIDE,  marioData);
        glEnableVertexAttribArray(aTextureLocation);


    }

    private void createViewMatrix() {
        // точка полоения камеры
        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = 1.5f;

        // точка направления камеры
        float centerX = -0.0f;
        float centerY = 0.0f;
        float centerZ = -5.0f;

        // up-вектор
        float upX = 0;
        float upY = 1;
        float upZ = 0;


        Matrix.setLookAtM(mViewMatrixNEW, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    private void setModelMatrix() {
/**        Метод translateM настраивает матрицу на перемещение.  В нем мы указываем model матрицу и нулевой отступ.
        Последние три параметра – это значение смещения соответственно по осям X, Y и Z. Мы задаем смещение по оси X, на 1.
        Т.к. камера смотрит на изображение с оси Z, то сместив треугольник по оси X на 1, мы получим смещение вправо. */
        //Matrix.translateM(mModelMatrixNEW, 0, 1, 0, 0);
        //В переменной angle угол будет меняться  от 0 до 360 каждые 10 секунд.

        if ( flag == 1 && x < (4*screenWidth/5) ) {
            x = (float) (x + 0.1);
            xEarth = -x;
        }
        if ( xEarth >= -(4*screenWidth/5 + 0.1) && flag == -1 && x > 0) {
            x = (float) (x - 0.1);
        }

            //void rotateM (float[] m,  int mOffset, float a,float x, float y, float z)
            //Rotates matrix m in place by angle a (in degrees) around the axis (x, y, z).
            Matrix.translateM(mModelMatrixNEW, 0, x, 0, 0);
    }

    private void setModelMatrixEarth() {
/**        Метод translateM настраивает матрицу на перемещение.  В нем мы указываем model матрицу и нулевой отступ.
 Последние три параметра – это значение смещения соответственно по осям X, Y и Z. Мы задаем смещение по оси X, на 1.
 Т.к. камера смотрит на изображение с оси Z, то сместив треугольник по оси X на 1, мы получим смещение вправо. */
        //Matrix.translateM(mModelMatrixNEW, 0, 1, 0, 0);
        //В переменной angle угол будет меняться  от 0 до 360 каждые 10 секунд.

        if ( flag == 1 ) {
                xEarth = (float) (xEarth - 0.1);
        }
        if ( flag == -1 && xEarth < -(4*screenWidth/5 + 0.1) ) {
            xEarth = (float) (xEarth + 0.1);
        }


        //void rotateM (float[] m,  int mOffset, float a,float x, float y, float z)
        //Rotates matrix m in place by angle a (in degrees) around the axis (x, y, z).
        Matrix.translateM(mModelMatrixNEW, 0, xEarth, 0, 0);
    }

    static public void Move() {
        flag = 1;
    }

    static public void MoveBack() {
        flag = -1;
    }
    static public void Stop() {
        flag = 0;
    }


    public static void TakeSize(int width, int height) {
        //screenWidth = width;
        //screenHeight = height;
    }
}

