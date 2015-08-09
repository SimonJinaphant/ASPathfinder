package com.symonjin.aspathfinder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class PathfinderDisplay extends SurfaceView implements Runnable {
    volatile boolean running = false;
    SurfaceHolder holder;
    Thread renderThread = null;

    private GridMap map;
    private Paint tileBorder;

    public PathfinderDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();

        tileBorder = new Paint();
        tileBorder.setStyle(Paint.Style.STROKE);
        tileBorder.setColor(Color.parseColor("#e0e0e0"));
        tileBorder.setStrokeWidth(3);
        map = new GridMap(12,17);
    }


    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    @Override
    public void run() {
        while(running) {
            if(!holder.getSurface().isValid()) continue;

            Canvas canvas = holder.lockCanvas();


            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++){
                    canvas.drawRect(x*90,y*90,x*90+90,y*90+90, map.getTileAt(x, y).draw());
                    canvas.drawRect(x*90,y*90,x*90+90,y*90+90, tileBorder);
                }
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX()/90;
        int y = (int)event.getY()/90;

        map.toggleWall(x, y);
        return super.onTouchEvent(event);
    }

    public void reset(){
        map.reset();
        map.generatePath();
    }

    public void pause() {
        running = false;
        boolean retry = true;
        while(retry) {
            try {
                renderThread.join();
                retry = false;
            } catch( InterruptedException e) {
                //retry
            }
        }
    }

}
