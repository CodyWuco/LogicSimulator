package com.gamecodeschool.logicsimulator;


public class NestedCircuitLogic {
    LogicNode head;

    //``````````````````````````````````````````````````````````````````````````````````````````````
    // This takes in a save name and loads the correct circuit
    public NestedCircuitLogic(String saveName){
        loadLogicCircuit(saveName);
    }

    //``````````````````````````````````````````````````````````````````````````````````````````````
    // This save the circuit to a file using a string
    private void saveLogicCurcuit(String saveName){}

    //``````````````````````````````````````````````````````````````````````````````````````````````
    // This loads a previously saved CircuitTree and sets head to the head of the tree
    private void loadLogicCircuit(String saveName){}

    public boolean eval(){ return head.eval();}

}
