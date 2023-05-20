package src.classes;

import java.util.Scanner;

public class Interpreter {

    Memory memory;
    DiskManager diskManager;
    Scheduler scheduler;

    public void parseAndExecute(String line, int pid) throws Exception {
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
                        String fileName = instructionComponents[3];
                        String data = diskManager.readFile(fileName);
                        //shouldn't i return this data?
                        break;
                    case "input":
                        Scanner sc = new Scanner(System.in);
                        System.out.print("Please enter a value:");
                        String input = sc.nextLine();
                        memory.assignVariable(varName, input, pid); break;
                }
                break;
            case "writeFile":
                String fileNameForWrite = (String) memory.getVariable(pid, instructionComponents[1]);
                String dataForWrite = (String) memory.getVariable(pid, instructionComponents[2]);
                diskManager.writeFile(fileNameForWrite, dataForWrite);
                break;
            case "readFile":
                String fileName = instructionComponents[3];
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
                boolean available = Semaphore.semWait(getResourceType(instructionComponents[1]), pid);
                if (!available) {
                    //block process
                    scheduler.addFromRunningToBlockedQueue();
                }
                break;
            case "semSignal":
                Semaphore.semSignal(getResourceType(instructionComponents[1]), pid); break;
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
        }
        return null;
    }
}