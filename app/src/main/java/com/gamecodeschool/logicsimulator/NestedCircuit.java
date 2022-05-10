package com.gamecodeschool.logicsimulator;

public class NestedCircuit extends LogicNode{
    NestedCircuitLogic logicTree;

    public NestedCircuit(AbstractGridCell mycell){
        super(mycell);
    }

    boolean eval() {
        return logicTree.eval();
    }
}


