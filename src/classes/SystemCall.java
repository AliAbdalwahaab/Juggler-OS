package classes;

public class SystemCall {
    Memory memory;
    DiskManager disk;

    public SystemCall(Memory memory, DiskManager disk) {
        this.memory = memory;
        this.disk = disk;
    }

    public void print(Object o)  {
        System.out.print(o);
    }

    public void println(Object o) {
        System.out.println(o);
    }
}
