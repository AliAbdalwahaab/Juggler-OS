package src.classes;
public class Scheduler {
    public static SchedulerQueue readyQueue;
    public static SchedulerQueue blockedQueue;
    public Pair<Integer, Integer> runningPid;
    public int timeSlice;
    public int cycles;

    public Scheduler (int timeSlice) {
        this.cycles = 0;
        this.runningPid = null;
        this.timeSlice = timeSlice;
        this.readyQueue = new SchedulerQueue();
        this.blockedQueue = new SchedulerQueue();
    }

    public int getCurrentProcess(){
        // if the running process still has cycles to get executed then it is the current process.
        if (runningPid != null) {
            if (runningPid.key > 0) {
                return runningPid.val;
            }
            else { // the first process in the ready queue is going to be the running process.
                // Note: I did not remove the process from the ready queue.
                return readyQueue.getFront();
            }
        }
        else {
            return -1;
        }
    }

    public void addFromRunningToReadyQueue () {
        if (runningPid != null) {
            int prevRunningProccesID = runningPid.val;
            readyQueue.add(prevRunningProccesID);
            // TODO in OSKernel class: we need also to go the memory and change the state of the old running process from running to ready.
            // set the new running process from the ready queue.
            if (!readyQueue.isEmpty()) {
                setToRunning(readyQueue.remove());
            }
            else {
                runningPid = null;
            }
        }
    }

    public void addFromRunningToBlockedQueue () {
        if (runningPid != null) {
            // we only block a process if that process is currently running and
            // requests the use of a resource while it is being used by another process.
            // hence you should only block currently running processes.
            // once the current process is blocked, the scheduler should schedule the next process in the ready queue to start executing.
            // first, we are going to alter the currently running process's state from running to blocked.
            int prevRunningProccesID = runningPid.val;
            blockedQueue.add(prevRunningProccesID);
            // TODO in OSKernel class: we need also to go the memory and change the state of the old running process from running to blocked.
            // set the new running process from the ready queue.
            if (!readyQueue.isEmpty()) {
                setToRunning(readyQueue.remove());
            }
            else {
                runningPid = null;
            }
        }
    }

    public void setToRunning (int pid) {
        // TODO in OSKernel class: if the next running process is not in memory hence we need to load it from disk to memory (either by swapping or directly loading it if there is enough contiguous space).
        // TODO in OSKernel class: we need also to go the memory and change the state of the new running process from ready to running.
        runningPid.val = pid;
        runningPid.key = timeSlice;
    }

    public void addFromBlockedQueueToReady (int pid) {
        SchedulerQueue tmpQueue = new SchedulerQueue();
        while (!blockedQueue.isEmpty()) {
            int currentPid = blockedQueue.remove();
            if (currentPid == pid) {
                readyQueue.add(currentPid);
            }
            else {
                tmpQueue.add(currentPid);
            }
        }
        while (!tmpQueue.isEmpty()) {
            blockedQueue.add(tmpQueue.remove());
        }
    }

    public void addToReadyQueue(int pid, boolean isNew){
        if (isNew) {
            // if the process is newly created, it is going to be added the ready queue.
            readyQueue.add(pid);
        }
        else {
            // if the process already exist, then we are going to look whether it is blocked or running and add it to the ready queue.
            if (runningPid != null && pid == runningPid.key) { // the process already exists and it is running.
                addFromRunningToReadyQueue();
            }
            else { // the process already exists and it is blocked.
                addFromBlockedQueueToReady(pid);
            }
        }
    }

    public void oneCyclePassed () {
        if (runningPid != null) {
            runningPid.key--;
            if (runningPid.key == 0) {
                addFromRunningToReadyQueue();
            }
        }
        cycles++;
    }

    public void setTimeSlice (int timeSlice) {
        this.timeSlice = timeSlice;
    }

    public void removePid (int pid) {
        if (runningPid != null && runningPid.val == pid) {
            if (!readyQueue.isEmpty()) {
                setToRunning(readyQueue.remove());
            }
        }
        else {
            SchedulerQueue tmpQueue = new SchedulerQueue();
            while (!readyQueue.isEmpty()) {
                int currentPid = readyQueue.remove();
                if (currentPid != pid) {
                    tmpQueue.add(currentPid);
                }
            }
            while (!tmpQueue.isEmpty()) {
                readyQueue.add(tmpQueue.remove());
            }
        }
    }
}