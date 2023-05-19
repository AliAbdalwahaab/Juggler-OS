package src.classes;

import java.util.HashSet;

public class Memory {
    static Object[] memory = new Object[40];
    static int availableSpace;
    static HashSet<Integer> pids;
    static int pidCounter;

    public Memory(){
        availableSpace = 40;
        pids = new HashSet<Integer>();
        pidCounter = 0;
    }

    public void assignVariable(String name, Object value, int pid){

    }

    public String getNextInstruction(int pid){
        return null;
    }

    public void removeProcessAndShift(int pid){

    }

    public void swapProcesses(int pidFromRam, int pidFromDisk){

    }

    public boolean areSwappable(int pidFromRam, int pidFromDisk){
        return false;
    }

    public int findSwappableProcess(int pidFromDisk){
        return 0;
    }

    public void addNewProcess(Process p){

    }

    public Process getProcessBlock(int pid){
        return null;
    }
}
