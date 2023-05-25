package classes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Vector;



public class DiskManager {
    public Vector<Process> disk = new Vector<>();
    public static final String dir = "src/main/resources/";

    public void writeFile(String fileName, String data) throws Exception{
        FileWriter write = new FileWriter((dir + fileName + ".txt"), false);
        PrintWriter print_line = new PrintWriter(write);
        print_line.print(data);
        print_line.close();
    }

    public String readFile(String fileName) throws Exception{
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(dir + fileName + ".txt")));
        return data;
    }

    public Process swapProcessFromRam(int pidOnDisk, Process processOnRam) throws Exception {
        deserializeProcesses();
        disk.add(processOnRam);
        for(Process p: disk){
            if(p.pid == pidOnDisk){
                disk.remove(p);
                serializeProcesses();
                System.out.println("Swapped process with PID " + pidOnDisk + " from Disk to RAM");
                return p;
            }
        }
        return null;
    }

    public Process getProcess(int pidOnDisk) throws Exception {
        deserializeProcesses();
        for (Process p: disk) {
            if (p.pid == pidOnDisk)
                return p;
        }
        return null;
    }

    public void removeProcess(int pid) throws Exception {
        deserializeProcesses();
        for (Process p: disk) {
            if (p.pid == pid) {
                disk.remove(p);
                serializeProcesses();
                return;
            }
        }
    }

    public void setState(int pidOnDisk, ProcessState state) throws Exception {
        deserializeProcesses();
        for (Process p: disk) {
            if (p.pid == pidOnDisk) {
                p.state = state;
                serializeProcesses();
                return;
            }
        }
        serializeProcesses();
    }

    public void addProcess(Process newProcess) throws Exception {
        deserializeProcesses();
        disk.add(newProcess);
        serializeProcesses();
    }

    public void serializeProcesses() throws Exception{
        FileOutputStream fos = new FileOutputStream(dir + "disk.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(disk);
        oos.close();
    }

    public void deserializeProcesses() throws Exception{
        FileInputStream fis = new FileInputStream(dir + "disk.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        disk = (Vector<Process>) ois.readObject();
        ois.close();
    }

    public void printDisk() throws Exception{
        deserializeProcesses();
        String gc = "\u001B[32m";
        String rc = "\u001B[33m";
        //System.out.println("=====================================");
        System.out.println(rc+"Disk Contents:");
        for(Process p: disk){
            System.out.println("Process ID " + p.pid);
            System.out.println("Process State " + p.state);
            System.out.println("Process Instructions " + p.linesOfCode.toString());
            System.out.println("Process Variables " + Arrays.toString(p.variables));
            System.out.println("-------------------------------------");

        }
        System.out.println("=====================================");
        System.out.print(gc);
    }

    public static void main(String[] args) throws Exception {
        DiskManager disk = new DiskManager();
        disk.writeFile("t", "I really the");
    }

}