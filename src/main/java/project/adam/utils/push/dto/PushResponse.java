package project.adam.utils.push.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PushResponse {

    private boolean validate_only;
    private Message message;

    @Getter
    @AllArgsConstructor
    public static class Message {
        private String token;
        private Notification notification;
        private Data data;
    }

    @Getter
    @AllArgsConstructor
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }

    @Getter
    @AllArgsConstructor
    public static class Data {
        private String postId;
    }
}
