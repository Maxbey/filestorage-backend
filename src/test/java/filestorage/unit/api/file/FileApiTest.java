package filestorage.unit.api.file;

import com.google.gson.Gson;
import filestorage.Application;
import filestorage.WebSecurity;
import filestorage.controllers.FileController;
import filestorage.models.Comment;
import filestorage.models.File;
import filestorage.models.Like;
import filestorage.models.User;
import filestorage.repositories.UserRepository;
import filestorage.services.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.*;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = FileController.class, secure = false)
@ContextConfiguration(classes = {WebSecurity.class, Application.class})
@AutoConfigureMockMvc
public class FileApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private FileService fileService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private LikeService likeService;

    private Gson serializer;

    private Set<File> files;

    private User existedUser;
    private String authToken;

    @Before
    public void setup(){
        existedUser = new User("email@email.com", "pass", "fname", "lname");
        existedUser.setId(1L);
        authToken = JWTService.createJWT(existedUser);

        serializer = new Gson();

        files = new HashSet<File>();
        files.add(new File("first", "first content", existedUser));
        files.add(new File("second", "second content", existedUser));

        when(userRepository.findOne(existedUser.getId())).thenReturn(existedUser);
    }

    @Test
    public void testGetFilesList() throws Exception {
        when(fileService.getUserAvailableFiles(existedUser)).thenReturn(files);
        mockMvc.perform(get("/file/")
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().json(serializer.toJson(files)));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFiles(existedUser);
    }

    @Test
    public void testGetFavoriteFilesList() throws Exception {
        when(fileService.getUserFavoriteFiles(existedUser)).thenReturn(files);
        mockMvc.perform(get("/file/favorite/")
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().json(serializer.toJson(files)));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserFavoriteFiles(existedUser);
    }

    @Test
    public void testGetSingleFile() throws Exception {
        File file = new File("file", "content", existedUser);
        Long fileId = 1L;

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(file);
        mockMvc.perform(get("/file/{id}", fileId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().json(new Gson().toJson(file)));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
    }

    @Test
    public void testGetMissingFile() throws Exception {
        Long fileId = 1L;

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(null);
        mockMvc.perform(get("/file/{id}", fileId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error': 'Resource not found.'}"));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
    }

    @Test
    public void testRemoveFile() throws Exception {
        File file = new File("file", "content", existedUser);
        Long fileId = 1L;

        when(fileService.getUserFile(fileId, existedUser)).thenReturn(file);
        mockMvc.perform(delete("/file/{id}", fileId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNoContent());

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserFile(fileId, existedUser);
        verify(fileService, times(1)).removeFile(file);
    }

    @Test
    public void testAttemptToRemoveMissingFile() throws Exception {
        Long fileId = 1L;

        when(fileService.getUserFile(fileId, existedUser)).thenReturn(null);
        mockMvc.perform(delete("/file/{id}", fileId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error': 'Resource not found.'}"));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserFile(fileId, existedUser);
    }

    @Test
    public void testAttemptToUploadFile() throws Exception {
        when(fileService.uploadFiles(any(MultipartHttpServletRequest.class), eq(existedUser))).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/file/")
                .file(new MockMultipartFile(
                        "data", "filename.txt", "text/plain", "some xml".getBytes())
                ).header("Authorization", "Token " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{'error': 'File cannot be uploaded.'}"));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).uploadFiles(
                any(MultipartHttpServletRequest.class), eq(existedUser)
        );
    }

    @Test
    public void testSuccessfulFileUpload() throws Exception {
        ArrayList<File> uploaded = new ArrayList<File>(files);
        when(fileService.uploadFiles(any(MultipartHttpServletRequest.class), eq(existedUser))).thenReturn(uploaded);

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/file/")
                .file(new MockMultipartFile(
                        "data", "filename.txt", "text/plain", "some xml".getBytes())
                ).header("Authorization", "Token " + authToken))
                .andExpect(status().isCreated());

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).uploadFiles(
                any(MultipartHttpServletRequest.class), eq(existedUser)
        );
    }

    @Test
    public void testLikeFile() throws Exception {
        File file = new File("file", "content", existedUser);
        Long fileId = 1L;

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(file);
        mockMvc.perform(post("/file/{id}/like/", fileId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().json(serializer.toJson(file)));

        verify(userRepository, times(2)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
        verify(likeService, times(1)).createLike(existedUser, file);
    }

    @Test
    public void testAttemptToLikeMissingFile() throws Exception {
        Long fileId = 1L;

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(null);
        mockMvc.perform(post("/file/{id}/like/", fileId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error': 'Resource not found.'}"));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
    }

    @Test
    public void testUnlikeFile() throws Exception {
        File file = new File("file", "content", existedUser);
        Like like = new Like(existedUser, file);
        Long fileId = 1L;

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(file);
        when(likeService.getLike(existedUser, file)).thenReturn(like);

        mockMvc.perform(post("/file/{id}/unlike/", fileId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().json(serializer.toJson(file)));

        verify(userRepository, times(2)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
        verify(likeService, times(1)).getLike(existedUser, file);
        verify(likeService, times(1)).removeLike(like);
    }

    @Test
    public void testAttemptToUnlikeMissingFile() throws Exception {
        Long fileId = 1L;

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(null);
        mockMvc.perform(post("/file/{id}/unlike/", fileId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error': 'Resource not found.'}"));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
    }

    @Test
    public void testAttemptToUnlikeMissingLike() throws Exception {
        File file = new File("file", "content", existedUser);
        Long fileId = 1L;

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(file);
        when(likeService.getLike(existedUser, file)).thenReturn(null);

        mockMvc.perform(post("/file/{id}/unlike/", fileId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error': 'Resource not found.'}"));

        verify(userRepository, times(2)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
        verify(likeService, times(1)).getLike(existedUser, file);
    }

    @Test
    public void testCommentFile() throws Exception {
        File file = new File("file", "content", existedUser);
        Long fileId = 1L;

        HashMap <String, String> payload = new HashMap<>();
        payload.put("content", "Some content");

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(file);
        mockMvc.perform(post("/file/{id}/comment/", fileId)
                .header("Authorization", "Token " + authToken)
                .content(serializer.toJson(payload)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(serializer.toJson(file)));

        verify(userRepository, times(2)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
        verify(commentService, times(1)).createComment(
                payload.get("content"), existedUser, file
        );
    }

    @Test
    public void testAttemptToCommentMissingFile() throws Exception {
        Long fileId = 1L;

        HashMap <String, String> payload = new HashMap<>();
        payload.put("content", "Some content");

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(null);
        mockMvc.perform(post("/file/{id}/comment/", fileId)
                .header("Authorization", "Token " + authToken)
                .content(serializer.toJson(payload)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error': 'Resource not found.'}"));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
    }


    @Test
    public void testDeleteFileComment() throws Exception {
        File file = new File("file", "content", existedUser);
        Comment comment = new Comment("a", existedUser, file);
        Long fileId = 1L;
        Long commentId = 2L;

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(file);
        when(commentService.getComment(commentId, existedUser)).thenReturn(comment);

        mockMvc.perform(delete("/file/{id}/comment/{cId}", fileId, commentId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().json(serializer.toJson(file)));

        verify(userRepository, times(2)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
        verify(commentService, times(1)).getComment(commentId, existedUser);
        verify(commentService, times(1)).removeComment(comment);
    }

    @Test
    public void testAttemptToDeleteCommentMissingFile() throws Exception {
        Long fileId = 1L;

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(null);
        mockMvc.perform(delete("/file/{id}/comment/{id}", fileId, 2L)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error': 'Resource not found.'}"));

        verify(userRepository, times(1)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
    }

    @Test
    public void testAttemptToDeleteMissingComment() throws Exception {
        File file = new File("file", "content", existedUser);
        Long fileId = 1L;
        Long commentId = 2L;

        when(fileService.getUserAvailableFile(fileId, existedUser)).thenReturn(file);
        when(likeService.getLike(existedUser, file)).thenReturn(null);

        mockMvc.perform(delete("/file/{id}/comment/{cId}/", fileId, commentId)
                .header("Authorization", "Token " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{'error': 'Resource not found.'}"));

        verify(userRepository, times(2)).findOne(existedUser.getId());
        verify(fileService, times(1)).getUserAvailableFile(fileId, existedUser);
        verify(commentService, times(1)).getComment(commentId, existedUser);
    }
}
