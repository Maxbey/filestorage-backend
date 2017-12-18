package filestorage.repositories;

import filestorage.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    Comment findByIdAndUserId(Long id, Long userId);
}
