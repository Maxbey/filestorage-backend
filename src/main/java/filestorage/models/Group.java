package filestorage.models;

import javax.persistence.*;

import java.util.Set;

@Entity
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Set<User> users;
    private Set<File> files;

    protected Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "group_file",
            joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id")
    )
    public Set<File> getFiles() {
        return files;
    }

    public void setFiles(Set<File> files) {
        this.files = files;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "group_user",
            joinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
