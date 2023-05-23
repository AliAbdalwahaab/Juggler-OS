package classes;
import java.io.Serializable;
import java.util.Vector;

public class Process implements Serializable {
    public int pid;
    public ProcessState state;
    public int pc;
    public Pair<Integer, Integer> boundaries;
    public Pair<String, Object>[] variables;
    public Vector<String> linesOfCode;
    public int size;


    // for a brand-new process
    public Process(Vector<String> linesOfCode, int size) {
        this.state = ProcessState.READY;
        this.pc = 0;
        this.variables = new Pair[] {new Pair<>("null", null), new Pair<>("null", null), new Pair<>("null", null)};
        this.linesOfCode = linesOfCode;
        this.size = 4 + variables.length + linesOfCode.size();
    }

    // for a replica of an existing process
    public Process(int pid, ProcessState state, int pc, Pair<String, Object>[] variables, Vector<String> linesOfCode) {
        this.pid = pid;
        this.state = state;
        this.pc = pc;
        if (variables.length != 3) {
            System.out.println("Process " + pid + " has " + variables.length + " variables. It should have 3.");
            return;
        }
        this.variables = variables;
        this.linesOfCode = linesOfCode;
        this.size = 4 + variables.length + linesOfCode.size();
    }

    public int getPid() {
        return this.pid;
    }
}
