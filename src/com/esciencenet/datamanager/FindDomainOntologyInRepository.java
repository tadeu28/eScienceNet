/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.datamanager;

import com.esciencenet.interestmanager.InterestManager;
import com.esciencenet.models.FindDomainFileModel;
import com.esciencenet.searchmanager.SearchProcessor;
import com.esciencenet.semanticmanager.SemanticManager;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import juniorvs.virtualdir.ControlaMensagens;

/**
 *
 * @author Tadeu
 */
public class FindDomainOntologyInRepository extends Thread{

    private String xmlResposta = "";
    private ControlaMensagens controlaMensagens;
    private String peerRequest = "";
    private boolean download;
    private String fileToDownload;
    
    private long gerarEspera(){
        return (long)(Math.random() * 3000)  ;
    }
    
    @Override
    public void run(){
        try {
            //gero uma espera de segundos
            sleep(this.gerarEspera());
             
            if(! isDownload()){
                xmlResposta = this.processarResposta();
            
                controlaMensagens.enviarMensagem("<" + peerRequest + ">#" + InterestManager.RESPONSE_DOMAINS + "#" + xmlResposta);
            }else{
                fileToDownload = fileToDownload.substring(fileToDownload.indexOf(InterestManager.GIVEME_OWL) + InterestManager.GIVEME_OWL.length() + 1, fileToDownload.length());
                
                String nomeSuperNo = SemanticManager.getInstance().getSuperNoByGroup(SemanticManager.getInstance().getInterestManager().getGrupoSelecionado().getGroupName());
                
                if(!nomeSuperNo.equalsIgnoreCase(peerRequest)){
                    this.processaDownload();
                }
            
                controlaMensagens.enviarMensagem("<" + peerRequest + ">#" + InterestManager.RESPONSE_OWL + "#" + fileToDownload);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SearchProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void processaDownload(){
        try{
            File owlFile = new File(SemanticManager.getInstance().getDomainOntologyPath() + File.separator + fileToDownload);            
            if(!SemanticManager.getInstance().getDataManager().getGerenciaProcura().localContentExists(fileToDownload)){
                SemanticManager.getInstance().getDataManager().getGerenciaProcura().addFile(owlFile);
            }
        }catch(Exception e){
            System.err.println(e);
        }
    }
    
    private String processarResposta(){
        
        List<FindDomainFileModel> lstFileModel = new ArrayList<>();
        
        File directory = new File(SemanticManager.getInstance().getDomainOntologyPath());        
        String[] arquivos = directory.list();        
        
        for(String arquivo : arquivos){
            
            File file = new File(directory.getAbsolutePath() + File.separator + arquivo);
            
            if(file.exists()){
                FindDomainFileModel fileModel = new FindDomainFileModel();
                fileModel.setSize(file.length() / 1024);
                fileModel.setNome(file.getName());
                
                lstFileModel.add(fileModel);
            }
        }
        
        XStream xstream = new XStream(new DomDriver());
        String retorno = xstream.toXML(lstFileModel);
        
        return retorno;
    }    

    /**
     * @param controlaMensagens the controlaMensagens to set
     */
    public void setControlaMensagens(ControlaMensagens controlaMensagens) {
        this.controlaMensagens = controlaMensagens;
    }

    /**
     * @param peerRequest the peerRequest to set
     */
    public void setPeerRequest(String peerRequest) {
        this.peerRequest = peerRequest;
    }

    /**
     * @return the download
     */
    public boolean isDownload() {
        return download;
    }

    /**
     * @param download the download to set
     */
    public void setDownload(boolean download) {
        this.download = download;
    }

    /**
     * @param fileToDownload the fileToDownload to set
     */
    public void setFileToDownload(String fileToDownload) {
        this.fileToDownload = fileToDownload;
    }
}
