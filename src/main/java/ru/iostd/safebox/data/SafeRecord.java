package ru.iostd.safebox.data;

import java.time.LocalDateTime;

public class SafeRecord implements Comparable<SafeRecord> {

    private String title;
    private String userName;
    private String password;
    private String url;
    private String notes;
    private LocalDateTime datetime;

    public SafeRecord(String title, String userName, String password, String url, String notes, LocalDateTime datetime) {
        this.title = title;
        this.userName = userName;
        this.password = password;
        this.url = url;
        this.notes = notes;
        this.datetime = datetime;
    }

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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    @Override
    public int compareTo(SafeRecord another) {
        if (this.datetime.isAfter(another.datetime)) {
            return -1;
        }
        return 1;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
