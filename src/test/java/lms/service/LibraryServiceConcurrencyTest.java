package lms.service;

import lms.exception.BookAlreadyIssuedException;
import lms.model.Book;
import lms.model.Student;
import lms.model.User;
import lms.util.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceConcurrencyTest {

    private LibraryService service;

    @BeforeEach
    public void setup() {
        // In-memory mock repositories
        InMemoryBookRepo bookRepo = new InMemoryBookRepo();
        InMemoryUserRepo userRepo = new InMemoryUserRepo();
        service = new LibraryService();

        // Add book
        Book book = new Book("B001", "Concurrent Programming", "Author A", "2022");
        bookRepo.load().add(book);

        // Add users
        Student s1 = new Student("S001", "Alice");
        Student s2 = new Student("S002", "Bob");
        userRepo.load().addAll(List.of(s1, s2));
    }

    @Test
    public void testConcurrentBorrowingSameBook() throws InterruptedException {
        AtomicReference<String> result1 = new AtomicReference<>();
        AtomicReference<String> result2 = new AtomicReference<>();

        Thread t1 = new Thread(() -> {
            try {
                service.borrowBook("S001", "B001");
                result1.set("success");
            } catch (BookAlreadyIssuedException e) {
                result1.set("already issued");
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                service.borrowBook("S002", "B001");
                result2.set("success");
            } catch (BookAlreadyIssuedException e) {
                result2.set("already issued");
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        List<String> results = List.of(result1.get(), result2.get());
        assertTrue(results.contains("success"));
        assertTrue(results.contains("already issued"));
        assertNotEquals(result1.get(), result2.get());
    }

    // --- In-memory Repos ---

    static class InMemoryBookRepo implements DataRepository<Book> {
        private final List<Book> books = Collections.synchronizedList(new ArrayList<>());
        public List<Book> load() { return books; }
        public void save(List<Book> books) {}
    }

    static class InMemoryUserRepo implements DataRepository<User> {
        private final List<User> users = Collections.synchronizedList(new ArrayList<>());
        public List<User> load() { return users; }
        public void save(List<User> users) {}
    }
}
