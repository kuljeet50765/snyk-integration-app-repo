package com.aptia.snykintegrationapp.service;

import com.aptia.snykintegrationapp.model.Item;
import com.aptia.snykintegrationapp.model.ItemPatchRequest;
import com.aptia.snykintegrationapp.model.ItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemFileServiceTest {

    @TempDir
    Path tempDir;

    private ItemFileService itemFileService;

    @BeforeEach
    void setUp() {
        Path storageFile = tempDir.resolve("items.txt");
        itemFileService = new ItemFileService(new ObjectMapper(), storageFile.toString());
    }

    @Test
    void shouldCreateAndReadItemsFromTextFile() {
        ItemRequest request = new ItemRequest();
        request.setName("First item");
        request.setDescription("Created in test");

        Item created = itemFileService.create(request);
        List<Item> items = itemFileService.findAll();

        assertThat(created.getId()).isNotBlank();
        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getName()).isEqualTo("First item");
    }

    @Test
    void shouldUpdatePatchAndDeleteItem() {
        ItemRequest createRequest = new ItemRequest();
        createRequest.setName("Original");
        createRequest.setDescription("Before update");

        Item created = itemFileService.create(createRequest);

        ItemRequest updateRequest = new ItemRequest();
        updateRequest.setName("Updated");
        updateRequest.setDescription("After update");

        Item updated = itemFileService.update(created.getId(), updateRequest).orElseThrow();

        ItemPatchRequest patchRequest = new ItemPatchRequest();
        patchRequest.setDescription("Patched description");

        Item patched = itemFileService.patch(created.getId(), patchRequest).orElseThrow();
        boolean deleted = itemFileService.delete(created.getId());

        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(patched.getDescription()).isEqualTo("Patched description");
        assertThat(deleted).isTrue();
        assertThat(itemFileService.findAll()).isEmpty();
    }
}