/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.forms;

import com.esciencenet.servicemanager.OWLSParam;
import java.awt.Color;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Tadeu
 */
public class FPutParameters extends javax.swing.JDialog {
    
    private boolean connector = false;
    
    /**
     * Creates new form FPutParameters
     */
    public FPutParameters(java.awt.Frame parent, boolean modal, String serviceName, OWLSParam input, boolean isFirstCase) {
        super(parent, modal);
        initComponents();
        this.firstOrFifthCase(input, isFirstCase, serviceName);   
        this.preencherParametrosTable(input);
    }
    
    public FPutParameters(java.awt.Frame parent, boolean modal, String serviceName, List<OWLSParam> lstInputs) {
        super(parent, modal);
        initComponents();
        this.connector = true;
        
        this.loadConenctors(lstInputs, serviceName);
        
        if(!lstInputs.isEmpty()){
            this.preencherParametrosTable(lstInputs.get(0));
        }
    }
    
    public FPutParameters(java.awt.Frame parent, boolean modal, String serviceName, OWLSParam outputs, List<OWLSParam> lstInputs, boolean isThird) {
        super(parent, modal);
        initComponents();
        
        this.preencherParametrosTable(outputs);
        
        if(isThird){
            this.thirdCase(outputs, lstInputs, serviceName);
        }else{
            this.secondCase(outputs, lstInputs, serviceName);
        }
    }

    private boolean checkIfParamExistInTable(OWLSParam param){
        
        for(int i = 0; i < tblParamters.getRowCount(); i++){
            if(((OWLSParam)tblParamters.getModel().getValueAt(i, 0)) == param){
                return true;
            }
        }
        
        return false;
    }
    
    private void loadConenctors(List<OWLSParam> lstInputs, String serviceName){
        this.setTitle("Service ["+ serviceName +"] - Input Connections");

        lblInicial.setText("Inputs:");
        lblFinal.setText("Connection Param (Next Task's outputs):");
        cmbPrimeiro.removeAllItems();
        cmbSegundo.removeAllItems();

        OWLSParam manual = new OWLSParam();
        manual.setParamName("[Manual Value]");
        cmbSegundo.addItem(manual);
        cmbSegundo.setSelectedIndex(0);
        
        for(OWLSParam input : lstInputs){
            cmbPrimeiro.addItem(input);
        }        
        cmbPrimeiro.setSelectedIndex(0);
    }
    
    private void thirdCase(OWLSParam input, List<OWLSParam> outputs, String serviceName){
        try{
            this.setTitle("Service ["+ serviceName +"] - Input Connections");

            lblInicial.setText("Inputs:");
            lblFinal.setText("Connection Param (Next Task's outputs):");
            cmbPrimeiro.removeAllItems();
            cmbSegundo.removeAllItems();
            
            OWLSParam manual = new OWLSParam();
            manual.setParamName("[Manual Value]");
            cmbSegundo.addItem(manual);

            for(OWLSParam param : outputs){                
                if(!checkIfParamExistInTable(param)){
                    cmbSegundo.addItem(param);
                }
            }

            cmbPrimeiro.addItem(input);
            cmbPrimeiro.setSelectedIndex(0);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to open the parameter connection's screem.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            this.setVisible(false);
        }
    }
    
    private void firstOrFifthCase(OWLSParam input, boolean isFirstCase, String serviceName){
        try{
            lblFinal.setText("Connection Param:");
            cmbPrimeiro.removeAllItems();
            cmbSegundo.removeAllItems();

            if(isFirstCase){
                this.setTitle("Service ["+ serviceName +"] - Input Connections");                
                lblInicial.setText("Input:");
                
                OWLSParam output = new OWLSParam();
                output.setParamName("[Manual Value]");
                cmbSegundo.addItem(output);
                cmbSegundo.setSelectedIndex(0);
            }else{
                this.setTitle("Service ["+ serviceName +"] - Output Connections");                
                lblInicial.setText("Output:");
            }
            
            cmbPrimeiro.addItem(input);
            cmbPrimeiro.setSelectedIndex(0);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to open the parameter connection's screem.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            this.setVisible(false);
        }
    }
    
    private void secondCase(OWLSParam output, List<OWLSParam> inputs, String serviceName){
        try{
            this.setTitle("Service ["+ serviceName +"] - Output Connections");

            lblInicial.setText("Output:");
            lblFinal.setText("Connection Param (Next Task's inputs):");
            cmbPrimeiro.removeAllItems();
            cmbSegundo.removeAllItems();

            for(OWLSParam param : inputs){
                if(!checkIfParamExistInTable(param)){
                    cmbSegundo.addItem(param);
                }
            }

            if(output.getParamConnected() == null){
                cmbSegundo.setSelectedIndex(0);
            }else{
                cmbSegundo.setSelectedItem(output.getParamConnected());
            }

            cmbPrimeiro.addItem(output);
            cmbPrimeiro.setSelectedIndex(0);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to open the parameter connection's screem.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            this.setVisible(false);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnFechar = new javax.swing.JButton();
        btnOK = new javax.swing.JButton();
        btnUnlink = new javax.swing.JButton();
        lblInicial = new javax.swing.JLabel();
        lblFinal = new javax.swing.JLabel();
        cmbPrimeiro = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        cmbSegundo = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        edtManual = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblParamters = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Put parameters");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(192, 192, 192));

        btnFechar.setBackground(new java.awt.Color(192, 192, 192));
        btnFechar.setText("Close");
        btnFechar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFecharActionPerformed(evt);
            }
        });

        btnOK.setBackground(new java.awt.Color(192, 192, 192));
        btnOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/connect-icon.png"))); // NOI18N
        btnOK.setText("Connect");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnUnlink.setBackground(new java.awt.Color(192, 192, 192));
        btnUnlink.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disconnect-icon.png"))); // NOI18N
        btnUnlink.setText("Disconnect");
        btnUnlink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnlinkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUnlink)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOK)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFechar)
                    .addComponent(btnOK)
                    .addComponent(btnUnlink))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblInicial.setText("lblInicial");

        lblFinal.setText("lblFinal");

        cmbPrimeiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPrimeiroActionPerformed(evt);
            }
        });

        jLabel1.setText("<->");

        cmbSegundo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSegundoActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(" Manual Content: "));

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        edtManual.setColumns(20);
        edtManual.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        edtManual.setLineWrap(true);
        edtManual.setRows(5);
        edtManual.setEnabled(false);
        jScrollPane1.setViewportView(edtManual);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(" Parameters Associated: "));

        tblParamters.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Parameters Associated"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblParamters);
        if (tblParamters.getColumnModel().getColumnCount() > 0) {
            tblParamters.getColumnModel().getColumn(0).setResizable(false);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 3, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmbPrimeiro, 0, 284, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addGap(5, 5, 5))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblInicial)
                                .addGap(18, 18, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFinal)
                            .addComponent(cmbSegundo, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblInicial)
                    .addComponent(lblFinal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbPrimeiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(cmbSegundo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.getAccessibleContext().setAccessibleName("");

        setSize(new java.awt.Dimension(671, 450));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnFecharActionPerformed

    //salvo a conexão
    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed

        if(cmbSegundo.getSelectedIndex() != -1){
            OWLSParam output = (OWLSParam) cmbSegundo.getSelectedItem();
            OWLSParam input = (OWLSParam) cmbPrimeiro.getSelectedItem();

            if(output.getParamName().equals("[Manual Value]")){
                input.setManualContent(edtManual.getText());
                if(input.getParamConnected().size() > 0){
                    input.getParamConnected().clear();
                }
            }else{
                input.getParamConnected().add(output);
                output.getParamConnected().add(input);
                input.setManualContent("");
            }

            this.preencherParametrosTable(input);

            cmbSegundo.removeItemAt(cmbSegundo.getSelectedIndex());
            edtManual.setText("");
        }
    }//GEN-LAST:event_btnOKActionPerformed

    private void preencherParametrosTable(OWLSParam params){
        
        DefaultTableModel model = (DefaultTableModel) tblParamters.getModel();
        
        if(!params.getManualContent().equals("")){
            
            if(model.getRowCount() > 0){
                model.setValueAt("[Manual Value] - " + params.getManualContent(), 0, 0);
            }else{
                Object[] linha = {"[Manual Value] - " + params.getManualContent()};
                model.addRow(linha);
            }          
            
        }else{
            this.limparTabela(tblParamters);
            
            for(OWLSParam param : params.getParamConnected()){
                Object[] linha = {param};
                model.addRow(linha);
            }            
        }    
        
        tblParamters.setModel(model);
    }
    
    private void limparTabela(JTable tabela){
        DefaultTableModel defaultTableModel = (DefaultTableModel) tabela.getModel();
                
        while (defaultTableModel.getRowCount() != 0){         
            defaultTableModel.removeRow(0);            
        }   
    }
    
    private void cmbSegundoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSegundoActionPerformed
        
        if(cmbSegundo.getSelectedIndex() != -1){
            OWLSParam output = (OWLSParam) cmbSegundo.getSelectedItem();

            if(output != null){
                if(output.getParamName().equals("[Manual Value]")){
                    edtManual.setEnabled(true);
                    edtManual.setText("");
                    edtManual.setBackground(Color.white);
                }else{
                    edtManual.setEnabled(false);
                    edtManual.setText("");
                    edtManual.setBackground(new Color(192,192,192));
                }  
            }
        }
    }//GEN-LAST:event_cmbSegundoActionPerformed

    private void cmbPrimeiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPrimeiroActionPerformed
        
        if(cmbPrimeiro.getSelectedIndex() != -1){
            OWLSParam input = (OWLSParam) cmbPrimeiro.getSelectedItem(); 
            
            if(input != null){

                if(input.getParamConnected().isEmpty()){
                    
                    if(connector){
                        if(input.getManualContent().equals("")){
                            if(cmbSegundo.getItemCount() == 0){
                                OWLSParam manual = new OWLSParam();
                                manual.setParamName("[Manual Value]");
                                cmbSegundo.addItem(manual);
                                cmbSegundo.setSelectedIndex(0);                            
                            }                            
                            edtManual.setBackground(Color.white);
                        }else{
                            cmbSegundo.removeAllItems();
                            edtManual.setText(input.getManualContent());
                            edtManual.setBackground(Color.white);
                        }
                        
                        this.preencherParametrosTable(input);
                    }else{
                        if(cmbSegundo.getItemCount() > 0){
                            cmbSegundo.setSelectedIndex(0);
                            edtManual.setText(input.getManualContent());
                            edtManual.setBackground(Color.white);
                        }
                    }
                }else{
                    cmbSegundo.setSelectedItem(input.getParamConnected());
                    edtManual.setText("");
                    edtManual.setBackground(new Color(192,192,192));
                }            
            }        
        }
    }//GEN-LAST:event_cmbPrimeiroActionPerformed

    private void btnUnlinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnlinkActionPerformed
        if(tblParamters.getSelectedRow() != -1){           

            OWLSParam input = (OWLSParam) cmbPrimeiro.getSelectedItem();
            
            DefaultTableModel defaultTableModel = (DefaultTableModel) tblParamters.getModel();
            
            if(!defaultTableModel.getValueAt(tblParamters.getSelectedRow(), 0).toString().contains("[Manual Value]")){
                OWLSParam output = (OWLSParam) defaultTableModel.getValueAt(tblParamters.getSelectedRow(), 0);
                cmbSegundo.addItem(output);
                input.getParamConnected().remove(output);
                output.getParamConnected().remove(input);
            }else{
                OWLSParam output = new OWLSParam();
                output.setParamName("[Manual Value]");
                cmbSegundo.addItem(output);
                
                input.setManualContent("");
            }
            defaultTableModel.removeRow(tblParamters.getSelectedRow());
            
            tblParamters.setModel(defaultTableModel);
        }
    }//GEN-LAST:event_btnUnlinkActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnUnlink;
    private javax.swing.JComboBox cmbPrimeiro;
    private javax.swing.JComboBox cmbSegundo;
    private javax.swing.JTextArea edtManual;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblFinal;
    private javax.swing.JLabel lblInicial;
    private javax.swing.JTable tblParamters;
    // End of variables declaration//GEN-END:variables
}
