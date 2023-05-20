package classes;

public class Semaphore {
    public static SchedulerQueue userInputBlockedQueue;
    public static SchedulerQueue userOutputBlockedQueue;
    public static SchedulerQueue fileBlockedQueue;
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

    public static void semSignal(ResourceType resource, int pid) {
        if (Scheduler.runningPid.val == pid){ //check if the process letting go of the resource is the running process
            if (resource.equals(ResourceType.userInput) && userInputUsed) {

                //Mark the resource as available
                userInputUsed = false; //Mark the resource as available

                //remove Process from the resource blocked queue if it were there after a failed request before
                userInputBlockedQueue.remove(pid);

                return;
            } else if (resource.equals(ResourceType.userOutput) && userOutputUsed) {

                //Mark the resource as available
                userOutputUsed = false;

                //remove Process from the resource blocked queue if it were there after a failed request before
                userOutputBlockedQueue.remove(pid);

                return;

            } else if (resource.equals(ResourceType.file) && fileUsed) {

                //Mark the resource as available
                fileUsed = false;

                //remove Process from the resource blocked queue if it were there after a failed request before
                fileBlockedQueue.remove(pid);
                return;
            }
        }
    }

    public static boolean semWait(ResourceType resource, int pid){
        //return flase by default if the process is not the running process
        if (Scheduler.runningPid.val == pid) {

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