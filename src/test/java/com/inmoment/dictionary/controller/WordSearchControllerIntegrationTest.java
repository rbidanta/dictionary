package com.inmoment.dictionary.controller;


import com.inmoment.dictionary.DictionaryApplication;
import com.inmoment.dictionary.model.RobotStatus;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DictionaryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WordSearchControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private HttpHeaders headers = new HttpHeaders();

    @Test
    public void getDefinition_ForWordThatExistsInDictionary() {

        Map<String, String> dictionaryMap = new HashMap<>();
        dictionaryMap.put("Pink", "A vessel with a very narrow stern; -- called also pinky. Sir W.Scott. Pink stern (Naut.), a narrow stern.");
        dictionaryMap.put("A-TIPTOE", "On tiptoe; eagerly expecting.We all feel a-tiptoe with hope and confidence. F. Harrison.");
        dictionaryMap.put("Orange", "The tree that bears oranges; the orange tree.");
        dictionaryMap.put("Z", "Z, the twenty-sixth and last letter of the English alphabet, isa vocal consonant. It is taken from the Latin letter Z, which camefrom the Greek alphabet, this having it from a Semitic source. Theultimate origin is probably Egyptian. Etymologically, it is mostclosely related to s, y, and j; as in glass, glaze; E. yoke, Gr.yugum; E. zealous, jealous. See Guide to Pronunciation, §§ 273, 274.");
        dictionaryMap.put("BIFACIAL", "Having the opposite surfaces alike.");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        for (Map.Entry<String,String> entry : dictionaryMap.entrySet()
        ) {
            ResponseEntity<RobotStatus> response = restTemplate.exchange(
                    createURLWithPort("/search/"+entry.getKey()),
                    HttpMethod.GET, entity, RobotStatus.class);
            Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assert.assertNotNull(response.getBody());
            Assert.assertTrue(entry.getValue().equalsIgnoreCase(response.getBody().getCurrentTermDefinition()));
        }
    }

    @Test
    public void getDefinition_ForWordThatDoesNotExistsInDictionary() throws JSONException {

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        String mockWordNotFoundJson = "{\"status\":\"Word Not Found\"}";
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/search/BEAST"),
                HttpMethod.GET, entity, String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        JSONAssert.assertEquals(mockWordNotFoundJson, response.getBody(), false);

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}