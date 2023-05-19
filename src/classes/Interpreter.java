package classes;
public class Interpreter {
    public void parseAndExecute(String line, int pid){
        String[] instructionComponents = line.split(" ");
        switch(instructionComponents[0]) {
            case "print":
                //if a process got to a print instruction then this means it is not blocked
                //so we can print directly

            case "assign":
            case "writeFile":
            case "readFile":
            case "printFromTo":
            case "semWait":
                Semaphore.semWait(getResourceType(instructionComponents[1]), pid);
            case "semSignal":
                Semaphore.semSignal(getResourceType(instructionComponents[1]), pid);
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