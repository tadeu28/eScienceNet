/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.models;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Tadeu
 */
public class ServicesModelView {
   
    private String serviceName;
    private String owlsName;
    private boolean connector;
    private String serviceRelated;
    private List<DomainsRelations> domainsRelations;
    private List<ParamServiceModel> lstParameters;
    
    public ServicesModelView(){
        this.serviceName = "";
        this.owlsName = "";
        this.domainsRelations = new ArrayList<>();
        this.lstParameters = new ArrayList<>();
    }

    public int indexOf(DomainsRelations domainsRelations){
        
        for(int i = 0; i < this.getDomainsRelations().size(); i++){
        
            if((this.getDomainsRelations().get(i).getDomainOntology().equals(domainsRelations.getDomainOntology())) && 
               (this.getDomainsRelations().get(i).getDomainTermName().equals(domainsRelations.getDomainTermName()))){
                return i;
            }            
        }
        
        return -1;
    }
    
    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the owlsName
     */
    public String getOwlsName() {
        return owlsName;
    }

    /**
     * @param owlsName the owlsName to set
     */
    public void setOwlsName(String owlsName) {
        this.owlsName = owlsName;
    }

    /**
     * @return the domainsRelations
     */
    public List<DomainsRelations> getDomainsRelations() {
        return domainsRelations;
    }

    /**
     * @param domainsRelations the domainsRelations to set
     */
    public void setDomainsRelations(List<DomainsRelations> domainsRelations) {
        this.domainsRelations = domainsRelations;
    }    

    /**
     * @return the lstParameters
     */
    public List<ParamServiceModel> getLstParameters() {
        return lstParameters;
    }

    /**
     * @param lstParameters the lstParameters to set
     */
    public void setLstParameters(List<ParamServiceModel> lstParameters) {
        this.lstParameters = lstParameters;
    }

    /**
     * @return the connector
     */
    public boolean isConnector() {
        return connector;
    }

    /**
     * @param connector the connector to set
     */
    public void setConnector(boolean connector) {
        this.connector = connector;
    }

    /**
     * @return the serviceRelated
     */
    public String getServiceRelated() {
        return serviceRelated;
    }

    /**
     * @param serviceRelated the serviceRelated to set
     */
    public void setServiceRelated(String serviceRelated) {
        this.serviceRelated = serviceRelated;
    }
}
