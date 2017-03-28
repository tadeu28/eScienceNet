package com.esciencenet.principal;

import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import juniorvs.virtualdir.desktop.VirtualDir;

/**
 * Classe principal do projeto e-ScienceNet
 * 
 * @author Tadeu Classe
 */
public class Main {
   
    /**
     * Método inicial da classe principal da e-ScienceNet
     * 
     * @param args argumento (parâmentros) inicial da aplicação
     */
    public static void main(String[] args){
        
        //chamo o método de alteração de visual
        alterarVisual();
        
        //crio uma intância da classe fundamental da e-ScienceNet
        VirtualDir iP2PFrame = new VirtualDir();
        //inicializo o JXTA
        iP2PFrame.iniciaPalaformaJXTA();
        //redimenciono o formulário principal na tela
        Dimension dim = iP2PFrame.getSize();
        Dimension screen = iP2PFrame.getToolkit().getScreenSize();
        iP2PFrame.setLocation((screen.width - dim.width) / 2, (screen.height - dim.height) / 2);
        //exibo o formulário
        iP2PFrame.setVisible(true);
    }
    
    /**
     * Altero o visual do sistema
     */
    public static void alterarVisual(){
        try {
            //seto o look and feel do windows na aplicação java
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(null, "Erro:\n + " + e.getMessage());
        }              
    }
    
}
