/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.models;

/**
 *
 * @author Tadeu
 */
public class ParamServiceModel {
    
    private String paramName;
    private DomainsRelations paramType;

    /**
     * @return the paramName
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * @param paramName the paramName to set
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    /**
     * @return the paramType
     */
    public DomainsRelations getParamType() {
        return paramType;
    }

    /**
     * @param paramType the paramType to set
     */
    public void setParamType(DomainsRelations paramType) {
        this.paramType = paramType;
    }
    
}
