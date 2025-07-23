package lms.service;

import lms.exception.BookNotFoundException;
import lms.exception.UserNotFoundException;
import lms.model.*;
import lms.util.AdminReportGenerator;
import lms.util.IdGenerator;

import java.util.*;
import java.util.Scanner;
import java.util.stream.Collectors;

public class UserManager {
    private final LibraryService libraryService;

    public UserManager(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    public User login(String userId) {
        try {
            User user = libraryService.findUserById(userId);
            System.out.println("User: "+userId+" logged successful as " + user.getRole());
            return user;
        } catch (UserNotFoundException e) {
            System.out.println("User not found.");
            return null;
        }
    }
    public User registerUser(String name, String role) {
        User newUser;
        if (role.equalsIgnoreCase("Student")) {
            newUser = new Student(IdGenerator.nextStudentId(), name);
            System.out.println("New Student created with ID: " + newUser.getUserId());
        } else if (role.equalsIgnoreCase("Librarian")) {
            newUser = new Librarian(IdGenerator.nextLibrarianId(), name);
            System.out.println("New Librarian created with ID: " + newUser.getUserId());
        } else {
            System.out.println("Invalid role.");
            return null;
        }

        if (libraryService.addUser(newUser)) {
            return newUser;
        } else {
            System.out.println("User already exists.");
            return null;
        }
    }


    public void handleUserActions(User user) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            if (user instanceof Student) {
                System.out.println("""
                    Student Menu:
                    1. List of Available Books
                    2. Search by Title
                    3. Borrow Book
                    4. My Issued Books
                    5. Return Book
                    6. View eBooks
                    7. Exit
                    """);
                System.out.print("Enter Choice: ");
                switch (scanner.next()) {
                    case "1" -> libraryService.printAvailableBookDetails();
                    case "2" -> {
                        System.out.print("Enter keyword: ");
                        String keyword = scanner.next();
                        libraryService.searchBooksByTitle(keyword).forEach(System.out::println);
                    }
                    case "3" -> {
                        System.out.print("Enter Book ID: ");
                        String id = scanner.next();
                        try {
                            libraryService.borrowBook(user.getUserId(), id);
                        } catch (BookNotFoundException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    case "4" -> {
                        List<Book> issuedBooks = libraryService.getAllBooks().stream()
                                .filter(b -> user.getUserId().equals(b.getIssuedTo()))
                                .collect(Collectors.toList());

                        if (issuedBooks.isEmpty()) {
                            System.out.println("No books currently issued to you.");
                        } else {
                            System.out.println("Books issued to you:");
                            issuedBooks.forEach(b -> System.out.println("   BookId:" +b.getBookId() + " -Title" + b.getTitle()));
                        }
                    }
                    case "5" -> {
                        System.out.print("Enter Book ID: ");
                        String id = scanner.next();
                        try {
                            libraryService.returnBook(user.getUserId(), id);
                        }catch (BookNotFoundException e){
                            System.out.println(e.getMessage());
                        }
                    }
                    case "6" -> {
                        List<Book> ebooks = libraryService.getAllBooks().stream()
                                .filter(b -> b instanceof EBook)
                                .collect(Collectors.toList());

                        if (ebooks.isEmpty()) {
                            System.out.println("No eBooks available.");
                        } else {
                            System.out.println("Available eBooks:");
                            for (Book book : ebooks) {
                                EBook ebook = (EBook) book;
                                System.out.println("- Title: " + ebook.getTitle());
                                System.out.println("  Author: " + ebook.getAuthor());
                                System.out.println("  Year: " + ebook.getPublishYear());
                                System.out.println("  Download: " + ebook.getDownloadLink());
                                System.out.println("-----------------------------");
                            }
                        }
                    }
                    case "7" -> exit = true;
                    default -> System.out.println("Invalid choice");
                }
            } else if (user instanceof Librarian) {
                System.out.println("""
                        \nLibrarian Menu:
                        1. List All Books
                        2. Add Book
                        3. Add User
                        4. Delete Book
                        5. Delete User
                        6. Return Book
                        7. Generate Report
                        8. List All Users
                        9. Issue Book by Title to Student
                        10. Exit
                    """);
                System.out.print("Enter Choice: ");
                switch (scanner.next()) {
                    case "1" -> libraryService.getAllBooks().forEach(System.out::println);
                    case "2" -> {
                        scanner.nextLine();
                        String id=IdGenerator.nextBookId();
                        System.out.print("Enter Title: ");
                        String title = scanner.next();
                        System.out.print("Enter Author: ");
                        String author = scanner.next();
                        String year;
                        System.out.print("Enter Publishing Year: ");
                        while (true) {
                            year = scanner.next();
                            if (year.matches("\\d+")) {
                                break;
                            } else {
                                System.out.println("Please enter digits only for the year.");
                            }
                        }
                        System.out.println("Is this an eBook? (Yes or No):");
                        String isEBook = scanner.next();
                        if (isEBook.equalsIgnoreCase("yes")) {
                            EBook ebook = new EBook(id, title, author, year);
                            ebook.setDownloadLink(title);
                            libraryService.addBook(ebook);
                        } else {
                            Book newBook = new Book(id, title, author, year);
                            libraryService.addBook(newBook);
                        }
                    }
                    case "3" -> {
                        scanner.nextLine();
                        System.out.print("Enter name: ");
                        String name = scanner.next();
                        System.out.print("Enter role (Student/Librarian): ");
                        String role = scanner.next();

                        User newUser;
                        String newId;
                        if (role.equalsIgnoreCase("Student")) {
                            newId=IdGenerator.nextStudentId();
                            newUser = new Student(newId, name);
                        } else if (role.equalsIgnoreCase("Librarian")) {
                            newId=IdGenerator.nextLibrarianId();
                            newUser = new Librarian(newId, name);
                        } else {
                            System.out.println("Invalid role.");
                            break;
                        }

                        if (libraryService.addUser(newUser)) {
                            System.out.println("User with Id: " + newId +" added successfully.");
                        }
                    }
                    case "4" -> {
                        scanner.nextLine();
                        System.out.print("Enter Book ID to delete: ");
                        String delId = scanner.next();
                        try {
                            if(libraryService.deleteBook(delId)) {
                                System.out.println("Book deleted.");
                            }
                        } catch (BookNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case "5" -> {
                        scanner.nextLine();
                        System.out.print("Enter User ID to delete: ");
                        String userId = scanner.next();
                        try {
                            if (libraryService.deleteUser(userId))
                                System.out.println("User deleted.");
                        } catch (UserNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case "6" -> {
                        scanner.nextLine();
                        System.out.print("Enter Book ID to force return: ");
                        String bid = scanner.next();

                        try {
                            if (libraryService.forceReturnBook(bid)) {
                                System.out.println("Book returned.");
                            } else {
                                System.out.println("Book is not currently issued.");
                            }
                        } catch (BookNotFoundException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                    case "7" -> AdminReportGenerator.generateReport(libraryService.getAllBooks());
                    case "8" -> libraryService.getAllUsers().forEach(System.out::println);
                    case "9" -> {
                        scanner.nextLine();
                        System.out.print("Enter student user ID: ");
                        String uid = scanner.next();
                        System.out.print("Enter title keyword: ");
                        String keyword = scanner.next();
                        libraryService.issueBookByTitle(uid, keyword);
                    }
                    case "10" -> exit = true;
                    default -> System.out.println("Invalid choice");
                }
            }
        }
    }
}
