package classes;

import java.util.Scanner;

public class Interpreter {

    SystemCall systemCall;
    Scheduler scheduler;
    Semaphore semaphore;
    DiskManager disk;
    Memory memory;

    public Interpreter(SystemCall systemCall, Scheduler scheduler, Semaphore semaphore, DiskManager disk, Memory memory) {
        this.systemCall = systemCall;
        this.scheduler = scheduler;
        this.semaphore = semaphore;
        this.disk = disk;
        this.memory = memory;
    }

    public void parseAndExecute(String line, int pid, boolean removeAfter) throws Exception {
        System.out.println("Process " + pid + " Executing line: " + line);

        String[] instructionComponents = line.split(" ");
        switch(instructionComponents[0]) {
            case "print":
                //if a process got to a print instruction then this means it is not blocked
                //so we can print directly
                Object var = systemCall.memory.getVariable(pid, instructionComponents[1]);
                if (var != null) {
                    System.out.println(var);
                }
                break;
            case "assign":
                String varName = instructionComponents[1];
                switch(instructionComponents[2]) {
                    case "readFile":
                        String fileName = (String) systemCall.memory.getVariable(pid, instructionComponents[3]);
                        String data = systemCall.disk.readFile(fileName);
                        // modify instruction
                        systemCall.memory.modifyAssignInstruction(pid, data, instructionComponents);
                        break;
                    case "input":
                        Scanner sc = new Scanner(System.in);
                        systemCall.print("Please enter a value: ");
                        String input = sc.nextLine();
                        // modify instruction
                        systemCall.memory.modifyAssignInstruction(pid, input, instructionComponents);
                    default:
                        systemCall.memory.assignVariable(varName, instructionComponents[2], pid);
                }
                break;
            case "writeFile":
                String fileNameForWrite = (String) systemCall.memory.getVariable(pid, instructionComponents[1]);
                String dataForWrite = (String) systemCall.memory.getVariable(pid, instructionComponents[2]);
                systemCall.disk.writeFile(fileNameForWrite, dataForWrite);
                break;
            case "readFile":
                String fileName = (String) systemCall.memory.getVariable(pid, instructionComponents[1]);;
                String data = systemCall.disk.readFile(fileName);
                //shouldn't i return this data?
                break;
            case "printFromTo":
                int from = Integer.parseInt((String) systemCall.memory.getVariable(pid, instructionComponents[1])); //either this or .toString() the thing
                int to = Integer.parseInt((String) systemCall.memory.getVariable(pid, instructionComponents[2]));
                for (int i = from; i <= to; i++) {
                    systemCall.println(i);
                }
                break;
            case "semWait":
                boolean available = semaphore.semWait(getResourceType(instructionComponents[1]), pid, scheduler);
                if (!available) {
                    //block process
                    scheduler.addFromRunningToBlockedQueue();
                    systemCall.memory.setState(pid, ProcessState.BLOCKED);
                    systemCall.memory.decrementPc(pid);
                }
                break;
            case "semSignal":
                semaphore.semSignal(getResourceType(instructionComponents[1]), pid, scheduler, disk, memory); break;
            default:
                systemCall.println("Invalid instruction");
                System.exit(1);
        }
        if (removeAfter) {
            systemCall.memory.removeProcessAndShift(pid);
            scheduler.removePid(pid);
            systemCall.memory.pids.remove(pid);
            systemCall.disk.removeProcess(pid);
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