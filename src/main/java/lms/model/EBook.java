package lms.model;

public class EBook extends Book {
    private String downloadLink;

    public EBook(String bookId, String title, String author, String publishYear) {
        super(bookId, title, author, publishYear);
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String title) {
        this.downloadLink = "https://lms/"+title;
    }

    @Override
    public boolean isAvailable() {
        return true; // eBooks are always available
    }

    @Override
    public String toString() {
        return super.toString() + ", Download Link: " + downloadLink;
    }
}
