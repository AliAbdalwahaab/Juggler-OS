package src.classes;

public class Interpreter {
    public void parseAndExecute(String line, int pid){
        String[] instructionComponents = line.split(" ");
        switch(instructionComponents[0]) {
            case "print":
                System.out.println(instructionComponents[1]);
            case "assign":
            case "writeFile":
            case "readFile":
            case "printFromTo":
            case "semWait":
                Semaphore.semWait(ProcessState.RUNNING, pid);
            case "semSignal":
        }

    }
}