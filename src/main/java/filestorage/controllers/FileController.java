package filestorage.controllers;

import filestorage.models.File;
import filestorage.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
