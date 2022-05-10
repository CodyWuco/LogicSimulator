package com.gamecodeschool.logicsimulator;

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// AbstractGridCell represents a single grid cell in the Logic Simulator. It is the bass class for
// each of the different stat classes that each grid cell can represent.
//

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
class Grid implements Serializable {
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
