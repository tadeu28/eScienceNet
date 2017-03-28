/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.searchmanager;

import com.esciencenet.models.FileModel;
import com.esciencenet.models.ServicesInfoModel;
import com.esciencenet.semanticmanager.SemanticManager;
import com.esciencenet.servicemanager.OWLSOperation;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import static java.lang.Thread.MIN_PRIORITY;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import juniorvs.virtualdir.desktop.VirtualDir;

/**
 *
 * @author Tadeu
 */
public class SearchProcessor extends Thread{
    
    private VirtualDir app;
    private String mensagem = "";
    private JTree treeResult;
    private boolean fileSearch = true;
    private boolean processConnectors = false;
    
    public SearchProcessor(VirtualDir app, String msg){
        this.app = app;
        this.mensagem = msg;
    }  
    
    private long gerarEspera(){
        return (long)(Math.random() * 3000)  ;
    }
    
    private long gerarEspera(int espera){
        return (long)(Math.random() * 3000) + espera  ;
    }
    
    @Override
    public void run(){
        try {
            //gero uma espera de segundos
            sleep(this.gerarEspera());
            if(fileSearch){
                //processo a resposta
                processarReposta(this.mensagem); 
            }else{
                if(processConnectors){
                    sleep(this.gerarEspera(5000));
                    processarRespostaConnectors(this.mensagem);
                }else{                
                    //processo a resposta
                    sleep(this.gerarEspera(5000));
                    processarRespostaServices(this.mensagem);
                }
            }
            
            SemanticManager.getInstance().getSearchManager().stopWait();
        } catch (InterruptedException ex) {
            Logger.getLogger(SearchProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void processarRespostaServices(String msg){
        try{            
            JTree treeFile = this.treeResult;
            DefaultTreeModel treeModel = (DefaultTreeModel) treeFile.getModel();      
            DefaultMutableTreeNode rootModel;            
            if (treeModel == null){                
                rootModel = new DefaultMutableTreeNode("Nenhum");
                treeModel = new DefaultTreeModel(rootModel);
            }else{
                rootModel = (DefaultMutableTreeNode) treeModel.getRoot();
            }

            String peerName = msg.substring(0, msg.indexOf("\n"));
            msg = msg.replaceFirst(peerName, "");
            msg = msg.trim();        
            
            if (SemanticManager.getInstance().getNomePeer().equals(peerName)){
                peerName = peerName + "[LOCAL]";
            }
            
            DefaultMutableTreeNode peerFolder = new DefaultMutableTreeNode(peerName);
            peerFolder = this.checkIfExistsNode(rootModel, peerFolder);

            if (!msg.contains("[ERRO]")){
                XStream xstream = new XStream(new DomDriver());
                OWLSOperation owlsOperation = (OWLSOperation) xstream.fromXML(msg);
                
                String operationName = this.changeOperationName(owlsOperation.getOperationName());
                owlsOperation.setOperationName(operationName);
                
                DefaultMutableTreeNode servico = new DefaultMutableTreeNode(owlsOperation.getOperationName() + " - [" + owlsOperation.getOperationDomainTerm()+ "]");   
                
                if(SemanticManager.getInstance().getSearchManager().buscaInfoService(owlsOperation.getOperationName()) == null){
                    
                    SearchManager.lstServicos.add(owlsOperation);
                }
                
                peerFolder.add(servico);
            }else{
                DefaultMutableTreeNode servico = new DefaultMutableTreeNode("The search don't had any results or failures happen in the research.");                     
                peerFolder.add(servico);
            }

            rootModel.add(peerFolder);    
            treeModel.setRoot(rootModel);            

            treeFile.setModel(treeModel);

            for (int i = 0; i < treeFile.getRowCount(); i++) {
                treeFile.expandRow(i);
            }  
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        } 
    }
    
    private String changeOperationName(String operation){

        int cont = 0;
        for(OWLSOperation owlsOperation : SearchManager.lstServicos){
            if(owlsOperation.getOperationName().contains(operation)){
                cont++;
            }
        }
        
        if(cont > 0){
            return operation + "_" + cont;
        }
        
        return operation;
    }
    
    private String changeOperationConnectorName(String operation){

        int cont = 0;
        for(OWLSOperation owlsOperation : SearchManager.lstConnectores){
            if(owlsOperation.getOperationName().contains(operation)){
                cont++;
            }
        }
        
        if(cont > 0){
            return operation + "_" + cont;
        }
        
        return operation;
    }
    
    private DefaultMutableTreeNode checkIfExistsNode(DefaultMutableTreeNode root, DefaultMutableTreeNode node){
        try{            
            for(int i = 0; i < root.getChildCount(); i++){
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
                
                if(child.getUserObject().toString().equals(node.getUserObject().toString())){
                    return child;
                }
            }
            
            return node;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public void processarRespostaConnectors(String msg){
        try{            
            JTree treeFile = this.treeResult;
            DefaultTreeModel treeModel = (DefaultTreeModel) treeFile.getModel();    
            DefaultMutableTreeNode rootModel;            
            if (treeModel == null){                
                rootModel = new DefaultMutableTreeNode("Nenhum");
                treeModel = new DefaultTreeModel(rootModel);
            }else{
                rootModel = (DefaultMutableTreeNode) treeModel.getRoot();
            }

            String peerName = msg.substring(0, msg.indexOf("\n"));
            msg = msg.replaceFirst(peerName, "");
            msg = msg.trim();        
            
            if (SemanticManager.getInstance().getNomePeer().equals(peerName)){
                peerName = peerName + "[LOCAL]";
            }
            
            DefaultMutableTreeNode peerFolder = new DefaultMutableTreeNode(peerName);
            peerFolder = this.checkIfExistsNode(rootModel, peerFolder);

            if (!msg.contains("[ERRO]")){
                XStream xstream = new XStream(new DomDriver());
                OWLSOperation owlsOperation = (OWLSOperation) xstream.fromXML(msg);

                    String operationName = this.changeOperationConnectorName(owlsOperation.getOperationName());
                    owlsOperation.setOperationName(operationName);
                
                    DefaultMutableTreeNode servico = new DefaultMutableTreeNode(owlsOperation.getOperationName());   
                                        
                    OWLSOperation opFind = SemanticManager.getInstance().getSearchManager().buscaInfoConnectores(owlsOperation.getOperationName());
                    if(opFind == null){
                        SearchManager.lstConnectores.add(owlsOperation);
                    }else{
                        opFind.setCompatibility(owlsOperation.getCompatibility());
                    }
                    
                    peerFolder.add(servico);
            }else{
                DefaultMutableTreeNode servico = new DefaultMutableTreeNode("The search don't had any results or failures happen in the research.");                     
                peerFolder.add(servico);
            }

            rootModel.add(peerFolder);    
            treeModel.setRoot(rootModel);            

            treeFile.setModel(treeModel);

            for (int i = 0; i < treeFile.getRowCount(); i++) {
                treeFile.expandRow(i);
            }  
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        } 
    }
    
    public void processarReposta(String msg){
        try{            
            JTree treeFile = app.getTreeFile();        
            DefaultTreeModel treeModel = (DefaultTreeModel) treeFile.getModel();      
            DefaultMutableTreeNode rootModel;            
            if (treeModel == null){                
                rootModel = new DefaultMutableTreeNode("Nenhum");
                treeModel = new DefaultTreeModel(rootModel);
            }else{
                rootModel = (DefaultMutableTreeNode) treeModel.getRoot();
            }

            String peerName = msg.substring(0, msg.indexOf("\n"));
            msg = msg.replaceFirst(peerName, "");
            msg = msg.trim();        
            
            if (SemanticManager.getInstance().getNomePeer().equals(peerName)){
                peerName = peerName + "[LOCAL]";
            }
            
            DefaultMutableTreeNode peerFolder = new DefaultMutableTreeNode(peerName);

            if (!msg.contains("[ERRO]")){
                XStream xstream = new XStream(new DomDriver());
                List<FileModel> lstFileModel = (ArrayList<FileModel>) xstream.fromXML(msg);

                for(FileModel fileModel : lstFileModel){
                    DefaultMutableTreeNode arquivo = new DefaultMutableTreeNode(fileModel.getName() + "" + fileModel.getExtension());   
                    SearchManager.lstArquivos.add(fileModel);
                    peerFolder.add(arquivo);
                }
            }else{
                DefaultMutableTreeNode arquivo = new DefaultMutableTreeNode("The search don't had any results or failures happen in the research.");                     
                peerFolder.add(arquivo);
            }

            rootModel.add(peerFolder);    
            treeModel.setRoot(rootModel);            

            treeFile.setModel(treeModel);

            for (int i = 0; i < treeFile.getRowCount(); i++) {
                treeFile.expandRow(i);
            }  
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        }        
    }

    /**
     * @param treeResult the treeResult to set
     */
    public void setTreeResult(JTree treeResult) {
        this.treeResult = treeResult;
    }

    /**
     * @param fileSearch the fileSearch to set
     */
    public void setFileSearch(boolean fileSearch) {
        this.fileSearch = fileSearch;
    }

    /**
     * @param processConnectors the processConnectors to set
     */
    public void setProcessConnectors(boolean processConnectors) {
        this.processConnectors = processConnectors;
    }
}
