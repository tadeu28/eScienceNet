/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.models;

import java.util.List;

/**
 *
 * @author Tadeu
 */
public class AbstractWorkflowsCreationModel {
    
    private String codGraph;
    private String nameTask;

    /**
     * @return the codGraph
     */
    public String getCodGraph() {
        return codGraph;
    }

    /**
     * @param codGraph the codGraph to set
     */
    public void setCodGraph(String codGraph) {
        this.codGraph = codGraph;
    }

    /**
     * @return the nameTask
     */
    public String getNameTask() {
        return nameTask;
    }

    /**
     * @param nameTask the nameTask to set
     */
    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }
    
    public static AbstractWorkflowsCreationModel getObjectByCode(List<AbstractWorkflowsCreationModel> lstAWCM, String codeTask){
        
        for(AbstractWorkflowsCreationModel awcm : lstAWCM){
            
            if(awcm.getCodGraph().equals(codeTask)){
                return awcm;
            }            
        }
        
        return null;
    }
}
