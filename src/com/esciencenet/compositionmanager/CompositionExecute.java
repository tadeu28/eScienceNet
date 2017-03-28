/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.esciencenet.compositionmanager;

import com.esciencenet.forms.FWFExecution;
import com.esciencenet.servicemanager.OWLSOperation;
import com.esciencenet.servicemanager.OWLSParam;
import com.esciencenet.servicemanager.wsdlmodels.WSDLRecover;
import com.esciencenet.servicemanager.wsdlmodels.WSDLRecoverPort;
import com.esciencenet.servicemanager.wsdlmodels.WSDLRecoverService;
import com.esciencenet.utils.EScienceNetUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.xml.soap.SOAPMessage;

/**
 *
 * @author Tadeu Classe
 */
public class CompositionExecute extends Thread{
    
    private FWFExecution frmExecution;
    private String erros = "";
    private boolean stop = false;
    
    public static final String capsulink = "-------------------------------------------------------------------------------\n"+
                                           "%s\n"+
                                           "-------------------------------------------------------------------------------";
    
    public CompositionExecute(FWFExecution frmExecution){
        this.frmExecution = frmExecution;
    }
    
    public void requestStop(){
        this.stop = true;
        Thread.currentThread().interrupt();
    }
    
    @Override
    public void run(){
        
        Timer tempo = this.frmExecution.getTempo();
        
        try{
            this.stop = false;
            this.erros = "";
            JTable tblTasks = this.frmExecution.tblTasks;
            
            if(tempo == null){
                tempo = new Timer(1, this.createTickTimeAction());
                tempo.start();
            }
            
            ImageIcon actualExec = new ImageIcon(getClass().getResource("/images/stopwatch-start-icon.png"));
            ImageIcon errorState = new ImageIcon(getClass().getResource("/images/stop-icon.png"));
            ImageIcon okState = new ImageIcon(getClass().getResource("/images/accept-icon16.png"));
            
            DefaultTableModel model = (DefaultTableModel) tblTasks.getModel();
            for(int i = 0; i < model.getRowCount(); i++){
                model.setValueAt(actualExec, i, 0);
                
                tblTasks.setRowSelectionInterval(i, i);
                this.frmExecution.obterParametros();
                
                CompositionTaskModel compositionTaskModel = this.findTaskModelFromOperationName(model.getValueAt(i, 2).toString(), this.frmExecution.getCompositionModel());
                
                if(compositionTaskModel != null){
                
                    frmExecution.exibirInfo("The task ["+ compositionTaskModel.getTask()+"] whose its operation is ["+ 
                                            compositionTaskModel.getOperation().getOperationName() +"] has been started."); 
                    
                    if(this.stop){
                        frmExecution.exibirInfo("The execution was canceled by the user.");
                        tempo.stop();
                        return;
                    }
                    
                    if(this.executeOperation(compositionTaskModel.getOperation())){
                        model.setValueAt(okState, i, 0);                        
                        tblTasks.setModel(model);
                        
                        frmExecution.exibirInfo("The task ["+ compositionTaskModel.getTask()+"] whose its operation is ["+ 
                                            compositionTaskModel.getOperation().getOperationName() +"] was executed with success."); 
                    }else{
                        model.setValueAt(errorState, i, 0);
                        tblTasks.setModel(model);
                        frmExecution.exibirInfo("The task ["+ compositionTaskModel.getTask()+"] whose its operation is ["+ 
                                            compositionTaskModel.getOperation().getOperationName() +"] wasn't executed.\n\nERROR:\n" + capsulink.format(capsulink, this.erros)); 
                        tempo.stop();
                        return;
                    }
                }
            }

            String finalResult = "";
            CompositionTaskModel compositionLastTask = this.frmExecution.getCompositionModel().getLstServices().
                                                                         get(this.frmExecution.getCompositionModel().getLstServices().size() - 1);
            if(compositionLastTask != null){
                for(OWLSParam param : compositionLastTask.getOperation().getOutputs()){
                    finalResult = finalResult + "\n" + param.getParamName() + " = " + param.getValueReturned();
                }
                
                for(OWLSParam param : compositionLastTask.getOperation().getOutputs()){
                    
                    for(OWLSOperation connector : param.getConnectorAssociated()){
                        
                        for(OWLSParam paramConnector : connector.getOutputs()){
                            finalResult = finalResult + "\n" + paramConnector.getParamName() + " = " + paramConnector.getValueReturned();
                        }                        
                    }                    
                    
                }
                
                finalResult = finalResult.trim();
            }
            
            frmExecution.exibirInfo("The " + this.frmExecution.getCompositionModel().getNameComposition() + " was executed completely with success at "+
                                    this.getFinalTime() 
                                    +"! Congratulations!" + 
                                    "\n\nFINAL RESULT:\n" + capsulink.format(capsulink, finalResult));
            frmExecution.stateComplete();
            
            tempo.stop();
        }catch(Exception e){            
            this.frmExecution.getTempo().stop();
        }finally{
            tempo.stop();
        }
    }    
    
    private boolean checkConnectors(OWLSOperation operation, boolean isPrevious){
        try{            
            if(isPrevious){
                for(OWLSParam input : operation.getInputs()){

                    if(!input.getConnectorAssociated().isEmpty()){

                        for(OWLSOperation connector : input.getConnectorAssociated()){

                            if(this.stop){
                                return true;
                            }
                            
                            frmExecution.exibirInfo("The connector ["+ connector.getOperationName() +"] has been started.");
                            
                            if(!this.executeOperation(connector)){
                                return false;
                            }else{
                                frmExecution.exibirInfo("The connector ["+ connector.getOperationName() +"] was executed with success."); 
                            }
                        }
                    }                
                }
            }else{
                for(OWLSParam output : operation.getOutputs()){
                
                    if(!output.getConnectorAssociated().isEmpty()){

                        for(OWLSOperation connector : output.getConnectorAssociated()){

                            if(this.stop){
                                return true;
                            }
                            
                            frmExecution.exibirInfo("The connector ["+ connector.getOperationName() +"] has been started."); 
                            
                            if(!this.executeOperation(connector)){
                                return false;
                            }else{
                                frmExecution.exibirInfo("The connector ["+ connector.getOperationName() +"] was executed with success."); 
                            }
                        }
                    }                
                }
            }
            
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean executeOperation(OWLSOperation operation){
        try{                     
            if(operation.getRestriction() == null){
                if(!this.processOperation(operation)){
                    return false;
                }
            }else{
                if(!this.processRestriction(operation)){
                    return false;
                }                
            }
            
            return true;
        }catch(Exception e){
            this.erros = e.getMessage();
            return false;
        }
    }
    
    public boolean sleepSomeSeconds(int seconds){
        try {
            Random rdn = new Random(1000);
            Thread.sleep(seconds * rdn.nextInt(1000));
            return true;
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            this.erros = e.getMessage();
            return false;
        }
    }
    
    private boolean processRestriction(OWLSOperation operation){
        try{           
            switch(operation.getRestriction().getRestrictionType()){
                case LOOP:{
                    
                    if(operation.getRestriction().getOwlsParam().getValueReturned() != null){
                        switch (operation.getRestriction().getOperator()) {
                            case "!=":
                                while(!operation.getRestriction().getOwlsParam().getValueReturned().equals(operation.getRestriction().getRestrictionTerm())){
                                    
                                    if(!this.processOperation(operation)){
                                        return false;
                                    }
                                    
                                    if(!this.sleepSomeSeconds(operation.getRestriction().getTime())){
                                        return false;
                                    }
                                }   break;
                            case "==":
                                while(operation.getRestriction().getOwlsParam().getValueReturned().equals(operation.getRestriction().getRestrictionTerm())){
                                    
                                    if(!this.sleepSomeSeconds(operation.getRestriction().getTime())){
                                        return false;
                                    }
                                    
                                    if(!this.processOperation(operation)){
                                        return false;
                                    }
                                }   break;
                        }
                        
                    }else{
                        return false;
                    }
                    
                    break;
                }
                case CONDICTION:{
                    break;
                }
            }
            
            return true;
        }catch(Exception e){
            this.erros = e.getMessage();
            return false;
        }
    }
    
    private boolean processOperation(OWLSOperation operation){
        try{            
            if(!this.checkConnectors(operation, true)){
                return false;
            }
                        
            if(this.stop){
                return true;
            }
            
            ProcessWSDLInformation processWSDLInformation = new ProcessWSDLInformation();
            
            WSDLRecover wsdlRecover = processWSDLInformation.getWSDLInfo(operation.getServiceURL(), 
                                                                         operation.getOperationRealName(), 
                                                                         null, null);
                
            if(wsdlRecover != null){
                String envelop = processWSDLInformation.generateEnvelopeSOAP(wsdlRecover, 
                                                                             operation.getServiceURL(),
                                                                             operation.getOperationRealName());

                if(!envelop.equals("")){
                    envelop = this.replaceInputParameters(envelop, operation.getInputs());
                    
                    SOAPMessage soapMessage = processWSDLInformation.createSOAPMessage(envelop);

                    WSDLRecoverService wsdlRecoverService = wsdlRecover.getServices().get(wsdlRecover.getServices().size() - 1);
                    if(wsdlRecoverService != null){
                        WSDLRecoverPort wsdlRecoverPort = wsdlRecoverService.getPorts().get(wsdlRecoverService.getPorts().size() - 1);
                        
                        if(wsdlRecoverPort != null){
                            if(!processWSDLInformation.soapRequest(wsdlRecoverPort.getAdress(), soapMessage, operation)){
                                this.erros = processWSDLInformation.getError();
                                return false;
                            }
                        }
                    }                    
                }else{
                    this.erros = processWSDLInformation.getError();
                    return false;
                }
                
                if(!this.checkConnectors(operation, false)){
                    return false;
                }
            }else{
                throw new Exception("Errors are happening in the moment to execute the service " + operation.getOperationName());
            }
            
            return true;
        }catch(Exception e){
            this.erros = e.getMessage();
            return false;
        }
    }
    
    private String replaceInputParameters(String envelop, List<OWLSParam> inputs){
        
        for(OWLSParam input : inputs){
            
            String toRemove = "";
            if(envelop.contains(input.getParamName() + ">???</")){
                toRemove = input.getParamName() + ">???</";
            }else if(envelop.contains(input.getParamName() + ">?XXX?</")){
                toRemove = input.getParamName() + ">?XXX?</";
            }else if(envelop.contains(input.getParamName() + ">?999?</")){
                toRemove = input.getParamName() + ">?999?</";
            }else if(envelop.contains(input.getParamName() + ">?999.99?</")){
                toRemove = input.getParamName() + ">?999.99?</";
            }else if(envelop.contains(input.getParamName() + ">?true?</")){
                toRemove = input.getParamName() + ">?true?</";
            }else{
                return envelop;
            }
            
            String toPut = "";
            if(!input.getParamConnected().isEmpty()){

                for(OWLSParam param : input.getParamConnected()){

                    StringTokenizer strTokenizer = new StringTokenizer(param.getValueReturned(), "§");
                    while(strTokenizer.hasMoreTokens()){
                        toPut = toPut + input.getParamName() + ">" + strTokenizer.nextToken().trim() + "</";
                    }                            
                }

                toPut = toPut.replaceAll("</" + input.getParamName() + ">" , "</" + input.getParamName() + "><" + input.getParamName() + ">");

            }else if(!input.getManualContent().equals("")){
                toPut = input.getParamName() + ">" + input.getManualContent() + "</";
            }else if(!input.getParamConnectorAssociated().isEmpty()){
                
                for(OWLSParam param : input.getParamConnectorAssociated()){

                    StringTokenizer strTokenizer = new StringTokenizer(param.getValueReturned(), "§");
                    while(strTokenizer.hasMoreTokens()){
                        toPut = toPut + input.getParamName() + ">" + strTokenizer.nextToken().trim() + "</";
                    }                            
                }

                toPut = toPut.replaceAll("</" + input.getParamName() + ">" , "</" + input.getParamName() + "><" + input.getParamName() + ">");
            }
            else{
                toPut = input.getParamName() + "></";
            }

            if(!toPut.equals("")){
                envelop = envelop.replace(toRemove, toPut);
            }  
        }
        
        envelop = envelop.replace("?true?", "");
        envelop = envelop.replace("?XXX?", "");
        envelop = envelop.replace("?999?", "");
        envelop = envelop.replace("?999.99?", "");
        
        return envelop;
    }
    
    private CompositionTaskModel findTaskModelFromOperationName(String operationName, CompositionModel compositionModel){
        
        for(CompositionTaskModel compositionTaskModel : compositionModel.getLstServices()){
            if(compositionTaskModel.getOperation().getOperationName().equals(operationName)){
                return compositionTaskModel;
            }
        }
        
        return null;
    }
    
        private ActionListener createTickTimeAction(){
        
        ActionListener actionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                long tempoCalc = System.currentTimeMillis() - CompositionExecute.this.frmExecution.getTempoInicio();                
                CompositionExecute.this.frmExecution.lblTime.setText("Time Elapsed ["+ EScienceNetUtils.formatMilisecond("hh:mm:ss.SSS", tempoCalc) +"]");
            }
        };
        
        return actionListener;
    }
    
    private String getFinalTime(){
        long tempoCalc = System.currentTimeMillis() - CompositionExecute.this.frmExecution.getTempoInicio();                
        return EScienceNetUtils.formatMilisecond("hh:mm:ss.SSS", tempoCalc);
    }
        
    /**
     * @return the erros
     */
    public String getErros() {
        return erros;
    }
}
