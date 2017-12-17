package filestorage.controllers;

import filestorage.models.Group;
import filestorage.models.User;
import filestorage.requests.GroupRequest;
import filestorage.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/group")
public class GroupController extends AbstractController{
    @Autowired
    private GroupService groupService;

    @RequestMapping(path = "/",method = RequestMethod.GET)
    public ResponseEntity<?> listGroups() {
        Set<Group> groups = groupService.getUserGroups(getCurrentUser());

        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @RequestMapping(path = "/{id}",method = RequestMethod.GET)
    public ResponseEntity<?> getGroup(@PathVariable("id") long id) {
        Group group = groupService.getGroup(id, getCurrentUser());

        if (group == null){
            return notFound();
        }

        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @RequestMapping(path = "/",method = RequestMethod.POST)
    public ResponseEntity<?> createGroup(@Valid @RequestBody GroupRequest request) {
        Group group = groupService.createGroup(request, getCurrentUser());

        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateGroup(@PathVariable("id") long id, @Valid @RequestBody GroupRequest request) {
        User user = getCurrentUser();
        Group group = groupService.getGroup(id, user);

        if (group == null){
            return notFound();
        }

        group = groupService.updateGroup(group, request, user);

        return new ResponseEntity<>(group, HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeGroup(@PathVariable("id") long id){
        Group group = groupService.getGroup(id, getCurrentUser());

        if (group == null){
            return notFound();
        }

        groupService.removeGroup(group);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
