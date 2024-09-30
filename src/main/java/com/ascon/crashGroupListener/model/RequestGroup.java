package com.ascon.crashGroupListener.model;

import lombok.Getter;
import lombok.Setter;

// Класс объекта группы для отправки PUT запроса
@Getter
@Setter
public class RequestGroup {

    private String comment;

    private String draft;

    private String filter;

    private String jiraURL;

    private String name;

    public RequestGroup(ResponseGroup responseGroup){

        setComment(responseGroup.getCrashGroupResp().getComment());

        setName(responseGroup.getCrashGroupResp().getGroupName());

        setDraft(responseGroup.getCrashGroupResp().getDraft());

        setFilter(responseGroup.getCrashGroupResp().getFilter());

        setJiraURL(responseGroup.getCrashGroupResp().getJiraURL());
    }
}