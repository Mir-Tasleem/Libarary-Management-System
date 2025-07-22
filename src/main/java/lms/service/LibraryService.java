package lms.service;

import lms.exception.*;
import lms.model.*;
import lms.util.BookRepository;
import lms.util.IdGenerator;
import lms.util.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class LibraryService {
    private final List<Book> books = Collections.synchronizedList(new ArrayList<>());
    private final List<User> users = Collections.synchronizedList(new ArrayList<>());
    private final BookRepository bookRepo = new BookRepository();
    private final UserRepository userRepo = new UserRepository();

    public LibraryService() {
        books.addAll(bookRepo.load());
        users.addAll(userRepo.load());
        IdGenerator.initialize(books, users);
    }

    public void addBook(Book newBook) {
        synchronized (books) {
            books.add(newBook);
            System.out.println("Added new book with ID: " + newBook.getBookId());
            saveBooks();
        }
    }

    public List<Book> getAvailableBooks() {
        synchronized (books) {
            return books.stream()
                    .filter(Book::isAvailable)
                    .collect(Collectors.toList());
        }
    }

    public void printAvailableBookDetails() {
        List<Book> available;
        synchronized (books) {
            available = getAvailableBooks().stream()
                    .filter(b -> !(b instanceof EBook)) // exclude eBooks
                    .collect(Collectors.toList());
        }
        for (Book book : available) {
            System.out.println("ID: " + book.getBookId());
            System.out.println("Title: " + book.getTitle());
            System.out.println("Author: " + book.getAuthor());
            System.out.println("Published Year: " + book.getPublishYear());
            System.out.println("------------");
        }
    }

    public List<Book> searchBooksByTitle(String keyword) {
        synchronized (books) {
            return books.stream()
                    .filter(b -> b.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    public void borrowBook(String userId, String bookId) {
        Book book = findBookById(bookId);
        synchronized (book) {
            User user = findUserById(userId);

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
            System.out.println("Book borrowed by user: " + userId);
            saveBooks();
        }
    }

    public void returnBook(String userId, String bookId) {
        synchronized (this) {
            User user = findUserById(userId);
            Book book = findBookById(bookId);

            if (!userId.equals(book.getIssuedTo())) {
                throw new RuntimeException("Book was not borrowed by this user");
            }

            if (user instanceof Student student) {
                student.returnBook(bookId);
            }

            book.setIssuedTo(null);
            book.setIssuedOn(null);
            saveBooks();
            System.out.println("Book with ID: " + bookId + " returned by user: " + userId);
        }
    }

    public boolean deleteBook(String bookId) {
        synchronized (books) {
            Book book = findBookById(bookId);
            boolean removed = books.remove(book);
            if (removed) saveBooks();
            return removed;
        }
    }

    public boolean deleteUser(String userId) {
        synchronized (users) {
            User user = findUserById(userId);
            boolean removed = users.remove(user);
            if (removed) saveUsers();
            return removed;
        }
    }

    public boolean forceReturnBook(String bookId) {
        synchronized (books) {
            Book book = findBookById(bookId);
            if (book.getIssuedTo() != null) {
                book.setIssuedTo(null);
                book.setIssuedOn(null);
                saveBooks();
                return true;
            }
            return false;
        }
    }

    public boolean addUser(User user) {
        synchronized (users) {
            for (User existing : users) {
                if (existing.getUserId().equalsIgnoreCase(user.getUserId())) {
                    return false;
                }
            }
            users.add(user);
            saveUsers();
            return true;
        }
    }

    public User findUserById(String id) {
        synchronized (users) {
            return users.stream()
                    .filter(u -> u.getUserId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));
        }
    }

    public Book findBookById(String id) {
        synchronized (books) {
            return books.stream()
                    .filter(b -> b.getBookId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found."));
        }
    }

    public void issueBookByTitle(String userId, String titleKeyword) {
        try {
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
                System.out.println("Book: " + match.get().getTitle() + " issued to Student: " + userId);
            } else {
                System.out.println("No available book found with that title.");
            }
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error issuing book: " + e.getMessage());
        }
    }


    public void saveBooks() {
        synchronized (books) {
            bookRepo.save(books);
        }
    }

    public void saveUsers() {
        synchronized (users) {
            userRepo.save(users);
        }
    }

    public List<Book> getAllBooks() {
        synchronized (books) {
            return new ArrayList<>(books); // return copy to avoid external modification
        }
    }

    public List<User> getAllUsers() {
        synchronized (users) {
            return new ArrayList<>(users);
        }
    }
}
