package com.inmoment.dictionary.model;

import java.util.StringJoiner;

public class RobotStatus {

    private String status;
    private long timeUsed;
    private long timeRemaining;
    private long currentPageIndex;
    private long currentTermIndex;
    private String currentTerm;
    private String currentTermDefinition;
    private boolean hasNextPage;
    private boolean hasNextTerm;
    private boolean hasPreviousPage;
    private boolean hasPreviousTerm;
    private String error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public long getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(long value) {
        this.timeUsed = value;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(long value) {
        this.timeRemaining = value;
    }

    public long getCurrentPageIndex() {
        return currentPageIndex;
    }

    public void setCurrentPageIndex(long value) {
        this.currentPageIndex = value;
    }

    public long getCurrentTermIndex() {
        return currentTermIndex;
    }

    public void setCurrentTermIndex(long value) {
        this.currentTermIndex = value;
    }

    public String getCurrentTerm() {
        return currentTerm;
    }

    public void setCurrentTerm(String value) {
        this.currentTerm = value;
    }

    public String getCurrentTermDefinition() {
        return currentTermDefinition;
    }

    public void setCurrentTermDefinition(String value) {
        this.currentTermDefinition = value;
    }

    public boolean getHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean value) {
        this.hasNextPage = value;
    }

    public boolean getHasNextTerm() {
        return hasNextTerm;
    }

    public void setHasNextTerm(boolean value) {
        this.hasNextTerm = value;
    }

    public boolean getHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean value) {
        this.hasPreviousPage = value;
    }

    public boolean getHasPreviousTerm() {
        return hasPreviousTerm;
    }

    public void setHasPreviousTerm(boolean value) {
        this.hasPreviousTerm = value;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RobotStatus.class.getSimpleName() + "[", "]")
                .add("status='" + status + "'")
                .add("timeUsed=" + timeUsed)
                .add("timeRemaining=" + timeRemaining)
                .add("currentPageIndex=" + currentPageIndex)
                .add("currentTermIndex=" + currentTermIndex)
                .add("currentTerm='" + currentTerm + "'")
                .add("currentTermDefinition='" + currentTermDefinition + "'")
                .add("hasNextPage=" + hasNextPage)
                .add("hasNextTerm=" + hasNextTerm)
                .add("hasPreviousPage=" + hasPreviousPage)
                .add("hasPreviousTerm=" + hasPreviousTerm)
                .add("error='" + error + "'")
                .toString();
    }
}
