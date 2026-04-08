package com.aptia.snykintegrationapp.controller;

import com.aptia.snykintegrationapp.model.Item;
import com.aptia.snykintegrationapp.service.ItemFileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemFileService itemFileService;

    @Test
    void shouldReturnAllItems() throws Exception {
        given(itemFileService.findAll()).willReturn(List.of(new Item("1", "Sample", "Example")));

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Sample"));
    }

    @Test
    void shouldReturnSingleItem() throws Exception {
        given(itemFileService.findById("1")).willReturn(Optional.of(new Item("1", "Sample", "Example")));

        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Example"));
    }

    @Test
    void shouldCreateItem() throws Exception {
        given(itemFileService.create(any())).willReturn(new Item("1", "Created", "From POST"));

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"name\": \"Created\",
                                  \"description\": \"From POST\"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void shouldUpdateAndPatchItem() throws Exception {
        given(itemFileService.update(eq("1"), any())).willReturn(Optional.of(new Item("1", "Updated", "From PUT")));
        given(itemFileService.patch(eq("1"), any())).willReturn(Optional.of(new Item("1", "Updated", "From PATCH")));

        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"name\": \"Updated\",
                                  \"description\": \"From PUT\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("From PUT"));

        mockMvc.perform(patch("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"description\": \"From PATCH\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("From PATCH"));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        given(itemFileService.delete("1")).willReturn(true);

        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());

        verify(itemFileService).delete("1");
    }
}