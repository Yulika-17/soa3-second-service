package com.itmo.secondspring.controller;

import com.itmo.secondspring.exception.InvalidQueryParameterException;
import com.itmo.secondspring.exception.NotFoundException;
import com.itmo.secondspring.model.Flat;
import com.itmo.secondspring.service.AgencyService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/agency", produces = MediaType.APPLICATION_XML_VALUE)
public class AgencyController {

    private final AgencyService agencyService;

    @GetMapping("/get-most-expensive/{id1}/{id2}/{id3}")
    public ResponseEntity<Long> getMostExpensiveFlat(
            @PathVariable Long id1,
            @PathVariable Long id2,
            @PathVariable Long id3
    ) {
        Long mostExpensiveFlatId = Long.valueOf(agencyService.getMostExpensiveFlat(id1, id2, id3));

        if (mostExpensiveFlatId == null) {
            throw new NotFoundException("Most expensive flat not found for the provided IDs");
        }

        return new ResponseEntity<>(mostExpensiveFlatId, HttpStatus.OK);
    }

    @GetMapping("/get-ordered-by-time-to-metro/{by-transport}/{desc}")
    public ResponseEntity<List<Flat>> getFlatsOrderedByTimeToMetro(
            @PathVariable("by-transport") boolean byTransport,
            @PathVariable("desc") boolean desc) {
        if (!isValidBoolean(byTransport) || !isValidBoolean(desc)) {
            throw new InvalidQueryParameterException("Invalid boolean values for by-transport or desc");
        }

        List<Flat> flats = agencyService.getFlatsOrderedByTimeToMetro(byTransport, desc);

        if (flats.isEmpty()) {
            throw new NotFoundException("No flats found with the specified criteria");
        }

        return new ResponseEntity<>(flats, HttpStatus.OK);
    }

    private boolean isValidBoolean(Boolean value) {
        return value != null;
    }

}