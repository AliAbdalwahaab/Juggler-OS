package classes;
import java.util.Vector;

public class Process {
    public int pid;
    public ProcessState state;
    public int pc;
    public Pair<Integer, Integer> boundaries;
    public Pair<String, Object>[] variables = new Pair[3];
    public Vector<String> linesOfCode;
}
