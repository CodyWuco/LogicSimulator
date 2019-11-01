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


import java.io.*;
import java.util.Random;
import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// AbstractGridCell represents a single grid cell in the Logic Simulator. It is the bass class for
// each of the different stat classes that each grid cell can represent.
//
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

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class EmptyGridCell extends AbstractGridCell{
    public EmptyGridCell(int x, int y,int w, int h)     {super(x, y, w, h);}
    public EmptyGridCell(AbstractGridCell myCell)       {super(myCell);}
    public AbstractGridCell selectObject()              {return this;}
}


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
abstract class LogicNode extends AbstractGridCell{
    LogicNode inputA,inputB;
    abstract boolean eval();
    public LogicNode(AbstractGridCell myCell)           {super((myCell)); this.inputA = null; this.inputB = null;}
    public void setInput(LogicNode n){if(inputA == null){setA(n);}else if(inputB == null){setB(n);}}
    public void setA(LogicNode n)                       {this.inputA = n;}
    public void setB(LogicNode n)                       {this.inputB = n;}
    public void clearInput()                            {this.inputA = null; this.inputB = null;}
    public void drawWires(Canvas canvas, Paint paint){

        if (inputA != null){ drawWire(canvas, paint, inputA); }
        if (inputB != null){ drawWire(canvas, paint, inputB);}
    }
    public void drawWire(Canvas canvas, Paint paint, LogicNode input) {
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
        canvas.drawLine( input.getX() +  input.getW() * 3/4,
                         input.getY() + input.getH() * 1/ 2,
                         this.getX() + this.getW() * 1/4,
                         this.getY() + this.getH()* 1/2, paint);
        paint.setStrokeWidth(1);

    }
}


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class SwitchNode extends LogicNode{
    boolean state;
    String statestr;
    public SwitchNode(AbstractGridCell myCell){
        super((myCell)); this.state = false; this.statestr = "OFF";
    }
    public void toggle(){
        this.state = !this.state;
        toggleString();
    }
    public void toggleString(){
        if(state){ statestr = "ON";}
        else{ statestr = "OFF";}
    }
    public boolean eval()                  {return state;}
    public AbstractGridCell selectObject() {toggle(); return this;}
    public AbstractGridCell clearShot()    {return new EmptyGridCell(this);}

    public void drawGrid(Canvas canvas, Paint paint) {
        super.drawGrid(canvas, paint, Color.GRAY);
        paint.setColor(Color.BLACK);
        drawText(canvas, paint, "Switch", statestr);
    }
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class AndNode extends LogicNode{
    String str;
    public AndNode(AbstractGridCell myCell){super((myCell)); str= ""; }
    public boolean eval(){
        if (inputA != null & inputB != null) {
            return inputA.eval() & inputB.eval();
        }else{ return false; }
    }
    public AbstractGridCell clearShot()                 {return new EmptyGridCell(this);}

    public void drawGrid(Canvas canvas, Paint paint) {
        super.drawGrid(canvas, paint, Color.GRAY);
        paint.setColor(Color.BLACK);
        /*
        if(inputA!=null && inputB!=null){str = "A,B";}
        else if(inputA!=null){
            if(inputA.eval()) {
                str = "1";
            }else{ str = "0"; }
        }
        else if(inputB!=null){str = "B";}
        if (eval()){ drawText(canvas, paint, "AND 1", str); }
        else{ drawText(canvas, paint, "AND 0", str);}

         */
        drawAndGate(canvas, paint);
        drawWires(canvas, paint);
    }

    @SuppressLint("NewApi")
    public void drawAndGate(Canvas canvas, Paint paint) {
        //body of the gate
        paint.setColor(Color.WHITE);
        canvas.drawArc(getX(), getY()+getH()/6,getX() + getW()*5/6,getY()+getH()*5/6, -90,180,false, paint);
        canvas.drawRect(getX()+getW()/6, getY()+getH()/6,getX() + getW()*3/6,getY()+getH()*5/6, paint);
        //input and output lines
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        canvas.drawLine(getX() + getW()*5/6,getY() +getH()/2,getX() + getW(),getY()+getH()/2,paint);
        canvas.drawLine(getX(),getY() +getH()/3,getX() + getW()*1/6,getY()+getH()/3,paint);
        canvas.drawLine(getX(),getY() +getH()*2/3,getX() + getW()*1/6,getY()+getH()*2/3,paint);
        paint.setStrokeWidth(1);
    }
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class OrNode extends LogicNode{
    String str;
    public OrNode(AbstractGridCell myCell){super((myCell)); this.inputA = null; this.inputB = null; str= "";}
    public boolean eval(){
        if (inputA != null & inputB != null) {
            return inputA.eval() | inputB.eval();
        }else{ return false; }
    }
    public AbstractGridCell clearShot()                 {return new EmptyGridCell(this);}

    public void drawGrid(Canvas canvas, Paint paint) {
        super.drawGrid(canvas, paint, Color.GRAY);
        paint.setColor(Color.BLACK);
        if(inputA!=null && inputB!=null){str = "A,B";}
        else if(inputA!=null){str = "A";}
        else if(inputB!=null){str = "B";}
        drawText(canvas, paint, "OR", "");
        drawWires(canvas, paint);
    }

    @SuppressLint("NewApi")
    public void drawAndGate(Canvas canvas, Paint paint) {
        Path path = new Path();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        path.addArc(getX() - getW()*2,getY()+getH()*1/6,getX()+getW()*5/6, getY()+getH()*2,-90, 90);
        canvas.drawPath(path,paint);


        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);

    }
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class NotNode extends LogicNode{
    public NotNode(AbstractGridCell myCell)             {super((myCell)); }
    public boolean eval()                               {return !inputA.eval();}
    public AbstractGridCell clearShot()                 {return new EmptyGridCell(this);}

    public void drawGrid(Canvas canvas, Paint paint) {
        super.drawGrid(canvas, paint, Color.GRAY);
        paint.setColor(Color.BLACK);
        drawText(canvas, paint, "NOT", "");
        drawWires(canvas, paint);
    }
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class LightNode extends LogicNode{
    boolean state;
    String statestr;
    public LightNode(AbstractGridCell myCell)
        {super((myCell)); this.state = false; this.statestr = "";}

    public boolean eval()     {
        if (inputA != null){
            return inputA.eval();
        }else return false;
    }
    public void setString(){ if(state){ statestr = "ON";} else{ statestr = "OFF";} }
    public void drawGrid(Canvas canvas, Paint paint) {
        updateNode();
        super.drawGrid(canvas, paint, Color.GRAY);
        paint.setColor(Color.BLACK);
        drawText(canvas, paint, "Light", statestr);
        drawWires(canvas, paint);
    }
    public void updateNode()                            {state = eval();setString();}
    public AbstractGridCell clearShot()                 {return new EmptyGridCell(this);}
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//  used in selection to determine action type
abstract class LogicIcon extends AbstractGridCell{
    public LogicIcon(AbstractGridCell myCell)           {super((myCell));}
}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class SwitchIcon extends LogicIcon{
    boolean isSelected;
    public SwitchIcon(AbstractGridCell myCell)          {super((myCell)); isSelected = false;}
    public void drawGrid(Canvas canvas, Paint paint){
        super.drawGrid(canvas,paint,Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "Switch", "");
    }
    public AbstractGridCell changeCellType(AbstractGridCell myCell) {return new SwitchNode(myCell);}
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class AndIcon extends LogicIcon{
    public AndIcon(AbstractGridCell myCell)             {super((myCell));}
    public void drawGrid(Canvas canvas, Paint paint) {
        super.drawGrid(canvas, paint, Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "AND", "GATE");
    }
    public AbstractGridCell changeCellType(AbstractGridCell myCell) {return new AndNode(myCell);}
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class OrIcon extends LogicIcon{
    public OrIcon(AbstractGridCell myCell)              {super((myCell));}
    public void drawGrid(Canvas canvas, Paint paint){
        super.drawGrid(canvas, paint, Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "OR", "GATE");
    }
    public AbstractGridCell changeCellType(AbstractGridCell myCell) {return new OrNode(myCell);}
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class NotIcon extends LogicIcon{
    public NotIcon(AbstractGridCell myCell)             {super((myCell));}
    public void drawGrid(Canvas canvas, Paint paint){
        super.drawGrid(canvas, paint, Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "NOT", "GATE");
    }
    public AbstractGridCell changeCellType(AbstractGridCell myCell) {return new NotNode(myCell);}
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class LightIcon extends LogicIcon{
    public LightIcon(AbstractGridCell myCell)           {super((myCell));}
    public void drawGrid(Canvas canvas, Paint paint){
        super.drawGrid(canvas, paint, Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "Light", "");
    }
    public AbstractGridCell changeCellType(AbstractGridCell myCell) {return new LightNode(myCell);}
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class DeleteIcon extends AbstractGridCell{
    public DeleteIcon(AbstractGridCell myCell)           {super(((myCell)));}
    public void drawGrid(Canvas canvas, Paint paint){
        super.drawGrid(canvas, paint, Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "Delete", "");
    }
}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class WireSourceIcon extends AbstractGridCell{
    public WireSourceIcon(AbstractGridCell myCell)           {super(((myCell)));}
    public void drawGrid(Canvas canvas, Paint paint){
        super.drawGrid(canvas, paint, Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "Wire", "Source");
    }
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class WireInputIcon extends AbstractGridCell{
    public WireInputIcon(AbstractGridCell myCell)           {super(((myCell)));}
    public void drawGrid(Canvas canvas, Paint paint){
        super.drawGrid(canvas, paint, Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "Wire", "Input");
    }
}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class ClearInputIcon extends AbstractGridCell{
    public ClearInputIcon(AbstractGridCell myCell)           {super(((myCell)));}
    public void drawGrid(Canvas canvas, Paint paint){
        super.drawGrid(canvas, paint, Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "Clear", "Input");
    }
}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class CreateSaveIcon extends AbstractGridCell{
    public CreateSaveIcon(AbstractGridCell myCell)           {super(((myCell)));}
    public void drawGrid(Canvas canvas, Paint paint){
        super.drawGrid(canvas, paint, Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "Create", "Save");
    }
}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class SavesIcon extends AbstractGridCell{
    String save;
    public SavesIcon(AbstractGridCell myCell)                {super(((myCell)));}
    public SavesIcon(AbstractGridCell myCell, String save)   {super(((myCell)));
                                                              this.save = save;}
    public void drawGrid(Canvas canvas, Paint paint){
        super.drawGrid(canvas, paint, Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "Save", save);
    }
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class ClearScreenIcon extends AbstractGridCell{
    public ClearScreenIcon(AbstractGridCell myCell)          {super(((myCell)));}
    public void drawGrid(Canvas canvas, Paint paint){
        super.drawGrid(canvas, paint, Color.DKGRAY);
        paint.setColor(Color.GREEN);
        drawText(canvas, paint, "Clear", "Screen");
    }
}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class Grid implements Serializable{
    int gridWidth, gridHeight, blockSize;
    Random rand;
    int gridSize;
    AbstractGridCell selected, previousSelection, wireSource;
    Context context;

    private class GridPosition{
        int x,y;
        public GridPosition(int x, int y)               {this.x=x; this.y=y;}
    }


    public Vector<AbstractGridCell> gridCells;

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public Grid(int x, int y){
        if (x > y){ gridSize = 6; }
        else { gridSize = 10; }

        blockSize = y / gridSize;
        gridWidth =  x / blockSize;
        gridHeight = y / blockSize;
        rand = new Random();
        selected = null;
        previousSelection = null;
        wireSource = null;
        reset();
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void reset(){
        gridCells = new Vector<>(gridHeight*gridWidth);

        for(int h=0; h<gridWidth*gridHeight; h++)
            for(int v=0; v<gridHeight; v++)
                gridCells.add((new EmptyGridCell(h*blockSize,v*blockSize, blockSize,
                        blockSize)));
            setupGrid();
    }

    private void setupGrid(){
        for(int h=0; h<gridWidth; h++)
            for(int v=0; v<gridHeight; v++)
                gridCells.get(gridSize*h+v).setLocation(h*blockSize,v*blockSize,
                                                            blockSize, blockSize);
    }

    public void setContext(Context context){this.context = context;}

    public void Save(String fileName) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(gridCells);
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void Load(String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            gridCells = (Vector<AbstractGridCell>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return;
        }
        setupGrid();
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void drawHud(){
        int row = 0, column = 0;
        addIconToHud(new SwitchIcon(gridCells.get(iconLocation(row,column))),row,column);
        row = 1; column = 0;
        addIconToHud(new AndIcon(gridCells.get(iconLocation(row,column))),row,column);
        row = 2 ; column = 0;
        addIconToHud(new OrIcon(gridCells.get(iconLocation(row,column))),row,column);
        row = 3 ; column = 0;
        addIconToHud(new NotIcon(gridCells.get(iconLocation(row,column))),row,column);
        row = 4 ; column = 0;
        addIconToHud(new LightIcon(gridCells.get(iconLocation(row,column))),row,column);
        row = 5 ; column = 0;
        addIconToHud(new DeleteIcon(gridCells.get(iconLocation(row,column))),row,column);
        row = 0 ; column = 1;
        addIconToHud(new WireSourceIcon(gridCells.get(iconLocation(row,column))),row,column);
        row = 1 ; column = 1;
        addIconToHud(new WireInputIcon(gridCells.get(iconLocation(row,column))),row,column);
        row = 2 ; column = 1;
        addIconToHud(new ClearInputIcon(gridCells.get(iconLocation(row,column))),row,column);
        row = 3 ; column = 1;
        addIconToHud(new ClearScreenIcon(gridCells.get(iconLocation(row,column))),row,column);
        row = 4 ; column = 1;
        addIconToHud(new CreateSaveIcon(gridCells.get(iconLocation(row,column))),row,column);
        row = 5 ; column = 1;
        addIconToHud(new SavesIcon(gridCells.get(iconLocation(row,column)), "A"),row,column);
        row = 0 ; column = 2;
        addIconToHud(new SavesIcon(gridCells.get(iconLocation(row,column)), "B"),row,column);
        row = 1 ; column = 2;
        addIconToHud(new SavesIcon(gridCells.get(iconLocation(row,column)), "C"),row,column);
    }

    public void addIconToHud(AbstractGridCell Icon, int row, int column){
        gridCells.set(iconLocation(row,column), Icon);
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public int gridColumn(int column)            { return 6 * (column );}

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public int gridRow(int row)                  { return (row);}

    public int iconLocation(int row, int column){return gridRow(row) + gridColumn(column);}

    //``````````````````````````````````````````````````````````````````````````````````````````````
    private int gridCellN(GridPosition p){return (gridHeight*p.x+p.y);}

    //``````````````````````````````````````````````````````````````````````````````````````````````
    private int distanceToClosestFrom(GridPosition shotP){
        int subD=gridWidth*gridHeight;

        for(int i=0; i<gridCells.size(); i++){
            AbstractGridCell agc = gridCells.get(1);
            if(agc instanceof LogicNode){
                // set subD to existing min, or distance from agc to shotP
            }
        }
        return subD;
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public int touchGrid(float touchX, float touchY){
        GridPosition tP = getGridTouchPosition(touchX, touchY);
        int currGridNum = gridCellN(tP);
        AbstractGridCell clickedCell = onClick(currGridNum);


        CellClickEvent(clickedCell, currGridNum);

        return distanceToClosestFrom(tP);
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public GridPosition getGridTouchPosition(float touchX, float touchY){
        return new GridPosition((int)touchX/ blockSize, (int)touchY/ blockSize);
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public AbstractGridCell onClick(int cellNumber){ return gridCells.get(cellNumber); }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void CellClickEvent(AbstractGridCell clickedCell, int currGridNum){
        if (isEmptyCell(clickedCell)){ doEmptyCellEvent(clickedCell, currGridNum); }
        else if (isLogicNode(clickedCell)){ doLogicNodeEvent(clickedCell, currGridNum);}
        else if (isSavesIcon(clickedCell)){ doSavesIconEvent(clickedCell, currGridNum);}
        else if (isClearScreenIconEvent(clickedCell)){doClearIconEvent(clickedCell, currGridNum);}
        else{ doSelectEvent(clickedCell);}
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public boolean isEmptyCell(AbstractGridCell cell){ return (cell instanceof EmptyGridCell); }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void doEmptyCellEvent(AbstractGridCell clickedCell, int currGridNum){
        if(isLogicIcon(selected)){gridCells.set(currGridNum, selected.changeCellType(clickedCell));}
        else { gridCells.set(currGridNum, clickedCell.selectObject()); }

        doSelectEvent(clickedCell);
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void doLogicNodeEvent(AbstractGridCell clickedCell, int currGridNum){
        if(isDeleteIcon(selected)){ gridCells.set(currGridNum, clickedCell.clearShot()); }
        else if(isWireInputIcon(selected)){
            LogicNode tempNode = (LogicNode) clickedCell;
            tempNode.setInput((LogicNode) wireSource);
            gridCells.set(currGridNum, tempNode);
        }
        else if(isWireSourceIcon(selected)){
            wireSource = clickedCell; }
        else if(isSwitch(clickedCell)){ clickedCell.selectObject();}
        else if(isClearInputIcon(selected)){
            LogicNode tempNode = (LogicNode) clickedCell;
            tempNode.clearInput();
            gridCells.set(currGridNum, tempNode);
            Log.d("Wire Source", "source");
        }

        previousSelection = selected;
        selected = null;
    }

    public void doSavesIconEvent(AbstractGridCell clickedCell, int currGridNum){
        if (isCreateSaveIcon(selected)){
            SavesIcon currSave = (SavesIcon)clickedCell;
            Save("save" + currSave.save);
        }else {
            SavesIcon currSave = (SavesIcon)clickedCell;
            Load("save" + currSave.save);
        }
        previousSelection = selected;
        selected = null;
    }

    public void doClearIconEvent(AbstractGridCell clickedCell, int currGridNum) {
        if(previousSelection instanceof ClearScreenIcon)
                                                { reset(); drawHud(); previousSelection = null; }
        else { previousSelection = clickedCell;}
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void doSelectEvent(AbstractGridCell clickedCell){ selected = clickedCell; }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public boolean isLogicIcon(AbstractGridCell cell){ return (cell instanceof LogicIcon); }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public boolean isDeleteIcon(AbstractGridCell cell){ return (cell instanceof DeleteIcon); }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public boolean isLogicNode(AbstractGridCell cell)     {return (cell instanceof LogicNode);}

    public boolean isWireInputIcon(AbstractGridCell cell) {return (cell instanceof WireInputIcon);}

    public boolean isWireSourceIcon(AbstractGridCell cell){return (cell instanceof WireSourceIcon);}

    public boolean isSwitch(AbstractGridCell cell)        {return (cell instanceof SwitchNode);}

    public boolean isClearInputIcon(AbstractGridCell cell){return (cell instanceof ClearInputIcon);}

    public boolean isSavesIcon(AbstractGridCell cell)     {return (cell instanceof SavesIcon);}

    public boolean isCreateSaveIcon(AbstractGridCell cell){return (cell instanceof CreateSaveIcon);}

    public boolean isClearScreenIconEvent(AbstractGridCell cell){return (cell instanceof ClearScreenIcon);}

    //``````````````````````````````````````````````````````````````````````````````````````````````
    public void drawGrid(Canvas canvas, Paint paint){
        for(AbstractGridCell agc:gridCells)
            agc.drawGrid(canvas,paint);
    }
}

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
