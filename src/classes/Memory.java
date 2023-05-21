package classes;

import java.util.HashSet;
import java.util.Stack;
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
        // get process base address
        int base = getPidBase(pid);
        if (base == -1) {
            return;
        }

        // remove pid from pids
        pids.remove(pid);

        // get process size
        Pair<Integer, Integer> startEndBlock = (Pair<Integer, Integer>) memory[base + 3];
        int processSize = startEndBlock.val - startEndBlock.key + 1;
        int lastPopulatedIndex = 40 - availableSpace - 1;

        // shift memory
        Pair<Integer, Integer> startEndBlockCurr = new Pair<>();
        int currBlockSize = 0;
        int j = base;
        for (int i = startEndBlock.key + 1, k = 0; i < lastPopulatedIndex ; i++, j++, k++) {
            if (k == 3) { // boundary block
                startEndBlockCurr = (Pair<Integer, Integer>) memory[i];
                currBlockSize = startEndBlockCurr.val - startEndBlockCurr.key + 1;
                memory[j] = new Pair<>(j - 3, (j - 3) + currBlockSize - 1);
            }

            if (j == startEndBlockCurr.val) {
                k = -1;
            }

            memory[j] = memory[i];
        }

        // fill the rest of the memory with null
        for (int i = j; i < 40; i++) {
            memory[i] = null;
        }

        // update available space
        availableSpace += processSize;
    }

    public void swapProcessFromDisk(int pidFromDisk, DiskManager disk, Scheduler scheduler){
        // get the swappable process from ram
        int swappableOnRam = findOptimalSwappableProcess(pidFromDisk, scheduler, disk);
        if (swappableOnRam == -1) {
            System.out.println("No swappable process found in ram.");
            return;
        }

        // get the process from memory
        Process onRam = getProcessBlock(swappableOnRam);

        // switch with process on disk
        Process onDisk = disk.swapProcessFromRam(pidFromDisk, onRam);

        // remove process from ram and add process from disk
        removeProcessAndShift(swappableOnRam);
        addNewProcess(onDisk, scheduler, disk);
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

    public int findOptimalSwappableProcess(int pidFromDisk, Scheduler scheduler, DiskManager disk){
        // get process on disk
        HashSet<Integer> swappablePids = new HashSet<>();
        for (int pid : pids) {
            if (areSwappable(pid, pidFromDisk, disk)) {
                swappablePids.add(pid);
            }
        }

        if (swappablePids.size() == 0) {
            return -1; // no swappable process found
        }

        // search for the process with the farthest scheduled time
        SchedulerQueue readyQueue = scheduler.readyQueue;
        int firstPidToSwap = -1;
        Stack<Integer> dropBack = new Stack<>();
        while (!readyQueue.isEmpty()) {
            int pid = readyQueue.getEnd();
            if (swappablePids.contains(pid)) {
                firstPidToSwap = pid;
                break;
            } else {
                readyQueue.removeLast();
                dropBack.push(pid);
            }
        }

        while (!dropBack.isEmpty()) {
            readyQueue.add(dropBack.pop());
        }

        if (firstPidToSwap == -1) {
            // check on blocked queue
            SchedulerQueue blockedQueue = scheduler.blockedQueue;
            while (!blockedQueue.isEmpty()) {
                int pid = blockedQueue.getEnd();
                if (swappablePids.contains(pid)) {
                    firstPidToSwap = pid;
                    break;
                } else {
                    blockedQueue.removeLast();
                    dropBack.push(pid);
                }
            }

            while (!dropBack.isEmpty()) {
                blockedQueue.add(dropBack.pop());
            }
        }

        return firstPidToSwap;
    }

    public int addNewProcess(Process p, Scheduler scheduler, DiskManager disk){
        if (pids.contains(p.pid)) {
            System.out.println("Process with pid " + p.pid + " already exists.");
            return -1;
        }

        if (p.size > availableSpace) {
            int pidToSwap = findOptimalSwappableProcess(p.pid, scheduler, disk);
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

    public boolean inMemory (int pid) {
        return getPidBase(pid) != -1;
    }
}
