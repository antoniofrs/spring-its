package it.antoniofrisenda.SpringController.controller;

import it.antoniofrisenda.SpringController.dto.BookDto;
import it.antoniofrisenda.SpringController.exception.BookNotFoundException;
import it.antoniofrisenda.SpringController.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@DisplayName("BookController")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    // ── GET /api/books ────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/books → 200 con lista di libri")
    void getAll_returnsOkWithList() throws Exception {
        List<BookDto> books = List.of(
                new BookDto("1", "1984", "George Orwell"),
                new BookDto("2", "Il Nome della Rosa", "Umberto Eco")
        );
        when(bookService.getAll()).thenReturn(books);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("1984"))
                .andExpect(jsonPath("$[1].author").value("Umberto Eco"));
    }

    @Test
    @DisplayName("GET /api/books → 200 con lista vuota")
    void getAll_returnsEmptyList() throws Exception {
        when(bookService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ── GET /api/books/{id}/v1 ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/books/{id}/v1 → 200 con il libro trovato")
    void getById_returnsOkWithBook() throws Exception {
        BookDto book = new BookDto("1", "1984", "George Orwell");
        when(bookService.getById("1")).thenReturn(book);

        mockMvc.perform(get("/api/books/1/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("1984"))
                .andExpect(jsonPath("$.author").value("George Orwell"));
    }

    @Test
    @DisplayName("GET /api/books/{id}/v1 → 404 quando il libro non esiste")
    void getById_returnsNotFoundWhenBookNotFound() throws Exception {
        when(bookService.getById("99")).thenThrow(new BookNotFoundException("99"));

        mockMvc.perform(get("/api/books/99/v1"))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/books ───────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/books → 201 con body valido")
    void create_returnsCreatedWithBook() throws Exception {
        BookDto book = new BookDto("3", "Don Chisciotte", "Miguel de Cervantes");
        when(bookService.create(any(BookDto.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.title").value("Don Chisciotte"))
                .andExpect(jsonPath("$.author").value("Miguel de Cervantes"));
    }

    @Test
    @DisplayName("POST /api/books → 400 quando il titolo è blank")
    void create_returnsBadRequestWhenTitleIsBlank() throws Exception {
        BookDto invalidBook = new BookDto("4", "", "Some Author");

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/books → 400 quando l'autore è blank")
    void create_returnsBadRequestWhenAuthorIsBlank() throws Exception {
        BookDto invalidBook = new BookDto("5", "Some Title", "");

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/books → 400 quando titolo e autore sono null")
    void create_returnsBadRequestWhenBodyIsEmpty() throws Exception {
        BookDto invalidBook = new BookDto("6", null, null);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).create(any());
    }

    // ── DELETE /api/books/{id}/v1 ─────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/books/{id}/v1 → 204 no content")
    void delete_returnsNoContent() throws Exception {
        doNothing().when(bookService).delete("1");

        mockMvc.perform(delete("/api/books/1/v1"))
                .andExpect(status().isNoContent());

        verify(bookService).delete("1");
    }
}
