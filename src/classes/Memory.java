package src.classes;

import java.util.HashSet;
import java.util.Vector;

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

    public void assignVariable(String varname, Object value, int pid){
        // get process base address
        int base = getPidBase(pid);
        if (base == -1) {;
            return;
        }

        boolean variableFound = false;
        for (int i = 0; i < 3; i++) {
            if (((Pair<String, Object>)memory[base + 4 + i]).key.equals(varname)) {
                ((Pair<String, Object>)memory[base + 4 + i]).val = value;
                variableFound = true;
                break;
            } else if (((Pair<String, Object>)memory[base + 4 + i]).key.equals("null")) {
                ((Pair<String, Object>)memory[base + 4 + i]).key = varname;
                ((Pair<String, Object>)memory[base + 4 + i]).val = value;
                variableFound = true;
                break;
            }
        }

        if (!variableFound) {
            System.out.println("Variable " + varname + " does not exist in process " + pid + ".");
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
            // TODO Other classes should check if pid exists from getPids before calling this method
            pids.remove(pid);
        }
        return (String) memory[codeBase + pc];
    }

    public HashSet<Integer> getPids() {
        return pids;
    }


    public void removeProcessAndShift(int pid){

    }

    public void swapProcesses(int pidFromRam, int pidFromDisk){

    }

    public boolean areSwappable(int pidFromRam, int pidFromDisk, DiskManager disk){
        // get process base address
        int base = getPidBase(pidFromRam);
        if (base == -1) {
            return false;
        }
        Pair<Integer, Integer> startEndBlock = (Pair<Integer, Integer>) memory[base + 3];
        int ramProcessSize = startEndBlock.val - startEndBlock.key + 1;

        // get disk process
        Process diskProcess = disk.getProcess(pidFromDisk);
        if (diskProcess == null) {
            System.out.println("Process with pid " + pidFromDisk + " does not exist in disk.");
            return false;
        }

        return (ramProcessSize + availableSpace >= diskProcess.size);
    }

    public int findOptimalSwappableProcess(int pidFromDisk){
        // TODO get ready queue
        return 0;
    }

    public int addNewProcess(Process p){
        if (pids.contains(p.pid)) {
            System.out.println("Process with pid " + p.pid + " already exists.");
            return -1;
        }

        if (p.size > availableSpace) {
            int pidToSwap = findOptimalSwappableProcess(p.pid);
            if (pidToSwap == -1) {
                System.out.println("Process with pid " + p.pid + " cannot be added. Not enough memory, even after attempting a swap.");
                return -1;
            }
            removeProcessAndShift(pidToSwap);
        }

        // add process to memory
        int base = 40 - availableSpace;
        Pair<Integer, Integer> startEndBlock = new Pair<>(base, base + p.size);
        p.pid = pidCounter++;
        p.boundaries = startEndBlock;
        memory[base] = p.pid;
        memory[base + 1] = p.state;
        memory[base + 2] = p.pc;
        memory[base + 3] = p.boundaries;
        for (int i = 0; i < 3; i++) {
            memory[base + 4 + i] = p.variables[i];
        }

        for (int i = 0; i < p.linesOfCode.size(); i++) {
            memory[base + 7 + i] = p.linesOfCode.get(i);
        }

        // update available space
        availableSpace -= p.size;

        // update pids
        pids.add(p.pid);

        return p.pid; // return the new pid
    }

    public Process getProcessBlock(int pid){
        // get process base address
        int base = getPidBase(pid);
        if (base == -1) {
            return null;
        }

        ProcessState state = (ProcessState) memory[base + 1];
        int pc = (int) memory[base + 2];
        Pair<Integer, Integer> startEndBlock = (Pair<Integer, Integer>) memory[base + 3];
        Pair<String, Object>[] variables = new Pair[3];
        for (int i = 0; i < 3; i++) {
            variables[i] = (Pair<String, Object>) memory[base + 4 + i];
        }
        int codeBase = (int) memory[base + 7];
        Vector<String> linesOfCode = new Vector<>();
        for (int i = 0, n = startEndBlock.key - (base + 7) + 1; i < n; i++) {
            linesOfCode.add((String) memory[base + 7 + i]);
        }
        Process p = new Process(pid, state, pc,  variables,  linesOfCode);
        return p;
    }

    public void setState (int pid,  ProcessState state) {
        int base = getPidBase(pid);
        if (base != -1) {
            memory[base + 1] = state;
        }
    }

    public boolean inMememory (int pid) {
        if (getPidBase(pid) != -1) {
            return true;
        }
        else {
            return false;
        }
    }
}
