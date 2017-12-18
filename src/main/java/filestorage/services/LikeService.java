package filestorage.services;

import filestorage.models.File;
import filestorage.models.Like;
import filestorage.models.User;
import filestorage.repositories.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    public Like getLike(User user, File file){

        return likeRepository.findByUserIdAndFileId(file.getId(), user.getId());
    }

    public Like createLike(User user, File file){
        Like instance = new Like(user, file);

        return likeRepository.save(instance);
    }
    public void removeLike(Like like){
        likeRepository.delete(like);
    }
}
