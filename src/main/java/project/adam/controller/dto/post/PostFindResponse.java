package project.adam.controller.dto.post;

import lombok.Getter;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.entity.post.PostImage;
import project.adam.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostFindResponse {
    private Long id;
    private Board board;
    private String writerName;
    private String createdDate;
    private boolean modified;
    private String title;
    private String body;
    private int viewCount;
    private int commentCount;
    private List<String> images = new ArrayList<>();

    public PostFindResponse(Post post) {
        this.id = post.getId();
        this.board = post.getBoard();
        this.title = post.getTitle();
        this.writerName = post.getWriter().getName();
        this.createdDate = DateUtils.getFormattedDateTime(post.getCreatedDate());
        this.modified = post.isModified();
        this.viewCount = post.getViewCount();
        this.body = post.getBody();
        this.commentCount = post.getComments().size();
        this.images = post.getImages().stream()
                .map(PostImage::getName)
                .collect(Collectors.toList());
    }
}
