package classes;

import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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

    }

    public void deserializeProcesses(){

    }

    public static void main(String[] args) throws Exception {
        Vector<String> l = new Vector<>();
        l.add("sh");
        Process p1 = new Process(l, 0);
        Vector<String> l2= new Vector<>();
        l2.add("sdf");
        Process p2 = new Process(l2, 0);
        DiskManager d = new DiskManager();
        d.addProcess(p1);
        Process theP = d.getProcess(0);
        System.out.println(theP.linesOfCode.get(0));
        d.swapProcessFromRam(0, p2);
        theP = d.getProcess(0);
        System.out.println(theP.linesOfCode.get(0));
    }

}