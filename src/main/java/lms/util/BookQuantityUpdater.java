package lms.util;

import lms.model.Book;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class BookQuantityUpdater {

    private final BookRepository bookRepo;
    private final BookQuantityRepository quantityRepo;
    private final ScheduledExecutorService scheduler;

    public BookQuantityUpdater() {
        this.bookRepo = new BookRepository();
        this.quantityRepo = new BookQuantityRepository();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void startUpdating() {
        Runnable updateTask = () -> {
            try {
                List<Book> books = bookRepo.load();
                Map<String, Integer> quantityMap = new HashMap<>();

                for (Book book : books) {
                    String key = book.getTitle().trim().toLowerCase() + "||" + book.getAuthor().trim().toLowerCase();
                    quantityMap.put(key, quantityMap.getOrDefault(key, 0) + 1);
                }

                quantityRepo.saveQuantities(quantityMap);
                System.out.println("Book quantities updated at: " + new Date());

            } catch (IOException e) {
                System.err.println("Error updating book quantities: " + e.getMessage());
            }
        };

        scheduler.scheduleAtFixedRate(updateTask, 0, 5, TimeUnit.MINUTES);
    }

    public void stopUpdating() {
        scheduler.shutdown();
    }
}
