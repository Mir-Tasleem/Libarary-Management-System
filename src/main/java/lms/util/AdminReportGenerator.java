package lms.util;

import lms.model.Book;

import java.lang.reflect.Field;
import java.util.List;

public class AdminReportGenerator {
    public static void generateReport(List<Book> books) {
        System.out.println("\n========= ADMIN BOOK REPORT =========");
        System.out.println("Total books: " + books.size());

        for (Book book : books) {
            System.out.println("\nBook: " + book.getBookId());
            for (Field field : Book.class.getDeclaredFields()) {
                field.setAccessible(true); // allows access to private fields
                try {
                    Object value = field.get(book);
                    System.out.printf("  %-15s: %s%n", field.getName(), value);
                } catch (IllegalAccessException e) {
                    System.out.printf("  %-15s: [ACCESS DENIED]%n", field.getName());
                }
            }
        }

        System.out.println("=====================================\n");
    }
}
