/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.searchmanager;

import com.esciencenet.forms.FWaitingResponse;
import com.esciencenet.models.FileModel;
import com.esciencenet.models.ServicesInfoModel;
import com.esciencenet.research.ConnectorsSearch;
import com.esciencenet.research.FileSearch;
import com.esciencenet.research.ServicesSearch;
import com.esciencenet.semanticmanager.SemanticManager;
import com.esciencenet.servicemanager.OWLSOperation;
import com.esciencenet.servicemanager.OWLSParam;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import juniorvs.virtualdir.ControlaMensagens;
import juniorvs.virtualdir.desktop.VirtualDir;

/**
 *
 * @author Tadeu Classe
 */
public class SearchManager {    
    
    private ControlaMensagens controlaMensagens;
    private VirtualDir app;
    private JTree treeResults;
    private JTree treeResultsConnector;
    private FWaitingResponse frmWatingReponse;
    
    public static List<FileModel> lstArquivos = new ArrayList<>();
    public static List<OWLSOperation> lstServicos = new ArrayList<>();
    public static List<OWLSOperation> lstConnectores = new ArrayList<>();
    
    public SearchManager(ControlaMensagens controlaMensagens, VirtualDir app){
        this.controlaMensagens = controlaMensagens;
        this.app = app;
        this.frmWatingReponse = new FWaitingResponse(null, true);
    }

    public void pesquisaRemota(String msg, String peerName){
        //Inicio o processo de pesquisa de arquivos
        FileSearch fileSearch = new FileSearch(this);
        fileSearch.setStrBusca(msg);
        fileSearch.setPeerName(peerName);
        fileSearch.setLocalSearch(false);
        fileSearch.start();          
    }
    
    public void pesquisaLocal(String msg, String peerName){
        //Inicio o processo de pesquisa de arquivos
        FileSearch fileSearch = new FileSearch(this);
        fileSearch.setStrBusca(msg);
        fileSearch.setPeerName(peerName);
        fileSearch.setLocalSearch(true);
        fileSearch.start();  
    }  
    
    public void servicesSearch(String msg, String peerName){
        ServicesSearch servicesSearch = new ServicesSearch(this);
        servicesSearch.setStrBusca(msg);
        servicesSearch.setPeerName(peerName);
        servicesSearch.start();
    }
    
    public void connectorsSearch(String msg, String peerName){
        ConnectorsSearch connectorsSearch = new ConnectorsSearch(this);
        connectorsSearch.setPeerName(peerName);
        connectorsSearch.setStrBusca(msg);
        connectorsSearch.start();
    }
    
    //envio a requisicao remota de pesquisa
    public void enviarRequisicaoRemota(String busca){
        getControlaMensagens().enviarMensagem(busca);
    }
    
    //metodo responsavel por processar a pesquisa local de arquivos;
    public void processaResposta(String msg, boolean localSearch){
        
        SearchProcessor searchProcessor = new SearchProcessor(app, msg);
        searchProcessor.setFileSearch(true);
        
        if (localSearch){
            searchProcessor.processarReposta(msg);
        }else{
            searchProcessor.start();
        }
    }
    
    //metodo responsavel por processar a pesquisa local de serviços;
    public void processaRespostaServico(String msg, boolean localSearch){
        
        if(this.treeResults != null){
            SearchProcessor searchProcessor = new SearchProcessor(app, msg);
            searchProcessor.setTreeResult(this.treeResults);
            searchProcessor.setFileSearch(false);
            searchProcessor.start();
        }else{
            JOptionPane.showMessageDialog(null, "The result tree wasn't created.", ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        }        
    }

    //metodo responsavel por processar a pesquisa de serviços
    public void processaRespostaConnectors(String msg){
        
        if(this.treeResultsConnector != null){
            SearchProcessor searchProcessor = new SearchProcessor(app, msg);
            searchProcessor.setTreeResult(this.treeResultsConnector);
            searchProcessor.setFileSearch(false);
            searchProcessor.setProcessConnectors(true);
            searchProcessor.start();
        }else{
            JOptionPane.showMessageDialog(null, "The result tree wasn't created.", ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        }        
    }
    
    /**
     * @return the controlaMensagens
     */
    public ControlaMensagens getControlaMensagens() {
        return controlaMensagens;
    }
    
    private FileModel buscaInfoFiles(String nome){
        
        for(FileModel fileModel : lstArquivos){
            
            String nomeBusca = fileModel.getName() + fileModel.getExtension();
            
            if (nomeBusca.equals(nome)){
                return fileModel;
            }
        }
        
        return null;
    }
    
    public OWLSOperation buscaInfoConnectores(String nome){
        
        if(nome.indexOf(" ") != -1){
            nome = nome.substring(0, nome.indexOf(" ")).trim();
        }
        
        for(OWLSOperation owlsOperation : lstConnectores){
            
            String nomeBusca = owlsOperation.getOperationName();
            
            if (nomeBusca.equals(nome)){
                return owlsOperation;
            }
        }
        
        return null;
    }
    
    public OWLSOperation buscaInfoService(String nome){
        
        if(nome.indexOf(" ") != -1){
            nome = nome.substring(0, nome.indexOf(" ")).trim();
        }
        
        for(OWLSOperation owlsOperation : lstServicos){
            
            String nomeBusca = owlsOperation.getOperationName();
            
            if (nomeBusca.equals(nome)){
                return owlsOperation;
            }
        }
        
        return null;
    }
    
    public String obterInformacoesArquivo(String name, boolean hasChiildren){
        try{
            
            //verifico se tem filhos, isso significa que é um nó
            if (hasChiildren){
                //verifico se o nome está vazio
                if(name.isEmpty()){
                    return "Could not display the file information, because it was not found.";                
                }

                //monto o html de exibição das informações do arquivo
                String html = "<font size='5' face='verdana' color='#0000FF'><b>Informations</font></b><br><hr size='3' color='#000000'>"+
                              "<font face='verdana' color='#000000' size='4'><ul>"+
                              " It's a Peer. Its name if <B>" + name + "</B>.<BR>"+
                              " Therefore any other information will be to given about it here."+
                              "</ul></font>";
                return html;
                
            }else{
                //busco o nome do arquivo na lista
                FileModel fileModel = buscaInfoFiles(name);
            
                //verifico se o modelo retornado é vazio
                if(fileModel == null){
                    return "Could not display the file information, because it was not found.";                
                }

                String downloadTest = "Content doesn't addtional for download...";
                String downloadImage = "/images/notdownload.png";
                
                if (SemanticManager.getInstance().getDataManager().verificarDownaloaded(fileModel.getName().concat(fileModel.getExtension()), fileModel.getPeerName())){
                    downloadTest = "Content ready for download...";                    
                    downloadImage = "/images/download.png";
                }
                
                //verifico se e um arquivo local                
                if (fileModel.getPeerName().equals(SemanticManager.getInstance().getNomePeer())){
                    downloadTest = "Content doesn't addtional for download because it's a local content...";                    
                    downloadImage = "/images/localfile.png";
                }
                
                //monto o html de exibição das informações do arquivo
                String html = "<font size='5' face='verdana' color='#0000FF'><b>File Informations</font></b><br><hr size='3' color='#000000'>"+
                              "<IMG SRC='"+ getClass().getResource(downloadImage) +"' align='middle' />   "+
                              "<font size='2' face='verdana' color='#000000'><b>"+ downloadTest +"</b></font><hr size='1' color='#000000'>"+
                              "<font face='verdana' color='#8800FF' size='3'><ul>"+
                              "        <li><b>Name: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getName() +"</font></li>"+
                              "        <ul>"+
                              "                <li><b>Extension: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getExtension() +"</font></li>"+
                              "                <li><b>Path: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getPath() +"</font></li>"+	
                              "                <li><b>Size: </b><font face='verdana' color='#000000' size='2'>"+ Double.toString(fileModel.getSize()) +" KB</font></li>"+	
                              "                <li><b>Type: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getType() +"</font></li>"+
                              "                <li><b>Creation Date: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getDate() +"</font></li>"+
                              "                <li><b>Description: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getDescription() +"</font></li>"+
                              "        </ul>"+
                              "        <li><b>Peer: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getPeerName() +"</font></li>"+
                              "        <li><b>Group: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getPeerGroup().getGroupName() +"</font></li>"+
                              "</ul></font>";

                return html;
            }
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return "Could not display the file information, because it was not found.";
        }      
    }
    
    public String obterInformacoes(OWLSOperation owlsOperation){
        try{
            //verifico se o modelo retornado é vazio
            if(owlsOperation == null){
                return "Could not display the service information, because it was not found.";                
            }

            String inputs = "";
            for(OWLSParam owlsParam : owlsOperation.getInputs()){
                inputs = inputs + 
                "<li><font face='verdana' color='#000000' size='2'><b>"+ owlsParam.getParamName() +" [Semantic]</b>: "+ owlsParam.getParamDomainTerm() +
                " - <b>[Syntatic]</b>: "+ owlsParam.getParamType() +"</font></li>";
            }

            String outputs = "";
            for(OWLSParam owlsParam : owlsOperation.getOutputs()){

                outputs = outputs + 
                "<li><font face='verdana' color='#000000' size='2'><b>"+ owlsParam.getParamName() +" [Semantic]</b>: "+ owlsParam.getParamDomainTerm() +
                " - <b>[Syntatic]</b>: "+ owlsParam.getParamType() +"</font></li>";
            }

            //monto o html de exibição das informações do arquivo
            String html = "<font face='verdana' color='#8800FF' size='2'><ul>"+
                          "<li><b>" + (owlsOperation.getOperationDomainTerm().equals("") ? "Connector Name:" : "Service Name:") + " </b><font face='verdana' color='#000000' size='3'>"+ owlsOperation.getOperationName() + "</font></li>"+
                          "        <ul>"+
                          "                <li><b>" + (owlsOperation.getOperationDomainTerm().equals("") ? "Connector Description:" : "Service Description:") + "</b><font face='verdana' color='#000000' size='2'>"+ owlsOperation.getServiceDescription() +"</font></li>"+
                          "                <li><b>Domain Term: </b><font face='verdana' color='#000000' size='2'>"+ owlsOperation.getOperationDomainTerm() +"</font></li>"+                              
                          "                <li><b>Inputs: </b></li><ul>"+ inputs + "</ul>" +
                          "                <li><b>Outputs: </b></li><ul>"+ outputs + "</ul>" +
                          "        </ul>"+
                          "</ul></font>";

            return html;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return "Could not display the file information, because it was not found.";
        }
    }
    
    public String obterInformacoesService(String name, boolean hasChiildren, String taskCode){
        try{
            //verifico se tem filhos, isso significa que é um nó
            if (hasChiildren){
                //verifico se o nome está vazio
                if(name.isEmpty()){
                    return "Could not display the service information, because it was not found.";                
                }

                //monto o html de exibição das informações do arquivo
                String html = "<font face='verdana' color='#000000' size='2'><ul>"+
                              " It's a Peer. Its name if <B>" + name + "</B>.<BR>"+
                              " Therefore any other information will be to given about it here."+
                              "</ul></font>";
                return html;
                
            }else{
                if(name.indexOf(" -") != -1){
                    name = name.substring(0, name.indexOf(" -"));
                }
                    
                //busco o nome do arquivo na lista
                OWLSOperation owlsOperation = this.buscaInfoService(name);
            
                //verifico se o modelo retornado é vazio
                if(owlsOperation == null){
                    return "Could not display the service information, because it was not found.";                
                }
                    
                String inputs = "";
                for(OWLSParam owlsParam : owlsOperation.getInputs()){
                    inputs = inputs + 
                    "<li><font face='verdana' color='#000000' size='2'><b>"+ owlsParam.getParamName() +" [Semantic]</b>: "+ owlsParam.getParamDomainTerm() +
                    " - <b>[Syntatic]</b>: "+ owlsParam.getParamType() +"</font></li>";
                    
                    if(owlsParam.getConnectorAssociated().size() > 0){
                        
                        inputs = inputs + "<ul><li><font face='verdana' color='#000000' size='3'><b>Connectors: </b><br>";
                        
                        for(int i = 0; i < owlsParam.getConnectorAssociated().size(); i++){
                            inputs = inputs + "<ul><li><font face='verdana' color='#000000' size='2'><b>Connector Associated: </b>" + 
                                                           owlsParam.getConnectorAssociated().get(i).getOperationName() +
                                     " | <b>Parameter Associated: </b>" + owlsParam.getParamConnectorAssociated().get(i).getParamName() + 
                                     " [Semantic]</b>: "+ owlsParam.getParamConnectorAssociated().get(i).getParamDomainTerm() +
                                     " - <b>[Syntatic]</b>: "+ owlsParam.getParamConnectorAssociated().get(i).getParamType() +
                                     "</font></li></ul>";
                        }
                    }
                }
                
                String outputs = "";
                for(OWLSParam owlsParam : owlsOperation.getOutputs()){
                    
                    outputs = outputs + 
                    "<li><font face='verdana' color='#000000' size='2'><b>"+ owlsParam.getParamName() +" [Semantic]</b>: "+ owlsParam.getParamDomainTerm() +
                    " - <b>[Syntatic]</b>: "+ owlsParam.getParamType() +"</font></li>";                    
                    
                    if(owlsParam.getConnectorAssociated().size() > 0){
                        outputs = outputs + "<ul><li><font face='verdana' color='#000000' size='3'><b>Connectors: </b><br>";
                        
                        for(int i = 0; i < owlsParam.getConnectorAssociated().size(); i++){
                            outputs = outputs + "<ul><li><font face='verdana' color='#000000' size='2'><b>Connector Associated: </b>" + 
                                                           owlsParam.getConnectorAssociated().get(i).getOperationName() +
                                     " | <b>Parameter Associated: </b>" + owlsParam.getParamConnectorAssociated().get(i).getParamName() + 
                                     " [Semantic]</b>: "+ owlsParam.getParamConnectorAssociated().get(i).getParamDomainTerm() +
                                     " - <b>[Syntatic]</b>: "+ owlsParam.getParamConnectorAssociated().get(i).getParamType() +
                                     "</font></li></ul>";
                        }
                    }
                }
                
                //monto o html de exibição das informações do arquivo
                String html = "<font face='verdana' color='#8800FF' size='2'><ul>"+
                              "<li><b>Service Name: </b><font face='verdana' color='#000000' size='3'>"+ owlsOperation.getOperationName() + "</font></li>"+
                              "        <ul>"+
                              "                <li><b>Service Description: </b><font face='verdana' color='#000000' size='2'>"+ owlsOperation.getServiceDescription() +"</font></li>"+
                              "                <li><b>Domain Term: </b><font face='verdana' color='#000000' size='2'>"+ owlsOperation.getOperationDomainTerm() +"</font></li>"+                              
                              "                <li><b>Inputs: </b></li><ul>"+ inputs + "</ul>" +
                              "                <li><b>Outputs: </b></li><ul>"+ outputs + "</ul>" +
                              "        </ul>"+
                              "</ul></font>";

                return html;
            }
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return "Could not display the file information, because it was not found.";
        }      
    }
    
    public String obterInformacoesConnectores(String name, boolean hasChiildren){
        try{
            //verifico se tem filhos, isso significa que é um nó
            if (hasChiildren){
                //verifico se o nome está vazio
                if(name.isEmpty()){
                    return "Could not display the connector information, because it was not found.";                
                }

                //monto o html de exibição das informações do arquivo
                String html = "<font face='verdana' color='#000000' size='2'><ul>"+
                              " It's a Peer. Its name if <B>" + name + "</B>.<BR>"+
                              " Therefore any other information will be to given about it here."+
                              "</ul></font>";
                return html;
                
            }else{
                if(name.indexOf(" -") != -1){
                    name = name.substring(0, name.indexOf(" -"));
                }
                    
                //busco o nome do arquivo na lista
                OWLSOperation owlsOperation = this.buscaInfoConnectores(name);
            
                //verifico se o modelo retornado é vazio
                if(owlsOperation == null){
                    return "Could not display the service information, because it was not found.";                
                }
                    
                String inputs = "";
                for(OWLSParam owlsParam : owlsOperation.getInputs()){
                    inputs = inputs + 
                    "<li><font face='verdana' color='#000000' size='2'><b>"+ owlsParam.getParamName() +" [Semantic]</b>: "+ owlsParam.getParamDomainTerm() +
                    " - <b>[Syntatic]</b>: "+ owlsParam.getParamType() +"</font></li>";
                }
                
                String outputs = "";
                for(OWLSParam owlsParam : owlsOperation.getOutputs()){
                    
                    outputs = outputs + 
                    "<li><font face='verdana' color='#000000' size='2'><b>"+ owlsParam.getParamName() +" [Semantic]</b>: "+ owlsParam.getParamDomainTerm() +
                    " - <b>[Syntatic]</b>: "+ owlsParam.getParamType() +"</font></li>";
                }
                
                //monto o html de exibição das informações do arquivo
                String html = "<font face='verdana' color='#8800FF' size='2'><ul>"+
                              "<li><b>Connector Name: </b><font face='verdana' color='#000000' size='3'>"+ owlsOperation.getOperationName() +"</font></li>"+
                              "<li><b>Connector Description: </b><font face='verdana' color='#000000' size='2'>"+ owlsOperation.getServiceDescription() +"</font></li>"+
                              "        <ul>"+                              
                              "                <li><b>Inputs: </b></li><ul>"+ inputs + "</ul>" +
                              "                <li><b>Outputs: </b></li><ul>"+ outputs + "</ul>" +
                              "        </ul>"+
                              "</ul></font>";

                return html;
            }
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return "Could not display the file information, because it was not found.";
        }      
    }
    
    //removo os arquivos de um determinado caso o mesmo saia do sistema
    public void removerArquivosListaByPeer(String peerName){
        
        for (FileModel file : lstArquivos){
            
            if (file.getPeerName().equals(peerName)){
                lstArquivos.remove(file);
            }            
        }
        
    }
    
    public void removerServicoByName(String name){
        
        if(name.indexOf(" ") != -1){
            name = name.substring(0, name.indexOf(" ")).trim();
        }
        
        for(OWLSOperation owlsOperation : lstServicos){
            
            String nomeBusca = owlsOperation.getOperationName();
            
            if (nomeBusca.equals(name)){
                lstServicos.remove(owlsOperation);
                break;
            }
        }
        
    }
    
    public void removerConnectoresByName(String name){
        
        if(name.indexOf(" ") != -1){
            name = name.substring(0, name.indexOf(" ")).trim();
        }
        
        for(OWLSOperation owlsOperation : lstConnectores){
            
            String nomeBusca = owlsOperation.getOperationName();
            
            if (nomeBusca.equals(name)){
                lstConnectores.remove(owlsOperation);
                break;
            }
        }
        
    }
   
    public void initializeWait(int timeOut){
        this.frmWatingReponse.setTimeOut(timeOut);
        this.frmWatingReponse.setVisible(true);
    }
    
    public void stopWait(){
        this.frmWatingReponse.setVisible(false);
    }
    
    //realizo a limpeza da lista de arquivos
    public void limparListaArquivos(){
        //limpo a lista de arquivos
        SearchManager.lstArquivos.clear();
    }
    
    //realizo a limpeza da lista de arquivos
    public void limparListaServicos(){
        //limpo a lista de arquivos
        SearchManager.lstServicos.clear();
    }
    
    //realizo a limpeza da lista de arquivos
    public void limparListaConnectores(){
        //limpo a lista de arquivos
        SearchManager.lstConnectores.clear();
    }

    /**
     * @param treeResults the treeResults to set
     */
    public void setTreeResults(JTree treeResults) {
        this.treeResults = treeResults;
    }

    /**
     * @param treeResultsConnector the treeResultsConnector to set
     */
    public void setTreeResultsConnector(JTree treeResultsConnector) {
        this.treeResultsConnector = treeResultsConnector;
    }
}
