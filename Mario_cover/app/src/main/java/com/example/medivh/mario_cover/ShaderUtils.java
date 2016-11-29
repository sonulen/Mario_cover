package com.example.medivh.mario_cover;

import android.content.Context;

import static android.opengl.GLES20.*;


public class ShaderUtils {

    public static int createProgram(int vertexShaderId, int fragmentShaderId) {

        final int programId = glCreateProgram();
        if (programId == 0) {
            return 0;
        }

        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        glLinkProgram(programId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            glDeleteProgram(programId);
            return 0;
        }
        return programId;

    }

    static int createShader(Context context, int type, int shaderRawId) {
        String shaderText = FileUtils
                .readTextFromRaw(context, shaderRawId);
        return ShaderUtils.createShader(type, shaderText);
    }

    static int createShader(int type, String shaderText) {
        final int shaderId = glCreateShader(type);
        if (shaderId == 0) {
            return 0;
        }
        glShaderSource(shaderId, shaderText);
        glCompileShader(shaderId);
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderId);
            return 0;
        }
        return shaderId;
    }


}
