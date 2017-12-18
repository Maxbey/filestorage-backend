package filestorage.controllers;

import filestorage.models.Comment;
import filestorage.models.File;
import filestorage.models.Like;
import filestorage.models.User;
import filestorage.requests.CommentRequest;
import filestorage.services.CommentService;
import filestorage.services.FileService;
import filestorage.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController extends AbstractController{
    @Autowired
    private FileService fileService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

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
        File file = fileService.getUserAvailableFile(id, getCurrentUser());

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

    @RequestMapping(path = "/{id}/like", method = RequestMethod.POST)
    public ResponseEntity<?> likeFile(@PathVariable("id") long id) {
        File file = fileService.getUserAvailableFile(id, getCurrentUser());

        if (file == null) {
            return notFound();
        }

        likeService.createLike(getCurrentUser(), file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{id}/unlike", method = RequestMethod.POST)
    public ResponseEntity<?> unlikeFile(@PathVariable("id") long id) {
        File file = fileService.getUserAvailableFile(id, getCurrentUser());

        if (file == null) {
            return notFound();
        }

        Like like = likeService.getLike(getCurrentUser(), file);

        if (like == null) {
            return notFound();
        }

        likeService.removeLike(like);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/{id}/comment", method = RequestMethod.POST)
    public ResponseEntity<?> commentFile(@PathVariable("id") long id, @Valid @RequestBody CommentRequest request) {
        File file = fileService.getUserAvailableFile(id, getCurrentUser());

        if (file == null) {
            return notFound();
        }

        commentService.createComment(request.getContent(), getCurrentUser(), file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{id}/comment/{commentId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> unlikeFile(@PathVariable("id") long id, @PathVariable("commentId") long commentId) {
        File file = fileService.getUserAvailableFile(id, getCurrentUser());

        if (file == null) {
            return notFound();
        }

        Comment comment = commentService.getComment(commentId, getCurrentUser());

        if (comment == null) {
            return notFound();
        }

        commentService.removeComment(comment);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    protected ResponseEntity<?> fileUploadError(){
        Map<String, String> responseData = new HashMap<String, String>();
        responseData.put("error", "File can't be uploaded.");

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }
}
