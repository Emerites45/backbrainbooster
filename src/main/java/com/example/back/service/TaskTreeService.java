package com.example.back.service;

import com.example.back.exception.BusinessException;
import com.example.back.model.Task;
import com.example.back.repository.ITaskRepository;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskTreeService implements ITaskTreeService {

    private final ITaskRepository taskRepository;

    public TaskTreeService(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void attachUnder(Task task, Task parent) {
        if (parent == null) {
            makeRoot(task);
            return;
        }
        if (!parent.getProject().getId().equals(task.getProject().getId())) {
            throw new BusinessException("Parent task must belong to the same project");
        }
        if (parent.isDeleted()) {
            throw new BusinessException("Parent task is deleted");
        }
        if (task.getId() != null && wouldCreateCycle(task, parent)) {
            throw new BusinessException("Cannot attach under descendant — cycle detected");
        }

        Task root = parent.getRootTask() != null ? parent.getRootTask() : parent;
        int level = parent.getLevel() + 1;
        task.applyHierarchy(parent, root, level);
    }

    @Override
    public void makeRoot(Task task) {
        task.applyHierarchy(null, null, 0);
    }

    @Override
    public boolean wouldCreateCycle(Task task, Task newParent) {
        if (newParent == null || task.getId() == null) {
            return false;
        }
        if (task.getId().equals(newParent.getId())) {
            return true;
        }
        Set<Long> descendantIds = collectDescendantIds(task);
        return descendantIds.contains(newParent.getId());
    }

    @Override
    @Transactional
    public void softDeleteSubtree(Task task) {
        List<Task> toDelete = new ArrayList<>();
        toDelete.add(task);
        Queue<Long> queue = new ArrayDeque<>();
        queue.add(task.getId());
        while (!queue.isEmpty()) {
            Long parentId = queue.poll();
            for (Task child : taskRepository.findActiveChildren(parentId)) {
                toDelete.add(child);
                queue.add(child.getId());
            }
        }
        for (Task t : toDelete) {
            t.softDelete();
            taskRepository.save(t);
        }
    }

    /**
     * Après save d’une tâche racine sans root_task_id, pointer root vers elle-même.
     */
    public void ensureSelfRoot(Task task) {
        if (task.getParentTask() == null && task.getRootTask() == null && task.getId() != null) {
            task.applyHierarchy(null, task, 0);
            taskRepository.save(task);
        }
    }

    /**
     * Recalcule level/root pour tout le sous-arbre après un déplacement.
     */
    @Transactional
    public void recalculateSubtree(Task moved) {
        Task root = moved.getRootTask() != null ? moved.getRootTask() : moved;
        Queue<Task> queue = new ArrayDeque<>();
        queue.add(moved);
        while (!queue.isEmpty()) {
            Task current = queue.poll();
            for (Task child : taskRepository.findActiveChildren(current.getId())) {
                child.applyHierarchy(current, root, current.getLevel() + 1);
                taskRepository.save(child);
                queue.add(child);
            }
        }
    }

    private Set<Long> collectDescendantIds(Task task) {
        Set<Long> ids = new HashSet<>();
        Queue<Long> queue = new ArrayDeque<>();
        queue.add(task.getId());
        while (!queue.isEmpty()) {
            Long parentId = queue.poll();
            for (Task child : taskRepository.findActiveChildren(parentId)) {
                if (ids.add(child.getId())) {
                    queue.add(child.getId());
                }
            }
        }
        return ids;
    }
}
