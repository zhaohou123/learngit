package com.example.abc.myapplication;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.graphics.Paint;
import android.graphics.Canvas;

@SuppressLint("AppCompatCustomView")
public class MyButton extends Button {

    private Paint bottomPaint, colorPaint;
    private long downTime = 0;
    private int eventX, eventY;
    private int viewWidth, viewHeight;
    private int maxRadio;
    private boolean isPushButton;
    private static final int INVALIDATE_DURATION = 15;
    private static int TAP_TIMEOUT;
    private static int DIFFUSE_GAP = 10;
    private int shaderRadio;
    private int pointX, pointY;

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint(){
        colorPaint = new Paint();
        bottomPaint = new Paint();
        colorPaint.setColor(getResources().getColor(R.color.reveal_color));
        bottomPaint.setColor(getResources().getColor(R.color.colorAccent));
    }

    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(downTime == 0)
                    downTime = SystemClock.elapsedRealtime();
                eventX = (int) event.getX();
                eventY = (int) event.getY();

                countMaxRadio();
                isPushButton = true;
                postInvalidateDelayed(INVALIDATE_DURATION);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(SystemClock.elapsedRealtime() - downTime < TAP_TIMEOUT){
                    DIFFUSE_GAP =  30;
                    postInvalidate();
                }else {
                    clearData();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    protected void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);

        if(!isPushButton)
            return;
        canvas.drawRect(pointX, pointY, pointX+viewWidth, pointY+viewHeight, bottomPaint);
        canvas.save();

        canvas.clipRect(pointX, pointY, pointX+viewWidth, pointY+viewHeight);
        canvas.drawCircle(eventX,eventY,shaderRadio,colorPaint);
        canvas.restore();

        if (shaderRadio < maxRadio){
            postInvalidateDelayed(INVALIDATE_DURATION,pointX,pointY,pointX+viewWidth,pointY+viewHeight);
            shaderRadio += DIFFUSE_GAP;

        }else{
            clearData();
        }
    }

    private void countMaxRadio() {
        if(viewWidth > viewHeight){
            if(eventX < viewWidth / 2){
                maxRadio = viewWidth - eventX;
            }else {
                maxRadio = viewWidth / 2 + eventX;
            }
        }else{
            if(eventY < viewHeight / 2){
                maxRadio = viewHeight - eventY;
            }else{
                maxRadio = viewHeight / 2 + eventY;
            }
        }
    }

    private void clearData(){
        downTime = 0;
        DIFFUSE_GAP = 10;
        isPushButton = false;
        shaderRadio = 0;
        postInvalidate();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = w;
        this.viewHeight = h;
    }
}
