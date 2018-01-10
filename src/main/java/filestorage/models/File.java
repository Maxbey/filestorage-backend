package filestorage.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String content;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "files")
    private Set<Group> groups;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    @OrderBy("created_at")
    private Set<Comment> comments;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private Set<Like> likes;

    protected File() {
    }

    public File(String name, String content, User user) {
        this.user = user;
        this.name = name;
        this.content = content;
    }

    public File(String name, String content, User user, Set<Group> groups) {
        this.user = user;
        this.name = name;
        this.content = content;

        this.groups = groups;
    }

    public Long getId() {
        return id;
    }

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

    @JsonIgnore
    public Set<Group> getGroups() {
        return groups;
    }

    @JsonProperty
    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Set<Like> getLikes() {
        return likes;
    }

    public void setId(Long id) {
        this.id = id;
    }
}