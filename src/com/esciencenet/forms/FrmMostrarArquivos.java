/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.forms;

import com.esciencenet.models.*;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import com.esciencenet.semanticmanager.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

/**
 *
 * @author Tadeu Classe
 */
public class FrmMostrarArquivos extends javax.swing.JDialog {

    private List<FileModel> lstFileModel;
    private boolean controleInformacoes = false;
        
    /**
     * Creates new form FrmMostrarArquivos
     */
    public FrmMostrarArquivos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();    
        
        if (System.getProperty("os.name").toLowerCase().equals("linux")){
            this.setType(Type.UTILITY);
        }else{
            this.setType(Type.POPUP);
        }
    }

    public FrmMostrarArquivos(java.awt.Frame parent, boolean modal, String peerName, List<FileModel> lstFileModel) {
        super(parent, modal);
        
        initComponents();
        
        if (System.getProperty("os.name").toLowerCase().equals("linux")){
            this.setType(Type.UTILITY);
        }else{
            this.setType(Type.POPUP);
        }
        
        setTitle("Local files - <"+ peerName +">");
        setLocationRelativeTo(null);
        this.lstFileModel = lstFileModel;
        this.inserirArquivosTabela();
        this.preencherCombo();
        
        tblArquivos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!controleInformacoes){
                    if (tblArquivos.getSelectedRow() >= 0){
                        exibirInformacoes(tblArquivos.getModel().getValueAt(tblArquivos.getSelectedRow(), 0).toString());
                    }
                }
            }
        });
    }
    
    private void exibirInformacoes(String nome){
        controleInformacoes = true;
        
        mmoInformacao.setText("");
        FileModel fileModel = obterModeloArquivo(nome);
        
        if (fileModel != null){
            //monto o html de exibição do grupo
            String html = "<font size='5' face='verdana' color='#0000FF'><b>File Informations</font></b><br><hr size='3' color='#000000'>"+
                          "<font face='verdana' color='#8800FF' size='3'><ul>"+
                          "        <li><b>Name: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getName() +"</font></li>"+
                          "        <ul>"+
                          "                <li><b>Extension: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getExtension() +"</font></li>"+
                          "                <li><b>Path: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getPath() +"</font></li>"+	
                          "                <li><b>Size: </b><font face='verdana' color='#000000' size='2'>"+ Double.toString(fileModel.getSize()) +" KB</font></li>"+	
                          "                <li><b>Type: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getType() +"</font></li>"+
                          "                <li><b>Creation Date: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getDate() +"</font></li>"+
                          "                <li><b>Description: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getDescription() +"</font></li>"+
                          "        </ul>"+
                          "        <li><b>Peer: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getPeerName() +"</font></li>"+
                          "        <li><b>Group: </b><font face='verdana' color='#000000' size='2'>"+ fileModel.getPeerGroup().getGroupName() +"</font></li>"+
                          "</ul></font>";

            //seto o tipo de exibião
            mmoInformacao.setContentType("text/html");
            //seto o texto no componente de informaçao
            mmoInformacao.setText(html);
        }
        
        controleInformacoes = false;
    }
    
    private FileModel obterModeloArquivo(String nome){
        
        for (FileModel fileModel : lstFileModel){
            
            if (fileModel.getName().equals(nome)){
                return fileModel;
            }            
        }
        
        return null;
    }
    
    public void preencherCombo(){
        
        ArrayList<PeerGroupModel> lstPeerGroupModel = SemanticManager.getInstance().obterGrupos();
        
        //crio o modelo de items da combobox
        DefaultComboBoxModel cmdModel = new DefaultComboBoxModel();        
        //crio um item vazio
        cmdModel.addElement("");
        
        //percorro a lista de grupos encontrados
        for (PeerGroupModel peerGroup : lstPeerGroupModel){
            //seto o elemento no modelo
            cmdModel.addElement(peerGroup.getGroupName());
        }       
        
        //seto o modelo e o indice do item na combobox
        cbmGrupos.setModel(cmdModel);        
        cbmGrupos.setSelectedIndex(0);        
    }
    
    public void inserirArquivosTabela(){       
        
        limparTabela();
        
        DefaultTableModel defaultTableModel = (DefaultTableModel) tblArquivos.getModel();
        
        TableColumn column = tblArquivos.getColumnModel().getColumn(4);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setWidth(0);
        column.setPreferredWidth(0);
        doLayout();
        
        for (FileModel fileModel : lstFileModel){
        
            Object[] linha = {fileModel.getName(),                               
                              fileModel.getSize(),                               
                              fileModel.getType(), 
                              fileModel.getPeerGroup().getGroupName(),
                              fileModel.getExtension()};
            
            defaultTableModel.addRow(linha);
        }        
    }
    
    public void limparTabela(){
        
        DefaultTableModel defaultTableModel = (DefaultTableModel) tblArquivos.getModel();
                
        while (defaultTableModel.getRowCount() != 0){         
            defaultTableModel.removeRow(0);            
        }        
    }
    
    public void exbirTabelaSobreFiltro(String filtro){       
        
        DefaultTableModel defaultTableModel = (DefaultTableModel) tblArquivos.getModel();
        
        TableColumn column = tblArquivos.getColumnModel().getColumn(4);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setWidth(0);
        column.setPreferredWidth(0);
        doLayout();
        
        for (FileModel fileModel : lstFileModel){
        
            if (fileModel.getPeerGroup().getGroupName().equals(filtro)){            
                Object[] linha = {fileModel.getName(),                               
                                  fileModel.getSize(),                               
                                  fileModel.getType(), 
                                  fileModel.getPeerGroup().getGroupName(),
                                  fileModel.getExtension()};

                defaultTableModel.addRow(linha);
            }
        } 
    }
    
    private void excluirArquivo(){
        String nomeArquivo = tblArquivos.getModel().getValueAt(tblArquivos.getSelectedRow(), 0).toString();
        String extensao = tblArquivos.getModel().getValueAt(tblArquivos.getSelectedRow(), 4).toString();
        
        //verifico se realmente é para excluir o arquivo
        if (JOptionPane.showConfirmDialog(this, "Do you really want to remove the file ["+ nomeArquivo +"]?", 
                                                ".: e-ScienceNet :.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
                                                (new javax.swing.ImageIcon(getClass().getResource("/images/question.png")))) == 0){
            //removo o arquivo da ontologia
            if (SemanticManager.getInstance().removerArquivoInOWL(nomeArquivo)){
                
                DefaultTableModel defaultTableModel = (DefaultTableModel) tblArquivos.getModel();
                defaultTableModel.removeRow(tblArquivos.getSelectedRow());
                                
                SemanticManager.getInstance().getDataManager().removerArquivo(nomeArquivo + extensao);
                
                String fileString = SemanticManager.getInstance().getDataManager().getPath() + File.separator + nomeArquivo + extensao;
                File file = new File(fileString);
                if(file.exists()){
                    file.delete();
                }
                
                JOptionPane.showMessageDialog(this, "O arquivo ["+ nomeArquivo +"] foi removido com sucesso!", ".: e-ScienceNet :.", JOptionPane.DEFAULT_OPTION,
                                              (new javax.swing.ImageIcon(getClass().getResource("/images/success.png"))));
            }
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ppmTabela = new javax.swing.JPopupMenu();
        actIrPara = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        actExcluir = new javax.swing.JMenuItem();
        pnlFooter = new javax.swing.JPanel();
        btnFechar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        btnMostrarResults = new javax.swing.JButton();
        pnlMain = new javax.swing.JPanel();
        pnlFiltro = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbmGrupos = new javax.swing.JComboBox();
        pnlInformacao = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        mmoInformacao = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblArquivos = new javax.swing.JTable();

        actIrPara.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/directory-accept-icon.png"))); // NOI18N
        actIrPara.setText("Show folder...");
        actIrPara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actIrParaActionPerformed(evt);
            }
        });
        ppmTabela.add(actIrPara);
        ppmTabela.add(jSeparator1);

        actExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/trash-icon.png"))); // NOI18N
        actExcluir.setText("Delete");
        actExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actExcluirActionPerformed(evt);
            }
        });
        ppmTabela.add(actExcluir);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
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

        btnExcluir.setBackground(new java.awt.Color(192, 192, 192));
        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/trash-icon.png"))); // NOI18N
        btnExcluir.setText("Delete");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnMostrarResults.setBackground(new java.awt.Color(192, 192, 192));
        btnMostrarResults.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Document-icon.png"))); // NOI18N
        btnMostrarResults.setText("View Results");
        btnMostrarResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMostrarResultsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFooterLayout = new javax.swing.GroupLayout(pnlFooter);
        pnlFooter.setLayout(pnlFooterLayout);
        pnlFooterLayout.setHorizontalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMostrarResults)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlFooterLayout.setVerticalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFechar)
                    .addComponent(btnExcluir)
                    .addComponent(btnMostrarResults))
                .addContainerGap())
        );

        pnlFiltro.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Group filter");

        cbmGrupos.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbmGruposItemStateChanged(evt);
            }
        });
        cbmGrupos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbmGruposActionPerformed(evt);
            }
        });
        cbmGrupos.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cbmGruposPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout pnlFiltroLayout = new javax.swing.GroupLayout(pnlFiltro);
        pnlFiltro.setLayout(pnlFiltroLayout);
        pnlFiltroLayout.setHorizontalGroup(
            pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltroLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbmGrupos, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlFiltroLayout.setVerticalGroup(
            pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbmGrupos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlInformacao.setBorder(javax.swing.BorderFactory.createTitledBorder(" Informations "));

        mmoInformacao.setEditable(false);
        jScrollPane2.setViewportView(mmoInformacao);

        javax.swing.GroupLayout pnlInformacaoLayout = new javax.swing.GroupLayout(pnlInformacao);
        pnlInformacao.setLayout(pnlInformacaoLayout);
        pnlInformacaoLayout.setHorizontalGroup(
            pnlInformacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 990, Short.MAX_VALUE)
        );
        pnlInformacaoLayout.setVerticalGroup(
            pnlInformacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
        );

        tblArquivos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Size (KB)", "Type", "Group", "Extension"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblArquivos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblArquivos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblArquivosMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblArquivos);
        if (tblArquivos.getColumnModel().getColumnCount() > 0) {
            tblArquivos.getColumnModel().getColumn(4).setResizable(false);
            tblArquivos.getColumnModel().getColumn(4).setPreferredWidth(0);
        }

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFiltro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlInformacao, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3)
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addComponent(pnlFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlInformacao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getAccessibleContext().setAccessibleName("frmMostrarArquivos");

        setSize(new java.awt.Dimension(1020, 624));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnFecharActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        this.excluirArquivo();
        btnMostrarResults.setVisible(false);
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void cbmGruposItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbmGruposItemStateChanged
        
        
        
    }//GEN-LAST:event_cbmGruposItemStateChanged

    private void cbmGruposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmGruposActionPerformed
        limparTabela();
        mmoInformacao.setText("");
        
        if (cbmGrupos.getSelectedItem().toString().equals("")){
            inserirArquivosTabela();
        }else{
            exbirTabelaSobreFiltro(cbmGrupos.getSelectedItem().toString());
        }
    }//GEN-LAST:event_cbmGruposActionPerformed

    private void cbmGruposPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cbmGruposPropertyChange
        
    }//GEN-LAST:event_cbmGruposPropertyChange

    private void tblArquivosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblArquivosMouseReleased
        if ((tblArquivos.getSelectedRow() >= 0) && (evt.isPopupTrigger())){
            ppmTabela.show(tblArquivos, evt.getX(), evt.getY());
        }
        
        if(tblArquivos.getSelectedRow() >= 0){
            
            if(tblArquivos.getModel().getValueAt(tblArquivos.getSelectedRow(), 4).toString().equals(".wfr")){
                btnMostrarResults.setVisible(true);
            }else{
                btnMostrarResults.setVisible(false);
            }            
        }
    }//GEN-LAST:event_tblArquivosMouseReleased

    private void actIrParaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actIrParaActionPerformed
                
        try {
            FileModel fileModel = this.obterModeloArquivo(tblArquivos.getModel().getValueAt(tblArquivos.getSelectedRow(), 0).toString());
            
            if (fileModel != null){
                String diretorio = fileModel.getPath();
                Runtime.getRuntime().exec("explorer " + diretorio);
            }
        } catch (IOException ex) {
            Logger.getLogger(FrmMostrarArquivos.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_actIrParaActionPerformed

    private void actExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actExcluirActionPerformed
        this.excluirArquivo();
    }//GEN-LAST:event_actExcluirActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        btnMostrarResults.setVisible(false);
    }//GEN-LAST:event_formComponentShown

    private void btnMostrarResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMostrarResultsActionPerformed
        if(tblArquivos.getSelectedRow() != -1){
            FileModel fileModel = this.obterModeloArquivo(tblArquivos.getModel().getValueAt(tblArquivos.getSelectedRow(), 0).toString());
            if (fileModel != null){
                String completeFileName = fileModel.getPath() + fileModel.getName() + fileModel.getExtension();
                
                FViewWFResult frmViewResult = new FViewWFResult(null, true, completeFileName);
                frmViewResult.setVisible(true);                
            }            
        }
    }//GEN-LAST:event_btnMostrarResultsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem actExcluir;
    private javax.swing.JMenuItem actIrPara;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnMostrarResults;
    private javax.swing.JComboBox cbmGrupos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTextPane mmoInformacao;
    private javax.swing.JPanel pnlFiltro;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JPanel pnlInformacao;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPopupMenu ppmTabela;
    private javax.swing.JTable tblArquivos;
    // End of variables declaration//GEN-END:variables

    public List<FileModel> getLstFileModel() {
        return lstFileModel;
    }

    public void setLstFileModel(List<FileModel> lstFileModel) {
        this.lstFileModel = lstFileModel;
    }
}
