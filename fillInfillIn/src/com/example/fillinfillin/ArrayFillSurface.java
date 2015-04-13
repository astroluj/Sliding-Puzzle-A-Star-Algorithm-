package com.example.fillinfillin;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

 
public class ArrayFillSurface extends GLSurfaceView {

    ArrayFillRenderer renderer ;

    // 이전 터치 지점의 x, y 좌표
    private float preX, preY;
    
 // 생성자
    public ArrayFillSurface(Context context) {
    	super(context);
    		
        renderer = new ArrayFillRenderer(context.getApplicationContext());
        setRenderer(renderer); 
    }
     
    public boolean onTouchEvent(MotionEvent event) {
        // 현재 측정된 터치 지점의 x, y 좌표
        float X = event.getX();
        float Y = event.getY();
         
        switch (event.getAction()) {
        case MotionEvent.ACTION_MOVE:
            // 이전 측정 지점과 현재 측정 지점 사이의 거리 계산
            float dX = X - preX;
            float dY = Y - preY;
            // cube 객체의 회전 각에 적용해 준다.
            renderer.cube.angleY += (dX * 0.5f);
            renderer.cube.angleX += (dY * 0.5f);
             
            break;
        }
         
        // 이전 터치 지점 변경
        preX = X ;
        preY = Y ;
         
        return true;
    }

}
