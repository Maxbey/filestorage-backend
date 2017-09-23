package filestorage.models;

import javax.persistence.*;
import java.util.Set;

@Entity
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private User user;
    private String name;
    private String content;

    private Set<Group> groups;

    protected File() {
    }

    public File(User user, String name, String content) {
        this.user = user;
        this.name = name;
        this.content = content;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ManyToMany(mappedBy = "groups")
    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }
}