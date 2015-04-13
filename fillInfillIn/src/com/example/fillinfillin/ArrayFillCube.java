package com.example.fillinfillin;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
 
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.View;

public class ArrayFillCube 
{
	PlayCrazy playC ;
	MainActivity mainAct ;
	
    // 정점 배열
   /* float[] vertex = {
        -1, -1, -1,
         1, -1, -1,
         1,  1, -1,
        -1,  1, -1,
         
        -1, -1,  1,
         1, -1,  1,
         1,  1,  1,
        -1,  1,  1,
    };*/
	 float vertex[] = {
				//Vertices according to faces
	    		-1.0f, -1.0f, 1.0f, //Vertex 0
	    		1.0f, -1.0f, 1.0f,  //v1
	    		-1.0f, 1.0f, 1.0f,  //v2
	    		1.0f, 1.0f, 1.0f,   //v3
	    		
	    		1.0f, -1.0f, 1.0f,	//...
	    		1.0f, -1.0f, -1.0f,    		
	    		1.0f, 1.0f, 1.0f,
	    		1.0f, 1.0f, -1.0f,
	    		
	    		1.0f, -1.0f, -1.0f,
	    		-1.0f, -1.0f, -1.0f,    		
	    		1.0f, 1.0f, -1.0f,
	    		-1.0f, 1.0f, -1.0f,
	    		
	    		-1.0f, -1.0f, -1.0f,
	    		-1.0f, -1.0f, 1.0f,    		
	    		-1.0f, 1.0f, -1.0f,
	    		-1.0f, 1.0f, 1.0f,
	    		
	    		-1.0f, -1.0f, -1.0f,
	    		1.0f, -1.0f, -1.0f,    		
	    		-1.0f, -1.0f, 1.0f,
	    		1.0f, -1.0f, 1.0f,
	    		
	    		-1.0f, 1.0f, 1.0f,
	    		1.0f, 1.0f, 1.0f,    		
	    		-1.0f, 1.0f, -1.0f,
	    		1.0f, 1.0f, -1.0f};
     
    // 정점을 연결할 인덱스 배열
    /* byte[] index = {
        0, 1, 2, 2, 3, 0,
        4, 5, 6, 6, 7, 4,
        0, 3, 7, 7, 4, 0,
        1, 2, 6, 6, 5, 1,
        0, 4, 5, 5, 1, 0,
        3, 7, 6, 6, 2, 3,
    };*/
    byte index[] = {
			//Faces definition
    		0,1,3, 0,3,2, 			//Face front
    		4,5,7, 4,7,6, 			//Face right
    		8,9,11, 8,11,10, 		//... 
    		12,13,15, 12,15,14, 	
    		16,17,19, 16,19,18, 	
    		20,21,23, 20,23,22};
     
    float[] color = {
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f, 1.0f};
     
    float texture[] = {    		
    		//Mapping coordinates for the vertices
    		0.0f, 0.0f,
    		0.0f, 1.0f,
    		1.0f, 0.0f,
    		1.0f, 1.0f,
    		
    		0.0f, 0.0f,
    		0.0f, 1.0f,
    		1.0f, 0.0f,
    		1.0f, 1.0f,
    		
    		0.0f, 0.0f,
    		0.0f, 1.0f,
    		1.0f, 0.0f,
    		1.0f, 1.0f,

    		0.0f, 0.0f,
    		0.0f, 1.0f,
    		1.0f, 0.0f,
    		1.0f, 1.0f,
    		
    		0.0f, 0.0f,
    		0.0f, 1.0f,
    		1.0f, 0.0f,
    		1.0f, 1.0f,
    		
    		0.0f, 0.0f,
    		0.0f, 1.0f,
    		1.0f, 0.0f,
    		1.0f, 1.0f};
    // 버퍼 클래스
    FloatBuffer vertexBuffer;
    ByteBuffer indexBuffer;
    FloatBuffer colorBuffer;
    FloatBuffer imgBuffer ;
     
    // X 축 각도
    protected static float angleX = 0;
    // Y 축 각도 
    protected static float angleY = 0;
    
    private final int WIDTH =mainAct.FILL_WIDTH, HEIGHT =mainAct.FILL_HEIGHT,
			SHAPE =mainAct.FILL_SHAPE_COUNT, SCALE_W =mainAct.scaleWidth, SCALE_H =mainAct.scaleHeight ;
	private BitmapFactory.Options resizeOption ;
    private int[] textures = new int[6];
    private int width, height ;
    
    // 생성자
    public ArrayFillCube(Context context) {
    	
        vertexBuffer = MyUtil.getFloatBuffer(vertex);
        indexBuffer = MyUtil.getByteBuffer(index);
        colorBuffer = MyUtil.getFloatBuffer(color);
        imgBuffer =MyUtil.getFloatBuffer(texture) ;
        
        width =SCALE_W /WIDTH ;
        height =SCALE_W /HEIGHT ;
        resizeOption =new Options () ;
        resizeOption.inSampleSize =2 ;
        
    	playC.setImg =new Bitmap[6] ;
    	playC.bitmapId =new int[6][HEIGHT][WIDTH] ;	
		playC.img =new Bitmap[SHAPE] ;
		
		for (int i =0 ; i < SHAPE ; i++) {
			playC.img[i] = BitmapFactory.decodeResource(context.getResources (),
					playC.imgId[i], resizeOption) ;
				playC.img[i] =Bitmap.createScaledBitmap(playC.img[i], width, height, true) ;
		}
		playC.pattern =BitmapFactory.decodeResource(context.getResources(), R.drawable.pattern, resizeOption) ;
		playC.pattern =Bitmap.createScaledBitmap(playC.pattern, SCALE_W, SCALE_W, true) ;
    }
     
    
    // 그리기
    public void draw(GL10 gl, Context context) 
    {    	    	
    	if (playC.resetFlag == true) {
    		playC.loaderFlag =false ;
    		playC.resetFlag =false ;
    		setRandomImg(context) ;
    		
    		angleX =0 ; angleY =0 ;
    		playC.setFlag =true ;
    	}
    	
    	if (playC.setFlag == true) {
    		onDrawed (gl) ;	
    		playC.setFlag =false ;
    		playC.loaderFlag =true ;
    	}

        // 그리는 방향
        gl.glFrontFace(GL10.GL_CCW);
        // 정점 배열을 지정
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 색상 배열 지정
      //gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        // 텍스트 배열지정
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY) ;
        // 정점 세팅
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        // 색상 세팅
        //gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
        // 텍스쳐 세팅
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, imgBuffer) ;
        // 축 회전 
        gl.glRotatef(angleX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(angleY, 0.0f, 1.0f, 0.0f);
        gl.glScalef(1.0f,  1.0f,  1.0f) ;
        
        for (int i =0 ; i < 6 ; i++) {
        	// 바인딩
        	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]) ;
        	// 그리기 
        	gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_BYTE, indexBuffer.position(6*i));
      
        }
        //gl.glDrawElements(GL10.GL_LINE_LOOP, index.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
        // 모드 해제
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        //gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_COORD_ARRAY) ;
    }
    
    public void loadGLTexture (GL10 gl, Context context) {		
    	//Generate one texture pointer...
    	gl.glGenTextures(6, textures, 0);	// 처음인자는 텍스쳐의 갯수
    	// 텍스쳐의 갯수
    	//GLES20.glGenTextures (textures.length, textures, 0) ;
    	
    	if (!playC.loaderFlag) {
    		setRandomImg(context) ;
    		angleX =0 ; angleY =0 ;
    	}
    	onDrawed(gl) ;
    	playC.loaderFlag =true ;
    }
    
    protected void onDrawed (GL10 gl)
    {   	
    	Canvas c =new Canvas () ;
   		for (int i =0 ; i < 6 ; i++) {
			c.setBitmap(playC.setImg[i]) ;
			
			for (int j =0 ; j < HEIGHT ; j++) {
				for (int k =0 ; k < WIDTH ; k++) {
					// createScaledBitmap사용시 인스턴스에 대입으로 사용하자
					for (int q =0 ; q < SHAPE ; q++) {
						if (playC.bitmapId[i][j][k] == playC.imgId[q]) {
							c.drawBitmap (playC.img[q],
									k *playC.setImg[i].getWidth() /WIDTH , 
									j *playC.setImg[i].getHeight() /HEIGHT, null) ; // 들어갈 좌표를 입력하여 그림
							break ;
						}
					}
				}
			}
			c.drawBitmap(playC.pattern, 0,  0, null) ;
			Matrix m =new Matrix () ;
			m.postRotate(90) ;
			playC.setImg[i] =Bitmap.createBitmap(playC.setImg[i], 0, 0,
					 playC.setImg[i].getWidth(), playC.setImg[i].getHeight(), m, true) ;
	    }
   		for (int i =0 ; i < 6 ; i++) {
    		//...and bind it to our array
    		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);
		
    		//Create Nearest Filtered Texture
    		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
    		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

    		//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
    		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
    		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		
    		//Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
    		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, playC.setImg[i], 0);
		}
    }
    
    public void setRandomImg (Context context)
    {	
    	playC.coord =new boolean[6][HEIGHT][WIDTH] ;
    	playC.dataList =new ArrayList<Data> () ;
		Data data =new Data () ;
		playC.dataList.add(data) ;
		playC.checkCnt =1 ;
		
    	for (int i =0 ; i < 6 ; i++) {
			for (int j =0 ; j< HEIGHT ; j++) {
				for (int k =0 ; k < WIDTH ; k++) {
				
					// 이미지를 랜덤하게 넣음
					playC.bitmapId[i][j][k] =playC.imgId[(int)(Math.random () *SHAPE)] ;
				}
			}
			playC.setImg[i] =Bitmap.createBitmap(SCALE_W, SCALE_W, Bitmap.Config.ARGB_8888) ;
		}
    }
}
