package filestorage.repositories;

import filestorage.models.File;
import filestorage.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface FileRepository extends JpaRepository<File, Long>{
    Set<File> findByIdInAndUserId(Set<Long> ids, Long userId);

    File findByIdAndUserId(Long id, Long userId);

    Set<File> findByUserIdOrGroups_idIn(Long userId, Set<Long> ids);

    File findByIdAndGroups_idIn(Long id, Set<Long> ids);

    Set<File> findByLikes_user_id(Long userId);
}