//package lms.service;
//
//import lms.model.Book;
//import lms.util.CSVUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public class BookManager {
//
//    private List<Book> books;
//    private final String bookDataFile = "books.csv";
//
//    public BookManager(List<Book> books) {
//        this.books = new ArrayList<>(CSVUtils.loadBooks());
//
//    }
//
//    public void addBook(Book book) {
//        books.add(book);
//        saveBooks();
//    }
//
//    public List<Book> searchByTitle(String keyword) {
//        return books.stream()
//                .filter(book -> book.getTitle().toLowerCase().contains(keyword.toLowerCase()))
//                .collect(Collectors.toList());
//    }
//
//
//    public List<Book> getAvailableBooks() {
//        return books.stream()
//                .filter(book -> book.getIssuedTo() == null)
//                .collect(Collectors.toList());
//    }
//
//    public Book findBookById(String bookId) {
//        Optional<Book> optionalBook = books.stream()
//                .filter(book -> book.getBookId().equals(bookId))
//                .findFirst();
//        return optionalBook.orElse(null);
//    }
//
//    public void markAsIssued(String bookId, String userId) {
//        Book book = findBookById(bookId);
//        if (book != null && book.getIssuedTo() == null) {
//            book.setIssuedTo(userId);
//            book.setIssuedOn(new java.util.Date());
//            saveBooks();
//        }
//    }
//
//    public void markAsReturned(String bookId) {
//        Book book = findBookById(bookId);
//        if (book != null && book.getIssuedTo() != null) {
//            book.setIssuedTo(null);
//            book.setIssuedOn(null);
//            saveBooks();
//        }
//    }
//
//    public List<Book> getAllBooks() {
//        return books;
//    }
//
//    public void saveBooks() {
//        CSVUtils.saveBooks(books);
//
//    }
//}
