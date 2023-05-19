package classes;
import java.util.Vector;

public class Process {
    public int pid;
    public ProcessState state;
    public int pc;
    public Pair<Integer, Integer> boundaries;
    public Pair<String, Object>[] variables;
    public Vector<String> linesOfCode;
    public int size;

    public Process(int pid, ProcessState state, int pc, Pair<Integer, Integer> boundaries, Pair<String, Object>[] variables, Vector<String> linesOfCode, int size) {
        this.pid = pid;
        this.state = state;
        this.pc = pc;
        this.boundaries = boundaries;
        if (variables.length != 3) {
            System.out.println("Process " + pid + " has " + variables.length + " variables. It should have 3.");
            return;
        }
        this.variables = variables;
        this.linesOfCode = linesOfCode;
        this.size = 4 + variables.length + linesOfCode.size();
    }
}
