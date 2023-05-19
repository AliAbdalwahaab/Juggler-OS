package src.classes;

public class Memory {
    public Object[] memory = new Object[40];
    public int availableSpace;
    public HashSet<int> pids;

    public void assignVariable(String name, Object value, int pid){

    }

    public String getNextInstruction(int pid){

    }

    public void removeProcessAndShift(int pid){

    }

    public void swapProcesses(int pidFromRam, int pidFromDisk){

    }

    public boolean areSwappable(int pidFromRam, int pidFromDisk){

    }

    public int findSwappableProcess(int pidFromDisk){

    }

    public void addNewProcess(Process p){

    }

    public Process getProcessBlock(int pid){

    }
}
