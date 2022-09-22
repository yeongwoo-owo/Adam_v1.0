package project.adam.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import project.adam.fcm.dto.FcmRequest;
import project.adam.fcm.dto.FcmRequest.Notification;
import project.adam.fcm.dto.FcmRequestBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

import static project.adam.fcm.dto.FcmRequest.Data;
import static project.adam.fcm.dto.FcmRequest.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private static final String FIREBASE_CONFIG_PATH = "firebase/firebase_service_key.json";
    private static final String GOOGLE_API_URL = "https://www.googleapis.com/auth/cloud-platform";

    @Value("${application.id.client}")
    private String clientAppId;
    private String clientPushAPI;

    @PostConstruct
    public void init() {
        this.clientPushAPI = "https://fcm.googleapis.com/v1/projects/" + clientAppId + "/messages:send";
    }

    private final ObjectMapper objectMapper;

    @Async
    public void sendMessageTo(FcmRequestBuilder requestBuilder) throws IOException {
        if (!requestBuilder.getMember().isLogin()) {
            return;
        }

        String message = makeMessage(
                requestBuilder.getMember().getDeviceToken(),
                requestBuilder.getTitle(),
                requestBuilder.getBody(),
                requestBuilder.getPostId());
        log.info("[FCM] Send message to {}", requestBuilder.getMember().getEmail());

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(clientPushAPI)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .build();

        client.newCall(request).execute();
    }

    private String makeMessage(String targetToken, String title, String body, Long postId) throws JsonProcessingException {
        Notification notification = new Notification(title, body, null);
        Data data = new Data(String.valueOf(postId));
        Message message = new Message(targetToken, notification, data);
        FcmRequest fcmRequest = new FcmRequest(false, message);

        return objectMapper.writeValueAsString(fcmRequest);
    }

    private String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream()).createScoped(List.of(GOOGLE_API_URL));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}