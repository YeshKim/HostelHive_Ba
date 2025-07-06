package com.hostelhive.hostelhive.Service;
import com.hostelhive.hostelhive.models.User;
import com.hostelhive.hostelhive.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepo.findAll().forEach(users::add);

        return users;
    }
}