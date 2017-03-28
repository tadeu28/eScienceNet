/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tadeu
 */
public class JarLoader extends URLClassLoader {

    public JarLoader (URL[] urls){
        super (urls);
    }
    
    public void addFile (String path){
        
        String urlPath = "jar:file://" + path + "!/";
        try {
            addURL(new URL (urlPath));
        } catch (MalformedURLException ex) {
            Logger.getLogger(JarLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
