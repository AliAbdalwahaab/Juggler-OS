package src.classes;

public class Semaphore {
    public SchedulerQueue userInputBlockedQueue;
    public SchedulerQueue userOutputBlockedQueue;
    public SchedulerQueue fileBlockedQueue;
    public boolean userInputUsed;
    public boolean userOutputUsed;
    public boolean fileUsed;

    public static boolean semWait(ProcessState resource, int pid){
        return false;
    }

    public static boolean semSignal(ProcessState resource, int pid){
        return false;
    }
}