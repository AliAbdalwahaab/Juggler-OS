package classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class OSKernel {
    public Memory memory;
    public Scheduler scheduler;
    public DiskManager disk;
    public Interpreter interpreter;
    public Semaphore semaphore;
    public SystemCall systemCall;
    public HashMap<Integer, Vector<Integer>> processArrival; // arrival time, program number


    public void runOS() throws Exception {
        memory = new Memory(40);
        disk = new DiskManager();
        systemCall = new SystemCall(memory, disk);
        scheduler = new Scheduler(2, memory, disk);
        semaphore = new Semaphore();
        interpreter = new Interpreter(systemCall, scheduler, semaphore, disk, memory);

        processArrival = new HashMap<>();
        promptUser();
        if (processArrival.size() == 0) {
            System.out.println("No processes to run!");
            return;
        }

        disk.serializeProcesses();

        boolean firstCycle = true;

        boolean processesDone = false;
        while(!processesDone) {
            if (processArrival.containsKey(scheduler.cycles)) {
                for (int p: processArrival.get(scheduler.cycles)) {
                    createProcess(p);
                }
            }

            // get next process from ready queue
            int runningPid = scheduler.getCurrentProcess();
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Cycle: " + scheduler.cycles);
            System.out.println("Running Process ID: " + runningPid);
            System.out.println("remaining instructions: " + scheduler.runningPid.val);
            System.out.print("Ready Queue: ");
            scheduler.readyQueue.print();
            System.out.print("Blocked Queue: ");
            scheduler.blockedQueue.print();
            System.out.print("UserInput Blocked Queue: ");
            semaphore.userInputBlockedQueue.print();
            System.out.print("UserOutput Blocked Queue: ");
            semaphore.userOutputBlockedQueue.print();
            System.out.print("File Blocked Queue: ");
            semaphore.fileBlockedQueue.print();


            if (runningPid == -1) {
                System.out.println("No process is running");
                scheduler.oneCyclePassed();
                continue;
            }
//            memory.setState(runningPid, ProcessState.RUNNING);
            Pair<String, Boolean> instruction = memory.getNextInstructionAndIncrementPC(runningPid, disk, scheduler);
            interpreter.parseAndExecute(instruction.key, runningPid, instruction.val);
            scheduler.oneCyclePassed();
//            int nextRunningPid = scheduler.getCurrentProcess();
//            if (nextRunningPid != -1 && nextRunningPid != runningPid) {
//                memory.setState(runningPid, ProcessState.READY);
//                disk.setState(runningPid, );
//            }
            memory.printMemory();
            disk.printDisk();
            processesDone = scheduler.readyQueue.size() + scheduler.blockedQueue.size() == 0 && scheduler.runningPid == null;
            if (processesDone) {
                System.out.println("All processes done!");
            } else {
                System.out.println("New Cycle Starting...");
            }
        }
    }

    public void createProcess(int n) throws Exception {
        // read from text file
        Vector<String> instructionsVector = new Vector<>();
        BufferedReader br = new BufferedReader(new FileReader("src/Project Programs/Program_" + n + ".txt"));
        while (br.ready()) {
            String line = br.readLine();
            instructionsVector.add(line);
        }
        int newPid = memory.getNewProcessId();
        Process process = new Process(instructionsVector, newPid);
        memory.addNewProcess(process, scheduler, disk);
        scheduler.addToReadyQueue(newPid, true);
    }

    public void promptUser() throws Exception {
        Scanner sc = new Scanner(System.in);
        String gc = "\u001B[32m";
        String rc = "\u001B[31m";
        System.out.println(gc+"==================================================");
        System.out.println("      -------------------------------------       ");
        System.out.println("      | Welcome to the Juggler OS Kernel! |       ");
        System.out.println("      -------------------------------------       ");
        System.out.println("Initating Simulation Parameters...");
        showTerminalLoading();
        String input = "";
        boolean stop = false;
        while(!stop) {
            System.out.print("Input Program Number To Run (X to exit): ");
            input = sc.nextLine();
            if (input.equals("x") || input.equals("X")) {
                stop = true;
                break;
            }
            int programNumber = Integer.parseInt(input);
            System.out.print("Input Arrival Cycle Number: ");
            input = sc.nextLine();
            if (input.equals("X") || input.equals("x")) {
                stop = true;
                break;
            }
            int arrivalCycle = Integer.parseInt(input);
            if (processArrival.containsKey(arrivalCycle)) {
                Vector<Integer> programNumbers = processArrival.get(arrivalCycle);
                programNumbers.add(programNumber);
                processArrival.put(arrivalCycle, programNumbers);
            } else {
                Vector<Integer> programNumbers = new Vector<>();
                programNumbers.add(programNumber);
                processArrival.put(arrivalCycle, programNumbers);
            }

        }
    }

    public static void showTerminalLoading() {
        int totalFrames = 15;
        int animationSpeed = 250; // in milliseconds

        for (int i = 0; i < totalFrames; i++) {
            try {
                Thread.sleep(animationSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String[] loadingFrames = {"/", "|", "\\", "-"};

            System.out.print("\rLoading... [" + getLoadingBar(i, loadingFrames) + "]");
        }

        System.out.println("\nLoading complete!");
    }

    private static String getLoadingBar(int index, String[] loadingFrames) {
        return loadingFrames[index % loadingFrames.length];
    }

    public static void main(String[] args) throws Exception {
        OSKernel os = new OSKernel();
        os.runOS();
    }
}