package classes;

import classes.DiskManager;
import classes.Memory;

public class OSKernel {
    public Memory memory;
    public classes.Scheduler scheduler;
    public DiskManager disk;
    public classes.Interpreter interpreter;
    public Semaphore semaphore;

    public void runOS(){

    }
}