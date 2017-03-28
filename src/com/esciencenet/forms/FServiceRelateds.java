/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.forms;

import com.esciencenet.semanticmanager.SemanticManager;
import java.util.HashMap;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Tadeu
 */
public class FServiceRelateds extends javax.swing.JDialog {

    private String serviceSelected = "";
    
    /**
     * Creates new form FServiceRelateds
     */
    public FServiceRelateds(java.awt.Frame parent, boolean modal, String wsdlURL) {
        super(parent, modal);
        initComponents();
    
        HashMap<String, String> services = SemanticManager.getInstance().servicesRelatedWithConnector(wsdlURL);
        
        preencherGrid(services);
    }

    private void preencherGrid(HashMap<String, String> services){
        DefaultTableModel model = (DefaultTableModel) tblServicos.getModel();
        
        if(services != null){
           for(String serviceName : services.keySet()){
               
               Object[] linha = {serviceName, services.get(serviceName)};
               
               model.addRow(linha);               
           }
        }
        
        tblServicos.setModel(model);
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFooter = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblServicos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Services Related");
        setResizable(false);

        pnlFooter.setBackground(new java.awt.Color(192, 192, 192));

        btnOK.setBackground(new java.awt.Color(192, 192, 192));
        btnOK.setText("Select Service");
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

        javax.swing.GroupLayout pnlFooterLayout = new javax.swing.GroupLayout(pnlFooter);
        pnlFooter.setLayout(pnlFooterLayout);
        pnlFooterLayout.setHorizontalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOK)
                .addContainerGap())
        );
        pnlFooterLayout.setVerticalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(btnOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblServicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Service Name", "Service Term Related"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblServicos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblServicos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tblServicos);
        tblServicos.getColumnModel().getColumn(0).setResizable(false);
        tblServicos.getColumnModel().getColumn(0).setPreferredWidth(220);
        tblServicos.getColumnModel().getColumn(1).setResizable(false);
        tblServicos.getColumnModel().getColumn(1).setPreferredWidth(400);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(638, 399));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        
        if(tblServicos.getSelectedRow() != -1){
            
            this.serviceSelected = tblServicos.getModel().getValueAt(tblServicos.getSelectedRow(), 0).toString() + " - " + 
                                   tblServicos.getModel().getValueAt(tblServicos.getSelectedRow(), 1).toString();
            
            this.setVisible(false);
        }
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnCancelarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnOK;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JTable tblServicos;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the serviceSelected
     */
    public String getServiceSelected() {
        return serviceSelected;
    }
}