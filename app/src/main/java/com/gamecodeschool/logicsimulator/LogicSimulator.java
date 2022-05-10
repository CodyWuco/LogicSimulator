package com.gamecodeschool.logicsimulator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.widget.ImageView;

class LogicSimulator{
    // Here are all the objects(instances)
    // of classes that we need to do some drawing
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paint;
    Grid grid;
    Point size;

    public LogicSimulator(Context context, Point size){
        initCanvas(context, size);
        initGrid(size);

        this.size = size;
        newGame();
        draw();
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    void initCanvas(Context context, Point size){
        // Initialize all the objects ready for drawing
        blankBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        paint = new Paint();
        gameView = new ImageView(context);
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    void initGrid(Point size){grid = new Grid(size.x, size.y);grid = new Grid(size.x, size.y);}

    //``````````````````````````````````````````````````````````````````````````````````````````````
    void newGame(){
        grid.drawHud();
        draw();
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    void draw(){
        gameView.setImageBitmap(blankBitmap);
        grid.drawGrid(canvas, paint);
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    void touchGrid(float touchX, float touchY){
        grid.touchGrid(touchX, touchY);
        draw();
    }

    void setContext(Context context)        { grid.setContext(context);}
    void Save(String fileName)              { grid.Save(fileName);}
    void Load(String fileName)              { grid.Load(fileName); draw();}
}
