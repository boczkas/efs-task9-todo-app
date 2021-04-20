package efs.task.todoapp.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static efs.task.todoapp.repository.TaskEntity.taskEntityFrom;
import static java.util.stream.Collectors.toList;

public class TaskRepository implements Repository<UUID, TaskEntity> {

    private final Map<UUID, TaskEntity> tasks;

    public TaskRepository() {
        tasks = new ConcurrentHashMap<>();
    }

    @Override
    public UUID save(TaskEntity taskEntity) {
        var uuid = UUID.randomUUID();
        var entity = taskEntityFrom(uuid, taskEntity);
        tasks.put(uuid, entity);
        return uuid;
    }

    @Override
    public TaskEntity query(UUID uuid) {
        return tasks.get(uuid);
    }

    @Override
    public List<TaskEntity> query(Predicate<TaskEntity> condition) {
        return tasks.values().stream()
                .filter(condition)
                .collect(toList());
    }

    @Override
    public TaskEntity update(UUID uuid, TaskEntity taskEntity) {
        var entity = taskEntityFrom(uuid, taskEntity);
        tasks.put(uuid, entity);
        return entity;
    }

    @Override
    public boolean delete(UUID uuid) {
        var removed = tasks.remove(uuid);
        return removed !=  null;
    }
}
