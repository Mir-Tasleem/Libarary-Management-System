package lms.service;

import lms.exception.BookAlreadyIssuedException;
import lms.model.Book;
import lms.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceConcurrencyTest {
    private LibraryService service;

    @BeforeEach
    void setUp() {
        service = new LibraryService();

        // Clear any previously loaded data
        service.getAllBooks().forEach(b -> service.deleteBook(b.getBookId()));
        service.getAllUsers().forEach(u -> service.deleteUser(u.getUserId()));

        // Add test users and a single available book
        service.addUser(new Student("S001", "Alice"));
        service.addUser(new Student("S002", "Bob"));
        service.addBook(new Book("B001", "Java Concurrency", "Author A", "2022"));
    }


    @BeforeEach
    void setup() {
        service = new LibraryService();

        service.addUser(new Student("S001", "Alice"));
        service.addUser(new Student("S002", "Bob"));
        service.addBook(new Book("B001", "Java Concurrency", "Author A", "2023"));
    }

    @Test
    public void testConcurrentBorrowSameBook() throws InterruptedException {
        AtomicReference<String> r1 = new AtomicReference<>(), r2 = new AtomicReference<>();
        Thread t1 = new Thread(() -> {
            try { service.borrowBook("S001","B001"); r1.set("success"); }
            catch (BookAlreadyIssuedException e) { r1.set("already issued"); }
        });
        Thread t2 = new Thread(() -> {
            try { service.borrowBook("S002","B001"); r2.set("success"); }
            catch (BookAlreadyIssuedException e) { r2.set("already issued"); }
        });

        t1.start(); t2.start();
        t1.join(); t2.join();

        assertTrue(r1.get().equals("success") || r2.get().equals("success"));
        assertTrue(r1.get().equals("already issued") || r2.get().equals("already issued"));
        assertNotEquals(r1.get(), r2.get(), "Exactly one should succeed");
    }

}
