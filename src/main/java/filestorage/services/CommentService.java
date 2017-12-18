package filestorage.services;

import filestorage.models.File;
import filestorage.models.Comment;
import filestorage.models.User;
import filestorage.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public Comment getComment(Long commentId, User user){

        return commentRepository.findByIdAndUserId(commentId, user.getId());
    }

    public Comment createComment(String content, User user, File file){
        Comment instance = new Comment(content, user, file);

        return commentRepository.save(instance);
    }
    public void removeComment(Comment comment){
        commentRepository.delete(comment);
    }
}
