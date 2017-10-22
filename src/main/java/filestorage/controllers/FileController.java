package filestorage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import filestorage.repositories.FileRepository;


@RestController
@RequestMapping("/file")
public class FileController extends AbstractController{
    @Autowired
    private FileRepository fileRepository;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listFiles() {
        return new ResponseEntity<>(getCurrentUser().getFirstName(), HttpStatus.OK);
    }

}
