package com.inmoment.dictionary.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmoment.dictionary.model.RobotArms;
import com.inmoment.dictionary.model.RobotCamera;
import com.inmoment.dictionary.model.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;


/**
 * @author Rashmi
 */

@Service
public class WordSearchService {

    @Value("${robot.endpoint}")
    private String ROBOT_API_ENDPOINT;
    @Value("${robot.api.key.secret}")
    private String ROBOT_API_SECRET;
    @Value("${robot.api.key}")
    private String ROBOT_API_KEY;

    private static final String ROBOT_STATUS = "status";

    private final RestTemplate restTemplate;

    private final WordCacheService wordCacheService;

    @Autowired
    public WordSearchService(RestTemplate restTemplate, WordCacheService wordCacheService) {
        this.restTemplate = restTemplate;
        this.wordCacheService = wordCacheService;
    }

    /**
     * This function initiates the process of searching the word
     * @param word The word to be looked up from the Robot API
     * @return Optional<String> The JSON string of RobotStatus of the word being searched if the word exists in the
     * dictionary otherwise a JSON string with error message
     * @throws JsonProcessingException If JSON processing exception occurs
     * @throws RestClientException If Rest API Client exceptions occurs
     */
    public Optional<String> searchWord(String word) throws JsonProcessingException , RestClientException {
        ObjectMapper mapper = new ObjectMapper();

        if(startsWithDigit(word)){
            return Optional.empty();
        }

        if (this.wordCacheService.getWordDictionary().containsKey(word)) {
            return Optional.of(mapper.writeValueAsString(this.wordCacheService.getWordDictionary().get(word)));
        }

        Optional<RobotStatus> optionalRobotStatus = askRobot(ROBOT_STATUS, HttpMethod.GET);

        if (!optionalRobotStatus.isPresent()) return Optional.empty();
        RobotStatus robotStatus = optionalRobotStatus.get();

        if (whereToStartForm(word, robotStatus).equalsIgnoreCase("first")) {
            robotStatus = askRobot(RobotArms.FIRST_PAGE.action(), RobotArms.FIRST_PAGE.httpMethod()).get();
        } else if (whereToStartForm(word, robotStatus).equalsIgnoreCase("last")) {
            robotStatus = askRobot(RobotArms.LAST_PAGE.action(), RobotArms.LAST_PAGE.httpMethod()).get();
        }

        Optional<RobotStatus> result = continueSearch(word, robotStatus);

        return result.isPresent() ? Optional.of(mapper.writeValueAsString(result.get())) : Optional.empty();
    }

    /**
     * This function does the heavy lifting of moving the robot's arms and cameras
     * @param word Word being searched
     * @param robotPosition Current position of robot
     * @return Optional<RobotStatus> Optional {@link RobotStatus} of the word being searched
     */
    private Optional<RobotStatus> continueSearch(String word, RobotStatus robotPosition) throws RestClientException {

        if (word.compareToIgnoreCase(robotPosition.getCurrentTerm()) == 0) {
            return Optional.of(robotPosition);
        } else if (word.compareToIgnoreCase(robotPosition.getCurrentTerm()) < 0) {
            RobotStatus term = askRobot(RobotCamera.FIRST_TERM.action(), RobotCamera.FIRST_TERM.httpMethod()).get();
            if (word.compareToIgnoreCase(term.getCurrentTerm()) > 0) {
                while (term.getHasNextTerm()) {
                    term = askRobot(RobotCamera.NEXT_TERM.action(), RobotCamera.NEXT_TERM.httpMethod()).get();
                    if (term.getCurrentTerm().equalsIgnoreCase(robotPosition.getCurrentTerm())) return Optional.empty();
                    if (word.compareToIgnoreCase(term.getCurrentTerm()) == 0) return Optional.of(term);
                }
                return Optional.empty();
            } else if (word.compareToIgnoreCase(term.getCurrentTerm()) == 0) {
                return Optional.of(term);
            }
            return continueSearch(word, askRobot(RobotArms.PREVIOUS_PAGE.action(), RobotArms.PREVIOUS_PAGE.httpMethod()).get());
        } else {
            RobotStatus term = askRobot(RobotCamera.LAST_TERM.action(), RobotCamera.LAST_TERM.httpMethod()).get();
            if (word.compareToIgnoreCase(term.getCurrentTerm()) < 0) {
                while (term.getHasPreviousTerm()) {
                    term = askRobot(RobotCamera.PREVIOUS_TERM.action(), RobotCamera.PREVIOUS_TERM.httpMethod()).get();
                    if (term.getCurrentTerm().equalsIgnoreCase(robotPosition.getCurrentTerm())) return Optional.empty();
                    if (word.compareToIgnoreCase(term.getCurrentTerm()) == 0) return Optional.of(term);
                }
                return Optional.empty();
            } else if (word.compareToIgnoreCase(term.getCurrentTerm()) == 0) {
                return Optional.of(term);
            }
            return continueSearch(word, askRobot(RobotArms.NEXT_PAGE.action(), RobotArms.NEXT_PAGE.httpMethod()).get());
        }
    }

    /**
     * @return HttpEntity with headers set with authorization parameters
     */
    private HttpEntity getHttpEntity() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(ROBOT_API_KEY, ROBOT_API_SECRET);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("parameters", httpHeaders);
    }


    /**
     * This function moves the {@link RobotArms} and {@link RobotCamera}
     * @param action This is a String that forms the URL endpoint for the API call
     * @param httpMethod This is of type {@link HttpMethod}
     * @return Optional<RobotStatus> Optional {@link RobotStatus} of the word being searched
     */
    private Optional<RobotStatus> askRobot(String action, HttpMethod httpMethod) throws RestClientException {
        HttpEntity httpEntity = getHttpEntity();
        String url = ROBOT_API_ENDPOINT + "/" + action;
        RobotStatus robotStatus = restTemplate.exchange(url, httpMethod, httpEntity, RobotStatus.class).getBody();
        if (null != robotStatus) {
            this.wordCacheService.getWordDictionary().put(robotStatus.getCurrentTerm(), robotStatus);
        }
        return Optional.ofNullable(robotStatus);
    }

    /**
     * This function decides whether to start searching the word from beginning or end of the  dictionary
     * @param word String of word being searched
     * @param currentPosition Current position of the robot
     * @return "first" or "last" or "stay"
     */
    private String whereToStartForm(String word, RobotStatus currentPosition) {
        char firstChar = word.toUpperCase().charAt(0);
        int distFromA = distance(firstChar, 'A');
        int distFromZ = distance(firstChar, 'Z');
        int distFromCurrTerm = distance(firstChar, currentPosition.getCurrentTerm().charAt(0));

        List<Integer> dList = Arrays.asList(distFromA, distFromZ, distFromCurrTerm);

        Collections.sort(dList);

        if (distFromA == dList.get(0)) {
            return "first";
        } else if (distFromZ == dList.get(0)) {
            return "last";
        }
        return "stay";
    }

    /**
     * This function takes two character and returns the distance between them
     * @param ch1 first Character
     * @param ch2 Second Character
     * @return Distance between the two characters
     */
    private int distance(char ch1, char ch2) {
        return Math.abs((int) ch1 - (int) ch2);
    }


    /**
     * This function checks if the word starts with digit
     * @param word Word to be checked for validity
     * @return Boolean True is the word starts with a number else false
     */
    private boolean startsWithDigit(String word){
        return Character.isDigit(word.charAt(0));
    }

}
