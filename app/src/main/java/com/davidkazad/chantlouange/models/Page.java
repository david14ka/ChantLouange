package com.davidkazad.chantlouange.models;

public class Page {
    private int id;
    private String title;
    private String originalTitle = "";
    private String reference = "";
    private String content;
    private String number;
    private int bookId;

    private FavoriteUtils favUtils;
    private ReadingListUtils readUtils;


    public boolean isFavorite() {
        return favUtils.isFavorite();
    }
    public void toggleFavorite() {

        favUtils.toggleFavorite();

    }

    public boolean wasRecentlyOpened() {
        return readUtils.wasOpenedRecently();
    }
    public void toggleRecent() {
        readUtils.toggleReadingList();
    }
    
    public void addToRecent() {
        readUtils.addToRecent();
    }

    private boolean isFavorite;

    public Page() {
    }

    public Page(int id, String number, String title, String content, int bookId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.number = number;
        this.bookId = bookId;

        favUtils = new FavoriteUtils(this);
        readUtils = new ReadingListUtils(this);

    }

    public Page(int id, String number, String reference, String title, String originalTitle, String content, int bookId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.number = number;
        this.bookId = bookId;
        this.reference = reference;
        this.originalTitle = originalTitle;

        favUtils = new FavoriteUtils(this);
        readUtils = new ReadingListUtils(this);
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

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getReference() {
        return reference;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
