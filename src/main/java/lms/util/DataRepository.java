package lms.util;

import java.util.List;

public interface DataRepository<T> {
    List<T> load();
    void save(List<T> items);
}
