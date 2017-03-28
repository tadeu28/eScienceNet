/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.models;

import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tadeu
 */
public class WorkflowABSModel {
    
    private String taskName;
    private String taskCode;
    private String taskPetriOp;
    private List<WorkflowABSModel> taskChild;
    private mxICell cell;
    private List<mxICell> petriCells;
    private boolean selected;
    private String domainOntolgyName;

    public WorkflowABSModel(){
        this.taskCode = "";
        this.taskName = "";
        this.taskPetriOp = "";
        this.selected = false;
        this.taskChild = new ArrayList<>();
        this.petriCells = new ArrayList<>();
        this.domainOntolgyName = "";
    }
    
    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName the taskName to set
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    /**
     * @return the taskPetriOp
     */
    public String getTaskPetriOp() {
        return taskPetriOp;
    }

    /**
     * @param taskPetriOp the taskPetriOp to set
     */
    public void setTaskPetriOp(String taskPetriOp) {
        this.taskPetriOp = taskPetriOp;
    }

    /**
     * @return the taskChild
     */
    public List<WorkflowABSModel> getTaskChild() {
        return taskChild;
    }

    /**
     * @param taskChild the taskChild to set
     */
    public void setTaskChild(List<WorkflowABSModel> taskChild) {
        this.taskChild = taskChild;
    }
    
    public static WorkflowABSModel getObjectByCode(WorkflowABSModel workflowABSModel, String codeTask){
       
        if(workflowABSModel.getTaskCode().equals(codeTask)){
            return workflowABSModel;
        }
        
        for(WorkflowABSModel wfabsm : workflowABSModel.getTaskChild()){
            WorkflowABSModel retorno = getObjectByCode(wfabsm, codeTask);
            
            if(retorno != null){
                return retorno;
            }
        }
        
        return null;
    }
    
    public static WorkflowABSModel getObjectByTaskName(WorkflowABSModel workflowABSModel, String nameTask){
        
        if(workflowABSModel.getTaskName().equals(nameTask)){
            return workflowABSModel;
        }
        
        for(WorkflowABSModel wfabsm : workflowABSModel.getTaskChild()){
            WorkflowABSModel retorno = getObjectByTaskName(wfabsm, nameTask);
            
            if(retorno != null){
                return retorno;
            }
        }
        
        return null;
    }
    
    public static WorkflowABSModel getObjectFather(WorkflowABSModel workflowABSModel, WorkflowABSModel child){
        
        if(workflowABSModel.getTaskChild().indexOf(child) != -1){
            return workflowABSModel;
        }
        
        for(WorkflowABSModel wfabsm : workflowABSModel.getTaskChild()){
            WorkflowABSModel retorno = getObjectFather(wfabsm, child);
            
            if(retorno != null){
                return retorno;
            }
        }
        
        return null;
    }
    
    public static void removerTasks(WorkflowABSModel workflowABSModel){
        
        while(!workflowABSModel.getTaskChild().isEmpty()){
            removerTasks(workflowABSModel.getTaskChild().get(0));
            workflowABSModel.getTaskChild().remove(0);
        }
    }
    
    public static void removerWFTasks(WorkflowABSModel workflowABSModel, mxGraph graph){
        
        if(workflowABSModel != null){
            
            mxICell cellParent = (mxICell) graph.getDefaultParent();
            for(int i = 0; i < cellParent.getChildCount(); i++){
             
                mxICell child = cellParent.getChildAt(i);
                if(child != null){
                    if(child.getValue() != null){
                        
                        if(child.getValue().toString().equals(workflowABSModel.getTaskCode())){
                            
                            for(Object objeto : workflowABSModel.getPetriCells()){
                                Object[] celula = {objeto};
                                graph.removeCells(celula);
                            }
                            
                            Object[] celula = {child};
                            graph.removeCells(celula);
                            break;
                        }
                    }
                }
            }
        }
        
        for(WorkflowABSModel wfabsm : workflowABSModel.getTaskChild()){
            removerWFTasks(wfabsm, graph);
        }
    }
    
    public static WorkflowABSModel getSelecionado(WorkflowABSModel workflowABSModel){
        if(workflowABSModel.isSelected()){
            return workflowABSModel;
        }
        
        for(WorkflowABSModel wfabsm : workflowABSModel.getTaskChild()){
            WorkflowABSModel retorno = getSelecionado(wfabsm);

            if(retorno != null){
                return retorno;
            }
        }
        
        return null;
    }
    
    public static int getCountNodes(WorkflowABSModel workflowABSModel, int counter){
        
        if(workflowABSModel != null){
            
            try{
                int code = Integer.parseInt(workflowABSModel.getTaskCode().replace("T", ""));
            
                if(code > counter){
                    counter = code;
                }
            }catch(Exception e){}
        }
        
        for(WorkflowABSModel wfabsm : workflowABSModel.getTaskChild()){
            counter = getCountNodes(wfabsm, counter);
        }
        
        return counter;
    }
    
    public static WorkflowABSModel getTaksByPetriObject(WorkflowABSModel workflowABSModel, mxICell petriObj){
        
        if(petriObj == null){
            return null;
        }
        
        for(mxICell cell : workflowABSModel.getPetriCells()){
            if(cell.equals(petriObj) ){
                return workflowABSModel;
            }
        }        
        
        for(WorkflowABSModel wfabsm : workflowABSModel.getTaskChild()){
            WorkflowABSModel retorno = getTaksByPetriObject(wfabsm, petriObj);
            
            if(retorno != null){
                return retorno;
            }
        }
        
        return null;
    }
    
    public static void unselectAll(WorkflowABSModel workflowABSModel){
        if(workflowABSModel != null){
            workflowABSModel.setSelected(false);
        }
        
        for(WorkflowABSModel wfabsm : workflowABSModel.getTaskChild()){
            unselectAll(wfabsm);
        }
    }

    /**
     * @return the cell
     */
    public mxICell getCell() {
        return cell;
    }

    /**
     * @param cell the cell to set
     */
    public void setCell(mxICell cell) {
        this.cell = cell;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the petriCells
     */
    public List<mxICell> getPetriCells() {
        return petriCells;
    }

    /**
     * @param petriCells the petriCells to set
     */
    public void setPetriCells(List<mxICell> petriCells) {
        this.petriCells = petriCells;
    }

    /**
     * @return the domainOntolgyName
     */
    public String getDomainOntolgyName() {
        return domainOntolgyName;
    }

    /**
     * @param domainOntolgyName the domainOntolgyName to set
     */
    public void setDomainOntolgyName(String domainOntolgyName) {
        this.domainOntolgyName = domainOntolgyName;
    }
    
}
