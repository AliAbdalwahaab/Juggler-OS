package src.classes;

public class Scheduler {
    public SchedulerQueue readyQueue;
    public SchedulerQueue blockedQueue;
    public Pair<int, int> runningPid;
    public int timeSlice;
    public int cycles;

    public int getCurrentProcess(){

    }

    public void blockProcess(int pid){

    }

    public void addToReadyQueue(int pid, boolean isNew){

    }
}