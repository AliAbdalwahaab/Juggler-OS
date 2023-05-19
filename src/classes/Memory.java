package classes;

import java.util.HashSet;

public class Memory {
    public Object[] memory = new Object[40];
    public int availableSpace;
    public HashSet<Integer> pids;

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
        return true;
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
