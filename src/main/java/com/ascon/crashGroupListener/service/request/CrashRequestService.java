package com.ascon.crashGroupListener.service.request;

import com.ascon.crashGroupListener.logger.Logger;
import com.ascon.crashGroupListener.mapper.JSONMapper;
import com.ascon.crashGroupListener.model.RequestGroup;
import com.ascon.crashGroupListener.model.ResponseGroup;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.util.DefaultJiraHome;
import com.google.common.reflect.TypeToken;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.Optional;

import static com.ascon.crashGroupListener.constants.Config.*;

public class CrashRequestService {

    private static Logger log = new Logger(CrashRequestService.class, new DefaultJiraHome().getLogDirectory().toString());
    private static HttpHeaders header = createHeaders();
    private static RestTemplate restTemplate = new RestTemplate();
    // Отправляю GET запрос для получения объекта группы
    public static Optional<ResponseGroup> getGroupObject(String groupID){
        try {
            String URL = getCrashUrl() + REST_REQUEST + groupID;
            URIBuilder builder = new URIBuilder(URL);
            String body = restTemplate.exchange(builder.build(), HttpMethod.GET, new HttpEntity<>(header), String.class).getBody();
            return Optional.ofNullable(JSONMapper.stringToObject(body, ResponseGroup.class));
        } catch (URISyntaxException e) {
            log.error("ERROR: URISyntaxException in CrashRequestService.getGroupObject()", e);
            return Optional.ofNullable(null);
        } catch (RestClientException e) {
            log.error("ERROR: Request dropped in CrashRequestService.getGroupObject()", e);
            return Optional.ofNullable(null);
        } catch (Exception e) {
            log.error("ERROR: Exception in CrashRequestService.getGroupObject()", e);
            return Optional.ofNullable(null);
        }
    }

    // Отправляю PUT запрос с добавлением ссылки на Jira
    public static void updateGroup(String groupID, RequestGroup updatedGroup){
        try {
            String URL = getCrashUrl() + REST_REQUEST + groupID;
            URIBuilder builder = new URIBuilder(URL);
            String putRequest = JSONMapper.objectToString(updatedGroup, RequestGroup.class);
            restTemplate.exchange(builder.build(), HttpMethod.PUT, new HttpEntity<>(putRequest, header), Void.class);
        } catch (URISyntaxException e) {
            log.error("ERROR: URISyntaxException in CrashRequestService.updateGroup()", e);
        } catch (RestClientException e) {
            log.error("ERROR: Request dropped in CrashRequestService.updateGroup()", e);
        } catch (Exception e) {
            log.error("ERROR: Exception in CrashRequestService.updateGroup()", e);
        }
    }

    // Формирую head для авторизации
    private static HttpHeaders createHeaders() {
        HttpHeaders head = new HttpHeaders();
        head.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        head.setContentType(MediaType.APPLICATION_JSON);
        head.set("token", TOKEN);
        head.set("privilege", PRIVILEGE);
        return head;
    }

    // Метод возвращает ссылку на тестовый или обычный CrashBoard, в зависимости от того, где стоит плагин
    private static String getCrashUrl(){
        String baseUrl = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
        return URL_JIRA.equals(baseUrl) ? URL_CRASH_BOARD : URL_CRASH_TEST;
    }


}
