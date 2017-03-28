/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.research;

import com.esciencenet.forms.FWaitingResponse;
import com.esciencenet.models.ServicesInfoModel;
import com.esciencenet.searchmanager.SearchManager;
import com.esciencenet.searchmanager.SearchManagerEnum;
import com.esciencenet.semanticmanager.SemanticManager;
import com.esciencenet.servicemanager.OWLSOperation;
import com.esciencenet.servicemanager.OWLSParam;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Tadeu
 */
public class ServicesSearch extends Thread{
 
    private String strBusca = "";
    private String errorMessage = "";
    private String peerName = "";    
    private SearchManager searchManager;
    private FWaitingResponse frmWatingReponse;    
        
    public ServicesSearch(SearchManager searchManager){
        this.searchManager = searchManager;
        this.errorMessage = "";
        this.frmWatingReponse = new FWaitingResponse(null, true);
    }
    
    @Override
    public void run(){
        
        this.errorMessage = "";
        
        if(!this.strBusca.equals("")){
                                    
            List<String> lstRelatedTerms = new ArrayList<>();
            //pego a string de busca
            strBusca = strBusca.replace(SearchManagerEnum.SERVICE_SEARCH.toString(), "");
                
            List<OWLSParam> outputsToCompare = null;
            
            if(strBusca.contains("||")){
                String xmlOWLSOperation = strBusca.substring(strBusca.lastIndexOf("||") + 2, strBusca.length());
                strBusca = strBusca.substring(0, strBusca.lastIndexOf("||"));
                                
                XStream xml = new XStream(new DomDriver());
                outputsToCompare = (List<OWLSParam>) xml.fromXML(xmlOWLSOperation.trim());
            }
            
            
            //divido a string pelos limitadores
            StringTokenizer strTokenizer = new StringTokenizer(strBusca, "#");
                
            //percorro a string de opcoes colocando-as em uma lista
            while (strTokenizer.hasMoreElements()){
                lstRelatedTerms.add(strTokenizer.nextToken());
            }
            
            if(!lstRelatedTerms.isEmpty()){
                List<ServicesInfoModel> lstServicesInfoModels = SemanticManager.getInstance().searchServices(lstRelatedTerms, false);

                if(! lstServicesInfoModels.isEmpty()){

                    List<OWLSOperation> lstOWLSOperations = new ArrayList<>();
                    
                    for(ServicesInfoModel servicesInfoModel : lstServicesInfoModels){
                        OWLSOperation owlsOperation = 
                                SemanticManager.getInstance().serviceSemanticResearch(servicesInfoModel.getOwlsFile(), servicesInfoModel.getDomainName(), false);
                        
                        if (owlsOperation != null){
                            
                            List<OWLSParam> lstSubParamenters = new ArrayList<>();
                            for(OWLSParam input : owlsOperation.getInputs()){
                                if(input.getParamDomainTerm().contains("Thing")){
                                    List<OWLSParam> subParams = SemanticManager.getInstance().getSubParameterOnSemanticSearch(input, servicesInfoModel.getOwlsFile());
                                    if(subParams != null){
                                        if(!subParams.isEmpty()){
                                            lstSubParamenters.addAll(subParams);
                                        }
                                    }
                                }
                            }
                            
                            if(!lstSubParamenters.isEmpty()){                                
                                owlsOperation.getInputs().addAll(lstSubParamenters);                                
                            }
                            
                            lstSubParamenters.clear();
                            for(OWLSParam output : owlsOperation.getOutputs()){
                                if(output.getParamDomainTerm().contains("Thing")){
                                    List<OWLSParam> subParams = SemanticManager.getInstance().getSubParameterOnSemanticSearch(output, servicesInfoModel.getOwlsFile());
                                    if(subParams != null){
                                        if(!subParams.isEmpty()){
                                            lstSubParamenters.addAll(subParams);
                                        }
                                    }
                                }
                            }
                            
                            if(!lstSubParamenters.isEmpty()){                                
                                owlsOperation.getOutputs().addAll(lstSubParamenters);                                
                            }
                                  
                            this.removerThing(owlsOperation);
                            
                            if(outputsToCompare != null){
                                if(SemanticManager.getInstance().getCompositionManager().compareSemanticServiceParams(outputsToCompare, owlsOperation.getInputs())){
                                    lstOWLSOperations.add(owlsOperation);
                                }
                            }else{
                                lstOWLSOperations.add(owlsOperation);
                            }
                        }
                    }               
                    
                    if(lstOWLSOperations.isEmpty()){
                        errorMessage = "[ERRO]";
                        searchManager.getControlaMensagens().enviarMensagem("<"+ peerName +">" + SearchManagerEnum.SERVICE_ANSWERS + "\n" + errorMessage);
                    }
                    
                    XStream xml = new XStream(new DomDriver());                   

                    for(OWLSOperation oWLSOperation : lstOWLSOperations){
                    
                        String xmlString = xml.toXML(oWLSOperation);                    
                        searchManager.getControlaMensagens().enviarMensagem("<"+ peerName +">" + SearchManagerEnum.SERVICE_ANSWERS + "\n" + xmlString + errorMessage);
                    }
                }else{
                    errorMessage = "[ERRO]";                    
                    searchManager.getControlaMensagens().enviarMensagem("<"+ peerName +">" + SearchManagerEnum.SERVICE_ANSWERS + "\n" + errorMessage);
                }
            }else{
                errorMessage = "Não foi passado nenhum critério de pesquisa.";
            }   
        }
    }
    
    private void removerThing(OWLSOperation owlsOperation){
        boolean thingFind = true;
        while(thingFind){    
            thingFind = false;

            for(OWLSParam input : owlsOperation.getInputs()){
                if(input.getParamDomainTerm().contains("Thing")){
                    owlsOperation.getInputs().remove(input);
                    thingFind = true;
                    break;                                        
                }
            }

            for(OWLSParam output : owlsOperation.getOutputs()){
                if(output.getParamDomainTerm().contains("Thing")){
                    owlsOperation.getOutputs().remove(output);
                    thingFind = true;
                    break;                                        
                }
            }
        }
    }

    /**
     * @param strBusca the strBusca to set
     */
    public void setStrBusca(String strBusca) {
        this.strBusca = strBusca;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @param peerName the peerName to set
     */
    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    /**
     * @param searchManager the searchManager to set
     */
    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }
    
    public FWaitingResponse getFrmWatingReponse() {
        return frmWatingReponse;
    }
}
