package classes;

import javax.swing.text.StyledEditorKit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class OSKernel {
    public Memory memory;
    public Scheduler scheduler;
    public DiskManager disk;
    public Interpreter interpreter;
    public Semaphore semaphore;
    public HashMap<Integer, Integer> processArrival; // arrival time, program number

    public void runOS() throws Exception {
        memory = new Memory();
        disk = new DiskManager();
        scheduler = new Scheduler(2, memory, disk);
        semaphore = new Semaphore();
        interpreter = new Interpreter(memory, disk, scheduler, semaphore);

        processArrival = new HashMap<>();
        promptUser();

        disk.serializeProcesses();

        boolean firstCycle = true;

        boolean processesDone = false;
        while(!processesDone) {
            if (processArrival.containsKey(scheduler.cycles)) {
                createProcess(processArrival.get(scheduler.cycles));
            }

            // get next process from ready queue
            int runningPid = scheduler.getCurrentProcess();
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Cycle: " + scheduler.cycles);
            System.out.println("Running Process ID: " + runningPid);
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
        System.out.println("==================================================");
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
            processArrival.put(arrivalCycle, programNumber);
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