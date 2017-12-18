package filestorage.requests;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class CommentRequest {
    @NotNull
    @NotEmpty
    private Long file;

    @NotNull
    @NotEmpty
    private String content;

    public Long getFile() {
        return file;
    }

    public void setFile(Long file) {
        this.file = file;
    }
}
