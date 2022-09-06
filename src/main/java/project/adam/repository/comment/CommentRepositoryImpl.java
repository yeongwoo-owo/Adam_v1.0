package project.adam.repository.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import project.adam.entity.comment.Comment;
import project.adam.entity.post.Post;

import java.util.List;

import static project.adam.entity.comment.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Value("${report.hiddenCount}")
    private int reportHiddenCount;

    @Override
    public Slice<Comment> findRootCommentByPost(Post post, Pageable pageable) {
        List<Comment> contents = queryFactory.selectFrom(comment)
                .leftJoin(comment.replies)
                .fetchJoin()
                .where(
                        comment.reports.size().lt(reportHiddenCount),
                        comment.post.eq(post)
                )
                .fetch();

        boolean hasNext = (contents.size() > pageable.getPageSize());
        if (hasNext) {
            contents.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }
}
