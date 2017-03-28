/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.models;

/**
 *
 * @author Tadeu Classe
 */
public class ResourceModel {

    private String date;
    private String description;
    private String name;
    private String peerName;
    private PeerGroupModel peerGroup;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public PeerGroupModel getPeerGroup() {
        return peerGroup;
    }

    public void setPeerGroup(PeerGroupModel peerGroup) {
        this.peerGroup = peerGroup;
    }
}
