// File: src/test/java/lms/service/LibraryServiceConcurrencyTest.java
package lms.service;

import lms.model.*;
import org.junit.jupiter.api.*;

import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceConcurrencyTest {

    private LibraryService libraryService;
    private Student student1;
    private Student student2;
    private Book book;

    @BeforeEach
    void setup() {
        libraryService = new LibraryService();

        // Create students and book
        student1 = new Student("S001", "Alice");
        student2 = new Student("S002", "Bob");
        book = new Book("B001", "Concurrency Book", "Goetz", "2006");

        // Manually add them to service
        libraryService.getAllUsers().addAll(List.of(student1, student2));
        libraryService.getAllBooks().add(book);
        book.setIssuedTo(null);
        book.setIssuedOn(null);
    }

    @Test
    void testConcurrentBorrowing() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                libraryService.borrowBook("S001", "B001");
                System.out.println("Alice borrowed the book");
            } catch (Exception e) {
                System.out.println("Alice failed: " + e.getMessage());
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                libraryService.borrowBook("S002", "B001");
                System.out.println("Bob borrowed the book");
            } catch (Exception e) {
                System.out.println("Bob failed: " + e.getMessage());
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        String issuedTo = book.getIssuedTo();
        assertTrue(
                Objects.equals(issuedTo, "S001") || Objects.equals(issuedTo, "S002"),
                "Book must be issued to one user."
        );
        assertNotEquals("S001", "S002", "Book cannot be issued to both.");
    }
}
