/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.compositionmanager;

import com.esciencenet.servicemanager.OWLSOperation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tadeu
 */
public class CompositionTaskModel {
    
    private OWLSOperation operation;
    private String task;
    private String petriOperation;
    private String nextTasks;
    private String taskCode;
    
    /**
     * @return the operation
     */
    public OWLSOperation getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(OWLSOperation operation) {
        this.operation = operation;
    }

    /**
     * @return the task
     */
    public String getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(String task) {
        this.task = task;
    }

    /**
     * @return the petriOperation
     */
    public String getPetriOperation() {
        return petriOperation;
    }

    /**
     * @param petriOperation the petriOperation to set
     */
    public void setPetriOperation(String petriOperation) {
        this.petriOperation = petriOperation;
    }

    /**
     * @return the nextTasks
     */
    public String getNextTasks() {
        return nextTasks;
    }

    /**
     * @param nextTasks the nextTasks to set
     */
    public void setNextTasks(String nextTasks) {
        this.nextTasks = nextTasks;
    }

    /**
     * @return the taskCode
     */
    public String getTaskCode() {
        return taskCode;
    }

    /**
     * @param taskCode the taskCode to set
     */
    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }
    
}
