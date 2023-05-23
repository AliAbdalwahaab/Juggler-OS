package classes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;



public class DiskManager {
    public Vector<Process> disk = new Vector<>();
    public static final String dir = "src/main/resources/";

    public static void writeFile(String fileName, String data) throws Exception{
        FileWriter write = new FileWriter((dir + fileName + ".txt"), false);
        PrintWriter print_line = new PrintWriter(write);
        print_line.print(data);
        print_line.close();
    }

    public static String readFile(String fileName) throws Exception{
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(dir + fileName + ".txt")));
        return data;
    }

    public Process swapProcessFromRam(int pidOnDisk, Process processOnRam){
        disk.add(processOnRam);
        for(Process p: disk){
            if(p.pid == pidOnDisk){
                disk.remove(p);
                return p;
            }
        }
        return null;
    }
    public Process getProcess(int pidOnDisk){
        for(Process p: disk){
            if(p.pid == pidOnDisk)
                return p;
        }
        return null;
    }

    public void addProcess(Process newProcess){
        disk.add(newProcess);
    }

    public void serializeProcesses() throws Exception{
        FileOutputStream fos = new FileOutputStream(dir + "disk.tmp");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(disk);
        oos.close();
    }

    public void deserializeProcesses() throws Exception{
        FileInputStream fis = new FileInputStream(dir + "disk.tmp");
        ObjectInputStream ois = new ObjectInputStream(fis);
        disk = (Vector<Process>) ois.readObject();
        ois.close();
    }

    public static void main(String[] args) throws Exception {
        writeFile("t", "I really the");
    }

}