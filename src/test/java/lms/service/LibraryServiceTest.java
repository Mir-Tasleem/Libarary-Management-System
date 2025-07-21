package lms.service;

import lms.exception.*;
import lms.model.Book;
import lms.model.Student;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceTest {

    private LibraryService libraryService;

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService();
    }

    @Test
    void testBorrowAndReturnBook() {
        String bookId = "B010";
        String studentId = "S010";

        Book book = new Book(bookId, "Test Book", "Author", "2022");
        Student student = new Student(studentId, "John");

        libraryService.addBook(book);
        libraryService.addUser(student);

        libraryService.borrowBook(studentId, bookId);
        assertEquals(studentId, libraryService.findBookById(bookId).getIssuedTo());

        libraryService.returnBook(studentId, bookId);
        assertNull(libraryService.findBookById(bookId).getIssuedTo());
    }

    @Test
    void testBorrowLimitExceeded() {
        Student student = new Student("S011", "Limit Tester");
        libraryService.addUser(student);

        for (int i = 0; i < 3; i++) {
            Book book = new Book("B01" + i, "Book " + i, "Author", "2020");
            libraryService.addBook(book);
            libraryService.borrowBook("S011", "B01" + i);
        }

        Book extraBook = new Book("B999", "Extra Book", "Author", "2021");
        libraryService.addBook(extraBook);

        assertThrows(BorrowLimitExceededException.class, () -> {
            libraryService.borrowBook("S011", "B999");
        });
    }

    @Test
    void testAddDuplicateBookIncreasesQuantity() {
        Book book1 = new Book("B020", "Duplicate", "Same Author", "2022");
        Book book2 = new Book("B021", "Duplicate", "Same Author", "2022");

        libraryService.addBook(book1);
        libraryService.addBook(book2);

        long quantity = libraryService.getAllBooks().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase("Duplicate"))
                .mapToLong(Book::getQuantity)
                .sum();

        assertTrue(quantity >= 2);
    }

    @Test
    void testDeleteUser() {
        Student student = new Student("S013", "Mark");
        libraryService.addUser(student);

        boolean deleted = libraryService.deleteUser("S013");
        assertTrue(deleted);
        assertThrows(UserNotFoundException.class, () -> libraryService.findUserById("S013"));
    }

    @Test
    void testSearchBookByTitle() {
        Book book = new Book("B030", "Java Concurrency", "Author", "2023");
        libraryService.addBook(book);

        List<Book> result = libraryService.searchBooksByTitle("concurrency");
        assertFalse(result.isEmpty());
    }
}
