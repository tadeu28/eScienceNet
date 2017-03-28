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
public class JTreeResearch extends DefaultTreeCellRenderer {
    
    private Icon leafIcon;
    private Icon serviceIcon;
    private Icon homeIcon;
    private Icon peerIcon;

    public JTreeResearch() {
        leafIcon = new ImageIcon(getClass().getResource("/images/program.png"));        
        homeIcon = new ImageIcon(getClass().getResource("/images/home.png"));
        peerIcon = new ImageIcon(getClass().getResource("/images/peerreserach.png"));
        serviceIcon = new ImageIcon(getClass().getResource("/images/selected.png"));
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        if (leaf){
            if (value.toString().contains("- [")){
                this.setIcon(serviceIcon);
            }else{
                this.setIcon(leafIcon);
            }
        }else{
            if (value.toString().contains("[LOCAL]")){
                this.setIcon(homeIcon);
                this.setText(value.toString().replace("[LOCAL]", ""));
            }else{
                this.setIcon(peerIcon);
            }        
        }
        
        return this;
    }
}
