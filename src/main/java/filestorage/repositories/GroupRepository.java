package filestorage.repositories;

import filestorage.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface GroupRepository extends JpaRepository<Group, Long>{
    Group findByIdAndOwnerId(Long id, Long ownerId);

    Set<Group> findByOwnerId(Long ownerId);
}