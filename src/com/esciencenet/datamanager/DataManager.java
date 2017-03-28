/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.datamanager;

import com.esciencenet.compositionmanager.CompositionModel;
import com.esciencenet.forms.*;
import com.esciencenet.semanticmanager.*;
import com.esciencenet.models.*;
import com.esciencenet.utils.EScienceNetUtils;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import juniorvs.virtualdir.ConteudoRemoto;
import juniorvs.virtualdir.GerenciaProcura;
        
/**
 *
 * @author Tadeu Classe
 */
public class DataManager {
    
    private final String DIRETORIO = "DataRepository";
    private final String DOWNLOADED_XML = "XMLFilesDownloaded.xml";
    private final String TEMP_FILE = "TemporaryFile.tmp";
    public static final String RESQUEST_FILE = "[GIVEME_FILE]";    
    
    private GerenciaProcura gerenciaProcura;
    private ConteudoRemoto conteudoRemoto;
    private List<DownloadedFilesModel> lstFilesDownloaded = new ArrayList<DownloadedFilesModel>();    
    private String path;
    
    public File createATempFile(){
        try{
            File file = new File(getPath() + File.separator + TEMP_FILE);;
            
            if(file.exists()){
                file.delete();
                file.createNewFile();
            }
            
            return file;
        }catch(Exception e){
            e.printStackTrace();
            return null;            
        }
    }
    
    public void inserirArquivo(){
     
        FrmIncluirArquivo frmIncluirArquivo = new FrmIncluirArquivo(null, true, SemanticManager.getInstance());
        frmIncluirArquivo.setVisible(true);
        
    }    
    
    public void removerArquivo(String file){
        try{
            this.gerenciaProcura.removeFile(file);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Wasn't possible to unshare the file [" + file +"].\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR);
        }        
    }
    
    public void visualizarArquivosLocais(String peerName){
        
        List<FileModel> lstFileModel = obterDadosArquivoPeer(peerName);
        
        FrmMostrarArquivos frmMostrarArquivos = new FrmMostrarArquivos(null, true, peerName, lstFileModel);
        frmMostrarArquivos.setVisible(true);        
    }
    
    public List<FileModel> obterDadosArquivoPeer(String peerName){
        try{
            List<FileModel> lstFileModel = new ArrayList<FileModel>();
            
            OntModel model = SemanticManager.getInstance().getPeerOntModel();
            
            OntClass group = model.getOntClass(SemanticManager.URI_PEERGROUPS_ONTOLOGY + "Files");
            
            Iterator i = null;
            for(i = group.listInstances(); i.hasNext();){
                Individual arquivo = (Individual) i.next();                
                
                Property isFileOf = model.getProperty(SemanticManager.URI_PEERGROUPS_ONTOLOGY + "isFileOf");
                
                if (arquivo.getPropertyValue(isFileOf).asResource().toString().replace(SemanticManager.URI_PEERGROUPS_ONTOLOGY, "").equals(peerName)){
                
                    FileModel fileModel = new FileModel();

                    Property filePath = model.getProperty(SemanticManager.URI_PEERGROUPS_ONTOLOGY + "filePath");
                    fileModel.setPath(arquivo.getPropertyValue(filePath).asLiteral().getString());

                    Property fileExtension = model.getProperty(SemanticManager.URI_PEERGROUPS_ONTOLOGY + "fileExtension");
                    fileModel.setExtension(arquivo.getPropertyValue(fileExtension).asLiteral().getString());

                    Property fileName = model.getProperty(SemanticManager.URI_PEERGROUPS_ONTOLOGY + "fileName");
                    fileModel.setName(arquivo.getPropertyValue(fileName).asLiteral().getString());

                    Property fileType = model.getProperty(SemanticManager.URI_PEERGROUPS_ONTOLOGY + "fileType");
                    fileModel.setType(arquivo.getPropertyValue(fileType).asLiteral().getString());

                    Property fileSize = model.getProperty(SemanticManager.URI_PEERGROUPS_ONTOLOGY + "fileSize");
                    fileModel.setSize(arquivo.getPropertyValue(fileSize).asLiteral().getDouble());

                    Property fileCreationDate = model.getProperty(SemanticManager.URI_PEERGROUPS_ONTOLOGY + "fileCreationDate");
                    fileModel.setDate(arquivo.getPropertyValue(fileCreationDate).asLiteral().getString());

                    Property fileDescription = model.getProperty(SemanticManager.URI_PEERGROUPS_ONTOLOGY + "fileDescription");
                    fileModel.setDescription(arquivo.getPropertyValue(fileDescription).asLiteral().getString());

                    fileModel.setPeerName(arquivo.getPropertyValue(isFileOf).asResource().toString().replace(SemanticManager.URI_PEERGROUPS_ONTOLOGY, ""));
                    
                    Property pertenceOneGroup = model.getProperty(SemanticManager.URI_PEERGROUPS_ONTOLOGY + "pertenceOneGroup");
                    PeerGroupModel peerGroupModel = new PeerGroupModel();
                    peerGroupModel.setGroupName(arquivo.getPropertyValue(pertenceOneGroup).asResource().toString().replace(SemanticManager.URI_PEERGROUPS_ONTOLOGY, ""));
                    fileModel.setPeerGroup(peerGroupModel);
                    
                    lstFileModel.add(fileModel);
                }
            }
            
            return lstFileModel;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * @param gerenciaProcura the gerenciaProcura to set
     */
    public void setGerenciaProcura(GerenciaProcura gerenciaProcura) {
        this.gerenciaProcura = gerenciaProcura;
    }
    
    public void carregarDownloadedFilesModel(String repoPath){
        try{
            //crio a string para a criação do diretorio
            path = repoPath + DIRETORIO;
            //crio o objeto de gravacao do diretorio        
            File dataPath = new File(getPath());
            //verifico se o diretorio ja existe
            if (!dataPath.exists()){
                //crio o repositorio de ontologias
                dataPath.mkdir();
            }
            
            //crio o objeto de verificaça da existencia do XML
            File xmlFile = new File(getPath() + File.separator + DOWNLOADED_XML);
            
            if (xmlFile.exists()){
                FileInputStream xml = new FileInputStream(xmlFile);
                
                XStream xstream = new XStream(new DomDriver());
                this.lstFilesDownloaded = (ArrayList<DownloadedFilesModel>) xstream.fromXML(xml);
            }
            
        }catch(Exception e){
            System.out.printf(e.getMessage());
        }
    }
    
    public boolean verificarDownaloaded(String fileName, String peerName){
        
        for(DownloadedFilesModel file : getLstFilesDownloaded()){
            
            if ((file.getFileName().equals(fileName)) && (file.getPeerName().equals(peerName)))  {
                return true;
            }            
        }
        return false;
    }
    
    public void processarDownloadRequest(String fileName){
        DataDownloadResquestProcessor dataProcessor = new DataDownloadResquestProcessor();
        dataProcessor.setFileName(fileName);
        dataProcessor.setGerenciaProcura(this.getGerenciaProcura());
        dataProcessor.setIsToGiveAdversament(true);
        dataProcessor.start();
    }
    
    public void gravarArquivos(){        
        try{
            XStream xstream = new XStream(new DomDriver());
            String retorno = xstream.toXML(this.lstFilesDownloaded);

            File xmlFile = new File(getPath() + File.separator + DOWNLOADED_XML);
            if (!xmlFile.exists()){
                xmlFile.createNewFile();
            }
            
            try (FileWriter fw = new FileWriter(getPath() + File.separator + DOWNLOADED_XML)) {
                fw.write(retorno);
            }               
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public boolean gravarLogWorkflow(String log, String fileName){
        try{
            File file = new File(getPath() + File.separator + fileName);
            if (!file.exists()){
                file.createNewFile();
            }
            
            try (FileWriter fw = new FileWriter(getPath() + File.separator + fileName)) {
                fw.write(log);
            }      
            
            FileModel fileModel = new FileModel();
            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
                        
            fileModel.setDate(date.format(new Date()));            
            fileModel.setExtension(file.getName().substring(file.getName().lastIndexOf("."), file.getName().length()));
            fileModel.setDescription("Workflow Result");
            fileModel.setName(file.getName().replace(fileModel.getExtension(), ""));
            fileModel.setPath(getPath() + File.separator);
            fileModel.setSize(file.length() / 1024);
            fileModel.setType(EScienceNetUtils.RemoveSpecialCharacters("Workflow Result"));
            
            fileModel.setPeerGroup(SemanticManager.getInstance().getInterestManager().getGrupoSelecionado());            
            fileModel.setPeerName(SemanticManager.getInstance().getNomePeer());
            
            if(!SemanticManager.getInstance().gravarArquivoInOWL(fileModel)){
                return false;
            }
            
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }   
    }
    
    public boolean gravarFunctionalWF(CompositionModel compositionModel, String fileName){
        try{
            XStream xstream = new XStream(new DomDriver());
            String retorno = xstream.toXML(compositionModel);

            File xmlFile = new File(getPath() + File.separator + fileName);
            if (!xmlFile.exists()){
                xmlFile.createNewFile();
            }
            
            try (FileWriter fw = new FileWriter(getPath() + File.separator + fileName)) {
                fw.write(retorno);
            }      
            
            FileModel fileModel = this.recuperarInformacoesArquivoWF(fileName, getPath() + File.separator, false);
            
            if(!SemanticManager.getInstance().gravarArquivoInOWL(fileModel)){
                return false;
            }
            
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public boolean gravarAbstractWF(WorkflowABSModel awf, String fileName){
        try{
            XStream xstream = new XStream(new DomDriver());
            String retorno = xstream.toXML(awf);

            File xmlFile = new File(getPath() + File.separator + fileName);
            if (!xmlFile.exists()){
                xmlFile.createNewFile();
            }
            
            try (FileWriter fw = new FileWriter(getPath() + File.separator + fileName)) {
                fw.write(retorno);
            }      
            
            FileModel fileModel = this.recuperarInformacoesArquivoWF(fileName, getPath() + File.separator, true);
            
            if(!SemanticManager.getInstance().gravarArquivoInOWL(fileModel)){
                return false;
            }
            
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    private FileModel recuperarInformacoesArquivoWF(String fileName, String filePath, boolean isABWF){
        try{
            FileModel fileModel = new FileModel();
            
            File file = new File(filePath + fileName);
            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
                        
            fileModel.setDate(date.format(new Date()));            
            fileModel.setExtension(file.getName().substring(file.getName().lastIndexOf("."), file.getName().length()));
            fileModel.setDescription(isABWF ? "Abstract Workflow to " + fileName.replace(fileModel.getExtension(), "") : 
                                              "Functional Workflow to " + fileName.replace(fileModel.getExtension(), ""));
            fileModel.setName(file.getName().replace(fileModel.getExtension(), ""));
            fileModel.setPath(filePath);
            fileModel.setSize(file.length() / 1024);
            fileModel.setType(EScienceNetUtils.RemoveSpecialCharacters(isABWF ? "Abstract Workflow File" : "Functional Workflow File"));
            
            fileModel.setPeerGroup(SemanticManager.getInstance().getInterestManager().getGrupoSelecionado());            
            fileModel.setPeerName(SemanticManager.getInstance().getNomePeer());
            
            return fileModel;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the lstFilesDownloaded
     */
    public List<DownloadedFilesModel> getLstFilesDownloaded() {
        return lstFilesDownloaded;
    }
    
    //verifico a existencia do arquivo
    public int indexOfFileDownloaded(String fileName, String peerName){
        
        for (DownloadedFilesModel fileModel : lstFilesDownloaded){
            if ((fileModel.getFileName().equals(fileName)) && (fileModel.getPeerName().equals(peerName))){
                return lstFilesDownloaded.indexOf(fileModel);
            }
        }
        
        return -1;
    }

    /**
     * @return the gerenciaProcura
     */
    public GerenciaProcura getGerenciaProcura() {
        return gerenciaProcura;
    }

    /**
     * @return the conteudoRemoto
     */
    public ConteudoRemoto getConteudoRemoto() {
        return conteudoRemoto;
    }

    /**
     * @param conteudoRemoto the conteudoRemoto to set
     */
    public void setConteudoRemoto(ConteudoRemoto conteudoRemoto) {
        this.conteudoRemoto = conteudoRemoto;
    }
}
