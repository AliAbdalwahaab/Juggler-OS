package classes;

import java.util.HashSet;

public class Memory {
    static Object[] memory = new Object[40]; // Store PCBs at the start of each process block
    static int availableSpace;
    static HashSet<Integer> pids;
    static int pidCounter;

    public Memory() {
        availableSpace = 40;
        pids = new HashSet<Integer>();
        pidCounter = 0;
    }

    public int getPidBase(int pid) {
        if (!pids.contains(pid)) {
            System.out.println("Process with pid " + pid + " does not exist.");
            return -1;
        }

        int k = 0;
        Pair<Integer, Integer> startEndBlock = new Pair<>();
        for (int base = 0, process = 0; process < pids.size(); base += k, process++) {
            startEndBlock = (Pair<Integer, Integer>) memory[base + 3];
            if ((int) memory[base] != pid) {
                k = startEndBlock.val - startEndBlock.key + 1;
            } else {
                return base; // pid found
            }
        }

        System.out.println("Process with pid " + pid + " does not exist. HS inconsistency /!\\");
        return -1; // pid not found
    }

    public void assignVariable(String name, Object value, int pid){
        // get process base address
        int base = getPidBase(pid);
        if (base == -1) {;
            return;
        }

        boolean variableFound = false;
        for (int i = 0; i < 3; i++) {
            if (((Pair<String, Object>)memory[base + 4 + i]).key.equals(name)) {
                ((Pair<String, Object>)memory[base + 4 + i]).val = value;
                variableFound = true;
                break;
            } else if (((Pair<String, Object>)memory[base + 4 + i]).key.equals("null")) {
                ((Pair<String, Object>)memory[base + 4 + i]).key = name;
                ((Pair<String, Object>)memory[base + 4 + i]).val = value;
                variableFound = true;
                break;
            }
        }

        if (!variableFound) {
            System.out.println("Variable " + name + " does not exist in process " + pid + ".");
        }

    }

    public Object getVariable(int pid, String varname) {
        // get process base address
        int base = getPidBase(pid);
        if (base == -1) {
            return null;
        }

        for (int i = 0; i < 3; i++) {
            if (((Pair<String, Object>)memory[base + 4 + i]).key.equals(varname))
                return ((Pair<String, Object>)memory[base + 4 + i]).val;
        }
        System.out.println("Variable " + varname + " does not exist in process " + pid + ".");
        return null;
    }

    public String getNextInstructionAndIncrementPC(int pid){
        // get process base address
        int base = getPidBase(pid);
        if (base == -1) {
            System.out.println("Process with pid " + pid + " does not exist. HS inconsistency /!\\");
            return null;
        }

        // get lines of code base
        int codeBase = (int) memory[base + 7];
        int pc = (int) memory[base + 2];
        memory[base + 2] = pc + 1; // increment pc
        Pair<Integer, Integer> startEndBlock = (Pair<Integer, Integer>) memory[base + 3];
        if (pc > startEndBlock.val) {
            System.out.println("Process " + pid + " is executing the last instruction.");
            removeProcessAndShift(pid);
        }
        return (String) memory[codeBase + pc];
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
