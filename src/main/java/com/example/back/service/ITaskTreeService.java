package com.example.back.service;

import com.example.back.model.Task;

/**
 * Domaine : arbre de tâches (level, root, prévention de cycles).
 */
public interface ITaskTreeService {

    /** Place {@code task} sous {@code parent} (même projet) et calcule level/root. */
    void attachUnder(Task task, Task parent);

    /** Place {@code task} à la racine du projet. */
    void makeRoot(Task task);

    /** True si {@code newParent} est {@code task} ou un de ses descendants. */
    boolean wouldCreateCycle(Task task, Task newParent);

    /** Soft-delete la tâche et tous ses descendants actifs. */
    void softDeleteSubtree(Task task);
}
