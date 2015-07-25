package ru.iostd.safebox.config;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ConfigManager {

    private Config config = new Config();

    public Config getConfig() {
        return config;
    }

    public void load() throws IOException {
        Gson gson = new Gson();
        try {
            Config configt = gson.fromJson(new FileReader(new File("config.json")), Config.class);
            if (configt != null) {
                config = configt;
            }
        } catch (FileNotFoundException ex) {
        }
    }

    public void save() throws IOException {
        try (Writer w = new FileWriter(new File("config.json"))) {
            Gson gson = new Gson();
            gson.toJson(config, w);
        }
    }

    private static ConfigManager instance;

    public static ConfigManager getInstance() {
        return instance == null ? instance = new ConfigManager() : instance;
    }
}
