package lms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student extends User {
    private static final int MAX_BORROW_LIMIT = 3;
    private List<String> borrowedBookIds = new ArrayList<>();

    public Student(String userId, String name) {
        super(userId, name);
    }

    public boolean canBorrowMore() {
        return borrowedBookIds.size() < MAX_BORROW_LIMIT;
    }

    public void borrowBook(String bookId) {
        if (canBorrowMore()) {
            borrowedBookIds.add(bookId);
        } else {
            throw new RuntimeException("Borrow limit reached!");
        }
    }

    public void returnBook(String bookId) {
        borrowedBookIds.remove(bookId);
    }

    public List<String> getBorrowedBookIds() {
        return borrowedBookIds;
    }

    @Override
    public String getRole() {
        return "Student";
    }


    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
