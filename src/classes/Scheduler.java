package classes;
public class Scheduler {
    public SchedulerQueue readyQueue;
    public SchedulerQueue blockedQueue;
    public Pair<Integer, Integer> runningPid;
    public int timeSlice;
    public int cycles;

    public int getCurrentProcess(){
        return 0;
    }

    public void blockProcess(int pid){

    }

    public void addToReadyQueue(int pid, boolean isNew){

    }
}