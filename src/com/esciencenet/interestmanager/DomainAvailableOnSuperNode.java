/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.interestmanager;

import com.esciencenet.semanticmanager.SemanticManager;
import com.esciencenet.utils.EScienceNetUtils;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JOptionPane;
import net.jxta.share.ContentAdvertisement;

/**
 *
 * @author Tadeu
 */
public class DomainAvailableOnSuperNode extends Thread{
    
    private String xml;
    private String fileName = "";
    private String superNode;
    private String simpleNode;
    private boolean upLoadSN;
    
    private String fileOWLNameToMSG;
    
    @Override
    public void run(){            
        fileOWLNameToMSG = "";
        
        if(fileName.equals("")){        
            if(SemanticManager.getInstance().getInterestManager().getInterestOperations() == InterestOperationEnum.showDomain){
                SemanticManager.getInstance().getInterestManager().getFrmDomainAvailable().setXmlDomains(xml);
                SemanticManager.getInstance().getInterestManager().getFrmDomainAvailable().setVisible(true);        
            }else{
                SemanticManager.getInstance().getInterestManager().getFrmIncludeNewDomain().setXml(xml);
                SemanticManager.getInstance().getInterestManager().getFrmIncludeNewDomain().setVisible(true);        
            }
        }else{
            if(!upLoadSN){
                if(this.gravarAquivo()){                      
                    fileName = fileName.substring(fileName.lastIndexOf("#") + 1);
                    
                    SemanticManager.getInstance().getInterestManager().manageDomainInformationReceived(fileName);
                    SemanticManager.getInstance().getInterestManager().getFrmWatingReponse().setVisible(false);
                    SemanticManager.getInstance().getInterestManager().getFrmDomainAvailable().update();                    
                }
            }else{
                String nomeSuperNo = SemanticManager.getInstance().getSuperNoByGroup(SemanticManager.getInstance().getInterestManager().getGrupoSelecionado().getGroupName());
                
                if(this.persistsOnDomainRepository()){        
                    if(nomeSuperNo.equals(simpleNode)){
                        fileName = fileName.substring(fileName.lastIndexOf("#") + 1);
                    
                        SemanticManager.getInstance().getInterestManager().manageDomainInformationReceived(fileName);
                        
                        JOptionPane.showMessageDialog(null, "The domain ontology was saved in Super Node!", ".: e-ScienceNet :.", JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        SemanticManager.getInstance().getInterestManager().getControlaMensagens().enviarMensagem("<" + simpleNode + ">#" + InterestManager.UP_DOMAIN_SN_OK + "#" + fileOWLNameToMSG);
                    }
                }else{
                    if(nomeSuperNo.equals(simpleNode)){
                        JOptionPane.showMessageDialog(null, "Wasn't possible save the domain ontology in Super Node!", ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
                    }else{
                        SemanticManager.getInstance().getInterestManager().getControlaMensagens().enviarMensagem("<" + simpleNode + ">#" + InterestManager.UP_DOMAIN_SN_ERROR + "#" + fileOWLNameToMSG);
                    }                
                }
                
                SemanticManager.getInstance().getInterestManager().getFrmWatingReponse().setVisible(false);                
            }            
        }
    }    

    private boolean persistsOnDomainRepository(){
        try{
            String nomeSuperNo = SemanticManager.getInstance().getSuperNoByGroup(SemanticManager.getInstance().getInterestManager().getGrupoSelecionado().getGroupName());
            
            String fileOWLName = fileName.substring(fileName.indexOf(InterestManager.UP_DOMAIN_SN) + InterestManager.UP_DOMAIN_SN.length() + 1, fileName.length());                   
            fileOWLNameToMSG = fileOWLName;
            
            String domainRepoSN = SemanticManager.getInstance().getDomainOntologyPath() + File.separator + fileOWLName;
            
            String nameOWL = SemanticManager.getInstance().getDirOntology() + File.separator + SemanticManager.DOMAINS_OWL_DIR + File.separator + fileOWLName;  
            
            File owlFile = new File(domainRepoSN);            
            
            if(!nomeSuperNo.equalsIgnoreCase(simpleNode)){                
                
                ContentAdvertisement adv = null;
                do{
                    //seto o conteúdo para o no selecionado            
                    adv = SemanticManager.getInstance().getInterestManager().getPeers().getPeer(simpleNode).getAnuncioConteudo(fileOWLName);
                    
                    if(adv != null){
                        SemanticManager.getInstance().getDataManager().getGerenciaProcura().recuperaConteudo(adv, domainRepoSN, 
                                                       SemanticManager.getInstance().getDataManager().getConteudoRemoto());      
                    }else{
                        this.sleep(10000);
                    }
                }while(adv == null);
                
            }else{
             
                if(!EScienceNetUtils.copyLocalFiles(nameOWL, domainRepoSN)){
                    throw new Exception("File not exists!");
                }                
            }
            
            owlFile = new File(domainRepoSN);
            
            while(!owlFile.exists()){
                this.sleep(100);
            }
            
            if(owlFile.exists()){
                if(!SemanticManager.getInstance().owlValidation(owlFile)){
                    owlFile.delete();
                    return false;
                }                    
                
                return SemanticManager.getInstance().gravarOntologiaByOWLAPI(owlFile);
            }else{
                throw new Exception("File not exists!");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean gravarAquivo(){
        try{
            String nomeSuperNo = SemanticManager.getInstance().getSuperNoByGroup(SemanticManager.getInstance().getInterestManager().getGrupoSelecionado().getGroupName());
            
            String nameOWL = fileName.substring(fileName.indexOf(InterestManager.RESPONSE_OWL) + InterestManager.RESPONSE_OWL.length() + 1, fileName.length());            
            String filePath = SemanticManager.getInstance().getDirOntology() + File.separator + SemanticManager.DOMAINS_OWL_DIR + File.separator + nameOWL;
            
            if(!nomeSuperNo.equalsIgnoreCase(SemanticManager.getInstance().getNomePeer())){
                //seto o conteúdo para o no selecionado            
                ContentAdvertisement adv = SemanticManager.getInstance().getInterestManager().getPeers().getPeer(superNode).getAnuncioConteudo(nameOWL);

                SemanticManager.getInstance().getDataManager().getGerenciaProcura().recuperaConteudo(adv, filePath, 
                                                   SemanticManager.getInstance().getDataManager().getConteudoRemoto());      
            }else{
             
                String domainRepoSN = SemanticManager.getInstance().getDomainOntologyPath() + File.separator + nameOWL;

                if(!EScienceNetUtils.copyLocalFiles(domainRepoSN, filePath)){
                    throw new Exception("File not exists!");
                }
                
            }
            
            File owlFile = new File(filePath);

            while(!owlFile.exists()){
                DomainAvailableOnSuperNode.sleep(100);
            }

            DomainAvailableOnSuperNode.sleep(500);
            
            if(owlFile.exists()){
                if(!SemanticManager.getInstance().owlValidation(owlFile)){
                    owlFile.delete();
                    return false;
                }                    
                
                return SemanticManager.getInstance().gravarOntologiaByOWLAPI(owlFile);
            }else{
                throw new Exception("File not exists!");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }        
    }
    
    /**
     * @param xml the xml to set
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @param superNode the superNode to set
     */
    public void setSuperNode(String superNode) {
        this.superNode = superNode;
    }

    /**
     * @param upLoadSN the upLoadSN to set
     */
    public void setUpLoadSN(boolean upLoadSN) {
        this.upLoadSN = upLoadSN;
    }

    /**
     * @param simpleNode the simpleNode to set
     */
    public void setSimpleNode(String simpleNode) {
        this.simpleNode = simpleNode;
    }
}
