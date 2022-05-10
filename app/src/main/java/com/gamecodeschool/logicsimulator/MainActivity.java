//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
// This code was created by modifying Doctor Posnett's SubHunter code                             //
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//


package com.gamecodeschool.logicsimulator;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.ImageView;




/*
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class ClickEvent{
    public AbstractGridCell selected, previousSelection;

    public ClickEvent(ClickEvent clickEvent, AbstractGridCell clicked){
        determineEventType(clicked);
        previousSelection = clickEvent.selected;
    }

    public ClickEvent(){
        InitializeSelections();
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void InitializeSelections(){
        selected = null;
        previousSelection = null;
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void determineEventType(AbstractGridCell clicked){
        if(clicked instanceof EmptyGridCell){
            clicked.
        }else if(clicked instanceof LogicIcon){
            setSelected(clicked);
        }
        //instandof
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void setSelected(AbstractGridCell clicked){
        if(selected != clicked) {
            selected = clicked;
        } else{ selected = clicked; }
    }
}

 */

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public class MainActivity extends Activity {
    LogicSimulator game;
    Display display;
    Point size;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenRes();
        game = new LogicSimulator(this, size);
        setContentView(game.gameView);

        game.setContext(this);
        game.Load("Quick Save");
    }

    // Get the current device's screen resolution
    void getScreenRes(){
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }


    public boolean onTouchEvent(MotionEvent motionEvent){
        if((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP){
            game.touchGrid(motionEvent.getX(),motionEvent.getY());
        }
            return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        game.Save("Quick Save");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        game.Load("Quick Save");
    }

}
