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
    private float[] mProjectionMatrix = new float[16];
    // Создаем матрицы вида
    private float[] mViewMatrix = new float[16];
    // Создаем матрицу модели
    private float[] mModelMatrix = new float[16];
    //
    /** Матрица модели вида проекции  */
    private float[] mMVPMatrix = new float[16];
    // Флоат размер в битах
    private final int mBytesPerFloat = 4;
    // Начальня позиция для буферов
    private final int mPositionOffset = 0;
    // Размер ( кол-во переменных ) для координат
    private final int mPositionDataSize = 3;
    // Начальная позиция для переменных цвета
    private final int mColorOffset = 3;
    /** Размер ( кол-во переменных ) для цветов RGBA */
    private final int mColorDataSize = 4;
    // Как много переменных ( в битах ) занимает одна точка
    private final int mStrideBytes = 7 * mBytesPerFloat;

    // Передаем в шейдер
    /* This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;
    /* This will be used to pass in model position information. */
    private int mPositionHandle;
    /* This will be used to pass in model color information. */
    private int mColorHandle;

    // Создаем переменные для сдвигов
    private float x;
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



// COPY
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

//  При смене ориентации и начальной загрузки указываем начало т.(-6,0) и длину и высоту отрисовки и задаем матрицу проекций
    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        // Начальная точка отображения
        GLES20.glViewport(-6, 0, width, height);
//        ПОСТАВЬ -40 ЧТО ЗА ХУЙНЯ
//        NOVOE
        createProjectionMatrix(width*5, height);
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

        glBindTexture(GL_TEXTURE_2D, textureSky);
        glDrawArrays(GL_TRIANGLE_STRIP, 8, 4);

        glBindTexture(GL_TEXTURE_2D, textureEarth);
        glDrawArrays(GL_TRIANGLE_STRIP, 12, 4);

        glBindTexture(GL_TEXTURE_2D, textureMario);
        Matrix.setIdentityM(mModelMatrix, 0);

        setModelMatrix();
        bindMatrix();


        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    }

// NOVOE
    private void createProjectionMatrix(int width, int height) {
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 4.0f;
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
        // Mario
        float[] mariocoord = {
                // Mario
                -2.0f, 0.3f, 0f,   0.46f, 0.002f,
                -2.0f, -0.3f, 0.0f,  0.46f, 0.257f,
                -1.4f, 0.3f, 0.0f,   0.634f, 0.002f,
                -1.4f, -0.3f, 0.0f,  0.634f, 0.257f,
                // Sky
                -6f, 4f, 0,   0, 0,
                -6f, 0, 0,      0, 0.49f,
                6,  4f, 0,   0.49f, 0,
                6, 0, 0,       0.49f, 0.49f,
                // Sea
                -6f, 0, 0,      0.50f, 0,
                -6f, -4, 0,       0.50f, 0.49f,
                6, 0, 0,      1, 0,
                6,-4, 0,        1, 0.49f,
                // Earth
                -6f, 0, 0,      0.0230547f, 0.644927536f,
                -6f, -0.5f, 0,       0.0230547f, 0.75724637f,
                6, 0, 0,      0.1123919f, 0.644927536f,
                6, -0.5f, 0,        0.1123919f, 0.75724637f,
        };


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
        float eyeX = -2.0f;
        float eyeY = 0.0f;
        float eyeZ = 1.5f;

        // точка направления камеры
        float centerX = -2.0f;
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
        if ( flag == 1 ) {
            if (x <= 6) {
                x = (float) (x + 0.1);
            } else {
                x = -6.0f;
            }
        }
            //void rotateM (float[] m,  int mOffset, float a,float x, float y, float z)
            //Rotates matrix m in place by angle a (in degrees) around the axis (x, y, z).
            Matrix.translateM(mModelMatrixNEW, 0, x, 0, 0);
    }

    static public void Move() {
        flag = 1;
    }
    static public void Stop() {
        flag = 0;
    }
}

