package classes;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class OSKernel {
    public Memory memory;
    public static Scheduler scheduler;
    public DiskManager disk;
    public Interpreter interpreter;
    public Semaphore semaphore;
    public HashMap<Integer, Integer> processArrival; // arrival time, program number

    public void runOS() throws Exception {
        memory = new Memory();
        scheduler = new Scheduler(2);
        disk = new DiskManager();
        semaphore = new Semaphore();
        interpreter = new Interpreter(memory, disk, scheduler, semaphore);

        processArrival = new HashMap<>();
        processArrival.put(0, 1);
        processArrival.put(1, 2);
        processArrival.put(4, 3);

        boolean firstCycle = true;

        boolean processesDone = false;
        while(!processesDone) {
            if (processArrival.containsKey(scheduler.cycles)) {
                createProcess(processArrival.get(scheduler.cycles));
            }

            // get next process from ready queue
            int runningPid = scheduler.getCurrentProcess();
            String line = memory.getNextInstructionAndIncrementPC(runningPid, scheduler);
            interpreter.parseAndExecute(line, runningPid);
            scheduler.oneCyclePassed();
            processesDone = scheduler.readyQueue.size() + scheduler.blockedQueue.size() == 0;
        }
    }

    public void createProcess(int n) throws Exception {
        // read from text file
        String instructions = new String(Files.readAllBytes(Paths.get("src/Project Programs/Program_" + n + ".txt")));
        String[] instArr = instructions.split("\\n");
        Vector<String> instructionsVector = new Vector<>();
        instructionsVector.addAll(Arrays.asList(instArr));
        int newPid = memory.getNewProcessId();
        Process process = new Process(instructionsVector, newPid);
        memory.addNewProcess(process, scheduler, disk);
        scheduler.addToReadyQueue(newPid, true);
    }
}