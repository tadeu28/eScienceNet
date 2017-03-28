/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.compositionmanager;

import com.esciencenet.forms.FAbstractWFSelect;
import com.esciencenet.forms.FComposeAbstractWorkflow;
import com.esciencenet.forms.FConnectParams;
import com.esciencenet.forms.FServicesResearch;
import com.esciencenet.forms.FViewWFResult;
import com.esciencenet.forms.FWaitingResponse;
import com.esciencenet.models.WorkflowABSModel;
import com.esciencenet.servicemanager.OWLSOperation;
import com.esciencenet.servicemanager.OWLSParam;
import com.esciencenet.servicemanager.wsdlmodels.WSDLRecover;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import juniorvs.virtualdir.ControlaMensagens;

/**
 *
 * @author Tadeu
 */
public class CompositionManager {
 
    private ControlaMensagens controlaMensagens;
    private ProcessWSDLInformation processWSDLInformation;
    private FWaitingResponse frmWatingReponse;
    
    public CompositionManager(){
        this.processWSDLInformation = new ProcessWSDLInformation();
    }
    
    public void composerAbstractWF(){
        FComposeAbstractWorkflow frmAbstractWorkflow = new FComposeAbstractWorkflow(null, true);
        frmAbstractWorkflow.setVisible(true);
    }
    
    public void showServicesSerach(){
        FServicesResearch frmFServicesResearch = new FServicesResearch(null, true);
        frmFServicesResearch.setVisible(true);
    }

        public String compareSemanticConnectorParams(List<OWLSParam> outputPrevService, List<OWLSParam> inputNextService){
        try{
            String compatibility = "";
            int countEqualParam = 0;

            for(OWLSParam owlPrevParam : outputPrevService){

                for(OWLSParam owlNextParam : inputNextService){

                    if(owlPrevParam.getParamDomainTerm().equals(owlNextParam.getParamDomainTerm())){
                        
                        if(this.compareSyntaticServiceParams(owlPrevParam, owlNextParam)){
                            countEqualParam++;
                            compatibility = compatibility + "|[" + owlPrevParam.getParamName() + " == " + owlNextParam.getParamName() + "]";
                        }
                    }
                }
            }
            
            if(!compatibility.isEmpty()){
                compatibility = compatibility.substring(1, compatibility.length()).trim();
            }
            
            if(countEqualParam == 0){
                return "";
            }else{
                return compatibility;
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to compare services.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return "";
        }
    }
    
    public boolean compareSingleService(OWLSParam prevParam, OWLSParam nextParam){
        try{

            if(prevParam.getParamDomainTerm().equals(nextParam.getParamDomainTerm())){

                if(this.compareSyntaticServiceParams(prevParam, nextParam)){
                    return true;
                }
            }             
            
            return false;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to compare services.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
        
    public boolean compareSemanticServiceParams(List<OWLSParam> outputPrevService, List<OWLSParam> inputNextService){
        try{
            int countEqualParam = 0;
            for(OWLSParam owlPrevParam : outputPrevService){
                for(OWLSParam owlNextParam : inputNextService){
                    if(owlPrevParam.getParamDomainTerm().equals(owlNextParam.getParamDomainTerm())){                        
                        if(this.compareSyntaticServiceParams(owlPrevParam, owlNextParam)){
                            countEqualParam++;
                            break;
                        }
                    }
                }
            }            
            if(countEqualParam == 0){
                return false;
            }else{
                return true;
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to compare services.\n\n" + 
                                          e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean compareSyntaticServiceParams(OWLSParam prevService, OWLSParam nextService){
        try{
            if((prevService.getParamType().contains("base")) || (nextService.getParamType().contains("base"))){
                return true;
            }
            
            if(prevService.getParamType().equals(nextService.getParamType())){
                return true;
            }
            
            return false;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to compare services.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public OWLSOperation moutnOWLSOperationToSearch(OWLSOperation operation, String petriOp, boolean isConnector){
        try{
            OWLSOperation newOperation = new OWLSOperation();
            newOperation.setOperationDomainTerm(operation.getOperationDomainTerm());
            newOperation.setOperationName(operation.getOperationName());
            newOperation.setServiceURL(operation.getServiceURL());            
        
            for(OWLSParam input : operation.getInputs()){

                if(input.getConnectorAssociated().size() > 0){

                    for(int i = 0; i < input.getConnectorAssociated().size(); i++){
                        
                        for(OWLSParam inputConn : input.getConnectorAssociated().get(i).getInputs()){
                  
                            String paramName = inputConn.getParamName();
                            if(inputConn.getParamName().equals(input.getParamConnectorAssociated().get(i).getParamName())){
                                paramName = input.getParamName();
                            }

                            OWLSParam newInput = new OWLSParam();
                            newInput.setParamDomainTerm(inputConn.getParamDomainTerm());
                            newInput.setParamName(paramName);
                            newInput.setParamType(inputConn.getParamType().contains("base") ? XSDDatatype.XSDstring.getURI() : inputConn.getParamType());

                            newOperation.getInputs().add(newInput);                        
                        }
                    }
                    
                    if(petriOp.contains("SPLIT") || isConnector){
                        OWLSParam newInput = new OWLSParam();
                        newInput.setParamDomainTerm(input.getParamDomainTerm());
                        newInput.setParamName(input.getParamName());
                        newInput.setParamType(input.getParamType().contains("base") ? XSDDatatype.XSDstring.getURI() : input.getParamType());

                        newOperation.getInputs().add(newInput);
                    }
                }else{                
                    OWLSParam newInput = new OWLSParam();
                    newInput.setParamDomainTerm(input.getParamDomainTerm());
                    newInput.setParamName(input.getParamName());
                    newInput.setParamType(input.getParamType().contains("base") ? XSDDatatype.XSDstring.getURI() : input.getParamType());
                    
                    newOperation.getInputs().add(newInput);
                }
            }

            for(OWLSParam output : operation.getOutputs()){

                if(output.getConnectorAssociated().size() > 0){

                    for(int i = 0; i < output.getConnectorAssociated().size(); i++){
                      
                        for(OWLSParam outputConn : output.getConnectorAssociated().get(i).getOutputs()){
                       
                            String paramName = outputConn.getParamName();
                            if(outputConn.getParamName().equals(output.getParamConnectorAssociated().get(i).getParamName())){
                                paramName = output.getParamName();
                            }

                            OWLSParam newOutput = new OWLSParam();
                            newOutput.setParamDomainTerm(outputConn.getParamDomainTerm());
                            newOutput.setParamName(paramName);
                            newOutput.setParamType(outputConn.getParamType().contains("base") ? XSDDatatype.XSDstring.getURI() : outputConn.getParamType());

                            newOperation.getOutputs().add(newOutput);                  
                        } 
                    }  
                    
                    if(petriOp.contains("SPLIT")|| isConnector){
                        OWLSParam newOutput = new OWLSParam();
                        newOutput.setParamDomainTerm(output.getParamDomainTerm());
                        newOutput.setParamName(output.getParamName());
                        newOutput.setParamType(output.getParamType().contains("base") ? XSDDatatype.XSDstring.getURI() : output.getParamType());

                        newOperation.getOutputs().add(newOutput);
                    }
                }else{

                    OWLSParam newOutput = new OWLSParam();
                    newOutput.setParamDomainTerm(output.getParamDomainTerm());
                    newOutput.setParamName(output.getParamName());
                    newOutput.setParamType(output.getParamType().contains("base") ? XSDDatatype.XSDstring.getURI() : output.getParamType());
                    
                    newOperation.getOutputs().add(newOutput);
                }
            }
            
            if((newOperation.getOutputs().isEmpty()) && (newOperation.getInputs().isEmpty())){
                return operation;
            }else{
                return newOperation;
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to mount the OWLSOperation.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public void openFunctionalWorkflows(){
        FAbstractWFSelect frmAbstractWFSelect = new FAbstractWFSelect(null, true, true);
        frmAbstractWFSelect.setVisible(true);

        File file = frmAbstractWFSelect.getAwfSelected();
        if(file != null){

            XStream xstream = new XStream(new DomDriver());
            CompositionModel compositionModel = (CompositionModel) xstream.fromXML(file);

            if(compositionModel != null){
                if(!compositionModel.getLstServices().isEmpty()){
                    FConnectParams frmConnectParams = new FConnectParams(null, true, compositionModel);
                    frmConnectParams.setVisible(true);
                }
            }
        }
    }
    
    public void openWorkflowResultFile(){
        FAbstractWFSelect frmAbstractWFSelect = new FAbstractWFSelect(null, true, false, true);
        frmAbstractWFSelect.setVisible(true);

        File file = frmAbstractWFSelect.getAwfSelected();
        if(file != null){

            FViewWFResult frmViewResult = new FViewWFResult(null, true, file.getAbsolutePath());
            frmViewResult.setVisible(true);    
        }
    }
    
    /**
     * @param controlaMensagens the controlaMensagens to set
     */
    public void setControlaMensagens(ControlaMensagens controlaMensagens) {
        this.controlaMensagens = controlaMensagens;
    }

    /**
     * @return the processWSDLInformation
     */
    public ProcessWSDLInformation getProcessWSDLInformation() {
        return processWSDLInformation;
    }

    /**
     * @return the frmWatingReponse
     */
    public FWaitingResponse getFrmWatingReponse(int tempo, String msg, boolean messager) {
        
        if(frmWatingReponse == null){
            frmWatingReponse = new FWaitingResponse(null, true, tempo, msg, messager);
        }        
        
        return frmWatingReponse;
    }
}
