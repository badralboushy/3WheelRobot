package com.example.a3wheeledrobot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
// , View.OnTouchListener
public class JoystickView extends SurfaceView implements SurfaceHolder.Callback ,View.OnTouchListener {
    private float centerX ;
    private float centerY;
    private float baseRadius ;
    private float hatRadius ;
    private JoystickListener joystickCallback;
    private final int RATIO = 10;
    private static boolean move_joystick_flage= false;
    private static boolean rotate_joystick_flage=false ;
    private static float movement_x=0 ;
    private static float movement_y =0;
    private static float movement_w=0;


    public JoystickView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if ( context instanceof JoystickListener){
            joystickCallback = (JoystickListener)context ;
        }
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if ( context instanceof JoystickListener){
            joystickCallback = (JoystickListener)context ;
        }

    }

    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
        setOnTouchListener(this);
        if ( context instanceof JoystickListener){
            joystickCallback = (JoystickListener)context ;
        }

    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        setupDimensions();
        drawJoystick(centerX,centerY);


    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    public void setupDimensions(){
        centerX = getWidth()/2;
        centerY = getHeight()/2 ;
        baseRadius = Math.min(getWidth(),getHeight())/3 ;
        hatRadius = Math.min(getWidth(),getHeight())/7 ;

    }

    private void drawJoystick(float newX, float newY){
        if ( getHolder().getSurface().isValid()){
            Canvas myCanvas = this.getHolder().lockCanvas();
            Paint colors = new Paint();
            // clear the canvas
            ;
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            // setARGB(int alpha, int red, int green, int blue);
            myCanvas.drawARGB(255,144,173,198);
            float hypotenuse = (float) Math.sqrt(Math.pow(newX - centerX, 2) + Math.pow(newY - centerY, 2));
            float sin = (newY - centerY) / hypotenuse; //sin = o/h

            float cos = (newX - centerX) / hypotenuse; //cos = a/h


            // draw the base
            colors.setARGB( 200,51,54,82);
            myCanvas.drawCircle(centerX,centerY,baseRadius,colors);
            for(int i = 1; i <= baseRadius / RATIO; i++){
                colors.setARGB((int) (255*i*RATIO/baseRadius),250,208,44);
                myCanvas.drawCircle(centerX,centerY,baseRadius-(i*RATIO/baseRadius)*(baseRadius-hatRadius),colors);

            }
//            colors.setARGB( 200,51,54,82);
//            myCanvas.drawCircle(centerX,centerY,baseRadius - baseRadius*2/100 ,colors);
            for(int i = 1; i <= baseRadius / RATIO; i++) {
                colors.setARGB(255/i, 51,54,82);

                myCanvas.drawCircle(newX - cos * hypotenuse * (RATIO/baseRadius) * i, newY - sin * hypotenuse * (RATIO/baseRadius) * i, i * (hatRadius * RATIO / baseRadius), colors);
            }
            // draw the hat

            for(int i = 1; i <= hatRadius / RATIO; i++) {
                colors.setARGB(255, (int) (i * (250 * RATIO/hatRadius)), (int) (i * (208 * RATIO/hatRadius)), 44);
                myCanvas.drawCircle(newX, newY, hatRadius - (float) i * (RATIO) / 3
                        , colors);
            }
            getHolder().unlockCanvasAndPost(myCanvas);

        }


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (move_joystick_flage && event.getAction()!= event.ACTION_UP && rotate_joystick_flage && !MainActivity.isConfiguration2w()){

            if( v.equals(v.findViewById(R.id.move_joystick))) {
                float displacement = (float) Math.sqrt(Math.pow(event.getX() - centerX, 2) + Math.pow(event.getY() - centerY, 2));
                if (displacement < baseRadius) {
                    drawJoystick(event.getX(), event.getY());
                    movement_x = (event.getX() - centerX) / baseRadius;
                    movement_y=(centerY - event.getY()) / baseRadius;


                } else {

                    float ratio = baseRadius / displacement;
                    float constrainedX = centerX + (event.getX() - centerX) * ratio;

                    float constrainedY = centerY + (event.getY() - centerY) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    movement_x = (constrainedX - centerX) / baseRadius ;
                    movement_y =(centerY - constrainedY) / baseRadius ;

                }



            }
            if( v.equals(v.findViewById(R.id.rotate_joyStick))){
                if ( Math.abs(event.getX() - centerX)>baseRadius){
                    if (event.getX()>centerX){
                        drawJoystick(centerX + baseRadius,centerY);
                        movement_w = 1 ;
                    }
                    else{
                        drawJoystick(centerX-baseRadius,centerY);
                        movement_w=-1 ;
                    }
                }else{
                    drawJoystick(event.getX(),centerY);
                    movement_w = (event.getX()-centerX)/baseRadius;

                }


            }

            joystickCallback.onJoystickMoved(movement_x
                    ,movement_y ,movement_w, 1000);

            Log.d("ONTOUCH", "BOTH : X : " + movement_x + " : Y : " + movement_y + " : W : " + movement_w);

        }

        else if( v.equals(v.findViewById(R.id.move_joystick))) {
            if (event.getAction() != event.ACTION_UP) { // if the action is not lifting the hand from the joystick
                move_joystick_flage = true ;

                float displacement = (float) Math.sqrt(Math.pow(event.getX() - centerX, 2) + Math.pow(event.getY() - centerY, 2));
                if (displacement < baseRadius) {
                    drawJoystick(event.getX(), event.getY());
                    movement_x = (event.getX() - centerX) / baseRadius;
                    movement_y=(centerY - event.getY()) / baseRadius;
                    joystickCallback.onJoystickMoved(movement_x
                            ,movement_y,0, getId());
                    Log.d("ONTOUCH", "MOVE : X : " + movement_x + " : Y : " + movement_y + " : W : " + movement_w);
                } else {

                    float ratio = baseRadius / displacement;
                    float constrainedX = centerX + (event.getX() - centerX) * ratio;

                    float constrainedY = centerY + (event.getY() - centerY) * ratio;
                    drawJoystick(constrainedX, constrainedY);
                    movement_x = (constrainedX - centerX) / baseRadius ;
                    movement_y =(centerY - constrainedY) / baseRadius ;
                    joystickCallback.onJoystickMoved(movement_x
                            ,movement_y ,0, v.getId());
                    Log.d("ONTOUCH", "MOVE : X : " + movement_x + " : Y : " + movement_y + " : W : " + movement_w);
                }
            } else {
                drawJoystick(centerX, centerY); // if the user lift his hand ..it would comback the center
                move_joystick_flage = false;
                movement_x = 0 ;
                movement_y = 0 ;
                Log.d("ONTOUCH", "MOVE : X : " + movement_x + " : Y : " + movement_y + " : W : " + movement_w);


            }
        }else if( v.equals(v.findViewById(R.id.rotate_joyStick)) && !MainActivity.isConfiguration2w()) {
            if (event.getAction() != event.ACTION_UP) {
                rotate_joystick_flage = true;
                if (Math.abs(event.getX() - centerX) > baseRadius) {
                    if (event.getX() > centerX) {
                        drawJoystick(centerX + baseRadius, centerY);
                        movement_w = 1;
                        joystickCallback.onJoystickMoved(0, 0, movement_w, v.getId());
                        Log.d("ONTOUCH", "rotate : X : " + movement_x + " : Y : " + movement_y + " : W : " + movement_w);
                    } else {
                        drawJoystick(centerX - baseRadius, centerY);
                        movement_w = -1;
                        joystickCallback.onJoystickMoved(0, 0, movement_w, v.getId());
                        Log.d("ONTOUCH", "rotate : X : " + movement_x + " : Y : " + movement_y + " : W : " + movement_w);
                    }
                }else {
                drawJoystick(event.getX(), centerY);
                movement_w = (event.getX() - centerX) / baseRadius;
                joystickCallback.onJoystickMoved(0, 0, movement_w, v.getId());
                Log.d("ONTOUCH", "rotate : X : " + movement_x + " : Y : " + movement_y + " : W : " + movement_w);
            }

        }else {
            drawJoystick(centerX, centerY); // if the user lift his hand ..it would comback the center
            rotate_joystick_flage=false ;
              movement_w=0;
             Log.d("ONTOUCH", "rotate : X : " + movement_x + " : Y : " + movement_y + " : W : " + movement_w);
        }
        }


            return true;
    }
    public interface JoystickListener    {
        void onJoystickMoved(float xPercent ,float yPercent ,float wPercent, int sourse);
    }


}
