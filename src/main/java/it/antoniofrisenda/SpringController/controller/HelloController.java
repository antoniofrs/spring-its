package it.antoniofrisenda.SpringController.controller;

import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;

@RestController
@RequestMapping("/api/test")
public class HelloController {

    @GetMapping("/hello/v1")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/{id}/v1")
    public String getPathVariable(@PathVariable String id) {
        return "Hai inserito l'id' : " + id;
    }

    @GetMapping("/v1")
    public String requestParam(@RequestParam String parametro) {
        return "Hai inserito il query parameter: " + parametro;
    }

    @GetMapping("/client/v1")
    public String getClient(@RequestHeader("X-Client-Id") String clientId) {
        return "Hai inserito l' header: " + clientId;
    }

    @PostMapping("/v1")
    public String postRequest(@RequestBody String body) {
        return "Hai inserito il body: " + body;
    }

}
