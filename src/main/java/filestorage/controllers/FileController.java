package filestorage.controllers;

import filestorage.models.File;
import filestorage.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController extends AbstractController{
    @Autowired
    private FileService fileService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listFiles() {
        Set<File> files = fileService.getUserAvailableFiles(getCurrentUser());

        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createFiles(MultipartHttpServletRequest request) {
        ArrayList<File> uploaded = fileService.uploadFiles(request, getCurrentUser());

        if (uploaded == null) {
            fileUploadError();
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getFile(@PathVariable("id") long id) {
        File file = fileService.getUserFile(id, getCurrentUser());

        if (file == null) {
            notFound();
        }

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

    protected ResponseEntity<?> fileUploadError(){
        Map<String, String> responseData = new HashMap<String, String>();
        responseData.put("error", "File can't be uploaded.");

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }
}
