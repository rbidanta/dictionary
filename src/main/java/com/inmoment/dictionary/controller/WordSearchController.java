package com.inmoment.dictionary.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.inmoment.dictionary.service.WordSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Component
@RequestMapping("/search")
public class WordSearchController {

    @Autowired
    WordSearchService wordSearchService;

    @GetMapping("/{word}")
    public ResponseEntity<String> getDefinition(@PathVariable String word){

        Optional<String> definition = null;
        try {
            definition = wordSearchService.searchWord(word);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>("Error Processing Request",HttpStatus.EXPECTATION_FAILED);
        }
        return definition.map(s -> new ResponseEntity<>(s, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>("{\"status\":\"Word Not Found\"}", HttpStatus.BAD_REQUEST));
    }
}
