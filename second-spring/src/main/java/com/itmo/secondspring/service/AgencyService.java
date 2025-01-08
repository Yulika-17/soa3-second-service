package com.itmo.secondspring.service;

import com.itmo.secondspring.exception.NotFoundException;
import com.itmo.secondspring.model.Flat;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgencyService {

    private final RestTemplate restTemplate;

    @Value("${first-service.url}")
    private String FIRST_SERVICE_URL;

    @Value("${first-service.port}")
    private Long FIRST_SERVICE_PORT;

    public Integer getMostExpensiveFlat(long id1, long id2, long id3) {
        String url1 = String.format("%s:%d/api/v1/flats/%d", FIRST_SERVICE_URL, FIRST_SERVICE_PORT, id1);
        String url2 = String.format("%s:%d/api/v1/flats/%d", FIRST_SERVICE_URL, FIRST_SERVICE_PORT, id2);
        String url3 = String.format("%s:%d/api/v1/flats/%d", FIRST_SERVICE_URL, FIRST_SERVICE_PORT, id3);

        Flat flat1 = getFlatFromService(url1);
        Flat flat2 = getFlatFromService(url2);
        Flat flat3 = getFlatFromService(url3);

        return findMostExpensive(flat1, flat2, flat3);
    }

    private Flat getFlatFromService(String url) {
        try {
            ResponseEntity<Flat> response = restTemplate.getForEntity(url, Flat.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to retrieve flat from the first service");
            }
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("Flat not found at " + url);
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException("Failed to retrieve flat from first service, status: " + ex.getStatusCode());
        }
    }

    private Integer findMostExpensive(Flat flat1, Flat flat2, Flat flat3) {
        List<Flat> flats = Arrays.asList(flat1, flat2, flat3).stream()
                .filter(flat -> flat != null)
                .collect(Collectors.toList());

        if (flats.isEmpty()) {
            throw new NotFoundException("No flats found for the provided IDs");
        }

        return flats.stream()
                .max(Comparator.comparing(Flat::getPrice))
                .map(Flat::getId)
                .orElseThrow(() -> new NotFoundException("Could not determine the most expensive flat"));
    }

    public List<Flat> getFlatsOrderedByTimeToMetro(boolean byTransport, boolean desc) {
        String url = String.format("%s:%d/api/v1/flats/all", FIRST_SERVICE_URL, FIRST_SERVICE_PORT);

        try {
            ResponseEntity<Flat[]> response = restTemplate.getForEntity(url, Flat[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new NotFoundException("No flats found with the specified criteria");
            }

            List<Flat> flats = Arrays.asList(response.getBody());

            if (flats.isEmpty()) {
                throw new NotFoundException("No flats found with the specified criteria");
            }

            Comparator<Flat> comparator;
            if (byTransport) {
                comparator = Comparator.comparing(Flat::getTimeToMetroByTransport, Comparator.nullsLast(Comparator.naturalOrder()));
            } else {
                comparator = Comparator.comparing(Flat::getTimeToMetroByFoot, Comparator.nullsLast(Comparator.naturalOrder()));
            }

            if (desc) {
                comparator = comparator.reversed();
            }

            return flats.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException.NotFound ex) {
            throw new NotFoundException("No flats found with the specified criteria");
        }
    }

}
