package me.staek.upload.domain;

import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.Map;
@Repository
public class ItemFileRepository {
    private final Map<Long, FileItem> store = new HashMap<>();
    private long sequence = 0L;
    public FileItem save(FileItem item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }
    public FileItem findById(Long id) {
        return store.get(id);
    }
}
