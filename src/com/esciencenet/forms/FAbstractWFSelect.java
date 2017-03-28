/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.forms;

import com.esciencenet.semanticmanager.SemanticManager;
import java.io.File;
import java.util.HashMap;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Tadeu
 */
public class FAbstractWFSelect extends javax.swing.JDialog {

    private File awfSelected = null;
    private boolean isFunctionalWF = false;
    private boolean isResults = false;
    
    /**
     * Creates new form FAbstractWFSelect
     */
    public FAbstractWFSelect(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public FAbstractWFSelect(java.awt.Frame parent, boolean modal, boolean isFunctionalWF) {
        super(parent, modal);
        initComponents();
        this.isFunctionalWF = isFunctionalWF;
    }
    
    public FAbstractWFSelect(java.awt.Frame parent, boolean modal, boolean isFunctionalWF, boolean isResults) {
        super(parent, modal);
        initComponents();
        this.isFunctionalWF = isFunctionalWF;
        this.isResults = isResults;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooserAWF = new javax.swing.JFileChooser();
        pnlFooter = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAWF = new javax.swing.JTable();

        fileChooserAWF.setDialogTitle("Select Workflow");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Abstract Workflow");
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        getContentPane().setLayout(null);

        pnlFooter.setBackground(new java.awt.Color(192, 192, 192));

        btnOK.setBackground(new java.awt.Color(192, 192, 192));
        btnOK.setText("Select");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(192, 192, 192));
        btnCancelar.setText("Cancel");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jCheckBox1.setBackground(new java.awt.Color(192, 192, 192));
        jCheckBox1.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setText("Other Selection...");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFooterLayout = new javax.swing.GroupLayout(pnlFooter);
        pnlFooter.setLayout(pnlFooterLayout);
        pnlFooterLayout.setHorizontalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 335, Short.MAX_VALUE)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlFooterLayout.setVerticalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancelar)
                    .addComponent(jCheckBox1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(pnlFooter);
        pnlFooter.setBounds(0, 249, 676, 51);

        tblAWF.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "File", "File Path"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAWF.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblAWF.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tblAWF);
        if (tblAWF.getColumnModel().getColumnCount() > 0) {
            tblAWF.getColumnModel().getColumn(0).setResizable(false);
            tblAWF.getColumnModel().getColumn(0).setPreferredWidth(250);
            tblAWF.getColumnModel().getColumn(1).setResizable(false);
            tblAWF.getColumnModel().getColumn(1).setPreferredWidth(450);
        }

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(0, 0, 676, 242);

        setSize(new java.awt.Dimension(678, 332));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        HashMap<String, String> arquivos = SemanticManager.getInstance().recoveryWF(!isFunctionalWF, this.isResults);
        
        DefaultTableModel model = (DefaultTableModel) tblAWF.getModel();        
        for(String file : arquivos.keySet()){
            
            String path = arquivos.get(file);
            
            Object[] linha = {file, path};            
            model.addRow(linha);
        }
        tblAWF.setModel(model);
    }//GEN-LAST:event_formComponentShown

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed

        if(tblAWF.getSelectedRow() != -1){
         
            this.awfSelected = new File(tblAWF.getModel().getValueAt(tblAWF.getSelectedRow(), 1).toString() + File.separator + 
                                        tblAWF.getModel().getValueAt(tblAWF.getSelectedRow(), 0).toString());
            
            if(this.awfSelected.exists()){
                this.setVisible(false);
            }else{
                JOptionPane.showMessageDialog(null, "The file " + tblAWF.getModel().getValueAt(tblAWF.getSelectedRow(), 0).toString() + "not exists.", 
                                              ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            }
        }else{           
            if(this.awfSelected.exists()){
                this.setVisible(false);
            }else{
                JOptionPane.showMessageDialog(null, "The file " + tblAWF.getModel().getValueAt(tblAWF.getSelectedRow(), 0).toString() + "not exists.", 
                                              ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            }
        }        
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        
        jCheckBox1.setSelected(true);
        fileChooserAWF.showOpenDialog(this);
        
        if(fileChooserAWF.getSelectedFile() != null){
                        
            if(fileChooserAWF.getSelectedFile().exists()){               
                this.awfSelected = fileChooserAWF.getSelectedFile();
                this.setVisible(false);
            }else{
                JOptionPane.showMessageDialog(null, "The file " + tblAWF.getModel().getValueAt(tblAWF.getSelectedRow(), 0).toString() + "not exists.", 
                                              ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            }
            
        }  
        
        jCheckBox1.setSelected(false);
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnOK;
    private javax.swing.JFileChooser fileChooserAWF;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JTable tblAWF;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the awfSelected
     */
    public File getAwfSelected() {
        return awfSelected;
    }
}
