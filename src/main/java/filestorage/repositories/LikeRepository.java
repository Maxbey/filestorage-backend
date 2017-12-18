package filestorage.repositories;

import filestorage.models.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface LikeRepository extends JpaRepository<Like, Long>{
    Like findByUserIdAndFileId(Long userId, Long fileId);

    Set<Like> findByUserId(Long userId);
}
