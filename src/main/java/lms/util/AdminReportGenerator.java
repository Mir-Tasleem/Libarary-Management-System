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

            Class<?> clazz = book.getClass();

            while (clazz != null) {
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(book);
                        String displayValue = value != null ? value.toString() : "N/A";
                        System.out.printf("  %-15s: %s%n", field.getName(), displayValue);
                    } catch (IllegalAccessException e) {
                        System.out.printf("  %-15s: [ACCESS DENIED]%n", field.getName());
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }

        System.out.println("=====================================\n");
    }
}
