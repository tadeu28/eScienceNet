/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.semanticmanager;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Tadeu
 */
public class OntologyFilter extends FileFilter{

    private String description;
    private String extensions[];

    public OntologyFilter(String description, String extension) {
        this(description, new String[] { extension });
    }

    public OntologyFilter(String description, String extensions[]) {
        if (description == null) {
            this.description = extensions[0] + "{ " + extensions.length + "} ";
        } else {
            this.description = description;
        }
        
        this.extensions = (String[]) extensions.clone();
        toLower(this.extensions);
    }

    private void toLower(String array[]) {
        for (int i = 0, n = array.length; i < n; i++) {
            array[i] = array[i].toLowerCase();
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        } else {
            String path = file.getAbsolutePath().toLowerCase();
            for (int i = 0, n = extensions.length; i < n; i++) {
                String extension = extensions[i];
                
                if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                    return true;
                }
            }
        }
        return false;
    }    
}
