/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.servicemanager.wsdlmodels;

/**
 *
 * @author Tadeu
 */
public class WSDLRecoverPort {
    
    private String portName;
    private String adress;
    private WSDLRecoverBinding binding;

    /**
     * @return the portName
     */
    public String getPortName() {
        return portName;
    }

    /**
     * @param portName the portName to set
     */
    public void setPortName(String portName) {
        this.portName = portName;
    }

    /**
     * @return the adress
     */
    public String getAdress() {
        return adress;
    }

    /**
     * @param adress the adress to set
     */
    public void setAdress(String adress) {
        this.adress = adress;
    }

    /**
     * @return the binding
     */
    public WSDLRecoverBinding getBinding() {
        return binding;
    }

    /**
     * @param binding the binding to set
     */
    public void setBinding(WSDLRecoverBinding binding) {
        this.binding = binding;
    }
    
}
