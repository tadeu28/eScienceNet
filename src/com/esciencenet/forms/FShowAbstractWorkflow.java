/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.forms;

import com.esciencenet.models.WorkflowABSModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;
import com.mxgraph.view.mxStylesheet;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author Tadeu
 */
public class FShowAbstractWorkflow extends javax.swing.JInternalFrame {

    private mxGraph graph;
    private mxGraphComponent graphComponent;
    
    private Point2D startPosition;
    private mxICell prevNode = null;
    private WorkflowABSModel workflowABSModel = new WorkflowABSModel();
    private JLabel lblSelectedTask;
    private JLabel lblTaskCode;
    
    /**
     * Creates new form FShowAbstractWorkflow
     */    
    public FShowAbstractWorkflow(String title, boolean resizible, boolean maximized, boolean minimizable, boolean closable){
        super(title);
        initComponents();
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
                    WorkflowABSModel workflowABSModel = WorkflowABSModel.getObjectByCode(FShowAbstractWorkflow.this.workflowABSModel, taks.getValue().toString());

                    if(workflowABSModel != null){
                        String information = "Task: " + workflowABSModel.getTaskName();

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
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlAbstractWF = new javax.swing.JPanel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        pnlAbstractWF.setBorder(javax.swing.BorderFactory.createTitledBorder(" Abstract Workflow "));

        javax.swing.GroupLayout pnlAbstractWFLayout = new javax.swing.GroupLayout(pnlAbstractWF);
        pnlAbstractWF.setLayout(pnlAbstractWFLayout);
        pnlAbstractWFLayout.setHorizontalGroup(
            pnlAbstractWFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 621, Short.MAX_VALUE)
        );
        pnlAbstractWFLayout.setVerticalGroup(
            pnlAbstractWFLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 304, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlAbstractWF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlAbstractWF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
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
        this.graphComponent.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.graphComponent.revalidate();
        this.graphComponent.repaint();
        
        this.graph.getSelectionModel().addListener(mxEvent.CHANGE, new mxEventSource.mxIEventListener(){
            @Override
            public void invoke(Object sender, mxEventObject evt){                
                if (sender instanceof mxGraphSelectionModel){
                    for (Object obj : ((mxGraphSelectionModel)sender).getCells()){
                        
                        mxICell cell = (mxICell) obj;
                        
                        if(cell.getStyle().equals("Start")){
                            WorkflowABSModel task = WorkflowABSModel.getTaksByPetriObject(FShowAbstractWorkflow.this.workflowABSModel, cell);
                            if(task != null){
                                lblSelectedTask.setText(task.getTaskName());
                                lblTaskCode.setText(task.getTaskCode());
                                WorkflowABSModel.unselectAll(workflowABSModel);
                                task.setSelected(true);
                            }
                        }else{                        
                            WorkflowABSModel task = WorkflowABSModel.getObjectByCode(FShowAbstractWorkflow.this.workflowABSModel, graph.getLabel(cell));
                            if(task != null){
                                lblSelectedTask.setText(task.getTaskName());
                                lblTaskCode.setText(task.getTaskCode());
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
        
        this.exibirAWF();
    }//GEN-LAST:event_formComponentShown

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        if(this.graphComponent != null){
            PageFormat format = new PageFormat();
            format.setOrientation(PageFormat.LANDSCAPE);         
            Paper paper = new Paper();
            paper.setSize(pnlAbstractWF.getBounds().getHeight(), pnlAbstractWF.getBounds().getWidth());
            format.setPaper(paper);
            this.graphComponent.setPageFormat(format);

            Double width = pnlAbstractWF.getBounds().getWidth();
            Double height = pnlAbstractWF.getBounds().getHeight();
            this.graphComponent.setBounds(0, 0, width.intValue(), height.intValue());

            pnlAbstractWF.revalidate();
            pnlAbstractWF.repaint();
            this.graphComponent.repaint();
            this.graph.repaint();
        }
    }//GEN-LAST:event_formComponentResized

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
    
    private void plotStartCell(){        
        prevNode = (mxICell) this.graph.insertVertex(this.graph.getDefaultParent(), null, null, this.startPosition.getX(), this.startPosition.getY(), 60, 60, "Start");
        this.graph.insertVertex(prevNode, null, "Start", 10, 10, 40, 40, "Start;" + mxConstants.STYLE_FILLCOLOR + "=#000000;"
                                                                                  + mxConstants.STYLE_FONTCOLOR + "=" + mxUtils.getHexColorString(Color.WHITE));

        this.startPosition.setLocation(prevNode.getGeometry().getX() + prevNode.getGeometry().getWidth() + 30, prevNode.getGeometry().getY());
    }    
    
    private void exibirAWF(){
        try{
            
            if(this.workflowABSModel != null){

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
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Wasn't possible to open the AWF file.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void plotFinalCell(){
      WorkflowABSModel selectedTask = WorkflowABSModel.getSelecionado(this.workflowABSModel); 
        if(selectedTask != null){
            if(selectedTask.getPetriCells().isEmpty()){
                
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlAbstractWF;
    // End of variables declaration//GEN-END:variables

    /**
     * @param workflowABSModel the workflowABSModel to set
     */
    public void setWorkflowABSModel(WorkflowABSModel workflowABSModel) {
        this.workflowABSModel = workflowABSModel;
    }

    /**
     * @return the lblSelectedTask
     */
    public JLabel getLblSelectedTask() {
        return lblSelectedTask;
    }

    /**
     * @param lblSelectedTask the lblSelectedTask to set
     */
    public void setLblSelectedTask(JLabel lblSelectedTask) {
        this.lblSelectedTask = lblSelectedTask;
    }

    /**
     * @return the lblTaskCode
     */
    public JLabel getLblTaskCode() {
        return lblTaskCode;
    }

    /**
     * @param lblTaskCode the lblTaskCode to set
     */
    public void setLblTaskCode(JLabel lblTaskCode) {
        this.lblTaskCode = lblTaskCode;
    }
    
}

