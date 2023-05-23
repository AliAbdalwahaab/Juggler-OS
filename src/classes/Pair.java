package classes;

import java.io.Serializable;

public class Pair<I,J> implements Serializable {
    public I key;
    public J val;

    public Pair(I key, J val){
        this.key = key;
        this.val = val;
    }

    public Pair() {

    }

    @Override
    public String toString() {
        return "v1: " + key + ", v2: " + val;
    }
}
