package com.inmoment.dictionary.model;

import org.springframework.http.HttpMethod;

public enum RobotCamera {
    NEXT_TERM("move-to-next-term", HttpMethod.POST),
    PREVIOUS_TERM("move-to-previous-term",HttpMethod.POST),
    FIRST_TERM("jump-to-first-term",HttpMethod.POST),
    LAST_TERM("jump-to-last-term",HttpMethod.POST);

    private String action;
    private HttpMethod httpMethod;
    RobotCamera(String action,HttpMethod httpMethod){
        this.action = action;
        this.httpMethod = httpMethod;
    }

    public String action(){
        return this.action;
    }

    public HttpMethod httpMethod(){
        return this.httpMethod;
    }
}
