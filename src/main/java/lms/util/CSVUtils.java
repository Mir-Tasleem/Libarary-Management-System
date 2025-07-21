package lms.util;

import lms.model.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CSVUtils {
    private static final String BOOK_FILE = "books.csv";
    private static final String USER_FILE = "users.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(BOOK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 6) {
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
                        book.setIssuedOn(DATE_FORMAT.parse(issuedOnStr));
                    }
                    books.add(book);
                }
            }
        } catch (IOException | java.text.ParseException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
        return books;
    }

    public static void saveBooks(List<Book> books) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BOOK_FILE))) {
            for (Book book : books) {
                writer.println(String.join(",",
                        book.getBookId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublishYear(),
                        book.getIssuedTo() != null ? book.getIssuedTo() : "",
                        book.getIssuedOn() != null ? DATE_FORMAT.format(book.getIssuedOn()) : ""
                ));
            }
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 3) {
                    String id = data[0];
                    String name = data[1];
                    String role = data[2];
                    if ("Student".equalsIgnoreCase(role)) {
                        users.add(new Student(id, name));
                    } else if ("Librarian".equalsIgnoreCase(role)) {
                        users.add(new Librarian(id, name));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public static void saveUsers(List<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            for (User user : users) {
                writer.println(String.join(",",
                        user.getUserId(),
                        user.getName(),
                        user.getRole()));
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
}
