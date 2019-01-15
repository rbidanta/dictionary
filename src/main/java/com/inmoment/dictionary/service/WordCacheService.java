package com.inmoment.dictionary.service;

import com.inmoment.dictionary.model.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class WordCacheService {


    private Map<String, RobotStatus> wordDictionary;

    public WordCacheService() {
    }

    public Map<String, RobotStatus> getWordDictionary() {
        if (null == wordDictionary) {
            this.wordDictionary = Collections.synchronizedMap(new HashMap<>());
        }
        return this.wordDictionary;
    }

}
