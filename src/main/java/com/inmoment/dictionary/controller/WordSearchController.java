package com.inmoment.dictionary.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.inmoment.dictionary.service.WordSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

/**
 * @author Rashmi
 */

@Component
@RequestMapping("/search")
public class WordSearchController {

    private final WordSearchService wordSearchService;

    @Autowired
    public WordSearchController(WordSearchService wordSearchService) {
        this.wordSearchService = wordSearchService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{word}")
    public ResponseEntity<String> getDefinition(@PathVariable String word) {

        Optional<String> definition;
        try {
            definition = wordSearchService.searchWord(word);
        } catch (Exception e) {
            return new ResponseEntity<>("{\"status\":\"Something Went Wrong "+ e.getMessage() +"\"}", HttpStatus.BAD_REQUEST);
        }
        return definition.map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>("{\"status\":\"Word Not Found\"}", HttpStatus.BAD_REQUEST));
    }
}
