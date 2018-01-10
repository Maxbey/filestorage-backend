package filestorage.unit.services;

import filestorage.models.Comment;
import filestorage.models.File;
import filestorage.models.Like;
import filestorage.models.User;
import filestorage.repositories.CommentRepository;
import filestorage.services.CommentService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = CommentService.class)
public class CommentServiceTest {
    @MockBean
    private CommentRepository commentRepository;

    @Autowired
    private CommentService service;

    private User user;
    private File file;

    @Before
    public void setup(){
        user = new User("email", "pass", "fname", "lname");
        user.setId(1L);

        file = new File("file", "content", user);
        file.setId(2L);
    }

    @Test
    public void testGetComment() throws Exception {
        Long commentId = 1L;

        service.getComment(commentId, user);

        verify(commentRepository, times(1)).findByIdAndUserId(
                commentId, user.getId()
        );
    }

    @Test
    public void testCreateComment() throws Exception {
        service.createComment("", user, file);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testRemoveComment() throws Exception {
        Comment comment = new Comment("content", user, file);

        service.removeComment(comment);
        verify(commentRepository, times(1)).delete(comment);
    }
}
