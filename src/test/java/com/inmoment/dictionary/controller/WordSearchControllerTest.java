package com.inmoment.dictionary.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmoment.dictionary.model.RobotStatus;
import com.inmoment.dictionary.service.WordSearchService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestClientException;

import java.util.Optional;


@RunWith(SpringRunner.class)
@WebMvcTest(value=WordSearchController.class)
public class WordSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordSearchService wordSearchService;


    @Test
    public void getDefinition_IfWordFound() throws Exception {
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

        ObjectMapper mapper = new ObjectMapper();
        String mockRobotStatusJson = mapper.writeValueAsString(mockRobotStatus);
        Mockito.when(wordSearchService.searchWord(Mockito.anyString())).thenReturn(Optional.of(mockRobotStatusJson));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/search/A-TIPTOE").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        JSONAssert.assertEquals(mockRobotStatusJson, result.getResponse()
                .getContentAsString(), false);
        Assert.assertEquals(HttpStatus.OK.value(),result.getResponse().getStatus());

    }

    @Test
    public void getDefinition_IfWordNotFound() throws Exception {

        String mockWordNotFoundJson = "{\"status\":\"Word Not Found\"}";
        Mockito.when(wordSearchService.searchWord(Mockito.anyString())).thenReturn(Optional.empty());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/search/BEAST").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        JSONAssert.assertEquals(mockWordNotFoundJson, result.getResponse()
                .getContentAsString(), false);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),result.getResponse().getStatus());
    }

    @Test
    public void getDefinition_IfJsonProcessingExceptionOccurs() throws Exception {
        String mockWordNotFoundJson = "{\"status\":\"Something Went Wrong Error\"}";
        Mockito.when(wordSearchService.searchWord(Mockito.anyString())).thenThrow(new JsonProcessingException("Error"){});
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/search/BEAST").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        JSONAssert.assertEquals(mockWordNotFoundJson, result.getResponse()
                .getContentAsString(), false);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),result.getResponse().getStatus());
    }

    @Test
    public void getDefinition_IfRestClientExceptionOccurs() throws Exception {
        String mockWordNotFoundJson = "{\"status\":\"Something Went Wrong Rest Client Exception\"}";
        Mockito.when(wordSearchService.searchWord(Mockito.anyString())).thenThrow(new RestClientException("Rest Client Exception"));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/search/BEAST").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        JSONAssert.assertEquals(mockWordNotFoundJson, result.getResponse()
                .getContentAsString(), false);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),result.getResponse().getStatus());
    }

    @Test
    public void getDefinition_IfProvidedWordStartsWithDigit() throws Exception{
        String mockWordNotFoundJson = "{\"status\":\"Word Not Found\"}";
        Mockito.when(wordSearchService.searchWord(Mockito.anyString())).thenReturn(Optional.empty());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/search/1").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        JSONAssert.assertEquals(mockWordNotFoundJson, result.getResponse()
                .getContentAsString(), false);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(),result.getResponse().getStatus());

    }

}