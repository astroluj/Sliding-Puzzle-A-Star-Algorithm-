package com.example.fillinfillin;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
 
import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;
 
public class ArrayFillRenderer implements Renderer{
     
    MainActivity mainAct ;
    PlayCrazy playC ;
    ArrayFillCube cube;
    
    private int angle = 0;
    private Context context ;
    
    public  ArrayFillRenderer (Context context)
    {	
    	this.context =context ;
    	cube =new ArrayFillCube (context) ;	
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
    	// 초기화
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();	// 반환행렬
        gl.glTranslatef(0.0f, 0.0f, -mainAct.scaleWidth /90.0f);	// 크기
        gl.glRotatef(45.0f, 1, 1, 0);	// 초기에 보여지는 각도
        cube.draw(gl, context);
    }
 
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);	// 화면에 보여지는 크기
        gl.glMatrixMode(GL10.GL_PROJECTION);	// 투영
        gl.glLoadIdentity();
        // 원근 법
        GLU.gluPerspective(gl, 45.0f, (float)width/height, 1.0f, 30.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);	// 이동, 회전 계산행렬
        gl.glLoadIdentity();	// 행렬 초기화
    }
 
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	cube.loadGLTexture(gl, context) ;
    	
        // 만들어질 모델을 부드럽게
        gl.glShadeModel(GL10.GL_SMOOTH);
        // 배경색 설정
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // 깊이 버퍼 초기화
        gl.glClearDepthf(1.0f);
        // 투영을 위한 길이 버퍼설정
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // 투영을 위한 텍스쳐 2D
        gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
        // 보이지않는 시야의 작업을 안함
        gl.glDepthFunc(GL10.GL_LEQUAL);
        // 투영의 품질을 최상위
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
     } 
   }
