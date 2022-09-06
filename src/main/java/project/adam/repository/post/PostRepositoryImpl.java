package project.adam.repository.post;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import project.adam.entity.post.Post;
import project.adam.service.dto.post.PostFindCondition;

import java.util.List;
import java.util.Optional;

import static project.adam.entity.QPost.post;
import static project.adam.entity.QPostReport.postReport;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Value("${report.hiddenCount}")
    private Long reportHiddenCount;

    @Override
    public Slice<Post> findAll(PostFindCondition condition, Pageable pageable) {
        List<Post> contents = queryFactory.query()
                .select(post)
                .from(post)
                .where(searchCondition(condition.getContent()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = (contents.size() > pageable.getPageSize());
        if (hasNext) {
            contents.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    private BooleanBuilder searchCondition(String text) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(titleCondition(text));
        builder.or(bodyCondition(text));
        builder.and(validateHiddenCondition());
        return builder;
    }

    private BooleanExpression titleCondition(String title) {
        return title == null ? null : post.title.like("%" + title + "%");
    }

    private BooleanExpression bodyCondition(String body) {
        return body == null ? null : post.body.like("%" + body + "%");
    }

    private BooleanExpression validateHiddenCondition() {
        return queryFactory.select(postReport.count())
                .from(postReport)
                .where(postReport.post.eq(post))
                .lt(reportHiddenCount);
    }

    @Override
    public Optional<Post> findPostIncViewCount(Long postId) {
        queryFactory.update(post)
                .set(post.viewCount, post.viewCount.add(1))
                .where(post.id.eq(postId))
                .execute();

        return Optional.ofNullable(
                queryFactory.selectFrom(post)
                        .where(post.id.eq(postId))
                        .fetchOne()
        );
    }
}