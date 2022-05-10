package com.gamecodeschool.logicsimulator;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Vector;

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
abstract class LogicNode extends AbstractGridCell{
    LogicNode inputA,inputB;
    abstract boolean eval();
    public LogicNode(AbstractGridCell myCell)           {super((myCell)); this.inputA = null; this.inputB = null;}
    public void setInput(LogicNode n){if(inputA == null){setA(n);}else if(inputB == null){setB(n);}}
    public void setA(LogicNode n)                       {this.inputA = n;}
    public void setB(LogicNode n)                       {this.inputB = n;}
    //pass in a vector and receive the inputs of the LogicNode
    public void getInputs(Vector<AbstractGridCell> inputs){
        inputs.add(inputA);
        inputs.add(inputB);
    }
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
class EmptyGridCell extends AbstractGridCell{
    public EmptyGridCell(int x, int y,int w, int h)     {super(x, y, w, h);}
    public EmptyGridCell(AbstractGridCell myCell)       {super(myCell);}
    public AbstractGridCell selectObject()              {return this;}
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