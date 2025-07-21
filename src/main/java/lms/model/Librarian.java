package lms.model;

public class Librarian extends User {

    public Librarian(String userId, String name) {
        super(userId, name);
    }

    // Librarians usually have admin privileges like adding/removing books

    @Override
    public String getRole() {
        return "Librarian";
    }
}
