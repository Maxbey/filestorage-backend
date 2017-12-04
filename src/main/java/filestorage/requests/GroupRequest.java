package filestorage.requests;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class GroupRequest {
    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private Set<Long> users;

    @NotNull
    @NotEmpty
    private Set<Long> files;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getUsers() {
        return users;
    }

    public void setUsers(Set<Long> users) {
        this.users = users;
    }

    public Set<Long> getFiles() {
        return files;
    }

    public void setFiles(Set<Long> files) {
        this.files = files;
    }
}
