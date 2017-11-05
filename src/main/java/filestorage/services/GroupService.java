package filestorage.services;

import filestorage.models.File;
import filestorage.models.Group;
import filestorage.repositories.FileRepository;
import filestorage.repositories.GroupRepository;
import filestorage.requests.GroupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import filestorage.models.User;
import filestorage.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    public Group getGroup(Long groupId, User user){
        return groupRepository.findByIdAndOwnerId(groupId, user.getId());
    }

    public Set<Group> getUserGroups(User user){
        return groupRepository.findByOwnerId(user.getId());
    }

    public Group createGroup(GroupRequest request, User user) {
        Set<File> files = fileRepository.findByIdInAndUserId(
                request.getFiles(), user.getId()
        );
        Set<User> users = userRepository.findByIdIn(request.getUsers());

        Group instance = new Group(request.getName(), user, users, files);

        return groupRepository.save(instance);
    }

    public Group updateGroup(Group group, GroupRequest request, User user){
        Set<File> files = fileRepository.findByIdInAndUserId(
                request.getFiles(), user.getId()
        );
        Set<User> users = userRepository.findByIdIn(request.getUsers());

        group.setName(request.getName());
        group.setFiles(files);
        group.setUsers(users);

        return groupRepository.save(group);
    }

    public void removeGroup(Group group){
        groupRepository.delete(group);
    }
}
