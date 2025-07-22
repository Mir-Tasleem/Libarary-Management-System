package lms.service;

import lms.exception.BookAlreadyIssuedException;
import lms.model.Book;
import lms.model.Student;
import lms.model.User;
import lms.util.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.spec.PSource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceConcurrencyTest {
    @Test
    public void sampleTest(){
        System.out.println("concurrency test is running");
    }
    private LibraryService service;


    @BeforeEach
    public void setup() {
        service = new LibraryService();

        service.getAllBooks().add(new Book("B001", "Concurrent Programming", "Author A", "2022"));
        service.getAllUsers().add(new Student("S001", "Alice"));
        service.getAllUsers().add(new Student("S002", "Bob"));
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

        // One should succeed, the other should fail
        assertTrue(results.contains("success"));
        assertTrue(results.contains("already issued"));
        assertNotEquals(result1.get(), result2.get());
    }

    // ---- In-memory Repos ----

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
