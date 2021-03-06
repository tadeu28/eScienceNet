/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.forms;

import com.esciencenet.semanticmanager.SemanticManager;
import com.esciencenet.servicemanager.wsdlmodels.WSDLRecover;
import com.esciencenet.servicemanager.wsdlmodels.WSDLRecoverParams;
import static java.awt.image.ImageObserver.WIDTH;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.xml.namespace.QName;
import org.mindswap.owl.vocabulary.OWL;
import org.mindswap.owl.vocabulary.XSD;
import org.mindswap.utils.QNameProvider;
import org.mindswap.wsdl.WSDLConsts;

/**
 *
 * @author Tadeu
 */
public class FSimpleParametesAnnotation extends javax.swing.JDialog {

    private String wsdl; 
    private String operation; 
    private String parameter;
    private QNameProvider qNames;
    private boolean isInput;
    private WSDLRecoverParams wsdlRecoverParam;
    private WSDLRecoverParams wsdlRecoverParamParent;
    
    private WSDLRecover wsdlRecover = null;
    
    /**
     * Creates new form FSimpleParametesAnnotation
     */
    public FSimpleParametesAnnotation(java.awt.Frame parent, boolean modal, String wsdl, String operation, String parameter, QNameProvider qNames, boolean isInput) {
        super(parent, modal);
        initComponents();
        lblHeader.setVisible(true);
        lblHeader.setText("Parameter annotation from complex param ["+ parameter +" ("+ operation +")]");
        
        this.wsdl = wsdl;
        this.operation = operation;
        this.parameter = parameter;
        this.qNames = qNames;
        this.isInput = isInput;
    }

    public FSimpleParametesAnnotation(java.awt.Frame parent, boolean modal, String wsdl, String operation, String parameter, QNameProvider qNames, 
                                      boolean isInput, WSDLRecoverParams wsdlRecoverParam) {
        super(parent, modal);
        initComponents();
        lblHeader.setVisible(true);
        lblHeader.setText("Parameter annotation from complex param ["+ parameter +" ("+ operation +")]");
        
        this.wsdl = wsdl;
        this.operation = operation;
        this.parameter = parameter;
        this.qNames = qNames;
        this.isInput = isInput;
        this.wsdlRecoverParam = wsdlRecoverParam;
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
        lblBusca = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblHeader = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblParams = new javax.swing.JTable();
        btnAnotateInput = new javax.swing.JButton();
        btnShowSubParams = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Annotate Sub-Parameters of Complex Type");
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

        lblBusca.setForeground(new java.awt.Color(255, 255, 255));
        lblBusca.setText("Searching by simple params...");

        javax.swing.GroupLayout pnlFooterLayout = new javax.swing.GroupLayout(pnlFooter);
        pnlFooter.setLayout(pnlFooterLayout);
        pnlFooterLayout.setHorizontalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblBusca)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlFooterLayout.setVerticalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFechar)
                    .addComponent(lblBusca))
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblHeader.setText("Annotate params from complex param []");
        jPanel1.add(lblHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        tblParams.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "WSDL Parameter", "WSDL Type", "Mandatory", "OWL-S Parameter", "OWL-S Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblParams.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblParamsMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblParams);
        tblParams.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblParams.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblParams.getColumnModel().getColumn(2).setPreferredWidth(45);
        tblParams.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblParams.getColumnModel().getColumn(4).setPreferredWidth(200);

        btnAnotateInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/domain-template-icon.png"))); // NOI18N
        btnAnotateInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnotateInputActionPerformed(evt);
            }
        });

        btnShowSubParams.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/tag-blue-edit-icon.png"))); // NOI18N
        btnShowSubParams.setEnabled(false);
        btnShowSubParams.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowSubParamsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlFooter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 679, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAnotateInput, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnShowSubParams, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 6, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnAnotateInput)
                        .addGap(5, 5, 5)
                        .addComponent(btnShowSubParams)
                        .addGap(0, 213, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(740, 417));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed

        for(int i = 0; i < tblParams.getRowCount(); i++){
            if((tblParams.getModel().getValueAt(i, 2).toString().equals("X")) && (tblParams.getModel().getValueAt(i, 4).toString().equals(""))){
                if(JOptionPane.showConfirmDialog(null, "The parameter ["+ tblParams.getModel().getValueAt(i, 0).toString() +
                               "] is mandatory.\n\nWould you like really to close this windows?", ".: e-ScienceNet :.", 
                               JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION){
                    return;
                }
            }else{
                if(tblParams.getModel().getValueAt(i, 4).toString().contains("Thing")){
                    if(this.wsdlRecoverParamParent.getSubParams().get(i).getSubParams().isEmpty()){
                        if(JOptionPane.showConfirmDialog(null, "The parameter ["+ tblParams.getModel().getValueAt(i, 0).toString() +
                                    "] is a complex type, but there isn't some sub-parameter associeted.\n\nWould you like really to close this windows?", ".: e-ScienceNet :.", 
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION){
                            return;
                        }
                    }
                }
            }
        }
        
        this.setVisible(false);
    }//GEN-LAST:event_btnFecharActionPerformed

    private void btnAnotateInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnotateInputActionPerformed

        if(tblParams.getSelectedRow() > -1){

            String wsdlType = tblParams.getModel().getValueAt(tblParams.getSelectedRow(), 1).toString();

            if(wsdlType.indexOf(":") != 0){
                wsdlType = wsdlType.substring(wsdlType.indexOf(":") + 1);
            }

            if(tblParams.getModel().getValueAt(tblParams.getSelectedRow(), 4).toString().contains("Thing")){
                if(JOptionPane.showConfirmDialog(null, "["+ tblParams.getModel().getValueAt(tblParams.getSelectedRow(), 0).toString() +"] is a complex param.\n\n"+ 
                            "Would you like really to annotate it?", ".: e-ScienceNet :.", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION){
                    return;
                }
            }
            
            String owlsName = tblParams.getModel().getValueAt(tblParams.getSelectedRow(), 3).toString();

            String[] suggestion = {operation, wsdlType, owlsName, parameter};

            FAssServicesAndDomain frmAssServicesAndDomain = new FAssServicesAndDomain(null, true, suggestion);
            frmAssServicesAndDomain.setVisible(true);

            if(frmAssServicesAndDomain.getClassesModel() != null){

                String uri = frmAssServicesAndDomain.getClassesModel().getUri().substring(0, frmAssServicesAndDomain.getClassesModel().getUri().lastIndexOf("#") + 1);

                if(frmAssServicesAndDomain.getClassesModel().getFile() != null){
                    qNames.setMapping(frmAssServicesAndDomain.getClassesModel().getFile().toLowerCase().replaceAll(".owl", ""), uri);
                }

                tblParams.getModel().setValueAt(qNames.shortForm(frmAssServicesAndDomain.getClassesModel().getUri()), tblParams.getSelectedRow(), 4);
                
                wsdlRecoverParamParent.getSubParams().get(tblParams.getSelectedRow()).setNameSpaceURI(frmAssServicesAndDomain.getClassesModel().getUri());
                wsdlRecoverParamParent.getSubParams().get(tblParams.getSelectedRow()).setLocalPart(qNames.shortForm(frmAssServicesAndDomain.getClassesModel().getUri()));
            }
        }
    }//GEN-LAST:event_btnAnotateInputActionPerformed

    private void btnShowSubParamsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowSubParamsActionPerformed
        if(tblParams.getSelectedRow() > -1){
         
            String wsdlParameter = tblParams.getModel().getValueAt(tblParams.getSelectedRow(), 0).toString();
            
            WSDLRecoverParams wsdlRecoverParameter = this.getParamSelected(wsdlParameter);
            
            if(wsdlRecoverParameter != null){
                FSimpleParametesAnnotation frmSimpleParam = new FSimpleParametesAnnotation(null, true, wsdl, operation, wsdlParameter, qNames, isInput, wsdlRecoverParameter);
                frmSimpleParam.setWsdlRecoverParamParent(wsdlRecoverParamParent.getSubParams().get(tblParams.getSelectedRow()));
                frmSimpleParam.setVisible(true);
            }
        }
    }//GEN-LAST:event_btnShowSubParamsActionPerformed

    private void tblParamsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParamsMouseReleased
        if(tblParams.getSelectedRow() != -1){
            
            if(tblParams.getModel().getValueAt(tblParams.getSelectedRow(), 1).toString().contains("xsd")){
                btnShowSubParams.setEnabled(false);
            }else{
                btnShowSubParams.setEnabled(true);
            }            
        }
    }//GEN-LAST:event_tblParamsMouseReleased

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
                
        if(wsdlRecoverParam == null){
            List<String> lstParameter = new ArrayList<>();
            lstParameter.add(parameter);
            
            if(isInput){
                wsdlRecover = SemanticManager.getInstance().getCompositionManager().getProcessWSDLInformation().getWSDLInfo(wsdl, operation, lstParameter, null);
            }else{
                wsdlRecover = SemanticManager.getInstance().getCompositionManager().getProcessWSDLInformation().getWSDLInfo(wsdl, operation, null, lstParameter);
            }
            
            if(wsdlRecover != null){
                this.varrerWSDLRecover(wsdlRecover);
            }
        }else{
            this.preencherTabela(wsdlRecoverParam);
            this.povoarLstParametros(tblParams, wsdlRecoverParamParent.getSubParams());
        }
        
        lblBusca.setVisible(false);
    }//GEN-LAST:event_formComponentShown

    private void varrerWSDLRecover(WSDLRecover wsdlRecover){
        
        if(isInput){
            for(WSDLRecoverParams inputs : wsdlRecover.getInputs()){
                this.preencherTabela(inputs);
                this.wsdlRecoverParam = inputs;
                this.povoarLstParametros(tblParams, wsdlRecoverParamParent.getSubParams());
            }
        }else{
            for(WSDLRecoverParams outputs : wsdlRecover.getOutputs()){
                this.preencherTabela(outputs);
                this.povoarLstParametros(tblParams, wsdlRecoverParamParent.getSubParams());
                this.wsdlRecoverParam = outputs;
            }
        }
    }
    
    public void preencherTabela(WSDLRecoverParams wsdlRecoverParam){

        DefaultTableModel model = (DefaultTableModel) tblParams.getModel();
        
        for(WSDLRecoverParams wsdlParam : wsdlRecoverParam.getSubParams()){

            QName paramType = (wsdlParam.getLocalPart() == null) ? new QName(WSDLConsts.xsdURI, "any") : new QName(wsdlParam.getNameSpaceURI(), wsdlParam.getLocalPart());
            String wsdlType = paramType.getNamespaceURI() + "#" + paramType.getLocalPart();

            String type = OWL.Thing.toString();

            if (paramType.getNamespaceURI().equals(WSDLConsts.soapEnc) || (paramType.getNamespaceURI().equals(WSDLConsts.xsdURI) &&  (! paramType.getLocalPart().equals("any")))){
                type = XSD.ns + paramType.getLocalPart();
            }

            if(type.contains(OWL.Thing.toString())){
                Object[] linha = {wsdlParam.getName(),
                                qNames.shortForm(wsdlType),
                                (wsdlParam.isMandatory() ? "X" : ""),
                                wsdlParam.getName(), 
                                qNames.shortForm(type)};

                model.addRow(linha);
            }else{
                Object[] linha = {wsdlParam.getName(),
                                qNames.shortForm(wsdlType),
                                (wsdlParam.isMandatory() ? "X" : ""),
                                wsdlParam.getName(), 
                                ""};

                model.addRow(linha);
            }
        }     
        
        tblParams.setModel(model);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnotateInput;
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnShowSubParams;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBusca;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JTable tblParams;
    // End of variables declaration//GEN-END:variables

    private WSDLRecoverParams getParamSelected(String paramName){
     
        if(isInput){
            for(WSDLRecoverParams param : wsdlRecoverParam.getSubParams()){
                if(param.getName().equals(paramName)){
                    return param;
                }
            }
        }else{
            for(WSDLRecoverParams param : wsdlRecoverParam.getSubParams()){
                if(param.getName().equals(paramName)){
                    return param;
                }
            }
        }
     
        return null;
    }    

    private void povoarLstParametros(JTable tblParametro, List<WSDLRecoverParams> lstParameter){
        for(int i = 0; i < tblParametro.getRowCount(); i++){
            
            boolean exists = false;
            for(WSDLRecoverParams param : lstParameter){
                if(tblParametro.getModel().getValueAt(i, 3).toString().equals(param.getName())){
                    exists = true;
                    tblParametro.getModel().setValueAt((param.getLocalPart() == null ? "" : param.getLocalPart()), i, 4);
                    break;
                }
            }
                    
            if(!exists){
                WSDLRecoverParams wsdlRecoverParams = new WSDLRecoverParams();
                wsdlRecoverParams.setName(tblParametro.getModel().getValueAt(i, 3).toString());
                wsdlRecoverParams.setParameterTypeXSD(qNames.longForm(tblParametro.getModel().getValueAt(i, 1).toString()));
                wsdlRecoverParams.setMandatory("X".equals(tblParametro.getModel().getValueAt(i, 2).toString()));

                if(!tblParametro.getModel().getValueAt(i, 4).toString().equals("")){
                    wsdlRecoverParams.setLocalPart(tblParametro.getModel().getValueAt(i, 4).toString());
                    wsdlRecoverParams.setNameSpaceURI(qNames.longForm(wsdlRecoverParams.getLocalPart()));
                }else{
                    wsdlRecoverParams.setLocalPart("");
                }
                
                lstParameter.add(wsdlRecoverParams);
            }
        }    
    }
    
    /**
     * @param wsdlRecoverParamParent the wsdlRecoverParamParent to set
     */
    public void setWsdlRecoverParamParent(WSDLRecoverParams wsdlRecoverParamParent) {
        this.wsdlRecoverParamParent = wsdlRecoverParamParent;
    }
}
