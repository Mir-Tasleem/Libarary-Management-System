package lms.model;

import java.util.Date;

public class EBook extends Book {

    private String fileFormat;
    private double fileSizeMB;  // e.g., 2.5 MB
    private String downloadLink;

    // No-arg constructor (required for serialization)
    public EBook() {
        super();
    }

    // Parameterized constructor
    public EBook(String bookId, String title, String author, String publishYear,
                 String fileFormat, double fileSizeMB, String downloadLink) {
        super(bookId, title, author, publishYear);
        setPublishYear(publishYear);
        this.fileFormat = fileFormat;
        this.fileSizeMB = fileSizeMB;
        this.downloadLink = downloadLink;
    }


    // Getters
    public String getFileFormat() {
        return fileFormat;
    }

    public double getFileSizeMB() {
        return fileSizeMB;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    // Setters
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public void setFileSizeMB(double fileSizeMB) {
        this.fileSizeMB = fileSizeMB;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    @Override
    public String toString() {
        return super.toString() + " [EBook: format=" + fileFormat +
                ", size=" + fileSizeMB + "MB, link=" + downloadLink + "]";
    }
}
