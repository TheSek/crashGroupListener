package com.ascon.crashGroupListener.model;

import lombok.Data;

// Класс объекта группы для получения GET запроса
@Data
public class ResponseGroup {

    private CrashGroupResp crashGroupResp;
    @Data
    public class CrashGroupResp {

        private String groupName;

        private String comment;

        private String jiraURL;

        private String filter;

        private String draft;
    }
}

