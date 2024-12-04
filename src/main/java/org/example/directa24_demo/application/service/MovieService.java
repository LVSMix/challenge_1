package org.example.directa24_demo.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Value("${movies.api.base-url}")
    private String baseUrl;

    public List<String> getDirectors(int threshold) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        Map<String, Integer> directorCount = new HashMap<>();
        int totalPages = 1;

        try {
            // Fetch the first page to determine total pages
            Map<String, Object> response = fetchPage(restTemplate, 1);
            totalPages = (int) response.get("total_pages");

            // Iterate through all pages
            for (int i = 1; i <= totalPages; i++) {
                response = fetchPage(restTemplate, i);
                List<Map<String, String>> movies = (List<Map<String, String>>) response.get("data");
                for (Map<String, String> movie : movies) {
                    String director = movie.get("Director");
                    directorCount.put(director, directorCount.getOrDefault(director, 0) + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Filter and sort the directors
        return directorCount.entrySet().stream()
                .filter(entry -> entry.getValue() > threshold)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    private Map<String, Object> fetchPage(RestTemplate restTemplate, int page) {
        String response = restTemplate.getForObject(baseUrl + page,String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing response: " + e.getMessage(), e);
        }
    }
}
