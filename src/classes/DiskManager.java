package classes;

import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

public class DiskManager {
    public Vector<Process> disk;
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

        return null;
    }
    public Process getProcess(int pidOnDisk){
        for(Process p: disk){
            if(p.pid == pidOnDisk)
                return p;
        }
        return null;
    }

    public void serializeProcesses() throws Exception{

    }

    public void deserializeProcesses(){

    }

    public static void main(String[] args) throws Exception {
        String y = "j\nj\n\nj";
        String x;
        x = readFile("hmm");
        System.out.println(x);
        writeFile("hmm", y);
        x = readFile("hmm");
        System.out.println(x);
    }

}