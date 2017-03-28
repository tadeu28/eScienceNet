/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.esciencenet.compositionmanager;

import com.esciencenet.forms.FWaitingResponse;
import com.esciencenet.servicemanager.OWLSOperation;
import com.esciencenet.servicemanager.OWLSParam;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Tadeu
 */
public class CompositionTasksConnectionValidation extends Thread{
    
    private FWaitingResponse frmWaintingResponse = null;
    private CompositionModel compositionModel = null;
    
    @Override
    public void run(){
        try{
            if(this.compositionModel != null){
                this.compositionModel.setValidated(false);

                for(CompositionTaskModel taskModel : this.compositionModel.getLstServices()){
                    
                    if(!taskModel.getNextTasks().equals("")){
                        if(taskModel.getPetriOperation().equals("SEQUENCE")){
                            if(!this.validateSequenceAndJoin(taskModel)){
                                throw new Exception("There isn't any connection at the outputs of the SEQUENCE [" + taskModel.getOperation().getOperationName() + "].");
                            }
                        }else if(taskModel.getPetriOperation().contains("SPLIT")){
                            if(!this.validateSplit(taskModel)){
                                throw new Exception("There isn't correct connections at the outputs of the SPLIT [" + taskModel.getOperation().getOperationName() + "].");
                            }
                        }else  if(taskModel.getPetriOperation().contains("JOIN")){
                            if(!this.validateSequenceAndJoin(taskModel)){
                                throw new Exception("There isn't any connection at the outputs of the JOIN [" + taskModel.getOperation().getOperationName() + "].");
                            }
                        }
                    }
                }
                
                this.compositionModel.setValidated(true);
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible validating the Workflow.\n\n" + e.getMessage(), ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            frmWaintingResponse.setCanceled(true);
        }finally{
            frmWaintingResponse.setVisible(false);
        }
    }   
    
    private boolean validateSequenceAndJoin(CompositionTaskModel taskModel){
        
        for(OWLSParam param : taskModel.getOperation().getOutputs()){
            if(!param.getParamConnected().isEmpty()){
                return true;
            }else if(!param.getParamConnectorAssociated().isEmpty()){
                for(OWLSParam connParam : param.getParamConnectorAssociated()){
                    if(connParam.getParamConnected() != null){
                        return true;
                    }
                }
            }
        }        
        
        return false;
    }
    
    private boolean validateSplit(CompositionTaskModel taskModel){
        
        int count = 0;
        for(OWLSParam param : taskModel.getOperation().getOutputs()){
            if(!param.getParamConnected().isEmpty()){
                count =  count + param.getParamConnected().size();
            }else if(!param.getParamConnectorAssociated().isEmpty()){
                for(OWLSOperation connect : param.getConnectorAssociated()){
                    for(OWLSParam connParam : connect.getOutputs()){
                        if(!connParam.getParamConnected().isEmpty()){
                            count = count + connParam.getParamConnected().size();
                        }else if(connParam.getParamConnected() != null){
                            count++;
                        }
                    }
                }
            }
        }        
        
        if(count > 1){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * @param frmWaintingResponse the frmWaintingResponse to set
     */
    public void setFrmWaintingResponse(FWaitingResponse frmWaintingResponse) {
        this.frmWaintingResponse = frmWaintingResponse;
    }

    /**
     * @param compositionModel the compositionModel to set
     */
    public void setCompositionModel(CompositionModel compositionModel) {
        this.compositionModel = compositionModel;
    }
}
