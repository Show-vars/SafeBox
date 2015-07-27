package ru.iostd.safebox.wizard.importdata.formats;

import com.google.gson.annotations.SerializedName;

public class JsonPassword {

    @SerializedName("Description")
    private String title;
    @SerializedName("Username")
    private String userName;
    @SerializedName("Password")
    private String password;
    @SerializedName("Notes")
    private String notes;

    public String getTitle() {
        return title;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getNotes() {
        return notes;
    }
}
