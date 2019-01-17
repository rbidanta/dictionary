package com.inmoment.dictionary.service;

import com.inmoment.dictionary.model.RobotStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class acts as a cache for the words being searched
 * @author Rashmi
 */

@Component
public class WordCacheService {

    private Map<String, RobotStatus> wordDictionary;

    public WordCacheService() {
    }

    /**
     * This Function returns the reference to cache
     * @return Map of words being searched
     */
    public Map<String, RobotStatus> getWordDictionary() {
        if (null == wordDictionary) {
            this.wordDictionary = Collections.synchronizedMap(new HashMap<>());
        }
        return this.wordDictionary;
    }

}
