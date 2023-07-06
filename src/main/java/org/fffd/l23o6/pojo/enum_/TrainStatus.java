package org.fffd.l23o6.pojo.enum_;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TrainStatus {
    @JsonProperty("预计正点") ON_TIME("预计正点"), @JsonProperty("预计晚点") DELAY("预计晚点");
    
    private String text;
    TrainStatus(String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
}
