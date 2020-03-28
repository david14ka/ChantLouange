package com.davidkazad.chantlouange.songs;

public class SongsItem {
    private int id;
    private String number;
    private String title;
    private String content;
    private int bookId;

    public SongsItem(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public SongsItem() {
    }

    public SongsItem(String number, String title, String content) {
        this.number = number;
        this.title = title;
        this.content = content;
    }
    public SongsItem(int id, String number, String title, String content) {
        this.id = id;
        this.number = number;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
