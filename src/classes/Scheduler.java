package classes;

import java.util.Vector;

public class Scheduler {
    public SchedulerQueue readyQueue;
    public SchedulerQueue blockedQueue;
    public Pair<Integer, Integer> runningPid; // pid, remainning cycles
    public int timeSlice;
    public int cycles;
    public Memory memory;
    public DiskManager disk;

    public Scheduler (int timeSlice, Memory memory, DiskManager disk) {
        this.cycles = 0;
        this.runningPid = null;
        this.timeSlice = timeSlice;
        this.readyQueue = new SchedulerQueue();
        this.blockedQueue = new SchedulerQueue();
        this.memory = memory;
        this.disk = disk;
    }

    public int getCurrentProcess(){
        // if the running process still has cycles to get executed then it is the current process.
        if (runningPid != null) {
            if (runningPid.val > 0) {
                return runningPid.key;
            }
            else if (!readyQueue.isEmpty()){ // the first process in the ready queue is going to be the running process.
                // Note: I did not remove the process from the ready queue.
                return readyQueue.getFront();
            } else {
                return -1;
            }
        }
        else if (!readyQueue.isEmpty()) {// the first process in the ready queue is going to be the running process.
            //setToRunning(readyQueue.getFront());
            return readyQueue.getFront();
        } else {
            return -1;
        }

    }

    public void addFromRunningToReadyQueue () throws Exception {
        if (runningPid != null) {
            int prevRunningProccesID = runningPid.key;
            readyQueue.add(prevRunningProccesID);
            memory.setState(prevRunningProccesID, ProcessState.READY);
            disk.setState(prevRunningProccesID, ProcessState.READY);
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

    public void addFromRunningToBlockedQueue () throws Exception {
        if (runningPid != null) {
            // we only block a process if that process is currently running and
            // requests the use of a resource while it is being used by another process.
            // hence you should only block currently running processes.
            // once the current process is blocked, the scheduler should schedule the next process in the ready queue to start executing.
            // first, we are going to alter the currently running process's state from running to blocked.
            int prevRunningProccesID = runningPid.key;
            blockedQueue.add(prevRunningProccesID);
            memory.setState(prevRunningProccesID, ProcessState.BLOCKED);
            disk.setState(prevRunningProccesID, ProcessState.BLOCKED);
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

    public void setToRunning (int pid) throws Exception {
        // TODO in OSKernel class: if the next running process is not in memory hence we need to load it from disk to memory (either by swapping or directly loading it if there is enough contiguous space).
        // TODO in OSKernel class: we need also to go the memory and change the state of the new running process from ready to running.
        runningPid = new Pair<>(pid, timeSlice);
        memory.setState(runningPid.key, ProcessState.RUNNING);
        disk.setState(runningPid.key, ProcessState.RUNNING);
    }

    public void addFromBlockedQueueToReady (int pid) throws Exception {
        SchedulerQueue tmpQueue = new SchedulerQueue();
        while (!blockedQueue.isEmpty()) {
            int currentPid = blockedQueue.remove();
            if (currentPid == pid) {
                memory.setState(pid, ProcessState.READY);
                disk.setState(pid, ProcessState.READY);
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

    public void addToReadyQueue(int pid, boolean isNew) throws Exception {
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

        if (runningPid == null) {
            if (!readyQueue.isEmpty()) {
                setToRunning(readyQueue.remove());
            }
        }
    }

    public void oneCyclePassed () throws Exception {
        if (runningPid != null) {
            runningPid.val--;
            if (runningPid.val == 0) {
                addFromRunningToReadyQueue();
            }
        }
        cycles++;
    }

    public void setTimeSlice (int timeSlice) {
        this.timeSlice = timeSlice;
    }

    public void removePid (int pid) throws Exception {
        if (runningPid != null && runningPid.key == pid) {
            if (!readyQueue.isEmpty()) {
                setToRunning(readyQueue.remove());
            }
            else {
                runningPid = null;
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
            setToRunning(readyQueue.remove());
        }
    }
}