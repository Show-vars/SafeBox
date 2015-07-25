package ru.iostd.safebox.config;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private final List<String> lastDbFiles = new ArrayList<>();
    private final List<String> lastKeyFiles = new ArrayList<>();

    public List<String> getLastDbFiles() {
        return lastDbFiles;
    }

    public List<String> getLastKeyFiles() {
        return lastKeyFiles;
    }

    public void addLastDbFile(String file) {
        if (lastDbFiles.size() > 5) {
            lastDbFiles.remove(0);
        }
        lastDbFiles.remove(file);

        lastDbFiles.add(file);
    }

    public void addLastKeyFile(String file) {
        if (lastKeyFiles.size() > 5) {
            lastKeyFiles.remove(0);
        }
        lastKeyFiles.remove(file);
        lastKeyFiles.add(file);
    }
}
