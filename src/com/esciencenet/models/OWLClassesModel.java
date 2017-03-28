package com.esciencenet.models;


import java.util.ArrayList;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Tadeu
 */
public class OWLClassesModel {

    private String name;
    private String uri;
    private String superClass;
    private String file;
    private List<OWLClassesModel> subClasses = new ArrayList<>();

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the subClasses
     */
    public List<OWLClassesModel> getSubClasses() {
        return subClasses;
    }

    /**
     * @param subClasses the subClasses to set
     */
    public void setSubClasses(List<OWLClassesModel> subClasses) {
        this.subClasses = subClasses;
    }

    /**
     * @return the superClass
     */
    public String getSuperClass() {
        return superClass;
    }

    /**
     * @param superClass the superClass to set
     */
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }
    
    public static OWLClassesModel verificarOWLClass(List<OWLClassesModel> classes, String className, boolean isToCheckContains){
        
        if (isToCheckContains){
            try{
                OWLClassesModel classReturn = null;

                for(OWLClassesModel owlClass : classes){               

                    if (owlClass.getName().contains(className)){
                        return owlClass;
                    }else{
                        classReturn = verificarOWLClass(owlClass.getSubClasses(), className, isToCheckContains);
                        if (classReturn.getName() != null){
                            return classReturn;
                        }
                    }

                }

                return new OWLClassesModel();
            }catch(Exception e){            
                return null;
            }
        }else{
            return verificarOWLClass(classes, className);
        }
        
    }
    
    public static OWLClassesModel verificarOWLClass(List<OWLClassesModel> classes, String className){
        try{
            OWLClassesModel classReturn = null;
            
            for(OWLClassesModel owlClass : classes){               
                
                if (owlClass.getName().equals(className)){
                    return owlClass;
                }else{
                    classReturn = verificarOWLClass(owlClass.getSubClasses(), className);
                    if (classReturn.getName() != null){
                        return classReturn;
                    }
                }
                
            }
            
            return new OWLClassesModel();
        }catch(Exception e){            
            return null;
        }
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }
    
}
