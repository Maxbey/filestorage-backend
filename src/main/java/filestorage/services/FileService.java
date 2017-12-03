package filestorage.services;

import filestorage.models.File;
import filestorage.models.Group;
import filestorage.models.User;
import filestorage.repositories.FileRepository;
import filestorage.repositories.GroupRepository;
import filestorage.requests.FileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private GroupRepository groupRepository;

    public File getUserFile(Long id, User user){
        return fileRepository.findByIdAndUserId(id, user.getId());
    }

    public File uploadFile(File file, User user) {
        File newFile = new File(file.getName(), file.getContent(), user);

        return fileRepository.saveAndFlush(newFile);
    }

    public File createFile(FileRequest request, User user) {
        File file = new File(request.getName(), request.getContent(), user);

        return fileRepository.saveAndFlush(file);
    }

    public Set<File> getUserAvailableFiles(User user){
        Set<Long> groupIds = new HashSet<Long>();

        for (Group group : user.getGroups()) {
            groupIds.add(group.getId());
        }


        return fileRepository.findByUserIdOrGroups_idIn(user.getId(), groupIds);
    }

    public void removeFile(File file){
        fileRepository.delete(file);
    }
}
