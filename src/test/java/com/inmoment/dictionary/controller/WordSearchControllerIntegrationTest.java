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


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DictionaryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WordSearchControllerIntegrationTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    @Test
    public void getDefinition_ForWordThatExistsInDictionary() {

        RobotStatus mockRobotStatus = new RobotStatus();
        mockRobotStatus.setStatus("READY");
        mockRobotStatus.setTimeUsed(435115);
        mockRobotStatus.setCurrentPageIndex(0);
        mockRobotStatus.setCurrentTerm("A-TIPTOE");
        mockRobotStatus.setCurrentTermDefinition("On tiptoe; eagerly expecting.We all feel a-tiptoe with hope and confidence. F. Harrison.");
        mockRobotStatus.setCurrentTermIndex(10);
        mockRobotStatus.setTimeRemaining(169684);
        mockRobotStatus.setHasNextPage(true);
        mockRobotStatus.setHasNextTerm(true);
        mockRobotStatus.setHasPreviousPage(false);
        mockRobotStatus.setHasPreviousTerm(true);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<RobotStatus> response = restTemplate.exchange(
                createURLWithPort("/search/A-TIPTOE"),
                HttpMethod.GET, entity, RobotStatus.class);

        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        Assert.assertTrue(mockRobotStatus.getCurrentTermDefinition().equalsIgnoreCase(response.getBody().getCurrentTermDefinition()));
    }

    @Test
    public void getDefinition_ForWordThatDoesNotExistsInDictionary() throws JSONException {

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        String mockWordNotFoundJson = "{\"status\":\"Word Not Found\"}";
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/search/BEAST"),
                HttpMethod.GET, entity, String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
        Assert.assertNotNull(response.getBody());
        JSONAssert.assertEquals(mockWordNotFoundJson, response.getBody(), false);

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}