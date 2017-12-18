package filestorage.models;


import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "app_comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "file_id")
    private File file;

    @NotNull
    private String content;

    protected Comment() {
    }

    public Comment(String content, User user, File file) {
        this.user = user;
        this.file = file;
        this.content = content;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content;}

    public Long getId() {
        return id;
    }
}
