/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.forms;

import com.esciencenet.models.DomainsRelations;
import com.esciencenet.models.ParamServiceModel;
import com.esciencenet.models.PeerGroupModel;
import com.esciencenet.models.ServicesInfoModel;
import com.esciencenet.models.ServicesModelView;
import com.esciencenet.semanticmanager.SemanticManager;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ScrollPaneConstants;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Tadeu
 */
public class FShowServices extends javax.swing.JDialog {

    private List<ServicesModelView> lstServicesModelView;
    private mxGraph graph;
    private mxGraphComponent graphComponent;
    
    /**
     * Creates new form FShowServices
     */
    public FShowServices(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();        
        this.preencherCombo();
        this.criarExibicaoGrafica();
        this.createStyleForOWL();
        this.createStyleForClass();
        this.createStyleForIndividual();
        this.createStyleForClassEdge();
        this.createStyleForIndividualEdge();
    }
    
    private void criarExibicaoGrafica(){       
       
        this.graph = new mxGraph();
        this.graph.setCellsEditable(false);
                
        this.graphComponent = new mxGraphComponent(this.graph);         
                
        pnlDiagrama.add(this.graphComponent);

        Double width = pnlDiagrama.getBounds().getWidth();
        Double height = pnlDiagrama.getBounds().getHeight();
        this.graphComponent.setBounds(0, 0, width.intValue(), height.intValue());

        pnlDiagrama.revalidate();
        pnlDiagrama.repaint();
    }
    
    private void createStyleForOWL(){
        mxStylesheet stylesheet = graph.getStylesheet();
        HashMap<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_FILLCOLOR, "#E2E2E2");
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.GRAY));
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_FONTSIZE, 16);
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_ROUNDED, true);
        style.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP);
        stylesheet.putCellStyle("OWL", style);
    }
    
    private void createStyleForClass(){
        mxStylesheet stylesheet = graph.getStylesheet();
        HashMap<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_FILLCOLOR, "#FFFF8B");
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        style.put(mxConstants.STYLE_FONTSIZE, 12);
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        stylesheet.putCellStyle("Class", style);   
    }
    
    private void createStyleForIndividual(){
        mxStylesheet stylesheet = graph.getStylesheet();
        HashMap<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_FILLCOLOR, "#67B6D4");
        style.put(mxConstants.STYLE_FONTSIZE, 12);
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.0);
        style.put(mxConstants.STYLE_STROKECOLOR, "#00009B");
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.WHITE));
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        stylesheet.putCellStyle("Individual", style); 
    }
    
    private void createStyleForClassEdge(){
        mxStylesheet stylesheet = graph.getStylesheet();
        HashMap<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));        
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        style.put(mxConstants.STYLE_STROKEWIDTH, 2);        
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);                
        //style.put(mxConstants.STYLE_EDGE, mxEdgeStyle.EntityRelation);
        stylesheet.putCellStyle("classEdge", style);         
    }
    
    private void createStyleForIndividualEdge(){
        mxStylesheet stylesheet = graph.getStylesheet();
        HashMap<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));                
        style.put(mxConstants.STYLE_STROKEWIDTH, 2);                
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OPEN);
        style.put(mxConstants.STYLE_STARTARROW, mxConstants.ARROW_OPEN);
        style.put(mxConstants.STYLE_DASHED, true);
        style.put(mxConstants.STYLE_ORTHOGONAL, true);    
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        stylesheet.putCellStyle("individualEdge", style); 
    }
    
    private void preencherCombo(){
        
        ArrayList<PeerGroupModel> lstPeerGroupModel = SemanticManager.getInstance().obterGrupos();
        
        //crio o modelo de items da combobox
        DefaultComboBoxModel cmdModel = new DefaultComboBoxModel();                
        
        //percorro a lista de grupos encontrados
        for (PeerGroupModel peerGroup : lstPeerGroupModel){
            //seto o elemento no modelo
            cmdModel.addElement(peerGroup.getGroupName());
        }       
        
        //seto o modelo e o indice do item na combobox
        cbmGrupos.setModel(cmdModel);        
        cbmGrupos.setSelectedIndex(0);        
    } 
    
    private void preencherTabela(){
        limparTabela();
    
        TableColumnModel ColumnModel = tblServices.getColumnModel();    
        FShowServices.JTableRenderer renderer = new FShowServices.JTableRenderer();    
        ColumnModel.getColumn(0).setCellRenderer(renderer);
        
        DefaultTableModel defaultTableModel = (DefaultTableModel) tblServices.getModel();
        
        for (ServicesModelView serviceModel : lstServicesModelView){
                
            String parametros = "";
            for(ParamServiceModel paramServiceModel : serviceModel.getLstParameters()){
                parametros = parametros + paramServiceModel.getParamName() + "#";
            }         
            if(!parametros.equals("")){
                parametros = parametros.substring(0, parametros.lastIndexOf("#"));
            }
            
            String terms = "";
            for(DomainsRelations domainsRelations : serviceModel.getDomainsRelations()){
                terms = terms + domainsRelations.getDomainTermName() + "#";
            }            
            
            if(!terms.equals("")){
                terms = terms.substring(0, terms.lastIndexOf("#"));
            }
            
            ImageIcon content = (serviceModel.isConnector() ? (new ImageIcon(getClass().getResource("/images/port-icon16.png"))) : 
                                                            (new ImageIcon(getClass().getResource("/images/world-icon16.png"))));
            
            Object[] linha = {content, 
                              serviceModel.getServiceName(),                               
                              serviceModel.getOwlsName(),
                              parametros, 
                              terms,
                              serviceModel.getServiceRelated()
            };
            
            defaultTableModel.addRow(linha);
        }  
    }
    
    private void limparTabela(){
        
        DefaultTableModel defaultTableModel = (DefaultTableModel) tblServices.getModel();
                
        while (defaultTableModel.getRowCount() != 0){         
            defaultTableModel.removeRow(0);            
        }        
    }

    private mxICell getVertexByName(mxICell graph, String vertexName, String style){
        try{
            for(int i = 0; i < graph.getChildCount(); i++){
                
                mxICell cell = graph.getChildAt(i);
                
                if (cell.isVertex()){                    
                    if((cell.getValue().toString().trim().equals(vertexName)) && (cell.getStyle().equals(style))){
                        return cell;
                    }                    
                }
                
                if (cell.getChildCount() != 0){                
                    mxICell cellResult = getVertexByName(cell, vertexName, style);
                    if (cellResult != null){
                        return cellResult;
                    }
                }
            }
            
            return null;
        }catch(Exception e){
            return null;
        }        
    }
    
    private void getMappingGraph(){           
        this.graph.removeCells(this.graph.getChildVertices(this.graph.getDefaultParent()), true);
        this.insertPeerOntologyGraphics();
        this.insertDomainOntologyGraphics();
        this.insertOWLS();
        
        this.generateMapLink();
    }
    
    private void generateMapLink(){
        
        mxICell peerOWL = this.getVertexByName((mxICell) this.graph.getDefaultParent(), SemanticManager.GROUP_ONTOLOGY.replace(".owl", ""), "OWL");
        
        mxICell owls = this.getVertexByName((mxICell) this.graph.getDefaultParent(), 
                                             tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 1).toString().replace("Service", ""), "OWL");
        
        if ((owls == null) || (peerOWL == null)){
            JOptionPane.showMessageDialog(null, "Wasn't possible crate the general ontology linking.", ".: e-ScienceNet :. ", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        mxICell peerServiceClass = this.getVertexByName((mxICell) peerOWL, "Service", "Class");
        mxICell owlsServiceClass = this.getVertexByName((mxICell) owls, "Service", "Class");
        
        this.graph.insertEdge(this.graph.getDefaultParent(), null, "mapping", peerServiceClass, owlsServiceClass, 
                              "classEdge;" + mxConstants.STYLE_STROKECOLOR +"="+ mxUtils.getHexColorString(Color.BLUE) +
                              ";" + mxConstants.STYLE_STARTARROW + "=" + mxConstants.ARROW_CLASSIC+
                              ";" + mxConstants.STYLE_ORTHOGONAL + "=" + true);        
    } 
    
    private void insertPeerOntologyGraphics(){
        this.graph.getModel().beginUpdate();
        try
        {
            Object newVertex = this.graph.insertVertex(this.graph.getDefaultParent(), null, SemanticManager.GROUP_ONTOLOGY.replace(".owl", ""), 
                               80, 40, 300, 180, "OWL");
            
            this.insertClassInPeerOntologyGraph((mxCell) newVertex);
        }finally{
            this.graph.getModel().endUpdate();
        }
    }
    
    public void insertClassInPeerOntologyGraph(mxCell owlVertex){
        this.graph.getModel().beginUpdate();
        try
        {
            mxCell service = (mxCell) this.graph.insertVertex(owlVertex, null, "Service", 
                             (owlVertex.getGeometry().getWidth() / 2) + 70, 30, 50, 40, "Class");
            
            mxCell indService = (mxCell) this.graph.insertVertex(owlVertex, null, "  " + 
                                tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 1).toString().replace("Service", "") + "  ", 
                                10, 30, 0, 0,  "Individual");
            
            mxCell group = (mxCell) this.graph.insertVertex(owlVertex, null, "Group", 
                           service.getGeometry().getX(),
                           service.getGeometry().getY() + service.getGeometry().getHeight() + 50, 50, 40, "Class");
            
            mxCell indGroup = (mxCell) this.graph.insertVertex(owlVertex, null, "  " + cbmGrupos.getSelectedItem().toString() + "  ", 
                              indService.getGeometry().getX(),
                              group.getGeometry().getY(), 0, 0,  "Individual");
            
            this.graph.insertEdge(owlVertex, null, "hasService", group, service, "classEdge");
            this.graph.insertEdge(owlVertex, null, "hasIndividual", group, indGroup, "individualEdge");
            this.graph.insertEdge(owlVertex, null, "hasIndividual", service, indService, "individualEdge");
            
            this.graph.updateCellSize(indService);
            this.graph.updateCellSize(indGroup);
        }
        finally
        {
            this.graph.getModel().endUpdate();
        }
    }
    
    private void insertOWLS(){
                
        mxICell vertex = this.getVertexByName((mxICell) this.graph.getDefaultParent(), SemanticManager.GROUP_ONTOLOGY.replace(".owl", ""), "OWL");
        
        if (vertex != null){
          
            mxCell mappingVertex = (mxCell) this.graph.insertVertex(this.graph.getDefaultParent(), null, 
                               tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 1).toString().replace("Service", ""), 
                               vertex.getGeometry().getX() + vertex.getGeometry().getWidth() + 250, 
                               vertex.getGeometry().getY(), 300, 200, "OWL");            
            
            mxCell service = (mxCell) this.graph.insertVertex(mappingVertex, null, "Service", 20, 30, 50, 40, "Class");            
            
            mxCell indService = (mxCell) this.graph.insertVertex(mappingVertex, null,  tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 1).toString(), 
                                 mappingVertex.getGeometry().getHeight() - 10, 30, 50, 40, "Individual");
            
            mxCell profile = (mxCell) this.graph.insertVertex(mappingVertex, null, "Profle", mappingVertex.getGeometry().getHeight() - 40, 
                                                              service.getGeometry().getY() + 100, 50, 40, "Class");
            
            this.graph.insertEdge(mappingVertex, null, "presents", service, profile, "classEdge;" + mxConstants.STYLE_EDGE+"="+mxEdgeStyle.EntityRelation);    
            
            this.graph.insertEdge(mappingVertex, null, "hasIndividual", service, indService, "individualEdge");
            this.graph.updateCellSize(indService);
            
            double posY = mappingVertex.getGeometry().getY() + 60;
            ServicesModelView servicesModelView =  this.getServiceByName(tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 1).toString());
            for(ParamServiceModel paramServiceModel : servicesModelView.getLstParameters()){
               
                mxCell indParam = (mxCell) this.graph.insertVertex(this.graph.getDefaultParent(), null,  paramServiceModel.getParamName(), 
                                                                   mappingVertex.getGeometry().getX() - 150, posY, 50, 40, "Individual");
            
                this.graph.updateCellSize(indParam);
                this.graph.insertEdge(this.graph.getDefaultParent(), null, "hasIndividual", profile, indParam, "individualEdge");
             
                if(paramServiceModel.getParamType() != null){
                    mxICell vertexTerm = this.getVertexByName((mxICell) this.graph.getDefaultParent(), paramServiceModel.getParamType().getDomainTermName(), "Class");

                    this.graph.insertEdge(this.graph.getDefaultParent(), null, "mappingDomain", indParam, vertexTerm, 
                                          "classEdge;" + mxConstants.STYLE_STARTARROW + "=" + mxConstants.ARROW_CLASSIC);
                }
                posY = posY + indParam.getGeometry().getHeight() + 10;                
            }
        }
    }
    
    private void insertDomainOntologyGraphics(){
        
        mxICell vertex = this.getVertexByName((mxICell) this.graph.getDefaultParent(), SemanticManager.GROUP_ONTOLOGY.replace(".owl", ""), "OWL");
        
        List<String> domainsOwl = new ArrayList<>();
        List<String> domainsTerm = new ArrayList<>();
        
        if (vertex != null){
          
            ServicesModelView servicesModelView =  this.getServiceByName(tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 1).toString());
            
            if(servicesModelView != null){
                
                double posY = vertex.getGeometry().getY() + vertex.getGeometry().getHeight() + 50;
                double posXClass = 0;
                
                for(DomainsRelations domainsRelations : servicesModelView.getDomainsRelations()){
                    
                    if(domainsOwl.indexOf(domainsRelations.getDomainOntology()) == -1){                    
                        mxCell domain = (mxCell) this.graph.insertVertex(this.graph.getDefaultParent(), null, domainsRelations.getDomainOntology(), 
                                        vertex.getGeometry().getX() + 30, posY, 280, 80, "OWL");

                        domainsOwl.add(domainsRelations.getDomainOntology());
                        
                        if(domainsTerm.indexOf(domainsRelations.getDomainTermName()) == -1){
                            domainsTerm.add(domainsRelations.getDomainTermName());
                        
                            mxCell domainTerm = (mxCell) this.graph.insertVertex(domain, null, domainsRelations.getDomainTermName(), 
                                                posXClass + 10, 
                                               (domain.getGeometry().getHeight() / 4) + 10, 50, 40, "Class");

                            this.graph.updateCellSize(domainTerm);
                        }
                        
                        
                        posY = posY + domain.getGeometry().getHeight() + 10;
                    }else{
                        mxICell domain = this.getVertexByName((mxICell) this.graph.getDefaultParent(), domainsRelations.getDomainOntology(), "OWL");
                        
                        if(domainsTerm.indexOf(domainsRelations.getDomainTermName()) == -1){
                            domainsTerm.add(domainsRelations.getDomainTermName());
                            
                            mxCell domainTerm = (mxCell) this.graph.insertVertex(domain, null, domainsRelations.getDomainTermName(), 
                                               (domain.getChildCount() * 50) + 40, 
                                               (domain.getGeometry().getHeight() / 4) + 10, 50, 40, "Class");

                            this.graph.updateCellSize(domainTerm);
                        }
                    }
                }
            }
        }
    }
    
    private ServicesModelView getServiceByName(String serviceName){
        
        for(ServicesModelView servicesModelView : lstServicesModelView){
            
            if(servicesModelView.getServiceName().equals(serviceName)){
                return servicesModelView;
            }            
        }
        
        return null;
    }
    
    private void gravarImagem(){
        try{                          
            saveDialog.setSelectedFile(new File(saveDialog.getCurrentDirectory().toString() + File.separator + "Mapping_" +
                                                tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 1).toString() + ".png")); 

            if (saveDialog.showSaveDialog(null) == 0){
                BufferedImage image = mxCellRenderer.createBufferedImage(this.graph, null, 1, null, this.graphComponent.isAntiAlias(), 
                                                                         null, this.graphComponent.getCanvas());            

                mxCodec codec = new mxCodec();
                String xml = URLEncoder.encode(mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
                mxPngEncodeParam param = mxPngEncodeParam.getDefaultEncodeParam(image);
                param.setCompressedText(new String[] { "mxGraphModel", xml });

                FileOutputStream outputStream = new FileOutputStream(saveDialog.getSelectedFile());
                mxPngImageEncoder encoder = new mxPngImageEncoder(outputStream, param);

                if (image != null){
                    encoder.encode(image);
                }else{                
                    JOptionPane.showMessageDialog(graphComponent, mxResources.get("noImageData"));
                    return;
                }
                
                JOptionPane.showMessageDialog(null, "The graphic was generated with success!", ".: e-ScienceNet :.", JOptionPane.INFORMATION_MESSAGE);
            }
        }catch (IOException | HeadlessException e) {
            Logger.getLogger(FShowServices.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        saveDialog = new javax.swing.JFileChooser();
        ppmMapping = new javax.swing.JPopupMenu();
        actDeleteMapping = new javax.swing.JMenuItem();
        pnlFooter = new javax.swing.JPanel();
        btnFechar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        pnlMain = new javax.swing.JPanel();
        pnlFiltro = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbmGrupos = new javax.swing.JComboBox();
        cbkShowConnectors = new javax.swing.JCheckBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblServices = new javax.swing.JTable();
        pnlDiagrama = new javax.swing.JPanel();
        tbrGraph = new javax.swing.JToolBar();
        btnImagem = new javax.swing.JButton();

        saveDialog.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        actDeleteMapping.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/trash-icon.png"))); // NOI18N
        actDeleteMapping.setText("Delete Mapping");
        actDeleteMapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actDeleteMappingActionPerformed(evt);
            }
        });
        ppmMapping.add(actDeleteMapping);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Services");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
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

        javax.swing.GroupLayout pnlFooterLayout = new javax.swing.GroupLayout(pnlFooter);
        pnlFooter.setLayout(pnlFooterLayout);
        pnlFooterLayout.setHorizontalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(btnExcluir))
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

        cbkShowConnectors.setText("Show connectors");
        cbkShowConnectors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbkShowConnectorsActionPerformed(evt);
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbkShowConnectors)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlFiltroLayout.setVerticalGroup(
            pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFiltroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cbmGrupos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbkShowConnectors))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblServices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Service Name", "OWLS File", "Parameters", "Domains Relationed", "Service Related"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblServices.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblServices.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblServicesMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblServices);
        tblServices.getColumnModel().getColumn(0).setResizable(false);
        tblServices.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblServices.getColumnModel().getColumn(1).setResizable(false);
        tblServices.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblServices.getColumnModel().getColumn(2).setResizable(false);
        tblServices.getColumnModel().getColumn(2).setPreferredWidth(275);
        tblServices.getColumnModel().getColumn(3).setResizable(false);
        tblServices.getColumnModel().getColumn(3).setPreferredWidth(250);
        tblServices.getColumnModel().getColumn(4).setResizable(false);
        tblServices.getColumnModel().getColumn(4).setPreferredWidth(250);
        tblServices.getColumnModel().getColumn(5).setResizable(false);
        tblServices.getColumnModel().getColumn(5).setPreferredWidth(200);

        pnlDiagrama.setBorder(javax.swing.BorderFactory.createTitledBorder(" Mapping Graph "));

        javax.swing.GroupLayout pnlDiagramaLayout = new javax.swing.GroupLayout(pnlDiagrama);
        pnlDiagrama.setLayout(pnlDiagramaLayout);
        pnlDiagramaLayout.setHorizontalGroup(
            pnlDiagramaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1046, Short.MAX_VALUE)
        );
        pnlDiagramaLayout.setVerticalGroup(
            pnlDiagramaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 423, Short.MAX_VALUE)
        );

        tbrGraph.setRollover(true);

        btnImagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/images.png"))); // NOI18N
        btnImagem.setText("Save Graphic");
        btnImagem.setFocusable(false);
        btnImagem.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnImagem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImagemActionPerformed(evt);
            }
        });
        tbrGraph.add(btnImagem);

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFiltro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1058, Short.MAX_VALUE)
            .addComponent(pnlDiagrama, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tbrGraph, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addComponent(pnlFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbrGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDiagrama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        getAccessibleContext().setAccessibleName("frmShowServices");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnFecharActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        
        if (tblServices.getSelectedRow() != -1){
            
            if (JOptionPane.showConfirmDialog(null, "Do you really want to remove the Service " + tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 1), 
                                              ".: e-ScienceNet :.", 
                                              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                if(tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 5).toString().equals("")){
                    
                    if(SemanticManager.getInstance().deleteAllConnector(tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 1).toString())){
                        
                        if(SemanticManager.getInstance().removerServico(tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 1).toString())){
                            JOptionPane.showMessageDialog(null, "The service and all its connectors were eraser of PeerOntology.", ".: e-ScienceNet :.", JOptionPane.INFORMATION_MESSAGE);

                            this.cbmGruposActionPerformed(null);
                            graph.removeCells(this.graph.getChildVertices(this.graph.getDefaultParent()), true);
                        }
                    }
                }else{
                    if(SemanticManager.getInstance().removerServico(tblServices.getModel().getValueAt(tblServices.getSelectedRow(), 1).toString())){
                        JOptionPane.showMessageDialog(null, "The connector was eraser of PeerOntology.", ".: e-ScienceNet :.", JOptionPane.INFORMATION_MESSAGE);

                        this.cbmGruposActionPerformed(null);
                        graph.removeCells(this.graph.getChildVertices(this.graph.getDefaultParent()), true);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void cbmGruposItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbmGruposItemStateChanged

    }//GEN-LAST:event_cbmGruposItemStateChanged

    private void cbmGruposActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmGruposActionPerformed
        
        lstServicesModelView = SemanticManager.getInstance().searchServiceInfoInPeerOWL(cbmGrupos.getSelectedItem().toString(), cbkShowConnectors.isSelected());
        
        this.preencherTabela();
        
    }//GEN-LAST:event_cbmGruposActionPerformed

    private void cbmGruposPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cbmGruposPropertyChange

    }//GEN-LAST:event_cbmGruposPropertyChange

    private void tblServicesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblServicesMouseReleased
        if ((tblServices.getSelectedRow() >= 0) && (evt.isPopupTrigger())){
            ppmMapping.show(tblServices, evt.getX(), evt.getY());
        }
        
        this.getMappingGraph();
        
        graphComponent.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        graphComponent.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        graphComponent.refresh();
    }//GEN-LAST:event_tblServicesMouseReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
                
        PageFormat format = new PageFormat();
        format.setOrientation(PageFormat.LANDSCAPE);         
        Paper paper = new Paper();
        paper.setSize(pnlDiagrama.getBounds().getHeight(), pnlDiagrama.getBounds().getWidth());
        format.setPaper(paper);
        this.graphComponent.setPageFormat(format);
        
        this.graphComponent.setPageVisible(true);
        this.graphComponent.setAntiAlias(true);
        this.graphComponent.setGridVisible(true);
        this.graphComponent.setConnectable(false);
        this.graphComponent.getViewport().setOpaque(true);
        this.graphComponent.getViewport().setBackground(Color.WHITE);
    }//GEN-LAST:event_formWindowOpened

    private void btnImagemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImagemActionPerformed
        this.gravarImagem();
    }//GEN-LAST:event_btnImagemActionPerformed

    private void actDeleteMappingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actDeleteMappingActionPerformed
        this.btnExcluirActionPerformed(null);
    }//GEN-LAST:event_actDeleteMappingActionPerformed

    private void cbkShowConnectorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbkShowConnectorsActionPerformed
        cbmGruposActionPerformed(null);
    }//GEN-LAST:event_cbkShowConnectorsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem actDeleteMapping;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnImagem;
    private javax.swing.JCheckBox cbkShowConnectors;
    private javax.swing.JComboBox cbmGrupos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel pnlDiagrama;
    private javax.swing.JPanel pnlFiltro;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPopupMenu ppmMapping;
    private javax.swing.JFileChooser saveDialog;
    private javax.swing.JTable tblServices;
    private javax.swing.JToolBar tbrGraph;
    // End of variables declaration//GEN-END:variables

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