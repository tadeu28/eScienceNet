/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.models;

/**
 *
 * @author Tadeu
 */
public class DomainFileModel {
    
    private String domainName;
    private String domainDescription;
    private String domainOWLFile;
    private PeerGroupModel group;
    private String domainURI;

    /**
     * @return the domainName
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * @param domainName the domainName to set
     */
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * @return the domainDescription
     */
    public String getDomainDescription() {
        return domainDescription;
    }

    /**
     * @param domainDescription the domainDescription to set
     */
    public void setDomainDescription(String domainDescription) {
        this.domainDescription = domainDescription;
    }

    /**
     * @return the domainOWLFile
     */
    public String getDomainOWLFile() {
        return domainOWLFile;
    }

    /**
     * @param domainOWLFile the domainOWLFile to set
     */
    public void setDomainOWLFile(String domainOWLFile) {
        this.domainOWLFile = domainOWLFile;
    }

    /**
     * @return the group
     */
    public PeerGroupModel getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(PeerGroupModel group) {
        this.group = group;
    }

    /**
     * @return the domainURI
     */
    public String getDomainURI() {
        return domainURI;
    }

    /**
     * @param domainURI the domainURI to set
     */
    public void setDomainURI(String domainURI) {
        this.domainURI = domainURI;
    }
    
}
