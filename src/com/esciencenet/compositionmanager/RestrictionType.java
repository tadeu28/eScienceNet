/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.esciencenet.compositionmanager;

/**
 *
 * @author Tadeu Classe
 */
public enum RestrictionType {
    CONDICTION,
    LOOP;
    
    @Override
    public String toString(){
        switch(this){
            case LOOP:{
                return "LOOP";
            }
            case CONDICTION:{
                return "IF";
            }
        }
        
        return null;
    }
}
