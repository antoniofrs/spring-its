package it.antoniofrisenda.SpringController.service;

import it.antoniofrisenda.SpringController.dto.BookDto;
import it.antoniofrisenda.SpringController.exception.BookNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private final List<BookDto> books = new ArrayList<>();

    public List<BookDto> getAll() {
        return books;
    }

    public BookDto getById(String id) {
        return books.stream().filter(book -> book.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public BookDto create(BookDto book) {
        books.add(book);
        return book;
    }

    public void delete(String id) {
        books.removeIf(b -> b.getId().equals(id));
    }
}
