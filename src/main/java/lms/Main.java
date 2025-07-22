package lms;

import lms.model.User;
import lms.service.LibraryService;
import lms.service.UserManager;
import lms.util.BookQuantityUpdater;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        LibraryService libraryService = new LibraryService();
        UserManager userManager = new UserManager(libraryService);

        BookQuantityUpdater updater = new BookQuantityUpdater();
        updater.startUpdating();

        // Start auto backup
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            libraryService.saveBooks();
            System.out.println("[Auto Backup] Books saved.");

        }, 0, 5, TimeUnit.MINUTES);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nWelcome to LMS");
            System.out.println("\nChoose an option:");
            System.out.println("1. Login");
            System.out.println("2. Create New Account");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter User ID to login: ");
                String userId = scanner.nextLine();
                User user = userManager.login(userId);
                if (user != null) {
                    userManager.handleUserActions(user);
                }
            } else if (choice == 2) {
                System.out.print("Enter your name: ");
                String name = scanner.nextLine();
                System.out.print("Enter role (Student/Librarian): ");
                String role = scanner.nextLine();

                User newUser = userManager.registerUser(name, role);
                if (newUser != null) {
                    System.out.println("Account created. Logging in...");
                    userManager.handleUserActions(newUser);
                }
            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }

        scheduler.shutdown();
        updater.stopUpdating();

        libraryService.saveBooks();
        libraryService.saveUsers();
        System.out.println("System shutdown complete. Goodbye!");
    }
}
