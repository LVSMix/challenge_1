package org.example.directa24_demo.infraestructure.controller;



import org.example.directa24_demo.application.service.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DirectorController {

    private final MovieService movieService;

    public DirectorController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping(value = "/directors", produces = "application/json")
    public ResponseEntity<Map<String, List<String>>> getDirectors(@RequestParam int threshold) {
        List<String> directors = movieService.getDirectors(threshold);
        return ResponseEntity.ok(Collections.singletonMap("directors", directors));
    }
}
