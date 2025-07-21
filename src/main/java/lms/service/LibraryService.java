package lms.service;

import lms.exception.*;
import lms.model.*;
import lms.util.BookRepository;
import lms.util.IdGenerator;
import lms.util.UserRepository;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryService {
    private final List<Book> books;
    private final List<User> users;
    private final BookRepository bookRepo = new BookRepository();
    private final UserRepository userRepo = new UserRepository();


    public LibraryService() {
        this.books = bookRepo.load();
        this.users = userRepo.load();
        IdGenerator.initialize(books, users);
    }

    // Book operations
    public  synchronized void addBook(Book newBook) {
//        Optional<Book> existing = books.stream()
//                .filter(b -> b.getTitle().equalsIgnoreCase(newBook.getTitle()))
//                .filter(b -> b.getAuthor().equalsIgnoreCase(newBook.getAuthor()))
//                .filter(b -> Objects.equals(b.getPublishYear(), newBook.getPublishYear()))
//                .findFirst();
//
//
//        if (existing.isPresent()) {
////            existing.get().incrementQuantity(quantity);
//            System.out.println("Book with title: " + newBook.getTitle()+ "already exists. Increased quantity by: "+quantity);
//        } else {
//            books.add(newBook);
//            System.out.println("New book added: " + newBook);
//        }
//        saveBooks();
        books.add(newBook);
        System.out.println("Added new book with ID: " +newBook.getBookId());
        saveBooks();
    }


    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = books.stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());

        if (availableBooks.isEmpty()) {
            System.out.println("No books are currently available.");
        }

        return availableBooks;
    }



    public List<Book> searchBooksByTitle(String keyword) {
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public synchronized void borrowBook(String userId, String bookId) {
        User user = findUserById(userId);
        Book book = findBookById(bookId);

        if (!(user instanceof Student student)) {
            throw new UnauthorizedActionException("Only students can borrow books.");
        }

        if (!book.isAvailable()) {
            throw new BookAlreadyIssuedException("Book is already borrowed.");
        }

        if (!student.canBorrowMore()) {
            throw new BorrowLimitExceededException("Student has reached borrow limit.");
        }

        student.borrowBook(bookId);
        book.setIssuedTo(userId);
        book.setIssuedOn(new Date());

//        book.decrementQuantity();
        System.out.println("Book: " + "Borrowed by user: "+userId);
        saveBooks();
    }


    public synchronized void returnBook(String userId, String bookId) {
        User user = findUserById(userId);
        Book book = findBookById(bookId);

        if (!userId.equals(book.getIssuedTo()))
            throw new RuntimeException("Book was not borrowed by this user");

        if (user instanceof Student student)
            student.returnBook(bookId);

//        book.incrementQuantity();
        book.setIssuedTo(null);
        book.setIssuedOn(null);
        saveBooks();
        System.out.println("Book with id: " +bookId+ " returned by user:" +userId);
    }


    public synchronized boolean deleteBook(String bookId) {
        Book book = findBookById(bookId);
        boolean removed = books.remove(book);
        if (removed) saveBooks();
        return removed;
    }

    public synchronized boolean deleteUser(String userId) {
        User user = findUserById(userId);
        boolean removed = users.remove(user);
        if (removed) saveUsers();
        return removed;
    }

    public synchronized boolean forceReturnBook(String bookId) {
        Book book = findBookById(bookId);
        if (book.getIssuedTo() != null) {
            book.setIssuedTo(null);
            book.setIssuedOn(null);
            saveBooks();
//            book.incrementQuantity();
            return true;
        }
        return false;
    }

    public synchronized boolean addUser(User user) {
        for (User existing : users) {
            if (existing.getUserId().equalsIgnoreCase(user.getUserId())) {
                return false; // duplicate
            }
        }
        users.add(user);
        saveUsers();
        return true;
    }


    // User and book lookup
    public User findUserById(String id) {
        return users.stream()
                .filter(u -> u.getUserId().equals(id))
                .findFirst()
                .orElseThrow(() ->  new UserNotFoundException("User with ID " + id + " not found."));
    }

    public Book findBookById(String id) {
        return books.stream()
                .filter(b -> b.getBookId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found."));
    }

    public void issueBookByTitle(String userId, String titleKeyword) {
        User user = findUserById(userId);
        if (!(user instanceof Student)) {
            System.out.println("Only students can borrow books.");
            return;
        }

        Optional<Book> match = books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(titleKeyword.toLowerCase()))
                .filter(Book::isAvailable)
                .findFirst();

        if (match.isPresent()) {
            borrowBook(userId, match.get().getBookId());
            System.out.println("Book: " + match.get().getTitle() + "issued to Student:" + userId);
        } else {
            System.out.println("No available book found with that title.");
        }
    }




    // Save operations
    public void saveBooks() {
        bookRepo.save(books);
    }

    public void saveUsers() {
        userRepo.save(users);
    }

    public List<Book> getAllBooks() {
        return books;
    }

    public List<User> getAllUsers() {
        return users;
    }
}
