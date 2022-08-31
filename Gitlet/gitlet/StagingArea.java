package gitlet;

import java.io.Serializable;
import java.util.HashMap;

public class StagingArea implements Serializable {
    private final HashMap<String, String> removed;
    private final HashMap<String, String> added;


    public StagingArea() {
        removed = new HashMap<>();
        added = new HashMap<>();
    }

    public boolean addToAdded(String name, String code) {
        if (!added.containsKey(name) || !added.get(name).equals(code)) {
            added.put(name, code);
            return true;
        }
        return false;
    }

    public void reset() {
        removed.clear();
        added.clear();
    }

    public boolean isEmpty() {
        return removed.isEmpty() && added.isEmpty();
    }

    public HashMap<String, String> getAdded() {
        return added;
    }

    public HashMap<String, String> getRemoved() {
        return removed;
    }
}
