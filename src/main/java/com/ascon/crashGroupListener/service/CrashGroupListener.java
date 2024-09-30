package com.ascon.crashGroupListener.service;

import com.ascon.crashGroupListener.logger.Logger;
import com.ascon.crashGroupListener.model.RequestGroup;
import com.ascon.crashGroupListener.model.ResponseGroup;
import com.ascon.crashGroupListener.service.request.CrashRequestService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.util.DefaultJiraHome;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.util.Optional;

import static com.ascon.crashGroupListener.constants.Config.*;

@Component
public class CrashGroupListener implements InitializingBean, DisposableBean {
    @ComponentImport
    private final CustomFieldManager customFieldManager;

    @ComponentImport
    private final EventPublisher eventPublisher;

    private static Logger log = new Logger(CrashGroupListener.class, new DefaultJiraHome().getLogDirectory().toString());

    @Inject
    public CrashGroupListener(CustomFieldManager customFieldManager, EventPublisher eventPublisher) {
        this.customFieldManager = customFieldManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eventPublisher.register(this);
    }

    @EventListener
    public void onIssueEvent(IssueEvent event) throws NullPointerException {
        try {
            handleEvent(event);
        } catch (Exception e){
            log.error("ERROR: Exception in CrashGroupListener.onIssueEvent()", e);
        }
    }
    private void handleEvent(IssueEvent event){
        Long eventTypeId = event.getEventTypeId();
        Issue issue = event.getIssue();

        if (isIssueCreatedEvent(eventTypeId) && isCrashBoardGroupFieldPresent(issue)) {
            String crashGroupID = getCrashBoardGroupFieldValue(issue);
            String issueURL = getIssueUrl(issue.getKey());
            updateCrashGroup(crashGroupID, issueURL);
        }
    }

    private boolean isIssueCreatedEvent(Long eventTypeID){
        return eventTypeID.equals(1L);
    }

    private boolean isCrashBoardGroupFieldPresent(Issue issue){
        return getCrashBoardGroupFieldValue(issue) != null;
    }

    private String getCrashBoardGroupFieldValue(Issue issue){
        CustomField customField = customFieldManager.getCustomFieldObject(getCustomFieldID());
        return customField.getValue(issue).toString();
    }

    private void updateCrashGroup(String crashGroupID, String issueURL){
        Optional<ResponseGroup> optGroupResponse = CrashRequestService.getGroupObject(crashGroupID);
        if (optGroupResponse.isPresent()) {
            RequestGroup requestGroup = new RequestGroup(optGroupResponse.get());
            requestGroup.setJiraURL(issueURL);
            CrashRequestService.updateGroup(crashGroupID, requestGroup);
        }
    }

    // Метод собирает ссылку на запись в Jira в зависимости от расположения плагина
    private String getIssueUrl(String issueKey){
        return ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL) + "/browse/" + issueKey;
    }

    // Метод возвращает ИД поля "CrashBoard Group" в зависимости от расположения плагина
    private Long getCustomFieldID(){
        String baseUrl = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
        return URL_JIRA.equals(baseUrl) ? CUSTOM_FIELD_ID : CUSTOM_FIELD_ID_TEST;
    }


}