package com.davidkazad.chantlouange.models;

public class Page {
    private int id;
    private String title;
    private String content;
    private String number;
    private int bookId;

    public Page() {
    }

    public Page(int id, String number, String title, String content, int bookId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.number = number;
        this.bookId = bookId;
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

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
