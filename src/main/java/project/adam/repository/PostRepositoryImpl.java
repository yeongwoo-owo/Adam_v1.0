package project.adam.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.entity.Post;
import project.adam.entity.Privilege;

import java.util.List;

import static project.adam.entity.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Post> findAll(PostFindCondition condition, Pageable pageable) {
        List<Post> contents = queryFactory.query()
                .select(post)
                .from(post)
                .where(
                        privilegeCondition(condition.getPrivilege()),
                        writerIdCondition(condition.getWriterId()),
                        titleCondition(condition.getTitleLike())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = (contents.size() > pageable.getPageSize());
        if (hasNext) {
            contents.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    private BooleanExpression privilegeCondition(Privilege privilege) {
        return privilege == null ? null : post.writer.privilege.eq(privilege);
    }

    private BooleanExpression writerIdCondition(String id) {
        return id == null ? null : post.writer.id.eq(id);
    }

    private BooleanExpression titleCondition(String title) {
        return title == null ? null : post.title.like("%" + title + "%");
    }
}
