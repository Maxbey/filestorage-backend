package filestorage.controllers;

import filestorage.models.File;
import filestorage.repositories.UserRepository;
import filestorage.requests.FileRequest;
import filestorage.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;


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

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createFile(@Valid @RequestBody FileRequest request) {
        File file = fileService.createFile(request, getCurrentUser());

        return new ResponseEntity<>(file, HttpStatus.CREATED);
    }
}
