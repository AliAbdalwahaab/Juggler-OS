package classes;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;

public class DiskManager {
    public Vector<Process> disk;
    public static final String dir = "src/main/resources/";

    public void writeFile(String fileName, String data){

    }

    public static String readFile(String fileName) throws Exception{
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(dir + fileName)));
        return data;
    }

    public Process swapProcessFromRam(int pidOnDisk, Process processOnRam){

        return null;
    }
    public static Process getProcess(int pidOnDisk){
        return null;
    }

    public void serializeProcesses(){

    }

    public void deserializeProcesses(){

    }

    public static void main(String[] args) throws Exception {
        String x;
        x = readFile("hmm");
        System.out.println();
    }

}