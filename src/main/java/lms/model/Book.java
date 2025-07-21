package lms.model;

import java.util.Date;
import java.util.Objects;

public class Book {
    private String bookId;
    private String author;
    private String title;
    private String publishYear;
    private String issuedTo=null;
    private Date issuedOn=null;
//    private int quantity=1;

    // No-arg constructor (required for serialization/deserialization)
    public Book() {}

    // Parameterized constructor
    public Book(String bookId, String title, String author,String publisYear) {
        this.bookId = bookId;
        this.author = author;
        this.title=title;
        this.publishYear=publisYear;
    }

    // Getters
    public String getBookId() {
        return bookId;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishYear() {
        return publishYear;
    }

    public String getIssuedTo() {
        return issuedTo;
    }

    public Date getIssuedOn() {
        return issuedOn;
    }

    public String getTitle() {
        return title;
    }

//    public int getQuantity() {
//        return quantity;
//    }

    // Setters
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublishYear(String publishYear) {
        this.publishYear = publishYear;
    }

    public void setIssuedTo(String issuedTo) {
        this.issuedTo = issuedTo;
    }

    public void setIssuedOn(Date issuedOn) {
        this.issuedOn = issuedOn;
    }

//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }

    // Utility methods

//    public void incrementQuantity(int amount) {
//        this.quantity+=amount;
//    }

//    public void incrementQuantity() {
//        this.quantity++;
//    }

//    public void decrementQuantity() {
//        if (quantity > 0) quantity--;
//    }
    public boolean isAvailable() {
        return issuedTo == null;
    }

    @Override
    public String toString() {
        return "Book{" +
                "Book Id:" + bookId + '\'' +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publishYear=" + publishYear +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(bookId, book.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }

}
