package classes;

import java.util.Scanner;

public class Interpreter {

    Memory memory;

    public void parseAndExecute(String line, int pid){
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
                    case "input":
                        Scanner sc = new Scanner(System.in);
                        System.out.print("Please enter a value:");
                        String input = sc.nextLine();
                        memory.assignVariable(varName, input, pid);
                }
                break;
            case "writeFile":
            case "readFile":
            case "printFromTo":
            case "semWait":
                Semaphore.semWait(getResourceType(instructionComponents[1]), pid); break;
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