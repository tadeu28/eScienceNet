/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.research;

import com.esciencenet.searchmanager.SearchManager;
import com.esciencenet.searchmanager.SearchManagerEnum;
import com.esciencenet.searchmanager.SearchProcessor;
import com.esciencenet.semanticmanager.SemanticManager;
import com.esciencenet.servicemanager.OWLSOperation;
import com.esciencenet.servicemanager.OWLSParam;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTree;

/**
 *
 * @author Tadeu
 */
public class ConnectorsSearch extends Thread{
    
    private String strBusca = "";
    private String errorMessage = "";
    private String peerName = "";
    private SearchManager searchManager;
        
    public ConnectorsSearch(SearchManager searchManager){
        this.searchManager = searchManager;
        this.errorMessage = "";
    }
    
    @Override
    public void run(){
        String peerNameToResponse = "";
        
        try{
            this.errorMessage = "";
            
            if(!this.strBusca.equals("")){
                
                //pego a string de busca
                strBusca = strBusca.replace(SearchManagerEnum.CONN_SEARCH.toString(), "");

                peerNameToResponse = strBusca.substring(0, strBusca.indexOf("|"));
                strBusca = strBusca.replace(peerNameToResponse + "|", "");
                
                String groupName = strBusca.substring(0, strBusca.indexOf("|"));
                strBusca = strBusca.replace(groupName + "|", "");

                boolean isInput = strBusca.contains("1|");
                strBusca = strBusca.replace("1|", "");
                strBusca = strBusca.replace("0|", "");

                String serviceName = strBusca.substring(0, strBusca.indexOf("|"));
                strBusca = strBusca.replace(serviceName + "|", "");

                XStream xml = new XStream(new DomDriver());
                List<OWLSParam> owlsParams = (List<OWLSParam>) xml.fromXML(strBusca.trim());

                if(owlsParams != null){
                    List<OWLSOperation> lstOperations = SemanticManager.getInstance().searchByConnectorsInPeerOWL(groupName, serviceName, owlsParams, isInput);
                    
                    if(lstOperations != null){
                        
                        if(lstOperations.isEmpty()){
                            errorMessage = "[ERRO]";
                            searchManager.getControlaMensagens().enviarMensagem("<"+ peerNameToResponse +">" + SearchManagerEnum.CONN_RES + "\n" + errorMessage);
                        }
                        
                        for(OWLSOperation operation : lstOperations){
                                                    
                            XStream xStream = new XStream(new DomDriver());
                            String xmlResults = xStream.toXML(operation);

                            String msgResponse = SearchManagerEnum.CONN_RES.toString() + "<" + peerNameToResponse + ">" + "\n" + xmlResults;

                            SemanticManager.getInstance().getSearchManager().getControlaMensagens().enviarMensagem(msgResponse);
                        }
                    }
                }else{
                    errorMessage = "[ERRO]";                    
                    searchManager.getControlaMensagens().enviarMensagem("<"+ peerNameToResponse +">" + SearchManagerEnum.CONN_RES + "\n" + errorMessage);
                }
            }
        }catch(Exception e){
            this.errorMessage = "It wasn't possible to process the connectors research!\n\n" + e;
            searchManager.getControlaMensagens().enviarMensagem("<"+ peerNameToResponse +">" + SearchManagerEnum.CONN_RES + "\n" + errorMessage);
        }
    }
    
    /**
     * @return the strBusca
     */
    public String getStrBusca() {
        return strBusca;
    }

    /**
     * @param strBusca the strBusca to set
     */
    public void setStrBusca(String strBusca) {
        this.strBusca = strBusca;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the peerName
     */
    public String getPeerName() {
        return peerName;
    }

    /**
     * @param peerName the peerName to set
     */
    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    /**
     * @return the searchManager
     */
    public SearchManager getSearchManager() {
        return searchManager;
    }

    /**
     * @param searchManager the searchManager to set
     */
    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }
}
