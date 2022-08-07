package project.adam;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Privilege;
import project.adam.service.CommentService;
import project.adam.service.MemberService;
import project.adam.service.PostService;
import project.adam.service.dto.comment.CommentCreateRequest;
import project.adam.service.dto.member.MemberJoinRequest;
import project.adam.service.dto.post.PostCreateRequest;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitData {

    private final Init init;

    @PostConstruct
    public void post() {
        init.createDummyData();
    }

    @Component
    @RequiredArgsConstructor
    @Transactional
    static class Init {

        private final MemberService memberService;
        private final PostService postService;
        private final CommentService commentService;
        private final EntityManager em;

        public void createDummyData() {
            String member1Id = memberService.join(new MemberJoinRequest("id1", "member1", Privilege.ADMIN));
            String member2Id = memberService.join(new MemberJoinRequest("id2", "member2"));

            Long post1Id = postService.create(new PostCreateRequest(memberService.find(member1Id).getId(), "FREE", "post1", "post body 1"));
            Long post2Id = postService.create(new PostCreateRequest(memberService.find(member1Id).getId(), "FREE", "post2", "post body 2"));
            Long post3Id = postService.create(new PostCreateRequest(memberService.find(member2Id).getId(), "FREE", "post3", "post body 3"));
            Long post4Id = postService.create(new PostCreateRequest(memberService.find(member2Id).getId(), "FREE", "post3", "post body 4"));

            commentService.create(post1Id, new CommentCreateRequest(memberService.find(member1Id).getId(), "comment body 1"));
            commentService.create(post1Id, new CommentCreateRequest(memberService.find(member2Id).getId(), "comment body 2"));
            commentService.create(post2Id, new CommentCreateRequest(memberService.find(member1Id).getId(), "comment body 3"));
            commentService.create(post2Id, new CommentCreateRequest(memberService.find(member2Id).getId(), "comment body 4"));
            commentService.create(post1Id, new CommentCreateRequest(memberService.find(member1Id).getId(), "comment body 5"));
            commentService.create(post1Id, new CommentCreateRequest(memberService.find(member2Id).getId(), "comment body 6"));
            commentService.create(post2Id, new CommentCreateRequest(memberService.find(member1Id).getId(), "comment body 7"));
            commentService.create(post2Id, new CommentCreateRequest(memberService.find(member2Id).getId(), "comment body 8"));

            em.flush();
            em.clear();
        }
    }
}
