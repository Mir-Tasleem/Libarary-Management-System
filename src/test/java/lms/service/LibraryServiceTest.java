package lms.service;

import lms.exception.*;
import lms.model.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceTest {

    private LibraryService service;
    private Student student;
    private Book book;

    @BeforeEach
    public void setUp() {
        service = new LibraryService();
        student = new Student("S100", "Test Student");
        book = new Book("B100", "Test Book", "Author", "2020");
        service.addUser(student);
        service.addBook(book);
    }

    @Test
    public void testAddBook() {
        Book newBook = new Book("B101", "New Book", "Author2", "2021");
        service.addBook(newBook);
        assertTrue(service.getAllBooks().contains(newBook));
    }

    @Test
    public void testSearchBooksByTitle() {
        List<Book> results = service.searchBooksByTitle("Test Book");
        assertFalse(results.isEmpty());
    }

    @Test
    public void testBorrowBook() {
        assertDoesNotThrow(() -> service.borrowBook(student.getUserId(), book.getBookId()));
        assertNotNull(service.findBookById(book.getBookId()).getIssuedTo());
    }

    @Test
    public void testReturnBook() {
        service.borrowBook(student.getUserId(), book.getBookId());
        assertDoesNotThrow(() -> service.returnBook(student.getUserId(), book.getBookId()));
        assertNull(service.findBookById(book.getBookId()).getIssuedTo());
    }

    @Test
    public void testDeleteBook() {
        assertTrue(service.deleteBook(book.getBookId()));
    }

    @Test
    public void testDeleteUser() {
        assertTrue(service.deleteUser(student.getUserId()));
    }

    @Test
    public void testAddUser() {
        Student newStudent = new Student("S101", "Another Student");
        assertTrue(service.addUser(newStudent));
    }

    @Test
    public void testFindBookById() {
        assertEquals(book, service.findBookById(book.getBookId()));
    }

    @Test
    public void testFindUserById() {
        assertEquals(student, service.findUserById(student.getUserId()));
    }

    @Test
    public void testIssueBookByTitle() {
        assertDoesNotThrow(() -> service.issueBookByTitle(student.getUserId(), "Test Book"));
    }

    @Test
    public void testGetAvailableBooks() {
        List<Book> available = service.getAvailableBooks();
        assertTrue(available.contains(book));
        service.borrowBook(student.getUserId(), book.getBookId());
        available = service.getAvailableBooks();
        assertFalse(available.contains(book));
    }
}
