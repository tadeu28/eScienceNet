/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.esciencenet.forms;

import com.esciencenet.compositionmanager.Restriction;
import com.esciencenet.compositionmanager.RestrictionType;
import com.esciencenet.servicemanager.OWLSOperation;
import com.esciencenet.servicemanager.OWLSParam;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Tadeu Classe
 */
public class FRestrictionService extends javax.swing.JDialog {

    private OWLSOperation owlsOperation;
    
    /**
     * Creates new form FRestrictionService
     */
    public FRestrictionService(java.awt.Frame parent, boolean modal, OWLSOperation owlsOperation) {
        super(parent, modal);
        initComponents();
        this.owlsOperation = owlsOperation;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFooter = new javax.swing.JPanel();
        btnFechar = new javax.swing.JButton();
        lblTask = new javax.swing.JLabel();
        btnCreate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbField = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        edtRestriction = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        cmbRestType = new javax.swing.JComboBox();
        edtTime = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cmbOperator = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        pnlFooter.setBackground(new java.awt.Color(192, 192, 192));

        btnFechar.setBackground(new java.awt.Color(192, 192, 192));
        btnFechar.setText("Close");
        btnFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFecharActionPerformed(evt);
            }
        });

        lblTask.setForeground(new java.awt.Color(255, 255, 255));
        lblTask.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                lblTaskPropertyChange(evt);
            }
        });

        btnCreate.setBackground(new java.awt.Color(192, 192, 192));
        btnCreate.setText("Create/Change");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(192, 192, 192));
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFooterLayout = new javax.swing.GroupLayout(pnlFooter);
        pnlFooter.setLayout(pnlFooterLayout);
        pnlFooterLayout.setHorizontalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap(81, Short.MAX_VALUE)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCreate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTask, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlFooterLayout.setVerticalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFechar)
                        .addComponent(btnCreate)
                        .addComponent(btnDelete))
                    .addComponent(lblTask))
                .addContainerGap())
        );

        jLabel1.setText("Condition of restriction");

        jLabel2.setText("Restriction for field");

        jLabel3.setText("Time to wait");

        edtRestriction.setColumns(20);
        edtRestriction.setRows(5);
        jScrollPane1.setViewportView(edtRestriction);

        jLabel4.setText("Restriction type");

        cmbRestType.setEnabled(false);
        cmbRestType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbRestTypeActionPerformed(evt);
            }
        });

        edtTime.setEnabled(false);

        jLabel5.setText("sec.");

        jLabel6.setText("Operator");

        cmbOperator.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "==", "!=", "<", ">", "<=", ">=" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(cmbField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbRestType, 0, 268, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(edtTime)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(cmbOperator, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbRestType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(edtTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbOperator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(419, 322));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnFecharActionPerformed

    private void lblTaskPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblTaskPropertyChange

    }//GEN-LAST:event_lblTaskPropertyChange

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        if(this.owlsOperation != null){
            
            Restriction restriction = null;
            if(this.owlsOperation.getRestriction() != null){
                restriction = this.owlsOperation.getRestriction();
            }else{
                restriction = new Restriction();
            }
            
            restriction.setOperator(cmbOperator.getSelectedItem().toString());
            restriction.setOwlsParam((OWLSParam) cmbField.getSelectedItem());
            restriction.setRestrictionTerm(edtRestriction.getText());
            restriction.setRestrictionType((RestrictionType) cmbRestType.getSelectedItem());
            restriction.setTime(Integer.parseInt(edtTime.getValue().toString()));
                    
            this.owlsOperation.setRestriction(restriction);
            
            this.setVisible(false);
        }
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if(this.owlsOperation != null){
            if(this.owlsOperation.getRestriction() != null){
                this.owlsOperation.setRestriction(null);
                this.setVisible(false);
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        if(this.owlsOperation != null){
            
            cmbRestType.setModel(new DefaultComboBoxModel(RestrictionType.values()));
            cmbField.setModel(new DefaultComboBoxModel(this.owlsOperation.getOutputs().toArray())); 
            this.addConnectorParameters();
            
            if(this.owlsOperation.getRestriction() != null){
                cmbRestType.setSelectedItem(this.owlsOperation.getRestriction().getRestrictionType());
                cmbOperator.setSelectedItem(this.owlsOperation.getRestriction().getOperator());
                cmbField.setSelectedItem(this.owlsOperation.getRestriction().getOwlsParam());
                edtTime.setValue(this.owlsOperation.getRestriction().getTime());
                edtRestriction.setText(this.owlsOperation.getRestriction().getRestrictionTerm());                
            }
            
            cmbRestType.setSelectedItem(RestrictionType.LOOP);
        }  
    }//GEN-LAST:event_formComponentShown

    private void addConnectorParameters(){
        
        for(OWLSParam param : this.owlsOperation.getOutputs()){
        
            if(!param.getConnectorAssociated().isEmpty()){
                
                List<OWLSParam> connParamteres = new ArrayList<>();                
                for(int i = 0; i < param.getConnectorAssociated().size(); i++){
                    connParamteres.addAll(param.getConnectorAssociated().get(i).getOutputs());
                }
                
                for(OWLSParam connParam : connParamteres){
                    cmbField.addItem(connParam);
                }
            }
        }        
    }
    
    private void cmbRestTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRestTypeActionPerformed
        if(((RestrictionType) cmbRestType.getSelectedItem()) == RestrictionType.LOOP){
            edtTime.setEnabled(true);
        }else{
            edtTime.setEnabled(false);
        }
    }//GEN-LAST:event_cmbRestTypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnFechar;
    private javax.swing.JComboBox cmbField;
    private javax.swing.JComboBox cmbOperator;
    private javax.swing.JComboBox cmbRestType;
    private javax.swing.JTextArea edtRestriction;
    private javax.swing.JSpinner edtTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTask;
    private javax.swing.JPanel pnlFooter;
    // End of variables declaration//GEN-END:variables
}
