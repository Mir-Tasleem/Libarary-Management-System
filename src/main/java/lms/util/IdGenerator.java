package lms.util;

import lms.model.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static AtomicInteger bookCounter = new AtomicInteger(0);
    private static AtomicInteger studentCounter = new AtomicInteger(0);
    private static AtomicInteger librarianCounter = new AtomicInteger(0);

    public static void initialize(List<Book> books, List<User> users) {
        int maxBook = books.stream()
                .mapToInt(b -> parseNumericPart(b.getBookId(), "B"))
                .max().orElse(0);
        bookCounter.set(maxBook);

        int maxStudent = users.stream()
                .filter(u -> u instanceof Student)
                .mapToInt(u -> parseNumericPart(u.getUserId(), "S"))
                .max().orElse(0);
        studentCounter.set(maxStudent);

        int maxLibrarian = users.stream()
                .filter(u -> u instanceof Librarian)
                .mapToInt(u -> parseNumericPart(u.getUserId(), "L"))
                .max().orElse(0);
        librarianCounter.set(maxLibrarian);
    }

    private static int parseNumericPart(String id, String prefix) {
        if (id != null && id.startsWith(prefix)) {
            try {
                return Integer.parseInt(id.substring(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public static String nextBookId() {
        return "B" + String.format("%03d", bookCounter.incrementAndGet());
    }

    public static String nextStudentId() {
        return "S" + String.format("%03d", studentCounter.incrementAndGet());
    }

    public static String nextLibrarianId() {
        return "L" + String.format("%03d", librarianCounter.incrementAndGet());
    }
}
