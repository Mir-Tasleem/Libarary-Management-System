package lms.util;

import lms.model.Book;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class BookRepository implements DataRepository<Book> {
    private static final String FILE = "books.csv";
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy");

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

                Book book = new Book(id, title, author, publishDateStr);
                if (!publishDateStr.isEmpty()) {
                    book.setPublishYear(publishDateStr);
                }
                if (!issuedTo.isEmpty()) {
                    book.setIssuedTo(issuedTo);
                }
                if (!issuedOnStr.isEmpty()) {
                    book.setIssuedOn(FORMAT.parse(issuedOnStr));
                }

                books.add(book);
//                System.out.printf("%s add with Id: %s to the library\n",title,id);
            }
        } catch (Exception e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
        return books;
    }

    @Override
    public void save(List<Book> books) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE))) {
            SimpleDateFormat issuedFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Book book : books) {
                String issuedTo = book.getIssuedTo() != null ? book.getIssuedTo() : "";
                String issuedOn = book.getIssuedOn() != null ? issuedFormat.format(book.getIssuedOn()) : "";
                String publishDate = book.getPublishYear();

                writer.println(String.join(",",
                        book.getBookId(),
                        book.getTitle(),
                        book.getAuthor(),
                        publishDate != null ? publishDate : "",
                        issuedTo,
                        issuedOn
                        ));
            }
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

}
