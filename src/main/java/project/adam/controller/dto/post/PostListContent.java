package project.adam.controller.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import project.adam.entity.post.Post;
import project.adam.utils.DateUtils;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostListContent {
    private Long id;
    private String writerName;
    private String board;
    private String createdDate;
    private boolean modified;
    private String title;
    private int viewCount;
    private int commentCount;
    private String thumbnail;

    public PostListContent(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.board = post.getBoard().toString();
        this.createdDate = DateUtils.getFormattedDateTime(post.getCreatedDate());
        this.modified = post.isModified();
        this.writerName = post.getWriter().getName();
        this.viewCount = post.getViewCount();
        this.thumbnail = post.getThumbnail() == null ? null : post.getThumbnail().getName();
        this.commentCount = post.getComments().size();
    }
}
