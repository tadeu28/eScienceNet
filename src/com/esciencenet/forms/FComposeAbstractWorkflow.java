/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.forms;

import com.esciencenet.models.AbstractWorkflowsCreationModel;
import com.esciencenet.models.DomainFileModel;
import com.esciencenet.models.OWLClassesModel;
import com.esciencenet.models.WorkflowABSModel;
import static com.esciencenet.models.WorkflowABSModel.unselectAll;
import com.esciencenet.semanticmanager.SemanticManager;
import com.esciencenet.semanticmanager.SemanticRestrictionEnum;
import com.esciencenet.utils.JTreeOWLRenderer;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.util.png.mxPngEncodeParam;
import com.mxgraph.util.png.mxPngImageEncoder;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;
import com.mxgraph.view.mxStylesheet;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.geom.Point2D;
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
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Tadeu
 */
public class FComposeAbstractWorkflow extends javax.swing.JDialog {

    private mxGraph graph;
    private mxGraphComponent graphComponent;
    private List<DomainFileModel> lstDomainFileModel;
    private List<OWLClassesModel> lstOWLClassesModels;
    private List<OWLClassesModel> lstOWLClassesRages;
    
    private Point2D startPosition;
    private mxICell prevNode = null;
    private int transitionID; 
    private WorkflowABSModel workflowABSModel = new WorkflowABSModel();
    private WorkflowABSModel taskJoinSelection;
    
    /**
     * Creates new form FComposeAbstractWorkflow
     */
    public FComposeAbstractWorkflow(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        lstDomainFileModel = SemanticManager.getInstance().obterDomainFiles(
                             SemanticManager.getInstance().getInterestManager().getGrupoSelecionado().getGroupName());
        
        this.preecherComboDomain(cmbDomainOntology);
        
        lstOWLClassesRages = new ArrayList<>();
        
        cmbRelations.setModel(new DefaultComboBoxModel(SemanticRestrictionEnum.values()));
        cmbRelations.setSelectedItem(SemanticRestrictionEnum.some);
        
        treeOWL.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }

    private void createStyleForStart(){
        mxStylesheet stylesheet = graph.getStylesheet();
        HashMap<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        style.put(mxConstants.STYLE_FONTSIZE, 10);
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        stylesheet.putCellStyle("Start", style);   
    }
    
    private void createStyleForTransition(){
        mxStylesheet stylesheet = graph.getStylesheet();
        HashMap<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        style.put(mxConstants.STYLE_FONTSIZE, 10);
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        stylesheet.putCellStyle("Trans", style);   
    }
    
    private void createStyleForArrow(){
        mxStylesheet stylesheet = graph.getStylesheet();
        HashMap<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Color.BLACK));        
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        style.put(mxConstants.STYLE_STROKEWIDTH, 2);        
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Color.BLACK));
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        stylesheet.putCellStyle("Arrow", style);         
    }
    
    private void preecherComboDomain(JComboBox comboBox){
        
        //crio o modelo de items da combobox
        DefaultComboBoxModel cmdModel = new DefaultComboBoxModel();        
        
        //percorro a lista de grupos encontrados
        for (DomainFileModel domainFileModel : lstDomainFileModel){
            //seto o elemento no modelo
            cmdModel.addElement(domainFileModel.getDomainName());
        }       
        
        //seto o modelo e o indice do item na combobox
        comboBox.setModel(cmdModel);               
        comboBox.setSelectedIndex(0);             
    }
    
    private void manualOntologyProcess(List<OWLClassesModel> lstClassModel, JTree tree){
        tree.setModel(null);
           
        DefaultMutableTreeNode rootModel = new DefaultMutableTreeNode("Nenhum");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootModel);

        this.percorrerOWLClasses(lstClassModel, rootModel);

        treeModel.setRoot(rootModel);            
        tree.setModel(treeModel);
        this.expandAllTreeNodes(tree);
        tree.setCellRenderer(new JTreeOWLRenderer());
    }
    
    private void expandAllTreeNodes(JTree tree){
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        } 
    }
    
    private void percorrerOWLClasses(List<OWLClassesModel> lstOWLClassesModels, DefaultMutableTreeNode parentNode){
        
        for(OWLClassesModel owlClassesModel : lstOWLClassesModels){
                        
            DefaultMutableTreeNode arquivo = new DefaultMutableTreeNode(owlClassesModel.getName());   
            parentNode.add(arquivo);            
            
            percorrerOWLClasses(owlClassesModel.getSubClasses(), arquivo);
        }        
    }
    
    private DomainFileModel getOWLDomainByName(String domainName){
        
        for (DomainFileModel domainFileModel : lstDomainFileModel){
            
            if (domainFileModel.getDomainName().equals(domainName)){
                return domainFileModel;
            }
        }
        
        return null;
    }
    
    private void limparTabela(JTable tabela){
        
        DefaultTableModel defaultTableModel = (DefaultTableModel) tabela.getModel();
                
        while (defaultTableModel.getRowCount() != 0){         
            defaultTableModel.removeRow(0);            
        }        
    }
    
    private void limparComboBox(JComboBox comboBox){
        
        DefaultComboBoxModel defaultComboBoxModel = (DefaultComboBoxModel) comboBox.getModel();
                
        while (defaultComboBoxModel.getSize() != 0){         
            defaultComboBoxModel.removeElementAt(0);            
        }        
    }
    
    private void criarExibicaoGrafica(){       
       
        this.graph = new mxGraph(){            
            @Override
            public boolean isCellFoldable(Object cell, boolean collapse)
            {
                return false;
            }
            @Override
            public boolean isCellResizable(Object cell)
            {
                return false;
            }
            @Override
            public String getToolTipForCell(Object cell){
                
                mxICell taks = (mxICell) cell;                
                
                if(taks.getValue() != null){
                    WorkflowABSModel workflowABSModel = WorkflowABSModel.getObjectByCode(FComposeAbstractWorkflow.this.workflowABSModel, taks.getValue().toString());

                    if(workflowABSModel != null){
                        String tasks = FComposeAbstractWorkflow.this.getTaskInformations(workflowABSModel.getTaskName());

                        if(!tasks.equals("")){
                            tasks = "Possibles Next Tasks: " + tasks.trim().substring(0, tasks.lastIndexOf("|"));
                        }

                        String information = "Task: " + workflowABSModel.getTaskName() + " - " + (tasks.isEmpty() ? "Finish" : tasks);

                        return information;
                    }
                }
                
                return "";                
            }
        };
        
        this.graph.setCellsEditable(false);
        this.graph.setCellsResizable(false);        
                
        this.graphComponent = new mxGraphComponent(this.graph);         
                
        pnlAbstractWF.add(this.graphComponent);

        Double width = pnlAbstractWF.getBounds().getWidth();
        Double height = pnlAbstractWF.getBounds().getHeight();
        this.graphComponent.setBounds(0, 0, width.intValue(), height.intValue());
        
        //this.graphComponent.setBorder(pnlAbstractWF.getBorder());

        pnlAbstractWF.revalidate();
        pnlAbstractWF.repaint();
    }
    
    private WorkflowABSModel processTaskToJoin(){
    
        WorkflowABSModel selectedTask = WorkflowABSModel.getSelecionado(this.workflowABSModel);
        WorkflowABSModel tasksToShow = new WorkflowABSModel();
        tasksToShow.setTaskCode("?");
        tasksToShow.setTaskName("TaskToShow");
        
        String tasks = this.getTaskInformations(selectedTask.getTaskName());

        if(!tasks.equals("")){
            tasks = tasks.trim().substring(0, tasks.lastIndexOf("|"));
            StringTokenizer strTasks = new StringTokenizer(tasks, "|");
            while (strTasks.hasMoreTokens()){
                
                String strTask = strTasks.nextToken().trim();
                
                mxICell cellParent = (mxICell) this.graph.getDefaultParent();                
                
                for(int i = 0; i < cellParent.getChildCount(); i++){

                    mxICell child = cellParent.getChildAt(i);

                    if(child.getValue() != null){
                        WorkflowABSModel taskWF = WorkflowABSModel.getObjectByCode(workflowABSModel, child.getValue().toString());
                        if(taskWF != null){
                            if(taskWF.getTaskName().equals(strTask)){
                                tasksToShow.getTaskChild().add(taskWF);
                            }                  
                        }
                    }
                }
                
                WorkflowABSModel taskWF = WorkflowABSModel.getObjectByTaskName(workflowABSModel, strTask);
                if(taskWF == null){
                    taskWF = new WorkflowABSModel();
                    taskWF.setTaskName(strTask);
                    tasksToShow.getTaskChild().add(taskWF);
                }                
            }
        }
        
        if(tasksToShow.getTaskChild().isEmpty()){
            return null;
        }else{            
            List<WorkflowABSModel> lstRemovibles = new ArrayList<>();
            
            for(WorkflowABSModel taskExistent : tasksToShow.getTaskChild()){
                boolean notExistent = false;
                
                for(int i = 0; i < tblClassesWF.getRowCount(); i++){
                    String taskName = tblClassesWF.getModel().getValueAt(i, 0).toString();
                    
                    if(taskName.equals(taskExistent.getTaskName())){
                        notExistent = true;
                        break;
                    }
                }
                
                if(!notExistent){
                    lstRemovibles.add(taskExistent);
                }
            }
            
            for(WorkflowABSModel taks : lstRemovibles){
                tasksToShow.getTaskChild().remove(taks);
            }
            
            /*boolean stop = false;
            int index = 0;
            while (!stop){
                try{
                    WorkflowABSModel taks = tasksToShow.getTaskChild().get(index);
                    index++;
                    
                    if(selectedTask.getTaskCode().equals(taks.getTaskCode())){
                        index = 0;
                        tasksToShow.getTaskChild().remove(taks);                    
                    }
                }catch(Exception e){
                    stop = true;
                }                
            } */       
            
            return tasksToShow;
        }
    }
    
    private int checkElementJTable(JTable table, String cell){
        
        for(int i = 0; i < table.getRowCount(); i++){
            if(table.getModel().getValueAt(i, 0).toString().equals(cell)){
                return i;
            }
        }
        
        return -1;
    }
    
        private void gravarImagem(){
        try{                          
            saveDialog.setSelectedFile(new File(saveDialog.getCurrentDirectory().toString() + File.separator + "AbatractWF.png")); 

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
                
                outputStream.close();
                
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
        pnlFooter = new javax.swing.JPanel();
        btnFechar = new javax.swing.JButton();
        lblSelectedCell = new javax.swing.JLabel();
        pnlDomain = new javax.swing.JPanel();
        cmbDomainOntology = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        btnLoadDomain = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        treeOWL = new javax.swing.JTree();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblObjProperties = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        treeOWLRanges = new javax.swing.JTree();
        cmbRelations = new javax.swing.JComboBox();
        btnContinueSeq = new javax.swing.JButton();
        btnAddClass = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblClassesWF = new javax.swing.JTable();
        btnRemoverClass = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        cmbTaskProperty = new javax.swing.JComboBox();
        pnlAbstractWF = new javax.swing.JPanel();
        btnAddTask = new javax.swing.JButton();
        cmbPetriOp = new javax.swing.JComboBox();
        btnCloseWF = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        btnRemoveNode = new javax.swing.JButton();
        btnFechar1 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnSalvarRecurso = new javax.swing.JButton();
        btnOpenGraph = new javax.swing.JButton();

        saveDialog.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Abstract Workflow Composition");
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

        lblSelectedCell.setForeground(new java.awt.Color(255, 255, 255));
        lblSelectedCell.setText("Task Selected:");

        javax.swing.GroupLayout pnlFooterLayout = new javax.swing.GroupLayout(pnlFooter);
        pnlFooter.setLayout(pnlFooterLayout);
        pnlFooterLayout.setHorizontalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSelectedCell)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFechar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlFooterLayout.setVerticalGroup(
            pnlFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlFooterLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnFechar)
                .addContainerGap())
            .addGroup(pnlFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSelectedCell)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDomain.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cmbDomainOntology.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDomainOntologyActionPerformed(evt);
            }
        });

        jLabel1.setText("Domain Ontology");

        btnLoadDomain.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/widgets-icon.png"))); // NOI18N
        btnLoadDomain.setText("Load Domain Ontology");
        btnLoadDomain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadDomainActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDomainLayout = new javax.swing.GroupLayout(pnlDomain);
        pnlDomain.setLayout(pnlDomainLayout);
        pnlDomainLayout.setHorizontalGroup(
            pnlDomainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDomainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbDomainOntology, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLoadDomain)
                .addContainerGap())
        );
        pnlDomainLayout.setVerticalGroup(
            pnlDomainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDomainLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDomainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbDomainOntology, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoadDomain))
                .addGap(409, 409, 409))
        );

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treeOWL.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeOWL.setRootVisible(false);
        treeOWL.setShowsRootHandles(true);
        treeOWL.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeOWLValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(treeOWL);

        jLabel2.setText("OWL Class");

        jLabel3.setText("Objetct Properties");

        tblObjProperties.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Object Property"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblObjProperties.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblObjProperties.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblObjPropertiesMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblObjProperties);
        tblObjProperties.getColumnModel().getColumn(0).setResizable(false);
        tblObjProperties.getColumnModel().getColumn(0).setPreferredWidth(350);

        jLabel4.setText("OWL Class Ranges");

        treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        treeOWLRanges.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeOWLRanges.setRootVisible(false);
        treeOWLRanges.setShowsRootHandles(true);
        treeOWLRanges.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeOWLRangesValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(treeOWLRanges);

        btnContinueSeq.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/arrow-undo-icon.png"))); // NOI18N
        btnContinueSeq.setText("Continue class sequence");
        btnContinueSeq.setEnabled(false);
        btnContinueSeq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinueSeqActionPerformed(evt);
            }
        });

        btnAddClass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/basket-put-icon.png"))); // NOI18N
        btnAddClass.setText("Insert in Previous WF");
        btnAddClass.setToolTipText("");
        btnAddClass.setEnabled(false);
        btnAddClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddClassActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel5.setText("Selected Classes to WF");

        tblClassesWF.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Classes"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblClassesWF.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblClassesWF.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblClassesWF.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblClassesWFMouseReleased(evt);
            }
        });
        tblClassesWF.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                tblClassesWFMouseMoved(evt);
            }
        });
        jScrollPane2.setViewportView(tblClassesWF);
        tblClassesWF.getColumnModel().getColumn(0).setResizable(false);
        tblClassesWF.getColumnModel().getColumn(0).setPreferredWidth(350);

        btnRemoverClass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Actions-remove-icon.png"))); // NOI18N
        btnRemoverClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverClassActionPerformed(evt);
            }
        });

        jLabel6.setText("Select related task property");

        pnlAbstractWF.setBorder(javax.swing.BorderFactory.createTitledBorder(" Abstract Workflow "));

        javax.swing.GroupLayout pnlAbstractWFLayout = new javax.swing.GroupLayout(pnlAbstractWF);
        pnlAbstractWF.setLayout(pnlAbstractWFLayout);
        pnlAbstractWFLayout.setHorizontalGroup(
            pnlAbstractWFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 795, Short.MAX_VALUE)
        );
        pnlAbstractWFLayout.setVerticalGroup(
            pnlAbstractWFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        btnAddTask.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/chart-line-add-icon.png"))); // NOI18N
        btnAddTask.setText("Insert task in WF");
        btnAddTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTaskActionPerformed(evt);
            }
        });

        cmbPetriOp.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SEQUENCE", "AND-JOIN", "AND-SPLIT", "OR-JOIN", "OR-SPLIT" }));
        cmbPetriOp.setToolTipText("");
        cmbPetriOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbPetriOpActionPerformed(evt);
            }
        });

        btnCloseWF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lock-icon.png"))); // NOI18N
        btnCloseWF.setText("Close WF");
        btnCloseWF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseWFActionPerformed(evt);
            }
        });

        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        btnRemoveNode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Actions-remove-icon.png"))); // NOI18N
        btnRemoveNode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveNodeActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveNode);

        btnFechar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/limpar.png"))); // NOI18N
        btnFechar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFechar1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnFechar1);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/images.png"))); // NOI18N
        jButton1.setToolTipText("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        btnSalvarRecurso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-as-icon.png"))); // NOI18N
        btnSalvarRecurso.setFocusable(false);
        btnSalvarRecurso.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSalvarRecurso.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSalvarRecurso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarRecursoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSalvarRecurso);

        btnOpenGraph.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/chart-line-edit-icon.png"))); // NOI18N
        btnOpenGraph.setFocusable(false);
        btnOpenGraph.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenGraph.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenGraphActionPerformed(evt);
            }
        });
        jToolBar1.add(btnOpenGraph);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDomain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlFooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(322, 322, 322)
                        .addComponent(jLabel3)
                        .addGap(110, 110, 110)
                        .addComponent(cmbRelations, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnContinueSeq, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(btnAddClass))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(cmbPetriOp, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbTaskProperty, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRemoverClass, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnAddTask, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCloseWF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlAbstractWF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlDomain, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel3))
                    .addComponent(cmbRelations, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel4)))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnContinueSeq)
                            .addComponent(btnAddClass))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbTaskProperty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(jLabel5)
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemoverClass))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbPetriOp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAddTask)
                            .addComponent(btnCloseWF))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAbstractWF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getAccessibleContext().setAccessibleName("");

        setSize(new java.awt.Dimension(1138, 788));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnFecharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFecharActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnFecharActionPerformed

    private void cmbDomainOntologyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDomainOntologyActionPerformed

    }//GEN-LAST:event_cmbDomainOntologyActionPerformed

    private void btnLoadDomainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadDomainActionPerformed
        lstOWLClassesModels = SemanticManager.getInstance().searchInFullInferredDomain(getOWLDomainByName(cmbDomainOntology.getSelectedItem().toString()));
        this.manualOntologyProcess(lstOWLClassesModels, treeOWL);
    }//GEN-LAST:event_btnLoadDomainActionPerformed

    private void treeOWLValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeOWLValueChanged
        
        if (!treeOWL.getSelectionModel().isSelectionEmpty()){
            DomainFileModel domainFileModel = this.getOWLDomainByName(cmbDomainOntology.getSelectedItem().toString());

            String owlClassName = treeOWL.getSelectionPath().getLastPathComponent().toString();
            
            List<String> lstPropriedades = SemanticManager.getInstance().getObjectPropertyByClass(owlClassName, domainFileModel.getDomainOWLFile());
            
            if(lstPropriedades != null){
                
                this.limparTabela(tblObjProperties);
                treeOWLRanges.setModel(null);
                btnContinueSeq.setEnabled(false);
                btnAddClass.setEnabled(false);
                
                DefaultTableModel model = (DefaultTableModel) tblObjProperties.getModel();
                
                for(String porperty : lstPropriedades){                   
                    Object[] linha = {porperty};
                    model.addRow(linha);
                }  
                
                tblObjProperties.setModel(model);
            }
        }
    }//GEN-LAST:event_treeOWLValueChanged

    private void tblObjPropertiesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblObjPropertiesMouseReleased
        
        if((tblObjProperties.getSelectedRow() > -1) || (!treeOWL.getSelectionModel().isSelectionEmpty())){
            
            DomainFileModel domainFileModel = this.getOWLDomainByName(cmbDomainOntology.getSelectedItem().toString());

            String owlClassName = treeOWL.getSelectionPath().getLastPathComponent().toString();
            
            String propertyName = tblObjProperties.getModel().getValueAt(tblObjProperties.getSelectedRow() , 0).toString();
            
            lstOWLClassesRages.clear();
            OWLClassesModel owlClassesModel = new OWLClassesModel();
            owlClassesModel.setName("owlThing");
            lstOWLClassesRages.add(owlClassesModel);
            
            SemanticManager.getInstance().getRangeClasseByObjProperty(owlClassName, propertyName, domainFileModel.getDomainOWLFile(), 
                                                                      owlClassesModel.getSubClasses(), SemanticRestrictionEnum.getValue(cmbRelations.getSelectedItem().toString()));
            
            if(!owlClassesModel.getSubClasses().isEmpty()){
                this.manualOntologyProcess(lstOWLClassesRages, treeOWLRanges);                
            }else{
                treeOWLRanges.setModel(null);
            }
        }
    }//GEN-LAST:event_tblObjPropertiesMouseReleased

    private void treeOWLRangesValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeOWLRangesValueChanged
        
        if(! treeOWLRanges.getSelectionModel().isSelectionEmpty()){
            btnContinueSeq.setEnabled(true);
            btnAddClass.setEnabled(true);
        }
    }//GEN-LAST:event_treeOWLRangesValueChanged

    private void btnContinueSeqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinueSeqActionPerformed
        
        treeOWL.setModel(treeOWLRanges.getModel());
        this.limparTabela(tblObjProperties);
        treeOWLRanges.setModel(null);
        btnAddClass.setEnabled(false);
        btnContinueSeq.setEnabled(false);
        this.expandAllTreeNodes(treeOWL);
    }//GEN-LAST:event_btnContinueSeqActionPerformed

    private void btnAddClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddClassActionPerformed
        
        if(! treeOWLRanges.getSelectionModel().isSelectionEmpty()){
            
            for(TreePath selected : treeOWLRanges.getSelectionModel().getSelectionPaths()){
                
                DefaultTableModel model = (DefaultTableModel) tblClassesWF.getModel();
                
                if(this.checkElementJTable(tblClassesWF, selected.getLastPathComponent().toString()) == -1){

                    Object[] linha = {selected.getLastPathComponent().toString()};

                    model.addRow(linha);

                    DomainFileModel domainFileModel = this.getOWLDomainByName(cmbDomainOntology.getSelectedItem().toString());

                    String owlClassName = selected.getLastPathComponent().toString();

                    List<String> lstPropriedades = SemanticManager.getInstance().getObjectPropertyByClass(owlClassName, domainFileModel.getDomainOWLFile());

                    for(String property : lstPropriedades){

                        DefaultComboBoxModel comboModel = (DefaultComboBoxModel) cmbTaskProperty.getModel();

                        if(comboModel.getIndexOf(property) == -1){
                            comboModel.addElement(property);
                            cmbTaskProperty.setModel(comboModel);
                        }
                    }
                }
                
                tblClassesWF.setModel(model);
            }

        }        
    }//GEN-LAST:event_btnAddClassActionPerformed

    private void tblClassesWFMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClassesWFMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tblClassesWFMouseReleased

    private void btnRemoverClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverClassActionPerformed
        if(tblClassesWF.getSelectedRow() != -1){            
            DefaultTableModel model = (DefaultTableModel) tblClassesWF.getModel();
            model.removeRow(tblClassesWF.getSelectedRow());
            tblClassesWF.setModel(model);
        }
    }//GEN-LAST:event_btnRemoverClassActionPerformed

    private void btnAddTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTaskActionPerformed
         
        if(tblClassesWF.getSelectedRow() > -1){
            mxICell grafico =  (mxICell) this.graph.getDefaultParent();
            mxICell startCell = this.getVertexByName(grafico, "", "Start");
            
            String owlClassName = tblClassesWF.getModel().getValueAt(tblClassesWF.getSelectedRow(), 0).toString();
            
            WorkflowABSModel selectedTask = WorkflowABSModel.getSelecionado(this.workflowABSModel);
            
            if(selectedTask != null){
                if(!cmbPetriOp.getSelectedItem().toString().contains("JOIN")){
                    if(!selectedTask.getTaskName().equals("")){
                        if(!checkPossibleTaks(owlClassName)){
                            return;
                        }
                    }   
                }                
                
                mxICell petriCell = this.createPetriOp(selectedTask.getCell());     

                if(petriCell == null){
                    return;
                }

                selectedTask.getPetriCells().add(petriCell);                 
            }else{            
                if(grafico.getChildCount() != 0){            
                    if(!cmbPetriOp.getSelectedItem().toString().contains("SPLIT")){                    
                        JOptionPane.showMessageDialog(null, "There isn't selected task in the abstract workflow", ".: e-ScienceNet :.", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            
            if(grafico.getChildCount() == 0){
                
                DomainFileModel domainFileModel = this.getOWLDomainByName(cmbDomainOntology.getSelectedItem().toString());

                String propertyName = cmbTaskProperty.getSelectedItem().toString();

                List<OWLClassesModel> lstLocalClassModel = new ArrayList<>();

                SemanticManager.getInstance().getRangeClasseByObjProperty(owlClassName, propertyName, domainFileModel.getDomainOWLFile(), 
                                                                          lstLocalClassModel, SemanticRestrictionEnum.some);

                if(lstLocalClassModel.size() > 0){
                    String tasks = "";
                    for(OWLClassesModel classModel : lstLocalClassModel){
                        tasks = tasks + classModel.getName() + ", ";
                    }

                    tasks = tasks.trim().substring(0, tasks.lastIndexOf(","));

                    JOptionPane.showMessageDialog(null, "Wasn't possible put taks " + owlClassName + 
                                                        " in the workflow because, it needs the \""+ propertyName +"\" the taks: [" + tasks +"] before put him.",
                                                        ".: e-ScienceNet :.", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                this.plotStartCell();
            }
            
            mxICell newNode = null;
            startCell = null;
            if(cmbPetriOp.getSelectedItem().toString().contains("JOIN")){
                if(this.taskJoinSelection != null){
                    owlClassName = this.taskJoinSelection.getTaskName();
                    if(this.taskJoinSelection.getCell() == null){                        
                        newNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, "T" + ++this.transitionID, 
                                    this.startPosition.getX(), this.startPosition.getY(), 25, 60, "Trans");
                    }else{
                        newNode = this.taskJoinSelection.getCell();
                    }
                }
            }else if(cmbPetriOp.getSelectedItem().toString().contains("SPLIT")){
                if(selectedTask == null){
                    
                    startCell = this.getVertexByName(grafico, "", "Start");
                    
                    mxICell petriCell = this.createPetriOp(startCell);     

                    if(petriCell == null){
                        return;
                    }
                    
                    this.workflowABSModel.getPetriCells().add(petriCell); 
                    
                    newNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, "T" + ++this.transitionID, 
                                    this.startPosition.getX() + prevNode.getGeometry().getWidth() + 30, this.startPosition.getY(), 25, 60, "Trans");
                }else{
                    newNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, "T" + ++this.transitionID, 
                                    this.startPosition.getX(), this.startPosition.getY(), 25, 60, "Trans");
                }
            }else{
                newNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, "T" + ++this.transitionID, 
                                    this.startPosition.getX(), this.startPosition.getY(), 25, 60, "Trans");
            }            
            
            if(selectedTask == null){
                this.graph.insertEdge(this.graph.getDefaultParent(), null, null, prevNode, newNode, "Arrow");
            }
            
            WorkflowABSModel.unselectAll(workflowABSModel);
            
            WorkflowABSModel wfabsm = WorkflowABSModel.getObjectByCode(this.workflowABSModel, (selectedTask != null ? selectedTask.getTaskCode() : "T0"));
            if(wfabsm != null){
                WorkflowABSModel newWfabsm = new WorkflowABSModel();
                newWfabsm.setTaskCode(newNode.getValue().toString());
                newWfabsm.setTaskName(owlClassName);                
                newWfabsm.setCell(newNode);
                
                if(cmbPetriOp.getSelectedItem().toString().contains("SPLIT")){
                    newWfabsm.setSelected(false);
                    wfabsm.setSelected(true);
                }else{
                    newWfabsm.setSelected(true);
                }
                
                wfabsm.getTaskChild().add(newWfabsm);                
            }else{
                if((cmbPetriOp.getSelectedItem().toString().contains("SPLIT")) && (startCell != null)){
                    WorkflowABSModel newWfabsm = new WorkflowABSModel();
                    newWfabsm.setTaskCode(newNode.getValue().toString());
                    newWfabsm.setTaskName(owlClassName);                
                    newWfabsm.setCell(newNode);
                    newWfabsm.setSelected(false);
                    this.workflowABSModel.getTaskChild().add(newWfabsm);
                    this.workflowABSModel.setTaskCode("TS");
                    this.workflowABSModel.setCell(startCell);
                }else{
                    this.workflowABSModel.setTaskCode(newNode.getValue().toString());                                               
                    this.workflowABSModel.setCell(newNode);
                    this.workflowABSModel.setSelected(true);
                    this.workflowABSModel.setTaskName(owlClassName);
                }                
            }
            
            selectedTask = WorkflowABSModel.getSelecionado(this.workflowABSModel);            
            
            if(startCell == null){                
                lblSelectedCell.setText("Task selected: " + selectedTask.getTaskCode() + " ["+ selectedTask.getTaskName() +"]");                
                this.graph.insertEdge(this.graph.getDefaultParent(), null, null, prevNode, newNode, "Arrow");            
                this.prevNode = newNode;
                this.startPosition.setLocation(prevNode.getGeometry().getX() + prevNode.getGeometry().getWidth() + 30, prevNode.getGeometry().getY());
            }else{
                this.workflowABSModel.setSelected(false);
                this.graph.insertEdge(this.graph.getDefaultParent(), null, null, prevNode, newNode, "Arrow");            
                this.prevNode = newNode;
                this.startPosition.setLocation(this.startPosition.getX(), prevNode.getGeometry().getY() + prevNode.getGeometry().getHeight() + 75);
            }
        }
    }//GEN-LAST:event_btnAddTaskActionPerformed

    private mxICell getVertexByName(mxICell graph, String vertexName, String style){
        try{
            for(int i = 0; i < graph.getChildCount(); i++){
                
                mxICell cell = graph.getChildAt(i);
                
                if (cell.isVertex()){                    
                    //if((cell.getStyle() != null) && (cell.getValue() != null)){
                        if((cell.getValue().toString().trim().equals(vertexName)) && (cell.getStyle().equals(style))){
                            return cell;
                        }                    
                    //}
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
    
    private void plotStartCell(){        
        prevNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, "", this.startPosition.getX(), this.startPosition.getY(), 60, 60, "Start");
        this.graph.insertVertex(prevNode, null, "Start", 10, 10, 40, 40, "Start;" + mxConstants.STYLE_FILLCOLOR + "=#000000;"
                                                                                  + mxConstants.STYLE_FONTCOLOR + "=" + mxUtils.getHexColorString(Color.WHITE));

        this.startPosition.setLocation(prevNode.getGeometry().getX() + prevNode.getGeometry().getWidth() + 30, prevNode.getGeometry().getY());
    }
    
    private boolean validatePetriConnection(WorkflowABSModel task){
        
        if(!task.getTaskPetriOp().equals(cmbPetriOp.getSelectedItem().toString())){
            JOptionPane.showMessageDialog(null, "It's impossible to connect a new task in " + task.getTaskName() +
                                        ", because already there is a Petri Operation.", ".: e-ScienceNet :.", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if(task.getPetriCells().isEmpty()){
            return true;
        }else{
            switch (task.getTaskPetriOp()) {
                case "AND-JOIN":
                    JOptionPane.showMessageDialog(null, "The task [" + task.getTaskName() + "] is a AND-JOIN connection, so, any connection is suppoted.", 
                                                  ".: e-ScienceNet :.", JOptionPane.WARNING_MESSAGE);
                    return false;
                case "SEQUENCE":
                    JOptionPane.showMessageDialog(null, "It's impossible to connect a new task in " + task.getTaskName() +
                                                  ", because it's is a SEQUENCE.", ".: e-ScienceNet :.", JOptionPane.WARNING_MESSAGE);
                    return false;
                default:
                    return true;
            }
        }
    }
    
    private mxICell createPetriOp(mxICell node){
        
        WorkflowABSModel selectedTask = WorkflowABSModel.getSelecionado(this.workflowABSModel);
        if(selectedTask != null){
            if(selectedTask.getTaskPetriOp().equals("")){
                selectedTask.setTaskPetriOp(cmbPetriOp.getSelectedItem().toString());
            }

            if(!validatePetriConnection(selectedTask)){
                return null;
            }
        }
        
        if(cmbPetriOp.getSelectedItem().toString().contains("OR")){
        
            if(cmbPetriOp.getSelectedItem().toString().contains("SPLIT")){
                if(selectedTask != null){     
                    
                    mxICell localNode = null;
                    
                    double posY = this.startPosition.getY() - 45;
                    double posX = this.startPosition.getX();
                    if(!selectedTask.getTaskChild().isEmpty()){
                        posY = selectedTask.getTaskChild().get(selectedTask.getTaskChild().size() - 1).getCell().getGeometry().getY() + 
                               selectedTask.getTaskChild().get(selectedTask.getTaskChild().size() - 1).getCell().getGeometry().getHeight() + 15;
                        posX = selectedTask.getPetriCells().get(0).getGeometry().getX();
                        
                        localNode = selectedTask.getPetriCells().get(0);
                    }else{
                        localNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, null, this.startPosition.getX(), 
                                              this.startPosition.getY() + 15, 30, 30, "Start");
                    }

                    this.graph.insertEdge(this.graph.getDefaultParent(), null, null, node, localNode, "Arrow");            
                    this.prevNode = localNode;                    
                    
                    this.startPosition.setLocation(posX + prevNode.getGeometry().getWidth() + 30, posY);
                    return localNode;
                }else{
                    mxICell localNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, null, this.startPosition.getX(), 
                                              this.startPosition.getY() + 15, 30, 30, "Start");

                    this.graph.insertEdge(this.graph.getDefaultParent(), null, null, node, localNode, "Arrow");
                    this.prevNode = localNode;
                    this.startPosition.setLocation(prevNode.getGeometry().getX(), localNode.getGeometry().getY() - 15);   
                    
                    return localNode;
                }
            }else{
                WorkflowABSModel taksToSelect = this.processTaskToJoin();
                
                if(taksToSelect == null){
                    return null;
                }else{
                    if(taksToSelect.getTaskChild().isEmpty()){
                        return null;
                    }
                }
                
                FSelectTaskBecausePetriJoin frmBecausePetriJoin = new FSelectTaskBecausePetriJoin(null, true);
                frmBecausePetriJoin.setTasksToSelect(taksToSelect);
                frmBecausePetriJoin.setVisible(true);
                
                if(frmBecausePetriJoin.getSelectedTask() == null){
                    JOptionPane.showMessageDialog(null, "Wasn't possible create OR-JOIN taks because any task was selected.",
                                                ".: e-ScienceNet :.", JOptionPane.WARNING_MESSAGE);
                    return null;
                }else{               
                    
                    WorkflowABSModel taskFather = WorkflowABSModel.getObjectFather(this.workflowABSModel, selectedTask);
                    
                    mxICell localNode = null;
                    if(taskFather.getTaskChild().isEmpty()){
                        localNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), 
                                null, null, selectedTask.getCell().getGeometry().getX() + selectedTask.getCell().getGeometry().getWidth() + 30, 
                                selectedTask.getCell().getGeometry().getY() + 15, 30, 30, "Start");
                    }else{
                        for(WorkflowABSModel taksSister : taskFather.getTaskChild()){
                            if(!taksSister.getPetriCells().isEmpty()){
                                localNode = taksSister.getPetriCells().get(taksSister.getPetriCells().size() - 1);
                                break;
                            }
                        }
                        
                        if(localNode == null){
                            localNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), 
                                null, null, selectedTask.getCell().getGeometry().getX() + selectedTask.getCell().getGeometry().getWidth() + 30, 
                                selectedTask.getCell().getGeometry().getY() + 15, 30, 30, "Start");
                        }
                    }

                    this.graph.insertEdge(this.graph.getDefaultParent(), null, null, node, localNode, "Arrow");            
                    this.prevNode = localNode;
                    this.startPosition.setLocation(prevNode.getGeometry().getX() + prevNode.getGeometry().getWidth() + 30, selectedTask.getCell().getGeometry().getY());
                    
                    this.taskJoinSelection = new WorkflowABSModel();
                    this.taskJoinSelection.setTaskName(frmBecausePetriJoin.getSelectedTask().getTaskName());
                    this.taskJoinSelection.setTaskCode(frmBecausePetriJoin.getSelectedTask().getTaskCode());
                    this.taskJoinSelection.setCell(frmBecausePetriJoin.getSelectedTask().getCell());
                    
                    return localNode;
                }
            }            
        }else if(cmbPetriOp.getSelectedItem().toString().contains("AND")){
            
            if(cmbPetriOp.getSelectedItem().toString().contains("SPLIT")){
                if(selectedTask != null){
                    double posY = this.startPosition.getY() - 45;
                    double posX = this.startPosition.getX();
                    if(!selectedTask.getTaskChild().isEmpty()){
                        posY = selectedTask.getPetriCells().get(selectedTask.getPetriCells().size() - 1).getGeometry().getY() + 
                               selectedTask.getPetriCells().get(selectedTask.getPetriCells().size() - 1).getGeometry().getHeight() + 45;
                        posX = selectedTask.getPetriCells().get(selectedTask.getPetriCells().size() - 1).getGeometry().getX();
                    }
                    
                    mxICell localNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, null, posX, posY, 30, 30, "Start");

                    this.graph.insertEdge(this.graph.getDefaultParent(), null, null, selectedTask.getCell(), localNode, "Arrow");
                    this.prevNode = localNode;
                    this.startPosition.setLocation(prevNode.getGeometry().getX() + prevNode.getGeometry().getWidth() + 30, localNode.getGeometry().getY() - 15);   
                    
                    return localNode;
                }else{
                    double posY = this.startPosition.getY() - 50;
                    double posX = this.startPosition.getX();
                    
                    mxICell localNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, null, posX, posY, 30, 30, "Start");

                    this.graph.insertEdge(this.graph.getDefaultParent(), null, null, node, localNode, "Arrow");
                    this.prevNode = localNode;
                    this.startPosition.setLocation(prevNode.getGeometry().getX(), localNode.getGeometry().getY() - 15);   
                    
                    return localNode;
                }
            }else{
                WorkflowABSModel taksToSelect = this.processTaskToJoin();
                
                if(taksToSelect == null){
                    return null;
                }else{
                    if(taksToSelect.getTaskChild().isEmpty()){
                        return null;
                    }
                }
                
                FSelectTaskBecausePetriJoin frmBecausePetriJoin = new FSelectTaskBecausePetriJoin(null, true);
                frmBecausePetriJoin.setTasksToSelect(taksToSelect);
                frmBecausePetriJoin.setVisible(true);
                
                if(frmBecausePetriJoin.getSelectedTask() == null){
                    JOptionPane.showMessageDialog(null, "Wasn't possible create AND-JOIN taks because any task was selected.",
                                                ".: e-ScienceNet :.", JOptionPane.WARNING_MESSAGE);
                    return null;
                }else{                    
                    mxICell localNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), 
                            null, null, selectedTask.getCell().getGeometry().getX() + selectedTask.getCell().getGeometry().getWidth() + 30, 
                            selectedTask.getCell().getGeometry().getY() + 15, 30, 30, "Start");

                    this.graph.insertEdge(this.graph.getDefaultParent(), null, null, node, localNode, "Arrow");            
                    this.prevNode = localNode;
                    this.startPosition.setLocation(prevNode.getGeometry().getX() + prevNode.getGeometry().getWidth() + 30, selectedTask.getCell().getGeometry().getY());
                    
                    this.taskJoinSelection = new WorkflowABSModel();
                    this.taskJoinSelection.setTaskName(frmBecausePetriJoin.getSelectedTask().getTaskName());
                    this.taskJoinSelection.setTaskCode(frmBecausePetriJoin.getSelectedTask().getTaskCode());
                    this.taskJoinSelection.setCell(frmBecausePetriJoin.getSelectedTask().getCell());
                    
                    return localNode;
                }
            }            
        }else{
            if(selectedTask != null){                
                mxICell localNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, null, this.startPosition.getX(), this.startPosition.getY() + 15, 30, 30, "Start");

                this.graph.insertEdge(this.graph.getDefaultParent(), null, null, node, localNode, "Arrow");            
                this.prevNode = localNode;
                this.startPosition.setLocation(prevNode.getGeometry().getX() + prevNode.getGeometry().getWidth() + 30, this.startPosition.getY());
                return localNode;
            }
        }
        
        return null;
    }
    
    private boolean checkPossibleTaks(String atualTaks){
        
        List<OWLClassesModel> lstLocalClassModel = new ArrayList<>();
        DomainFileModel domainFileModel = this.getOWLDomainByName(cmbDomainOntology.getSelectedItem().toString());
        String propertyName = cmbTaskProperty.getSelectedItem().toString();
        
        WorkflowABSModel selectedTask = WorkflowABSModel.getSelecionado(this.workflowABSModel);
        
        SemanticManager.getInstance().discoverNextTask(selectedTask.getTaskName(), propertyName, 
                              domainFileModel.getDomainOWLFile(), lstLocalClassModel, SemanticRestrictionEnum.some);
        
        boolean taskFindend = false;
        String tasks = "";
        for(OWLClassesModel owlClassesModel : lstLocalClassModel){
            if(atualTaks.equals(owlClassesModel.getName())){
                taskFindend = true;
                return true;
            }

            tasks = tasks + owlClassesModel.getName() + ", ";
        }
        
        if(!taskFindend){
            if(!tasks.equals("")){
                tasks = tasks.trim().substring(0, tasks.lastIndexOf(","));
            }

            JOptionPane.showMessageDialog(null, "Wasn't possible put taks " + atualTaks + 
                                                " in the workflow because, it needs the next task: [" + tasks +"] before put him.",
                                                ".: e-ScienceNet :.", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public String getTaskInformations(String taks){
        List<OWLClassesModel> lstLocalClassModel = new ArrayList<>();
        DomainFileModel domainFileModel = this.getOWLDomainByName(cmbDomainOntology.getSelectedItem().toString());
        String propertyName = cmbTaskProperty.getSelectedItem().toString();
        String selectedTask = taks;

        SemanticManager.getInstance().discoverNextTask(selectedTask, propertyName, 
                              domainFileModel.getDomainOWLFile(), lstLocalClassModel, SemanticRestrictionEnum.some);

        String tasks = "";
        for(OWLClassesModel owlClassesModel : lstLocalClassModel){
            tasks = tasks + owlClassesModel.getName() + " | ";
        }
        
        return tasks;
    }
    
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.criarExibicaoGrafica();
        
        PageFormat format = new PageFormat();
        format.setOrientation(PageFormat.LANDSCAPE);         
        Paper paper = new Paper();
        paper.setSize(pnlAbstractWF.getBounds().getHeight(), pnlAbstractWF.getBounds().getWidth());
        format.setPaper(paper);
        this.graphComponent.setPageFormat(format);
        
        //this.graphComponent.setPageVisible(true);
        this.graphComponent.setAntiAlias(true);
        this.graphComponent.setGridVisible(true);
        this.graphComponent.setConnectable(true);
        this.graphComponent.setAutoExtend(true);
        this.graphComponent.getViewport().setOpaque(true);
        this.graphComponent.getViewport().setBackground(Color.WHITE);
        this.graphComponent.setToolTips(true);                
        this.graphComponent.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.graphComponent.revalidate();
        this.graphComponent.repaint();
        
        this.graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxIEventListener(){
            @Override
            public void invoke(Object sender, mxEventObject evt){                
                if (sender instanceof mxGraphSelectionModel){
                    for (Object obj : ((mxGraphSelectionModel)sender).getCells()){
                        
                        mxICell cell = (mxICell) obj;
                        
                        if(cell.getStyle().equals("Start")){
                            WorkflowABSModel task = WorkflowABSModel.getTaksByPetriObject(FComposeAbstractWorkflow.this.workflowABSModel, cell);
                            if(task != null){
                                lblSelectedCell.setText("Task selected: " + task.getTaskCode() + " ["+ task.getTaskName() +"]");
                                WorkflowABSModel.unselectAll(workflowABSModel);
                                task.setSelected(true);
                            }
                        }else{                        
                            WorkflowABSModel task = WorkflowABSModel.getObjectByCode(FComposeAbstractWorkflow.this.workflowABSModel, graph.getLabel(cell));
                            if(task != null){
                                lblSelectedCell.setText("Task selected: " + task.getTaskCode() + " ["+ task.getTaskName() +"]");
                                WorkflowABSModel.unselectAll(workflowABSModel);
                                task.setSelected(true);
                            }
                        }
                    }
                }
            }
        });
        
        this.createStyleForStart();
        this.createStyleForTransition();
        this.createStyleForArrow();
        
        this.startPosition = new Point2D.Double(10, (this.graphComponent.getBounds().getCenterY() - 20));
        this.transitionID = 0;
        this.workflowABSModel = new WorkflowABSModel();        
        this.taskJoinSelection = null;
        this.lblSelectedCell.setText("Task selected:");
    }//GEN-LAST:event_formWindowOpened

    private void btnFechar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFechar1ActionPerformed
        this.limparTabela(tblClassesWF);
        this.limparTabela(tblObjProperties);
        this.limparComboBox(cmbTaskProperty);
        treeOWL.setModel(null);
        treeOWLRanges.setModel(null);
        graph.removeCells(this.graph.getChildVertices(this.graph.getDefaultParent()), true);
        
        this.startPosition = new Point2D.Double(10, (this.graphComponent.getBounds().getCenterY() - 20));
        this.transitionID = 0;
        this.workflowABSModel = new WorkflowABSModel();
        this.taskJoinSelection = null;
        this.lblSelectedCell.setText("Task selected:");
        cmbPetriOp.setSelectedIndex(0);
    }//GEN-LAST:event_btnFechar1ActionPerformed

    private void cmbPetriOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbPetriOpActionPerformed

    }//GEN-LAST:event_cmbPetriOpActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.gravarImagem();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnCloseWFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseWFActionPerformed
        this.plotFinalCell();
    }//GEN-LAST:event_btnCloseWFActionPerformed

    private void plotFinalCell(){
      WorkflowABSModel selectedTask = WorkflowABSModel.getSelecionado(this.workflowABSModel); 
        if(selectedTask != null){
            if(selectedTask.getTaskPetriOp().equals("")){
                
                mxICell finish = null;
                
                mxICell cellParent = (mxICell) this.graph.getDefaultParent();

                for(int i = 0; i < cellParent.getChildCount(); i++){
                    mxICell child = cellParent.getChildAt(i);
                    
                    if(child.getValue() != null){
                        if(child.getValue().toString().equals("Finish")){
                            finish = child;
                        }
                    }
                }
                
                if(finish == null){
                    finish =  (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, "Finish", 
                               selectedTask.getCell().getGeometry().getX() + selectedTask.getCell().getGeometry().getWidth() + 30, 
                               selectedTask.getCell().getGeometry().getY(), 60, 60, "Start;" + mxConstants.STYLE_FILLCOLOR + "=#E0E0E0");
                }
                            
                this.graph.insertEdge(this.graph.getDefaultParent(), null, null, selectedTask.getCell(), finish, "Arrow");
           }
        }
    }
    
    private void tblClassesWFMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClassesWFMouseMoved
        
        if(tblClassesWF.getSelectedRow() > -1){
            String tasks = this.getTaskInformations(tblClassesWF.getModel().getValueAt(tblClassesWF.getSelectedRow(), 0).toString());
            
            if(!tasks.equals("")){
                tasks = "Possibles Next Tasks: " + tasks.trim().substring(0, tasks.lastIndexOf("|"));
            }
            
            tblClassesWF.setToolTipText(tasks);
        }
    }//GEN-LAST:event_tblClassesWFMouseMoved

    private void btnRemoveNodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveNodeActionPerformed
        
        if(this.graph.getSelectionCell() != null){
                 
            WorkflowABSModel taskWF = WorkflowABSModel.getSelecionado(workflowABSModel);
            if(taskWF != null){
                
                if(this.workflowABSModel.getTaskCode().equals(taskWF.getTaskCode())){
                    graph.removeCells(this.graph.getChildVertices(this.graph.getDefaultParent()), true);
        
                    this.startPosition = new Point2D.Double(10, (this.graphComponent.getBounds().getCenterY() - 20));
                    this.transitionID = 0;
                    this.workflowABSModel = new WorkflowABSModel();
                    this.taskJoinSelection = null;
                    this.lblSelectedCell.setText("Task selected:");
                }
                
                WorkflowABSModel.removerWFTasks(taskWF, this.graph);
                WorkflowABSModel.removerTasks(taskWF);

                WorkflowABSModel taskFather = WorkflowABSModel.getObjectFather(this.workflowABSModel, taskWF);
                if(taskFather != null){
                    taskFather.getTaskChild().remove(taskWF);
                    for(Object objeto : taskFather.getPetriCells()){
                        Object[] obj = {objeto};
                        this.graph.removeCells(obj);
                    }                            
                    
                    while(!taskFather.getPetriCells().isEmpty()){
                        taskFather.getPetriCells().remove(0);
                    }
                    
                    taskFather.setTaskPetriOp("");
                    taskFather.setSelected(true);                    
                    
                    String taskCode = taskFather.getTaskCode().replace("T", "").trim();
                    this.transitionID = Integer.parseInt(taskCode);
                    this.taskJoinSelection = null;
                    this.lblSelectedCell.setText("Task selected: ["+ taskFather.getTaskName() +"]");                    
                    this.startPosition.setLocation(taskFather.getCell().getGeometry().getX() + taskFather.getCell().getGeometry().getWidth() + 30, 
                                                   taskFather.getCell().getGeometry().getY());
                }
            }
            
            this.graph.removeCells(this.graph.getSelectionCells());
            
            for(int i = 0; i < ((mxICell) this.graph.getDefaultParent()).getChildCount(); i++){
                mxICell child = ((mxICell) this.graph.getDefaultParent()).getChildAt(i);
                if(child != null){
                    if(child.getValue() != null){
                        if(child.getValue().toString().toLowerCase().equals("finish")){
                            Object[] obj = {child};
                            this.graph.removeCells(obj);
                        }
                    }
                }
            }
        }        
    }//GEN-LAST:event_btnRemoveNodeActionPerformed

    private void btnSalvarRecursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarRecursoActionPerformed
        
        String xmlFile = JOptionPane.showInputDialog(null, "Enter with file name (No extension)", ".: e-ScienceNet :.", JOptionPane.WARNING_MESSAGE);
        
        if(!xmlFile.equals("")){            
            
            xmlFile = xmlFile + ".awf";
            
            workflowABSModel.setDomainOntolgyName(cmbDomainOntology.getSelectedItem().toString().trim());
            
            if(SemanticManager.getInstance().getDataManager().gravarAbstractWF(workflowABSModel, xmlFile)){
                               
                SemanticManager.getInstance().getDataManager().processarDownloadRequest(xmlFile);
                
                JOptionPane.showMessageDialog(null, "Abstract Workflows was save as e-ScienceNet resource.", ".: e-ScienceNet :.", JOptionPane.INFORMATION_MESSAGE);
                this.btnFechar1ActionPerformed(null);
            }
        }
    }//GEN-LAST:event_btnSalvarRecursoActionPerformed

    private void btnOpenGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenGraphActionPerformed
        try{
            FAbstractWFSelect frmAbstractWFSelect = new FAbstractWFSelect(null, true);
            frmAbstractWFSelect.setVisible(true);

            File file = frmAbstractWFSelect.getAwfSelected();
            if(file != null){
                
                this.btnFechar1ActionPerformed(null);

                XStream xstream = new XStream(new DomDriver());
                this.workflowABSModel = (WorkflowABSModel) xstream.fromXML(file);  

                if(this.workflowABSModel != null){

                    cmbDomainOntology.setSelectedItem(this.workflowABSModel.getDomainOntolgyName());
                    
                    this.plotGraph(this.workflowABSModel);
                    this.plotEdges(this.workflowABSModel);

                    if(!this.workflowABSModel.getTaskCode().equals("TS")){
                        this.plotStartCell();
                        this.graph.insertEdge(this.graph.getDefaultParent(), null, null, prevNode, this.workflowABSModel.getCell(), "Arrow");
                    }

                    int counter = 0;
                    counter = WorkflowABSModel.getCountNodes(workflowABSModel, counter);

                    WorkflowABSModel lastNode = WorkflowABSModel.getObjectByCode(this.workflowABSModel, "T" + counter);

                    if(lastNode != null){

                        WorkflowABSModel.unselectAll(this.workflowABSModel);
                        lastNode.setSelected(true);
                        this.plotFinalCell();                    
                    }
                }
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Wasn't possible to open the AWF file.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnOpenGraphActionPerformed
    
    private void plotGraph(WorkflowABSModel workflowABSModel){
        if(workflowABSModel != null){
            if(workflowABSModel.getCell() != null){                
                this.graph.addCell(workflowABSModel.getCell());
            }
            
            if(workflowABSModel.getPetriCells() != null){
                for(mxICell petri : workflowABSModel.getPetriCells()){
                    this.graph.addCell(petri);
                }
            }
        }
        
        for(WorkflowABSModel wfabsm : workflowABSModel.getTaskChild()){
            plotGraph(wfabsm);
        }
    }
    
    private void plotEdges(WorkflowABSModel workflowABSModel){
        
        if(workflowABSModel != null){
            if(workflowABSModel.getCell() != null){                            
                if(workflowABSModel.getPetriCells() != null){
                    for(int i = 0; i < workflowABSModel.getPetriCells().size(); i++){
                        this.graph.insertEdge(this.graph.getDefaultParent(), null, null, workflowABSModel.getCell(), workflowABSModel.getPetriCells().get(i), "Arrow");
                        this.graph.insertEdge(this.graph.getDefaultParent(), null, null, workflowABSModel.getPetriCells().get(i), 
                                              workflowABSModel.getTaskChild().get(i).getCell(), "Arrow");
                    }
                }
            }
        }
        
        for(WorkflowABSModel wfabsm : workflowABSModel.getTaskChild()){
            plotEdges(wfabsm);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddClass;
    private javax.swing.JButton btnAddTask;
    private javax.swing.JButton btnCloseWF;
    private javax.swing.JButton btnContinueSeq;
    private javax.swing.JButton btnFechar;
    private javax.swing.JButton btnFechar1;
    private javax.swing.JButton btnLoadDomain;
    private javax.swing.JButton btnOpenGraph;
    private javax.swing.JButton btnRemoveNode;
    private javax.swing.JButton btnRemoverClass;
    private javax.swing.JButton btnSalvarRecurso;
    private javax.swing.JComboBox cmbDomainOntology;
    private javax.swing.JComboBox cmbPetriOp;
    private javax.swing.JComboBox cmbRelations;
    private javax.swing.JComboBox cmbTaskProperty;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblSelectedCell;
    private javax.swing.JPanel pnlAbstractWF;
    private javax.swing.JPanel pnlDomain;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JFileChooser saveDialog;
    private javax.swing.JTable tblClassesWF;
    private javax.swing.JTable tblObjProperties;
    private javax.swing.JTree treeOWL;
    private javax.swing.JTree treeOWLRanges;
    // End of variables declaration//GEN-END:variables
}
