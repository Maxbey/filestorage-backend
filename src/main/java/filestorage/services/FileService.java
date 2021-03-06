package filestorage.services;

import filestorage.models.File;
import filestorage.models.Group;
import filestorage.models.User;
import filestorage.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    public File getUserFile(Long id, User user){
        return fileRepository.findByIdAndUserId(id, user.getId());
    }

    public File createFile(User user, String fileName, String content) {
        File newFile = new File(fileName, content, user);

        return fileRepository.save(newFile);
    }

    public ArrayList<File> uploadFiles(MultipartHttpServletRequest request, User user) {
        ArrayList<File> createdFiles = new ArrayList<File>();
        Iterator<String> iterator = request.getFileNames();

        try {

            while (iterator.hasNext()) {
                String uploadedFile = iterator.next();
                MultipartFile file = request.getFile(uploadedFile);
                String filename = file.getOriginalFilename();
                String content = new String(file.getBytes());

                createdFiles.add(createFile(user, filename, content));
            }
        }
        catch (Exception exception) {
            return null;
        }

        return createdFiles;
    }

    public File getUserAvailableFile(Long id, User user){
        Set<Long> groupIds = getUserGroupIds(user);

        File file = fileRepository.findByIdAndUserId(id, user.getId());

        if (file == null){
            file = fileRepository.findByIdAndGroups_idIn(id, groupIds);
        }

        return file;
    }

    private Set<Long> getUserGroupIds(User user){
        Set<Long> groupIds = new HashSet<Long>();

        for (Group group : user.getGroups()) {
            groupIds.add(group.getId());
        }

        return groupIds;
    }

    public Set<File> getUserFavoriteFiles(User user){
        return fileRepository.findByLikes_user_id(user.getId());
    }

    public Set<File> getUserAvailableFiles(User user){
        Set<Long> groupIds = getUserGroupIds(user);

        return fileRepository.findByUserIdOrGroups_idIn(user.getId(), groupIds);
    }

    public void removeFile(File file){
        fileRepository.delete(file);
    }
}
