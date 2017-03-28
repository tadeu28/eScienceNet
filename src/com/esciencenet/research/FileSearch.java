/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.research;

import com.esciencenet.models.FileModel;
import com.esciencenet.searchmanager.*;
import com.esciencenet.semanticmanager.SemanticManager;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Classe responsável pela thread de pesquisa dos arquivos em ontologias.
 * 
 * @author Tadeu
 */
public class FileSearch extends Thread{
    
    private String strBusca = "";
    private String errorMessage = "";
    private String peerName = "";
    private boolean localSearch = false;
    private SearchManager searchManager;
        
    public FileSearch(SearchManager searchManager){
        this.searchManager = searchManager;
        this.errorMessage = "";
        this.localSearch = false;
    }
    
    @Override
    public void run(){
    
        errorMessage = "";
        
        if (! strBusca.equals("")){            
            //crio a lista de opções a serem pesquisadas
            List<String> lstOptions = new ArrayList<String>();
            //pego a string de busca
            strBusca = strBusca.replace(SearchManagerEnum.FILE_SEARCH.toString(), "");
            //divido a string pelos limitadores
            StringTokenizer strTokenizer = new StringTokenizer(strBusca, "#");
            //pego o primeiro termo antes do limitador, sabendo que o mesmo é a string de busca
            String termoBusca = strTokenizer.nextToken();
                
            //percorro a string de opcoes colocando-as em uma lista
            while (strTokenizer.hasMoreElements()){
                lstOptions.add(strTokenizer.nextToken());
            }
            
            List<FileModel> lstFileModel;
            
            //verifico se é uma busca local, pois se for o caso, não existe a necessidade de enviar mensagem a todos peers.
            if (!localSearch){
                //realizo a pesquisa retornando os modelos de arquivos encontrados
                lstFileModel = SemanticManager.getInstance().buscarArquivos(termoBusca, SemanticManager.getInstance().getNomePeer(), lstOptions);
            }else{
                //realizo a pesquisa retornando os modelos de arquivos encontrados
                lstFileModel = SemanticManager.getInstance().buscarArquivos(termoBusca, peerName, lstOptions);
            }
                
            //verifico se foram encontrados resultados
            if ((lstFileModel == null) || (lstFileModel.isEmpty())){
                errorMessage = "[ERRO]";
            }
             
            XStream xstream = new XStream(new DomDriver());
            String retorno = xstream.toXML(lstFileModel);

            //verifico se é uma busca local, pois se for o caso, não existe a necessidade de enviar mensagem a todos peers.
            if (!localSearch){
                searchManager.getControlaMensagens().enviarMensagem("<"+ peerName +">" + SearchManagerEnum.FILE_ANSWERS + "\n" + retorno + errorMessage);
            }else{
                //envio uma mensagem local
                searchManager.processaResposta(peerName + "\n" + retorno + errorMessage, localSearch);
            }
        }else{
            errorMessage = "Não foi passado nenhum critério de pesquisa.";
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
     * @return the localSearch
     */
    public boolean isLocalSearch() {
        return localSearch;
    }

    /**
     * @param localSearch the localSearch to set
     */
    public void setLocalSearch(boolean localSearch) {
        this.localSearch = localSearch;
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
}
