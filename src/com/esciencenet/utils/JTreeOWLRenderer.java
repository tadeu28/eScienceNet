/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.utils;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Tadeu
 */
public class JTreeOWLRenderer extends DefaultTreeCellRenderer {
    
    private Icon leafIcon;
    private Icon classIcon;
    private Icon colapsedIcon;
    private Icon homeIcon;
    private Icon selectedIcon;

    public JTreeOWLRenderer() {
        leafIcon = new ImageIcon(getClass().getResource("/images/leaf.png"));
        classIcon = new ImageIcon(getClass().getResource("/images/class.png"));
        colapsedIcon = new ImageIcon(getClass().getResource("/images/plus.png"));
        homeIcon = new ImageIcon(getClass().getResource("/images/home.png"));
        selectedIcon = new ImageIcon(getClass().getResource("/images/selected.png"));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        if(leaf){
            this.setIcon(leafIcon);
        }else{
            this.setIcon(classIcon);            
            
            if(! expanded){
                this.setIcon(colapsedIcon);
            }
        }
        
        if(hasFocus){
            this.setIcon(selectedIcon);                
        }
        
        if(row == 0){
            this.setIcon(homeIcon);
        }

        return this;
    }
}
