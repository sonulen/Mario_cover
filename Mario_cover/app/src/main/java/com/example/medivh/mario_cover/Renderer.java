package com.example.medivh.mario_cover;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

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
    // Karta
    private static float[] map = {
        // где 0 пустота
        0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
    };

    // Размеры экрана
    private static float screenWidth = 2.7f;
    private static float screenHeight = 1.5f;
    private static int lengthmap = 0;
    private static int successSlots = 0;
    // Создаем переменные для сдвигов
    private static float x=0;
    private float MarioPositionX = 0;
    private static float MarioPositionY = 0;
    private static float xSpeed = (float) 0.00;
    private static float ySpeed = (float) 0.00;
    public static float dY = (float) 0.00;
//
    static public int flag;
    static public int flagUP;
    static public int endGame = 0;

// NOVOE
    private FloatBuffer marioData;
    private int textureMario;
    private int textureSky;
    private int textureSea;
    private int textureEarth;
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
        MarioPositionY = 0;
        MarioPositionX = -(4/5)* screenWidth;
        ySpeed = 0;
        x = MarioPositionX;
        xSpeed = 0;
        endGame = 0;
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


        glBindTexture(GL_TEXTURE_2D, textureEarth);
        for ( int i = 0; i < successSlots; i++ ) {
            glDrawArrays(GL_TRIANGLE_STRIP, (12+(i*4)), 4);
        }

        if ( xSpeed > 0) textureMario = TextureUtils.loadTexture(context, R.drawable.move);
        if ( xSpeed < 0) textureMario = TextureUtils.loadTexture(context, R.drawable.moveback);
        if ( xSpeed == 0) textureMario = TextureUtils.loadTexture(context, R.drawable.stop);

        if ( flagUP == 1 ) {
            if ( xSpeed >= 0) textureMario = TextureUtils.loadTexture(context, R.drawable.jump);
            if ( xSpeed < 0) textureMario = TextureUtils.loadTexture(context, R.drawable.jumpback);
        }

        glBindTexture(GL_TEXTURE_2D, textureMario);
        if ( x < (4 * screenWidth / 5)) {
            Matrix.setIdentityM(mModelMatrix, 0);
            MarioMoveWithoutCamera();
            bindMatrix();
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        } else {
            Matrix.setIdentityM(mModelMatrix, 0);
            MarioMoveWithCamera();
            bindMatrix();
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
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


        successSlots = 0;
        for ( int i = 0; i < map.length; i++ )
        {
            if (map[i] != 0) successSlots += 1;
        }
        lengthmap = map.length;
        float[] mariocoord = new float[((successSlots+1)*20)+60];

//        final String LOG_TAG = "SuccessSlots - ";

 //       Log.d("Заход в PrepareData #", "1234");
        successSlots = 0;
        int skylenth = lengthmap / 20;
        skylenth = skylenth + 1;
        float[] mariocoordMISSCLICK = {
                // Mario
                -screenWidth, 0f, 0f,   0.1f, 0f,       // 0 1 2 3 4
                -screenWidth, -(screenHeight/3), 0.0f,  0f, 1f, // 5 6 7 8 9
                -(screenWidth*4/5), 0, 0.0f,   1f, 0f, // 10 11 12 13 14
                -(screenWidth*4/5), -(screenHeight/3), 0.0f,  1f, 1f, // 15 16 17 18 19
                // Sky
                -screenWidth, screenHeight, 0,   0, 0, // 20 21 22 23 24
                -screenWidth, -(screenHeight/3), 0,      0, 1f, // 25 26 27 28 29
                (screenWidth*2*skylenth),  screenHeight, 0,   1f, 0,  // 30 31 32 33 34
                (screenWidth*2*skylenth), -(screenHeight/3), 0,       1f, 1f, // 35 36 37 38 39
                // Sea
                -screenWidth, -screenHeight, 0,      0, 0, // 40 41 42 43 44
                -screenWidth, -(screenHeight/3), 0,       0, 1f, // 45 46 47 48 49
                (screenWidth*2*skylenth), -screenHeight, 0,      1, 0, // 50 51 52 53 54
                (screenWidth*2*skylenth), -(screenHeight/3), 0,        1, 1f, // 55 56 57 58 59
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
        textureMario = TextureUtils.loadTexture(context, R.drawable.stop);
        textureSky = TextureUtils.loadTexture(context, R.drawable.nebonebo);
        textureSea = TextureUtils.loadTexture(context, R.drawable.seasea);
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


    private void MarioMoveWithoutCamera() {


        if (canIMoveX(MarioPositionX) && endGame == 0) {
            if (flag == 1) {
                if (xSpeed < 0.07) xSpeed += 0.01;
            }
            if (flag == -1 && x >= 0.02 ) {
                if (xSpeed > -0.07) xSpeed -= 0.005;
            }
            if (xSpeed < 0 && x < 0.2) xSpeed = 0;
            if (flag == 0) {
                if (xSpeed > 0) xSpeed -= 0.003;
                if (xSpeed > 0 && xSpeed < 0.003) xSpeed = 0;
                if (xSpeed < 0 && xSpeed > -0.003) xSpeed = 0;
                if (xSpeed < 0) xSpeed += 0.003;
                if ( x < xSpeed ) xSpeed = 0;
            }
            if (xSpeed != 0 ) {
                x = x + xSpeed;
                MarioPositionX += xSpeed;
            }
            MoveY();
        }
        else {
            xSpeed = 0;
        }

        Log.d("xSpeed:", Float.toString(xSpeed));
        Matrix.translateM(mModelMatrixNEW, 0, x, MarioPositionY, 0);
    }

    private void MarioMoveWithCamera() {
        boolean canI = true;
        if ( endGame == 0) {
            changeXspeed(flag);
            changeYspeed(flagUP);
            if (fallingMario()) {
                canI = canIMoveX(MarioPositionX);
            }
        }
        MarioPositionY += ySpeed;
        if ( canI ) {
            x = x + xSpeed;
            MarioPositionX += xSpeed;
        }
        Matrix.translateM(mModelMatrixNEW, 0, x, MarioPositionY, 0);
        Matrix.setLookAtM(mViewMatrixNEW, 0, x-(4 * screenWidth / 5), 0, 1.5f, x-(4 * screenWidth / 5), 0, -5.0f, 0, 1, 0);
    }

    public void changeXspeed ( int flag ) {
        if (flag == 1 ) {
            if (xSpeed < 0.07) xSpeed += 0.01;
        }
        if (flag == -1 ) {
            if (xSpeed > -0.07) xSpeed -= 0.005;
        }

        if (flag == 0) {
            if (xSpeed > 0) xSpeed -= 0.001;
            if (xSpeed > 0 && xSpeed < 0.001) xSpeed = 0;
            if (xSpeed < 0 && xSpeed > -0.001) xSpeed = 0;
            if (xSpeed < 0) xSpeed += 0.001;
        }
    }

    public void changeYspeed ( int flagUP ) {
        if ( flagUP == 1) {
            if (ySpeed == 0) ySpeed = 0.05f;
            if ( (MarioPositionY - dY) >= screenHeight/2)  {
                ySpeed = -0.05f;
                flagUP = 0;
            }
        }
        if ( flagUP == 0) {
            if ( ySpeed != 0) ySpeed = -0.05f;
        }
        if ( Math.abs(MarioPositionY - dY) < 0.05f && ySpeed < 0) {
            MarioPositionY = dY;
            ySpeed = 0;
        }
    }

    public boolean fallingMario () {
        float Mariox = MarioPositionX;
        if ( xSpeed > 0) Mariox = Mariox + screenWidth/6;
        if ( xSpeed < 0) Mariox = Mariox - screenWidth/6;
        int slotx= 0;
        int level = 0;
        int check1 = (int) ((Mariox) / (screenWidth/5));
        int check2 = (int) ((Mariox + screenWidth*1/40) / (screenWidth/5));
        int check3 = (int) ((Mariox - screenWidth*1/40) / (screenWidth/5));

        if ( check2 != check1 ) slotx = check2;
        else {
            if (check3 != check1) {
                slotx = check3;
            } else slotx = check1;
        }


        if ( MarioPositionY >= 0 && MarioPositionY < screenHeight/3) level = 1;
        if ( MarioPositionY >= screenHeight/3 ) level = 2;

        if ( level == 1) {
            if ( map[slotx + map.length/2] == 0 && ySpeed <= 0) {
                ySpeed = -0.05f;
                xSpeed = 0f;
                endGame = 1;
                return false;
            }
        }
        if ( level == 2) {
            if ( map[slotx] == 0 && ySpeed <= 0) {
                ySpeed = -0.05f;
                return false;
            }
            if ( map[slotx] == 1 && MarioPositionY >= screenHeight/3 && MarioPositionY <= (screenHeight/3+0.01f) ) {
                ySpeed = 0f;
                return false;
            }
        }
        return true;
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

    public static void MoveUp() {
        if ( flagUP == 0 && ySpeed == 0 && (MarioPositionY - dY) < 0.01f) dY = MarioPositionY;
        flagUP = 1;
    }

    public static void StopUp() {
            flagUP = 0;
    }

    static boolean canIMoveX (float Mariox) {
        if ( xSpeed < 0) Mariox = Mariox + screenWidth/6;
        int slotx= 0;
        int level = 0;
        int check1 = (int) ((Mariox) / (screenWidth/5));
        int check2 = (int) ((Mariox + screenWidth*1/40) / (screenWidth/5));

        if ( check2 != check1 ) slotx = check2;
        else slotx = check1;


        if ( MarioPositionY >= 0 && MarioPositionY < screenHeight/3) level = 1;
        if ( MarioPositionY >= screenHeight/3 ) level = 2;

        if ( level == 1) {
            if ( map[slotx+1] == 1 && xSpeed >= 0) {
                return false;
            }
            if ( slotx > 0) {
                if (map[slotx - 1] == 1 && xSpeed <= 0) {
                    return false;
                }
            }
        }

        return true;
    }

    boolean canIMoveY () {

        int slotx=0 ;
        int check1 = (int) ((MarioPositionX) / (screenWidth/5));
        int check2 = (int) ((MarioPositionX + screenWidth/10) / (screenWidth/5));

        slotx = (int) (((MarioPositionX) / (screenWidth/5)));
        if ( ySpeed > 0 ) return true;
        if ( ySpeed < 0) {
            if (MarioPositionY >= screenHeight / 3) {
                if (MarioPositionY > screenHeight / 3) {
                    if (map[slotx] == 1) {
                        return true;
                    }
                }
                if (MarioPositionY == screenHeight / 3) {
                    if (map[slotx] == 1) {
                        return false;
                    }
                }
            } else {
                if (MarioPositionY > 0) {
                    if (map[lengthmap / 2 + slotx] == 1) {
                        return true;
                    }
                }
                if (MarioPositionY == 0) {
                    if (map[lengthmap / 2 + slotx] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    void MoveY () {

        if (canIMoveY()) {

            if (flagUP == 1) {
                // Вот здесь на сколько прыжок
                if (((MarioPositionY - dY) == 0) && ySpeed != 0) {
                    flagUP = 0;
                }
                if ((MarioPositionY - dY < (screenHeight / 2)) && ySpeed >= 0) {
                    ySpeed = 0.05f;
                } else {
                    if ((MarioPositionY - dY) != 0) {
                        ySpeed = -0.05f;
                    }
                    if ((MarioPositionY - dY) <= 0.05f) {
                        ySpeed = 0;
                    }
                }
            }
            if (flagUP == 0) {
                if ((MarioPositionY - dY) != 0 && ySpeed > 0) ySpeed = -0.05f;
                if ((MarioPositionY - dY) == 0 || (MarioPositionY - dY) <= 0.05f) {
                    ySpeed = 0;
                    MarioPositionY = dY;
                }
            }
            MarioPositionY += ySpeed;
        }
    }

}

