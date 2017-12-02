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

    public File createFile(FileRequest request, User user) {
        Set<Group> groups = groupRepository.findByOwnerId(user.getId());
        File file = new File(request.getName(), request.getContent(), user, groups);

        return fileRepository.save(file);
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

    public File updateFile(File file, FileRequest request, User user) {
        Set<Group> groups = groupRepository.findByOwnerId(user.getId());

        file.setName(request.getName());
        file.setContent(request.getContent());
        file.setGroups(groups);

        return fileRepository.save(file);
    }
}
