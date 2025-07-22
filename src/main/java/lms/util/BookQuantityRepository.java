package lms.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class BookQuantityRepository {

    private static final String QUANTITY_CSV = "book_quantities.csv";

    public void saveQuantities(Map<String, Integer> quantityMap) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(QUANTITY_CSV))) {
            writer.println("Title,Author,Quantity");
            for (Map.Entry<String, Integer> entry : quantityMap.entrySet()) {
                String[] parts = entry.getKey().split("\\|\\|");
                writer.printf("%s,%s,%d%n", parts[0], parts[1], entry.getValue());
            }
        }
    }
}
