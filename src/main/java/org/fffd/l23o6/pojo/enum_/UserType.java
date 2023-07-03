package org.fffd.l23o6.pojo.enum_;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum UserType {
    @JsonProperty("管理员") ADMIN("管理员"), @JsonProperty("用户") USER("用户");

    private String text;

    UserType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
