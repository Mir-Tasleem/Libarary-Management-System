package lms.model;

public class Librarian extends User {

    public Librarian(String userId, String name) {
        super(userId, name);
    }

    @Override
    public String getRole() {
        return "Librarian";
    }
}
