package org.example.directa24_demo.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private MovieService movieService;

    private final String baseUrl = "http://eron-movies.wiremockapi.cloud/api/movies/search?page=";

    @BeforeEach
    void setUp() throws IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        movieService = new MovieService();
        // Inyectar manualmente el valor en el campo privado baseUrl
        FieldUtils.writeField(movieService, "baseUrl", baseUrl, true);
    }

    @Test
    void testGetDirectors() {
        // Simular respuesta de la API
        Map<String, Object> page1Response = new HashMap<>();
        page1Response.put("total_pages", 1);
        page1Response.put("data", List.of(
                Map.of("Director", "Director A"),
                Map.of("Director", "Director B"),
                Map.of("Director", "Director A")
        ));

        when(restTemplate.getForObject(eq(baseUrl + 1), eq(String.class)))
                .thenReturn(toJson(page1Response));

        // Ejecutar el método
        List<String> directors = movieService.getDirectors(1);

        // Validar resultados
        assertEquals(6, directors.size());
        assertEquals("Clint Eastwood", directors.get(0));
    }

    private String toJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing to JSON", e);
        }
    }

    @Test
    void testGetDirectorsNoResults() {
        // Simular respuesta de la API sin directores que superen el umbral
        Map<String, Object> page1Response = new HashMap<>();
        page1Response.put("total_pages", 1);
        page1Response.put("data", List.of(
                Map.of("Director", "Director A"),
                Map.of("Director", "Director B")
        ));

        when(restTemplate.getForObject(eq(baseUrl + 1), eq(String.class)))
                .thenReturn(toJson(page1Response));

        // Ejecutar el método con un umbral alto
        List<String> directors = movieService.getDirectors(3);

        // Validar resultados
        assertEquals(4, directors.size());
        
    }
}
