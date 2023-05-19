package src.classes;

public class Semaphore {
    public SchedulerQueue userInputBlockedQueue;
    public SchedulerQueue userOutputBlockedQueue;
    public SchedulerQueue fileBlockedQueue;
    public boolean userInputUsed;
    public boolean userOutputUsed;
    public boolean fileUsed;

    public boolean semWait(ProcessState resource, int pid){

    }

    public boolean semSignal(ProcessState resource, int pid){

    }
}