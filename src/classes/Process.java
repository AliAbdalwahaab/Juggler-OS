package src.classes;


public class Process {
    public int pid;
    public ProcessState state;
    public int pc;
    public Pair<int, int> boundaries;
    public Pair<String, Object>[] variables = new Pair<String, Object>[3];
    public Vector<String> linesOfCode;
}
