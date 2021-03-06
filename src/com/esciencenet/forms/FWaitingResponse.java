/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author Tadeu
 */
public class FWaitingResponse extends javax.swing.JDialog {

    private Timer tempo;
    private int contadorSegundos;
    private int timeOut;
    private boolean canceled;
    private boolean messager;
    private Thread action;
    
    /**
     * 
     * Creates new form FWaitingResponse
     */
    public FWaitingResponse(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        //padrao de timeOut é 30 segundos
        this.timeOut = 30;
        this.tempo = new Timer(1000, this.createTickTimeAction());
        this.setLocationRelativeTo(null);
        jButton2.setContentAreaFilled(false);
        this.messager = false;
    }

    public FWaitingResponse(java.awt.Frame parent, boolean modal, int time, String message, boolean messager) {
        super(parent, modal);
        initComponents();
        this.timeOut = time;
        this.setTimeOut(timeOut);
        this.tempo = new Timer(1000, this.createTickTimeAction());
        this.setLocationRelativeTo(null);
        this.lblWaiting.setText(message);
        jButton2.setContentAreaFilled(false);        
        this.messager = messager;
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblWaiting = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        lblTime = new javax.swing.JLabel();
        pgbTime = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblWaiting.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblWaiting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/hourglass-icon.png"))); // NOI18N
        lblWaiting.setText("Waiting for answers...");

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancel-icon.png"))); // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        lblTime.setText("Time: 0/0");
        lblTime.setAutoscrolls(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pgbTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTime)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblWaiting)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(lblWaiting))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pgbTime, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblWaiting.getAccessibleContext().setAccessibleName("lblMensagem");
        jButton2.getAccessibleContext().setAccessibleName("btnCancel");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setSize(new java.awt.Dimension(315, 115));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.setCanceled(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        this.setCanceled(false);
        this.contadorSegundos = 0;
        this.tempo.start();
        
        if(this.messager){
            if(this.action != null){
                this.action.start();
            }
        }
    }//GEN-LAST:event_formComponentShown

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        
    }//GEN-LAST:event_formWindowClosed

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        this.tempo.stop();
    }//GEN-LAST:event_formComponentHidden

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblTime;
    public javax.swing.JLabel lblWaiting;
    private javax.swing.JProgressBar pgbTime;
    // End of variables declaration//GEN-END:variables

    private ActionListener createTickTimeAction(){
        
        ActionListener actionListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(contadorSegundos >= timeOut){
                    tempo.stop();
                    JOptionPane.showMessageDialog(null, "Timeout to request...", ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);  
                    FWaitingResponse.this.setCanceled(true);
                    FWaitingResponse.this.setVisible(false);
                }
                
                contadorSegundos++;                
                FWaitingResponse.this.lblTime.setText("Time: " + Integer.toString(contadorSegundos) + "/" + Integer.toString(timeOut));
                FWaitingResponse.this.pgbTime.setValue(contadorSegundos * 2);
            }
        };
        
        return actionListener;
    }

    /**
     * @param timeOut the timeOut to set
     */
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        pgbTime.setMaximum(timeOut * 2);
        pgbTime.setValue(0);
        lblTime.setText("Time: 0/"+ timeOut);
    }

    /**
     * @return the canceled
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * @param action the action to set
     */
    public void setAction(Thread action) {
        this.action = action;
    }

    /**
     * @param canceled the canceled to set
     */
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
