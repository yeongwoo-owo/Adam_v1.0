package project.adam.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import project.adam.exception.ApiException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static project.adam.exception.ExceptionEnum.AUTHORIZATION_FAILED;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity implements Persistable<UUID> {

    @Id
    @Column(name = "member_id", length = 16)
    private UUID id;

    private String name;

    private String imageName;

    @Enumerated(EnumType.STRING)
    private Privilege privilege;

    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();

    @Column(length = 16)
    private UUID sessionId = null;

    public Member(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.privilege = Privilege.USER;
        this.sessionId = UUID.randomUUID();
    }

    public Member(UUID id, String name, Privilege privilege) {
        this(id, name);
        this.privilege = privilege;
        this.sessionId = UUID.randomUUID();
    }

    public void authorization(Privilege privilege) {
        if (this.privilege.value < privilege.value) {
            throw new ApiException(AUTHORIZATION_FAILED);
        }
    }

    public UUID login() {
        this.sessionId = UUID.randomUUID();
        return sessionId;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public boolean isNew() {
        return getCreateDate() == null;
    }
}
