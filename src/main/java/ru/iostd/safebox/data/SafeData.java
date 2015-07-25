package ru.iostd.safebox.data;

import java.util.ArrayList;
import java.util.List;

public class SafeData {
   private final List<SafeRecord> entries = new ArrayList<>();

    public List<SafeRecord> getRecords() {
        return entries;
    }
    
}
