package filestorage.requests;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class FileRequest {
    @NotNull
    private String name;

    @NotNull
    private String content;

    private Set<Long> groups;

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

    public Set<Long> getGroups() {
        return groups;
    }

    public void setGroups(Set<Long> groups) {
        this.groups = groups;
    }
}
