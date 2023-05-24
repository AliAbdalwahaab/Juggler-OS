package classes;

import java.util.Scanner;

public class Interpreter {

    Memory memory;
    DiskManager diskManager;
    Scheduler scheduler;
    Semaphore semaphore;

    public Interpreter(Memory memory, DiskManager diskManager, Scheduler scheduler, Semaphore semaphore) {
        this.memory = memory;
        this.diskManager = diskManager;
        this.scheduler = scheduler;
        this.semaphore = semaphore;
    }

    public void parseAndExecute(String line, int pid, boolean removeAfter) throws Exception {
        System.out.println("Process " + pid + " Executing line: " + line);
        String[] instructionComponents = line.split(" ");
        switch(instructionComponents[0]) {
            case "print":
                //if a process got to a print instruction then this means it is not blocked
                //so we can print directly
                Object var = memory.getVariable(pid, instructionComponents[1]);
                if (var != null) {
                    System.out.println(var);
                }
                break;
            case "assign":
                String varName = instructionComponents[1];
                switch(instructionComponents[2]) {
                    case "readFile":
                        String fileName = (String) memory.getVariable(pid, instructionComponents[3]);
                        String data = diskManager.readFile(fileName);
                        // modify instruction
                        memory.modifyAssignInstruction(pid, data, instructionComponents);
                        break;
                    case "input":
                        Scanner sc = new Scanner(System.in);
                        System.out.print("Please enter a value: ");
                        String input = sc.nextLine();
                        // modify instruction
                        memory.modifyAssignInstruction(pid, input, instructionComponents);
                    default:
                        memory.assignVariable(varName, instructionComponents[2], pid);
                }
                break;
            case "writeFile":
                String fileNameForWrite = (String) memory.getVariable(pid, instructionComponents[1]);
                String dataForWrite = (String) memory.getVariable(pid, instructionComponents[2]);
                diskManager.writeFile(fileNameForWrite, dataForWrite);
                break;
            case "readFile":
                String fileName = (String) memory.getVariable(pid, instructionComponents[1]);;
                String data = diskManager.readFile(fileName);
                //shouldn't i return this data?
                break;
            case "printFromTo":
                int from = Integer.parseInt((String) memory.getVariable(pid, instructionComponents[1])); //either this or .toString() the thing
                int to = Integer.parseInt((String) memory.getVariable(pid, instructionComponents[2]));
                for (int i = from; i <= to; i++) {
                    System.out.println(i);
                }
                break;
            case "semWait":
                boolean available = semaphore.semWait(getResourceType(instructionComponents[1]), pid, scheduler);
                if (!available) {
                    //block process
                    scheduler.addFromRunningToBlockedQueue();
                    memory.setState(pid, ProcessState.BLOCKED);
                    memory.decrementPc(pid);
                }
                break;
            case "semSignal":
                semaphore.semSignal(getResourceType(instructionComponents[1]), pid, scheduler, diskManager, memory); break;
            default:
                System.out.println("Invalid instruction");
                System.exit(1);
        }
        if (removeAfter) {
            memory.removeProcessAndShift(pid);
            scheduler.removePid(pid);
            memory.pids.remove(pid);
            diskManager.removeProcess(pid);
        }

    }

    public ResourceType getResourceType(String resource){
        switch(resource){
            case "userInput":
                return ResourceType.userInput;
            case "userOutput":
                return ResourceType.userOutput;
            case "file":
                return ResourceType.file;
            default:
                System.out.println("Invalid resource type: " + resource + resource.length());

        }
        return null;
    }
}