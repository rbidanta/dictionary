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
    private static final String ROBOT_STATUS_READY = "ready";

    private final RestTemplate restTemplate;

    //private final WordRepository wordRepository;

    private final WordCacheService wordCacheService;

    @Autowired
    public WordSearchService(RestTemplate restTemplate, WordCacheService wordCacheService) {
        //this.wordRepository = wordRepository;
        this.restTemplate = restTemplate;
        this.wordCacheService = wordCacheService;
    }

    /**
     *
     * @param word
     * @return
     * @throws JsonProcessingException
     * @throws RestClientException
     */
    public Optional<String> searchWord(String word) throws JsonProcessingException , RestClientException {
        ObjectMapper mapper = new ObjectMapper();
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
     * @param word
     * @param robotPosition
     * @return
     */
    private Optional<RobotStatus> continueSearch(String word, RobotStatus robotPosition) throws RestClientException {

        // If  word == robotposition.currentterm return robotPosition
        // If  word < robotposition.currentterm
        // check the first term on the page
        // if word < first term:
        // continue search in previous page
        // if word > first term:
        // while(word!=currentterm) find the next term
        // if next term == robotposition.currentterm break
        // return optionalempty
        // if word > robotposition.currentterm
        // check the last term on the page
        // if word > last term:
        // continue search in next page
        // if word < last term:
        // while(word!=currentterm) find the previous term
        // if previous term == robotposition.currentterm break
        // return optionalempty

        //System.out.println("Page Number:"+robotPosition.getCurrentPageIndex()+" Term Index:"+robotPosition.getCurrentTermIndex());
        //System.out.println("Search Word:"+word+" Current Word:"+robotPosition.getCurrentTerm());
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
     * @return
     */
    private HttpEntity getHttpEntity() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(ROBOT_API_KEY, ROBOT_API_SECRET);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>("parameters", httpHeaders);
    }


    /**
     * @param action
     * @param httpMethod
     * @return
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
     * @param word
     * @param currentPosition
     * @return
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
     * @param ch1
     * @param ch2
     * @return
     */
    private int distance(char ch1, char ch2) {
        return Math.abs((int) ch1 - (int) ch2);
    }

}
