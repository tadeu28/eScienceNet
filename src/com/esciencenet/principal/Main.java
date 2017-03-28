package com.esciencenet.principal;

import java.awt.Dimension;
import java.util.Locale;
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
        VirtualDir eScienceNet = new VirtualDir();
        //inicializo o JXTA
        eScienceNet.iniciaPalaformaJXTA();
        //exibo o formulário
        eScienceNet.setVisible(true);
    }
    
    /**
     * Altero o visual do sistema
     */
    public static void alterarVisual(){
        try {
            //altero o idioma para ingles
            Locale.setDefault(Locale.US);
            
            if (System.getProperty("os.name").toLowerCase().equals("linux")){            
                //seto o visual linux na aplicação
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            }else{ 
                //seto o look and feel do windows na aplicação java
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(null, "Erro:\n + " + e.getMessage());
        }              
    }
    
}
