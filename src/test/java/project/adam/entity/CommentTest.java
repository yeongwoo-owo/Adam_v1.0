package project.adam.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentTest {

    @Test
    void test_update() {
        //given
        Member member = new Member(UUID.randomUUID(), "member");
        Post post = new Post(member, Board.FREE, "title1", "body1");
        Comment comment1 = new Comment(member, post, null, "comment1");

        //when
        comment1.update("comment2");

        //then
        assertThat(comment1.getBody()).isEqualTo("comment2");
    }

}