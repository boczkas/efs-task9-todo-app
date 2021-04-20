package efs.task.todoapp.repository;

import efs.task.todoapp.model.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static java.util.Objects.nonNull;

public class UserRepository implements Repository<String, User> {

    private final Map<String, User> users;

    public UserRepository() {
        users = new ConcurrentHashMap<>();}

    @Override
    public String save(User user) {
        var username = user.getUsername();
        var previousUername = users.putIfAbsent(username, user);
        if (nonNull(previousUername)) {
            return null;
        }
        return username;
    }

    @Override
    public User query(String username) {
        return users.get(username);
    }

    @Override
    public List<User> query(Predicate<User> condition) {
        return null;
    }

    @Override
    public User update(String s, User user) {
        return null;
    }

    @Override
    public boolean delete(String s) {
        return false;
    }
}
