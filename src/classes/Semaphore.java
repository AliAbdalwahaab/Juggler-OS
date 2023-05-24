package classes;

public class Semaphore {
    public SchedulerQueue userInputBlockedQueue;
    public SchedulerQueue userOutputBlockedQueue;
    public SchedulerQueue fileBlockedQueue;
    public static boolean userInputUsed;
    public static boolean userOutputUsed;
    public static boolean fileUsed;

    public Semaphore() {
        userInputBlockedQueue = new SchedulerQueue();
        userOutputBlockedQueue = new SchedulerQueue();
        fileBlockedQueue = new SchedulerQueue();
        userInputUsed = false;
        userOutputUsed = false;
        fileUsed = false;
    }

    public void semSignal(ResourceType resource, int pid, Scheduler scheduler, DiskManager disk, Memory memory) throws Exception {
        if (scheduler.runningPid.key == pid){ //check if the process letting go of the resource is the running process
            int unblockPid = -1;

            if (resource.equals(ResourceType.userInput) && userInputUsed) {

                //Mark the resource as available
                userInputUsed = false; //Mark the resource as available

                //remove Process from the resource blocked queue if it were there after a failed request before
                if (!userInputBlockedQueue.isEmpty())
                    unblockPid = userInputBlockedQueue.remove();

            } else if (resource.equals(ResourceType.userOutput) && userOutputUsed) {

                //Mark the resource as available
                userOutputUsed = false;

                //remove Process from the resource blocked queue if it were there after a failed request before
                if (!userOutputBlockedQueue.isEmpty())
                    unblockPid = userOutputBlockedQueue.remove();


            } else if (resource.equals(ResourceType.file) && fileUsed) {

                //Mark the resource as available
                fileUsed = false;

                //remove Process from the resource blocked queue if it were there after a failed request before
                if (!fileBlockedQueue.isEmpty())
                    unblockPid = fileBlockedQueue.remove();

            }
            if (unblockPid != -1) {
                scheduler.addFromBlockedQueueToReady(unblockPid);
                disk.setState(unblockPid, ProcessState.READY);
                memory.setState(unblockPid, ProcessState.READY);
            }
        }
    }

    public boolean semWait(ResourceType resource, int pid, Scheduler scheduler){
        //return false by default if the process is not the running process
        if (scheduler.runningPid.key == pid) {

            // if the resource is available, mark it as used and return true
            if (resource.equals(ResourceType.userInput) && !userInputUsed) {
                userInputUsed = true;
                return true;

            } else if (resource.equals(ResourceType.userOutput) && !userOutputUsed) {
                userOutputUsed = true;
                return true;

            } else if (resource.equals(ResourceType.file) && !fileUsed) {
                fileUsed = true;
                return true;

                // if the resource is not available, add the process to the blocked queue of that resource and return false
            } else if (resource.equals(ResourceType.userInput) && userInputUsed) {
                userInputBlockedQueue.add(pid);
                return false;

            } else if (resource.equals(ResourceType.userOutput) && userOutputUsed) {
                userOutputBlockedQueue.add(pid);
                return false;

            } else if (resource.equals(ResourceType.file) && fileUsed) {
                fileBlockedQueue.add(pid);
                return false;
            }
        }
        return false;
    }



}