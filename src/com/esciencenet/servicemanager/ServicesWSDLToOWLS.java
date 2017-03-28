/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.servicemanager;

import com.esciencenet.forms.FAddServices;
import com.esciencenet.forms.FAssServicesAndDomain;
import com.esciencenet.semanticmanager.SemanticManager;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author Tadeu
 */
public class ServicesWSDLToOWLS extends Thread{
    
    private String appDir;
    private String owlsDir;
    private boolean isConnector;
    
    @Override
    public void run(){
        try {
            FAddServices frmAddService = new FAddServices(null, true);  
            frmAddService.setServiceManager(SemanticManager.getInstance().getServiceManager());            
            frmAddService.setConnector(isConnector);
            frmAddService.setVisible(true);
            
            if(frmAddService.getServiceInfoModel() != null){
                if(! frmAddService.getServiceInfoModel().getWsdlServiceName().equals("")){                         
                    if(! SemanticManager.getInstance().persistServiceInPeerOntology(frmAddService.getServiceInfoModel(), isConnector)){
                        this.suspend();
                    }
                }
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE, 
                                      (new javax.swing.ImageIcon(getClass().getResource("/images/error.png"))));
        }
    }  
    
    /**
     * @return the appDir
     */
    public String getAppDir() {
        return appDir;
    }

    /**
     * @param appDir the appDir to set
     */
    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }

    /**
     * @return the owlsDir
     */
    public String getOwlsDir() {
        return owlsDir;
    }

    /**
     * @param owlsDir the owlsDir to set
     */
    public void setOwlsDir(String owlsDir) {
        this.owlsDir = owlsDir;
    }  

    /**
     * @param isConnector the isConnector to set
     */
    public void setIsConnector(boolean isConnector) {
        this.isConnector = isConnector;
    }
    
}
