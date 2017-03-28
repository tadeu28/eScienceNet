/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.datamanager;

import com.esciencenet.models.FileModel;
import com.esciencenet.semanticmanager.SemanticManager;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import juniorvs.virtualdir.GerenciaProcura;

/**
 *
 * @author Tadeu
 */
public class DataDownloadResquestProcessor extends Thread {
 
    private String fileName;
    private GerenciaProcura gerenciaProcura;    
    private boolean isToGiveAdversament;
    
    public DataDownloadResquestProcessor(){
        isToGiveAdversament = false;
    }
    
    @Override
    public void run(){
            
        if (isToGiveAdversament){
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(DataDownloadResquestProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            List<FileModel> lstFiles = SemanticManager.getInstance().getDataManager().obterDadosArquivoPeer(SemanticManager.getInstance().getNomePeer());

            File arquivo = null;
            for (FileModel file : lstFiles){
                if (file.getName().concat(file.getExtension()).equals(fileName)){
                    arquivo = new File(file.getPath() + File.separator + fileName);

                    if (!arquivo.exists()){
                        return;
                    }else{
                        break;
                    }
                }
            }

            if (arquivo != null){
                this.gerarAvisoArquivo(arquivo);
            }
        }
    }
    
    private void gerarAvisoArquivo(File file){        
        try {              
            gerenciaProcura.addFile(file);
        } catch (Exception ex) {
            Logger.getLogger(DataDownloadResquestProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @param gerenciaProcura the gerenciaProcura to set
     */
    public void setGerenciaProcura(GerenciaProcura gerenciaProcura) {
        this.gerenciaProcura = gerenciaProcura;
    }

    /**
     * @param isToGiveAdversament the isToGiveAdversament to set
     */
    public void setIsToGiveAdversament(boolean isToGiveAdversament) {
        this.isToGiveAdversament = isToGiveAdversament;
    }

}
