package com.inmoment.dictionary.model;

import org.springframework.http.HttpMethod;

public enum RobotArms {
    NEXT_PAGE("move-to-next-page", HttpMethod.POST),
    PREVIOUS_PAGE("move-to-previous-page",HttpMethod.POST),
    FIRST_PAGE("jump-to-first-page",HttpMethod.POST),
    LAST_PAGE("jump-to-last-page",HttpMethod.POST);

    private String action;
    private HttpMethod httpMethod;
    RobotArms(String action,HttpMethod httpMethod){
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


