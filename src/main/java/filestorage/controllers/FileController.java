package filestorage.controllers;

import filestorage.models.File;
import filestorage.models.User;
import filestorage.repositories.UserRepository;
import filestorage.requests.FileRequest;
import filestorage.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.util.Iterator;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController extends AbstractController{
    @Autowired
    private FileService fileService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listFiles() {
        Set<File> files = fileService.getUserAvailableFiles(getCurrentUser());

        if (files == null) {
            return notFound();
        }

        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createFile(MultipartHttpServletRequest request) {
        try {
            Iterator<String> iterator = request.getFileNames();

            while (iterator.hasNext()) {
                String uploadedFile = iterator.next();
                MultipartFile file = request.getFile(uploadedFile);
                String filename = file.getOriginalFilename();
                String content = new String(file.getBytes());

                File newFile = new File(filename, content, getCurrentUser());

                fileService.uploadFile(newFile, getCurrentUser());
            }
        }
        catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("{ good: very }", HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getFile(@PathVariable("id") long id) {
        File file = fileService.getUserFile(id, getCurrentUser());

        return new ResponseEntity<>(file, HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeFile(@PathVariable("id") long id) {
        File file = fileService.getUserFile(id, getCurrentUser());

        if (file == null) {
            return notFound();
        }

        fileService.removeFile(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
