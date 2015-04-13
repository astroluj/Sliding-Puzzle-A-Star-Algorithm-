package com.example.fillinfillin;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
 
public class MyUtil {
    // byte 형 배열을 ByteBuffer 객체로 만들어주는 메소드
    public static ByteBuffer getByteBuffer(byte[] array) {
        // ByteBuffer 객체 생성 
        ByteBuffer buffer = ByteBuffer.allocateDirect(array.length);
        // 인자값으로 들어온 배열의 값을 ByteBuffer에 넣어준다.
        buffer.put(array);
        // 배열에 접근 할 때 사용하는 포인터의 위치를 제일 처음 위치로 위치시킨다.
        buffer.position(0);
         
        return buffer;
    }
     
    // float 형 배열을 FloatBuffer 객체로 만들어주는 메서드
    public static FloatBuffer getFloatBuffer(float[] array) {
        // ByteBuffer 생성 float 가 4바이트 이므로 *4 를 해
        ByteBuffer tempBuffer = ByteBuffer.allocateDirect(array.length *4);
        // 데이터를 담을때 담는 데이터의 Ordering 설정 
        tempBuffer.order(ByteOrder.nativeOrder());
        // ByteBuffer 를 FloatBuffer 로 변
        FloatBuffer buffer = tempBuffer.asFloatBuffer();
        // 데이터 셋
        buffer.put(array);
        // 위치 초기화 
        buffer.position(0);
         
        return buffer;
    }
     
    // int 형 배열을 IntBuffer 로 만들어주는 메서드
    public static IntBuffer getIntBuffer(int[] array) {
        ByteBuffer tempBuffer = ByteBuffer.allocateDirect(array.length * 4);
        tempBuffer.order(ByteOrder.nativeOrder());
        IntBuffer buffer = tempBuffer.asIntBuffer();
        buffer.put(array);
        buffer.position();
        return buffer;
    }
     
    // short 형 배열을 ShortBuffer로 만들어주는 메서드
    public static ShortBuffer getShortBuffer(short[] array) {
        ByteBuffer tempBuffer = ByteBuffer.allocateDirect(array.length *2);
        tempBuffer.order(ByteOrder.nativeOrder());
        ShortBuffer buffer = tempBuffer.asShortBuffer();
        buffer.put(array);
        buffer.position(0);
        return buffer;
    }
}

