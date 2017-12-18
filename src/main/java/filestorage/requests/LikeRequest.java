package filestorage.requests;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class LikeRequest {
    @NotNull
    @NotEmpty
    private Long file;

    public Long getFile() {
        return file;
    }

    public void setFile(Long file) {
        this.file = file;
    }
}
