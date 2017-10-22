package filestorage.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import filestorage.models.User;


public interface UserRepository extends JpaRepository<User, Long>{
    List<User> findByEmailContaining(String email);
    User findByEmail(String email);
}