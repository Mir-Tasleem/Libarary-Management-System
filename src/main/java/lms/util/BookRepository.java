package lms.util;

import lms.model.Book;
import lms.model.EBook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class BookRepository implements DataRepository<Book> {
    private static final String FILE = "books.csv";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public List<Book> load() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                String id = data[0];
                String title = data[1];
                String author = data[2];
                String publishDateStr = data[3];
                String issuedTo = data[4];
                String issuedOnStr = data[5];
                String type = data.length >= 7 ? data[6] : "book";

                Book book = "ebook".equalsIgnoreCase(type)
                        ? new EBook(id, title, author, publishDateStr)
                        : new Book(id, title, author, publishDateStr);

                if (!issuedTo.isEmpty()) {
                    book.setIssuedTo(issuedTo);
                }
                if (!issuedOnStr.isEmpty()) {
                    book.setIssuedOn(FORMAT.parse(issuedOnStr));
                }

                books.add(book);
            }
        } catch (Exception e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
        return books;
    }

    @Override
    public void save(List<Book> books) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE))) {
            for (Book book : books) {
                String issuedTo = book.getIssuedTo() != null ? book.getIssuedTo() : "";
                String issuedOn = book.getIssuedOn() != null ? FORMAT.format(book.getIssuedOn()) : "";
                String publishDate = book.getPublishYear();
                String type = book instanceof EBook ? "ebook" : "book";

                writer.println(String.join(",",
                        book.getBookId(),
                        book.getTitle(),
                        book.getAuthor(),
                        publishDate != null ? publishDate : "",
                        issuedTo,
                        issuedOn,
                        type
                ));
            }
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }
}
