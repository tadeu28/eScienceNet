/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.datamanager;

/**
 *
 * @author Tadeu
 */
public class DownloadedFilesModel {
 
    private String peerName;
    private String fileName;
    
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
}
