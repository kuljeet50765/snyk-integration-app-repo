package com.aptia.snykintegrationapp.service;

import com.aptia.snykintegrationapp.model.Item;
import com.aptia.snykintegrationapp.model.ItemPatchRequest;
import com.aptia.snykintegrationapp.model.ItemRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ItemFileService {

    private static final TypeReference<List<Item>> ITEM_LIST_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Path storagePath;

    public ItemFileService(ObjectMapper objectMapper,
                           @Value("${storage.file.path:data/items.txt}") String storageFilePath) {
        this.objectMapper = objectMapper;
        this.storagePath = Path.of(storageFilePath);
        initializeStorage();
    }

    public synchronized List<Item> findAll() {
        return readItems();
    }

    public synchronized Optional<Item> findById(String id) {
        return readItems().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    public synchronized Item create(ItemRequest request) {
        List<Item> items = readItems();
        Item item = new Item(UUID.randomUUID().toString(), request.getName(), request.getDescription());
        items.add(item);
        writeItems(items);
        return item;
    }

    public synchronized Optional<Item> update(String id, ItemRequest request) {
        List<Item> items = readItems();

        for (Item item : items) {
            if (item.getId().equals(id)) {
                item.setName(request.getName());
                item.setDescription(request.getDescription());
                writeItems(items);
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    public synchronized Optional<Item> patch(String id, ItemPatchRequest request) {
        List<Item> items = readItems();

        for (Item item : items) {
            if (item.getId().equals(id)) {
                if (request.getName() != null && !request.getName().isBlank()) {
                    item.setName(request.getName());
                }
                if (request.getDescription() != null) {
                    item.setDescription(request.getDescription());
                }
                writeItems(items);
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    public synchronized boolean delete(String id) {
        List<Item> items = readItems();
        boolean removed = items.removeIf(item -> item.getId().equals(id));

        if (removed) {
            writeItems(items);
        }

        return removed;
    }

    private void initializeStorage() {
        try {
            if (storagePath.getParent() != null) {
                Files.createDirectories(storagePath.getParent());
            }
            if (!Files.exists(storagePath)) {
                Files.writeString(storagePath, "[]");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to initialize storage file", exception);
        }
    }

    private List<Item> readItems() {
        try {
            String content = Files.readString(storagePath);
            if (content.isBlank()) {
                return new ArrayList<>();
            }

            List<Item> items = objectMapper.readValue(content, ITEM_LIST_TYPE);
            return items == null ? new ArrayList<>() : new ArrayList<>(items);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read storage file", exception);
        }
    }

    private void writeItems(List<Item> items) {
        try {
            Files.writeString(storagePath, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(items));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to write storage file", exception);
        }
    }
}