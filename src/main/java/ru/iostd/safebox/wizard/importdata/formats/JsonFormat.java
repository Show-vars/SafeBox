package ru.iostd.safebox.wizard.importdata.formats;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class JsonFormat {
    
    @SerializedName("Passwords")
    private List<JsonPassword> passwords = new ArrayList<>();

    public List<JsonPassword> getPasswords() {
        return passwords;
    }
}
