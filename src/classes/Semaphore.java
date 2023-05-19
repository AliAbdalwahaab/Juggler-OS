package classes;

public class Semaphore {
    public classes.SchedulerQueue userInputBlockedQueue;
    public classes.SchedulerQueue userOutputBlockedQueue;
    public classes.SchedulerQueue fileBlockedQueue;
    public boolean userInputUsed;
    public boolean userOutputUsed;
    public boolean fileUsed;


    public boolean semSignal(ResourceType resource, int pid) {
        return true;
    }

    public static boolean semWait(ResourceType resource, int pid) {
        return false;
    }
}