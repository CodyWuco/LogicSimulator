package com.gamecodeschool.logicsimulator;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.io.Serializable;

abstract class AbstractGridCell implements Serializable {
    private int x,y,w,h;

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public AbstractGridCell(int x, int y, int w, int h){this.x=x; this.y=y; this.w=w; this.h=h;}
    public AbstractGridCell(AbstractGridCell myCell){this(myCell.x, myCell.y, myCell.w, myCell.h);}

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void drawGrid(Canvas canvas, Paint paint, int fillColor){
        //Here you draw the grid with the known size given in x,y,w,h
        // Draw the player's shot
        fillRect(canvas, paint, fillColor, 3);

        // Change the paint color to black
        paint.setColor(Color.BLACK);
        canvas.drawLine(x, y, x + h, y, paint);
        canvas.drawLine(x, y, x, y + h, paint);
        drawText(canvas,paint, "", "");
    }
    void drawText(Canvas canvas, Paint paint, String strLine1, String strLine2){
        // Re-size the text appropriate for the
        // score and distance text
        paint.setTextSize(40);
        canvas.drawText(strLine1, (float)x , (float)y +60, paint);
        canvas.drawText(strLine2, (float)x , (float)y +120, paint);
    }

    void fillRect(Canvas canvas, Paint paint, int fillColor, int size){
        paint.setColor(fillColor);
        canvas.drawRect(x, y, x + w*size, y + h*size, paint );
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void drawGrid(Canvas canvas, Paint paint)    {drawGrid(canvas, paint, Color.WHITE);}

    public void setLocation(int x, int y, int h, int w){ this.x = x; this.y = y;
        this.h = h;this.w = w; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getH() { return h; }
    public int getW() { return w; }

    public AbstractGridCell selectObject()              {return this;}
    public AbstractGridCell clearShot()                 {return this;}
    public AbstractGridCell changeCellType(AbstractGridCell myCell)
    {return this;}
}
