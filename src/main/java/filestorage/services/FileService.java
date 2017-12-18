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

    public File getUserAvailableFile(Long id, User user){

        return fileRepository.findOne(id);
    }

    public File getUserFile(Long id, User user){
        return fileRepository.findByIdAndUserId(id, user.getId());
    }

    protected File createFile(User user, String fileName, String content) {
        File newFile = new File(fileName, content, user);

        return fileRepository.saveAndFlush(newFile);
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
