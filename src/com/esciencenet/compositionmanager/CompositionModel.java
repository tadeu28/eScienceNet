/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.compositionmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Tadeu
 */
public class CompositionModel {
    
    private String nameComposition;
    private Date data;
    private String description;
    private List<CompositionTaskModel> lstServices;
    private boolean validated;

    public CompositionModel(){
        this.lstServices = new ArrayList<>();
    }
    
    /**
     * @return the nameComposition
     */
    public String getNameComposition() {
        return nameComposition;
    }

    /**
     * @param nameComposition the nameComposition to set
     */
    public void setNameComposition(String nameComposition) {
        this.nameComposition = nameComposition;
    }

    /**
     * @return the data
     */
    public Date getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Date data) {
        this.data = data;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the lstServices
     */
    public List<CompositionTaskModel> getLstServices() {
        return lstServices;
    }

    /**
     * @param lstServices the lstServices to set
     */
    public void setLstServices(List<CompositionTaskModel> lstServices) {
        this.lstServices = lstServices;
    }

    /**
     * @return the validated
     */
    public boolean isValidated() {
        return validated;
    }

    /**
     * @param validated the validated to set
     */
    public void setValidated(boolean validated) {
        this.validated = validated;
    }
    
}
