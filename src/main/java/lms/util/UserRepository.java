package lms.util;

import lms.model.*;

import java.io.*;
import java.util.*;

public class UserRepository implements DataRepository<User> {
    private static final String FILE = "users.csv";

    @Override
    public List<User> load() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 3) {
                    String id = data[0];
                    String name = data[1];
                    String role = data[2];
                    if ("Student".equalsIgnoreCase(role)) {
                        users.add(new Student(id, name));
                    } else if ("Librarian".equalsIgnoreCase(role)) {
                        users.add(new Librarian(id, name));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    @Override
    public void save(List<User> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE))) {
            for (User user : users) {
                writer.println(String.join(",",
                        user.getUserId(),
                        user.getName(),
                        user.getRole()));
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
}
