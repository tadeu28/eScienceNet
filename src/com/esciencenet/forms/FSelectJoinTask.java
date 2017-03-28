/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.forms;

import com.esciencenet.forms.FServicesResearch;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Tadeu
 */
public class FSelectJoinTask extends javax.swing.JDialog {

    private String selectPrevious = "";
    
    /**
     * Creates new form FSelectJoinTask
     */
    public FSelectJoinTask(java.awt.Frame parent, boolean modal, JTable tblServices, String serviceSelected) {
        super(parent, modal);
        initComponents();
        this.preencherTabela(tblServices, serviceSelected);
    }

    private void preencherTabela(JTable tblServices, String serviceSelected){
        
        DefaultTableModel modelServices = (DefaultTableModel) tblServices.getModel();
        DefaultTableModel modelTasks = (DefaultTableModel) tblTask.getModel();
        
        TableColumnModel ColumnModel = tblTask.getColumnModel();    
        FSelectJoinTask.JTableRenderer renderer = new FSelectJoinTask.JTableRenderer();    
        ColumnModel.getColumn(0).setCellRenderer(renderer);
        ImageIcon content = new ImageIcon(getClass().getResource("/images/world-icon16.png"));
        
        for(int i = 0; i < modelServices.getRowCount(); i++){
            
            if(modelServices.getValueAt(i, 4) != null){
                if((modelServices.getValueAt(i, 4).toString().contains("JOIN")) && (!modelServices.getValueAt(i, 2).toString().equals(serviceSelected))
                   && (i != tblServices.getSelectedRow())){

                    Object[] linha = {content,
                                      modelServices.getValueAt(i, 2).toString(), 
                                      modelServices.getValueAt(i, 3).toString(),
                                      modelServices.getValueAt(i, 7).toString()};

                    modelTasks.addRow(linha);                
                }            
            }
        }
        
        tblTask.setModel(modelTasks);
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFooter = new javax.swing.JPanel();
        btnFechar = new javax.swing.JButton();
        lblTask = new javax.swing.JLabel();
        btnFechar1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTask = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Associated Join");
        setResizable(false);

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

        btnFechar1.setBackground(new java.awt.Color(192, 192, 192));
        btnFechar1.setText("Process Selection");
        btnFechar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFechar1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFooterLayout = new javax.swing.GroupLayout(pnlFooter);
        pnlFooter.setLayout(pnlFooterLayout);
        pnlFooterLayout.setHorizontalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addGap(243, 243, 243)
                .addComponent(btnFechar1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTask, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlFooterLayout.setVerticalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnFechar)
                        .addComponent(btnFechar1))
                    .addComponent(lblTask))
                .addContainerGap())
        );

        tblTask.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Task", "Operation", "Task Code"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTask.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblTask.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(tblTask);
        if (tblTask.getColumnModel().getColumnCount() > 0) {
            tblTask.getColumnModel().getColumn(0).setResizable(false);
            tblTask.getColumnModel().getColumn(0).setPreferredWidth(20);
            tblTask.getColumnModel().getColumn(1).setResizable(false);
            tblTask.getColumnModel().getColumn(1).setPreferredWidth(180);
            tblTask.getColumnModel().getColumn(2).setResizable(false);
            tblTask.getColumnModel().getColumn(2).setPreferredWidth(180);
            tblTask.getColumnModel().getColumn(3).setResizable(false);
            tblTask.getColumnModel().getColumn(3).setPreferredWidth(100);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(494, 380));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnFecharActionPerformed

    private void lblTaskPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lblTaskPropertyChange

    }//GEN-LAST:event_lblTaskPropertyChange

    private void btnFechar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFechar1ActionPerformed
        
        if(tblTask.getSelectedRows() != null){
            for(int i : tblTask.getSelectedRows()){
                selectPrevious = selectPrevious + "," + tblTask.getModel().getValueAt(i, 1).toString();
            }
        }
        
        selectPrevious = selectPrevious.substring(1);
        this.setVisible(false);
    }//GEN-LAST:event_btnFechar1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnFechar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTask;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JTable tblTask;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the selectPrevious
     */
    public String getSelectPrevious() {
        return selectPrevious;
    }
    
    public class JTableRenderer extends DefaultTableCellRenderer  {    
    
        @Override
        protected void setValue(Object value){    

            if (value instanceof ImageIcon){    

                if (value != null){    
                    ImageIcon d = (ImageIcon) value;    
                    setIcon(d);
                } else{    
                    setText("");    
                    setIcon(null);  
                    this.setFont(this.getFont().deriveFont(16));
                }    

            } else {    
                super.setValue(value);    
            }    

            this.setHorizontalAlignment(CENTER);
        }
    }
}
