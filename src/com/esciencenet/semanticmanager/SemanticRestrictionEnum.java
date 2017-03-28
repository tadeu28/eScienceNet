/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.semanticmanager;

/**
 *
 * @author Tadeu
 */
public enum SemanticRestrictionEnum {
    
    only, some;
    
    @Override
    public String toString(){
        
        switch(this){            
            case some:{
                return "someValuesFrom";
            }
            case only: {
                return "allValuesFrom";
            }
        }
        return super.toString();
    }    
    
    public static SemanticRestrictionEnum getValue(String nome){
        
        if(nome.equals(some.toString())){
            return some;
        }else if(nome.equals(only.toString())){
            return only;
        }
        return null;
    }
}
