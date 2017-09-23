package filestorage.repositories;

import filestorage.models.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Integer>{
}