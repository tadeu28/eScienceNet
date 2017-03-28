/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.models;

/**
 *
 * @author Tadeu
 */
public class DomainsRelations{

        private String domainOntology;
        private String domainTermName;

        public String getDomainOntology(){
            return this.domainOntology;
        }

        public void setDomainOntology(String domainOntology){
            this.domainOntology = domainOntology;
        }

        public String getDomainTermName(){
            return this.domainTermName;
        }

        public void setDomainTermName(String domainTermName){
            this.domainTermName = domainTermName;
        }
    }
