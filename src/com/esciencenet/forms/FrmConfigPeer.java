package com.esciencenet.forms;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.jxta.exception.ConfiguratorException;
import net.jxta.ext.config.Address;
import net.jxta.ext.config.Configurator;
import net.jxta.ext.config.TcpTransportAddress;
import net.jxta.ext.config.Transport;
import net.jxta.impl.config.Config;



/**
 * Formulário de configuração do nó JXTA para a e-ScienceNet
 * 
 * @author Tadeu Classe
 */
public final class FrmConfigPeer extends javax.swing.JDialog {

    //atributos do formulário
    private DefaultListModel listaRendezvous, listaRelay;     
    private Configurator configurator;
    private Transport transportTCP;
    private Transport transportHTTP;
    private List listPeerEncontro;
    private List listPeerRelay;
    
    /**
     * Método construtor da formulário de configuração
     * 
     * @param config componente de configuração do JXTA
     */
    public FrmConfigPeer(Configurator config) {
        //chamo o pai do formulário e inicializo os componentes do formulário
        super();
        initComponents();        
        
        //coloco a posição do formulário para o centro da tela
        setLocationRelativeTo(null);  
        
        //passo o objeto de configuração para a classe
        this.configurator = config;
        
        //crio as listas de IP relay e rendezVous
        listaRelay = new DefaultListModel();
        listaRendezvous = new DefaultListModel();
        
        //chamo o métodos de configuração inicial do JXTA
        iniciarConfiguracao();
    }

    /**
     * Método construtor da formulário de configuração
     * 
     * @param jFrame
     * @param b 
     */
    private FrmConfigPeer(JFrame jFrame, boolean b) {
        super(jFrame, b);
    }

    /**
     * Método inicial de configuração de JXTA
     */
    public void iniciarConfiguracao(){     
        try {		            

            //Configuracoes Default do JXTA
            configurator.setSecurity(false);
            configurator.setRelay(true);
            configurator.setRendezVous(true);
            configurator.setRendezVousAutoStart(true);

            //verifico se o nome do ponto está configurado
            if (configurator.getName() == null) {
                //seto o campo de nome como editável
                edtNome.setEditable(true);
            } else {
                //seto o campo de nome como não editável junto com os demais componentes
                edtNome.setText(configurator.getName());    
                edtNome.setEditable(false);
                gpbSeguraca.setEnabled(false);
                edtSenha.setEnabled(false);
                edtUsuario.setEditable(false);
            }
            
            //obtenho a lista de configurações de transporte
            List listTransports = configurator.getTransports();

            //pego a lista de IPs de TCP
            transportTCP = (Transport) listTransports.get(0);
            cbkRedeLocal.setSelected(transportTCP.isEnabled());

            //verifico se já foi configurado os ips de TCP
            TcpTransportAddress ts = (TcpTransportAddress) transportTCP.getAddresses().get(0);
            if (ts.getAddress().getHost().trim().equals("0.0.0.0")) {
                //seto o ip local do nó
                edtIPTCP.setText("127.0.0.1");
            } else {
                //seto o ip de rede que foi configurado
                edtIPTCP.setText(ts.getAddress().getHost());
            }

            //pego os ips de configuração HTTP
            transportHTTP = (Transport) listTransports.get(1);
            cbkInternet.setSelected(transportHTTP.isEnabled());

            //verifico se já foram configurados os Ips HTTP
            Address address = (Address) transportHTTP.getAddresses().get(0);
            if (address.getAddress().getHost() == null) {
                //seto o ip local do nó
                edtIPHTTP.setText("127.0.0.1");
            } else {
                //seto o ip que foi configurado
                edtIPHTTP.setText(address.getAddress().getHost());
            }

            //insiro o nome do peer no componente de nome
            edtNome.setText(configurator.getName());
            
            //peer rendevouz
            cbkRendezvous.setSelected(configurator.isRendezVous());
            listPeerEncontro = configurator.getRendezVous();    
            
            //percorro a lista de nós redezVous e adiciono na lista de configuração
            for(int i = 0; i < listPeerEncontro.size(); i++){
                if ((listPeerEncontro.get(i).toString().indexOf("http") != -1) || (listPeerEncontro.get(i).toString().indexOf("tcp") != -1)){
                    listaRendezvous.addElement(listPeerEncontro.get(i).toString());
                }
                
            }
            
            //insiro os ips configurados na lisra de nós rendezVous
            lstIPRendezvous.setModel(listaRendezvous);        
            
            //peer relay
            cbkRelay.setSelected(configurator.isRelay());
            listPeerRelay = configurator.getRelays();

            //percorro a lista de nós relay e adiciono na lista de configuração
            for(int i = 0; i < listPeerRelay.size(); i++){
                if ((listPeerRelay.get(i).toString().indexOf("http") != -1) || (listPeerRelay.get(i).toString().indexOf("tcp") != -1)){
                    listaRelay.addElement(listPeerRelay.get(i).toString());
                }                
            }
            
            //insiro os ips configurados na lisra de nós relay
            lstIPRelay.setModel(listaRelay);        
            
            //atualizo as listas de relay e redezVous
            lstIPRelay.updateUI();
            lstIPRendezvous.updateUI();
        } catch (Exception e) {
            //exibo a mensagem de erro
            JOptionPane.showMessageDialog(null, "Erro:\n" + e.getMessage());
        }        
    }
    
    /**
     * Método responsável pela configuração e gravação das configuração no diretório do JXTA
     */
    public void processar(){
        try {
            //Nome do peer
            configurator.setName(edtNome.getText());

            //crio a lista de transporte
            List listTransport = new ArrayList();
            
            //TCP - Local
            transportTCP.setEnabled(cbkRedeLocal.isSelected());
            TcpTransportAddress addressTCP = new TcpTransportAddress();
            addressTCP.setAddress(new URI("tcp://" + edtIPTCP.getText() + ":9701"));
            transportTCP.setAddress(addressTCP);

            //HTTP - Internet
            transportHTTP.setEnabled(cbkInternet.isSelected());
            Address addressHTTP = new Address();
            addressHTTP.setAddress(new URI("http://" + edtIPHTTP.getText() + ":9700"));
            transportHTTP.setAddress(addressHTTP);

            //insiro os IPs TCP e HTTP nas configurações de protocolo de transporte
            listTransport.add(transportHTTP);
            listTransport.add(transportTCP);
            configurator.setTransports(listTransport);

            //seto as configurações de segurança passando o usuário e senha para acesso a e-ScienceNet
            configurator.setSecurity(edtUsuario.getText(), edtSenha.getText());
            
            //faço as configurações de peer relay e rendezVous
            configurator.setRendezVous(cbkRendezvous.isSelected());
            configurator.setRelay(cbkRelay.isSelected());
            configurator.setRendezVousDiscovery(true);
            configurator.setRendezVousAutoStart(true);

            //limpo a lista de rendezvous
            listPeerEncontro.clear();
            //percorro a lista de IPs RendezVous
            for (int i = 0; i < lstIPRendezvous.getModel().getSize(); i++){            
                //insiro na lisra de Peer rendezVous
                listPeerEncontro.add(new URI(lstIPRendezvous.getModel().getElementAt(i).toString()));
            }
            
            //limpo as lisra de relay
            listPeerRelay.clear();
            for (int i = 0; i < lstIPRelay.getModel().getSize(); i++){            
                //percorro a lista de nos relay inserindo-as
                listPeerRelay.add(new URI(lstIPRelay.getModel().getElementAt(i).toString()));
            }
            
            //seto os nos relay e rendezVous no configurador do JXTA
            configurator.setRelays(listPeerRelay);
            configurator.setRendezVous(listPeerEncontro);            
            
            //processo de salvamento            
            configurator.save(new File(Config.JXTA_HOME));

            //fecho a tela de configuração
            setVisible(false);
        } catch (URISyntaxException | IllegalArgumentException | ConfiguratorException e) {
            //exibo a mensagem de erro
            JOptionPane.showMessageDialog(null, "Erro:\n" + e.getMessage());
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        gpbSeguraca = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        edtUsuario = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        edtSenha = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        cbkRedeLocal = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        edtIPTCP = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        cbkInternet = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        edtIPHTTP = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        cbkRelay = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        cmbRelay = new javax.swing.JComboBox();
        edtIPRelay = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        edtPortaRelay = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstIPRelay = new javax.swing.JList();
        btnDelRelay = new javax.swing.JButton();
        btnAddRelay = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        cbkRendezvous = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        cmbRendezvous = new javax.swing.JComboBox();
        edtIPRendezvous = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        edtPortaRendezvous = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstIPRendezvous = new javax.swing.JList();
        btnDelRedezvous = new javax.swing.JButton();
        btnAddRedezvous = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        btnOK = new javax.swing.JButton();
        btnCancela = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        edtNome = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Configuração e-ScienceNet");
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setLayout(null);

        gpbSeguraca.setBorder(javax.swing.BorderFactory.createTitledBorder(" Seguranca "));
        gpbSeguraca.setLayout(null);

        jLabel1.setText("Usuário");
        gpbSeguraca.add(jLabel1);
        jLabel1.setBounds(10, 20, 50, 16);
        gpbSeguraca.add(edtUsuario);
        edtUsuario.setBounds(10, 40, 330, 20);

        jLabel2.setText("Senha");
        gpbSeguraca.add(jLabel2);
        jLabel2.setBounds(10, 70, 36, 16);
        gpbSeguraca.add(edtSenha);
        edtSenha.setBounds(10, 90, 330, 22);

        jPanel2.add(gpbSeguraca);
        gpbSeguraca.setBounds(10, 200, 350, 120);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(" TCP "));
        jPanel1.setLayout(null);

        cbkRedeLocal.setText("Ativar Rede Local");
        jPanel1.add(cbkRedeLocal);
        cbkRedeLocal.setBounds(10, 20, 310, 25);

        jLabel4.setText("IP");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(10, 50, 11, 16);
        jPanel1.add(edtIPTCP);
        edtIPTCP.setBounds(10, 70, 330, 22);

        jPanel2.add(jPanel1);
        jPanel1.setBounds(10, 0, 350, 100);
        jPanel1.getAccessibleContext().setAccessibleDescription("");

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(" HTTP "));
        jPanel6.setLayout(null);

        cbkInternet.setText("Ativar Internet");
        cbkInternet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbkInternetActionPerformed(evt);
            }
        });
        jPanel6.add(cbkInternet);
        cbkInternet.setBounds(10, 20, 310, 25);

        jLabel5.setText("IP");
        jPanel6.add(jLabel5);
        jLabel5.setBounds(10, 50, 11, 16);
        jPanel6.add(edtIPHTTP);
        edtIPHTTP.setBounds(10, 70, 330, 22);

        jPanel2.add(jPanel6);
        jPanel6.setBounds(10, 100, 350, 100);

        jTabbedPane1.addTab("Config. Geral", jPanel2);

        jPanel3.setLayout(null);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(" Relay "));
        jPanel8.setLayout(null);

        cbkRelay.setText("Atuar como peer Relay");
        jPanel8.add(cbkRelay);
        cbkRelay.setBounds(10, 20, 190, 25);

        jLabel3.setText("Lista");
        jPanel8.add(jLabel3);
        jLabel3.setBounds(10, 50, 26, 16);

        cmbRelay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TCP", "HTTP" }));
        cmbRelay.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cmbRelayPropertyChange(evt);
            }
        });
        jPanel8.add(cmbRelay);
        cmbRelay.setBounds(10, 70, 58, 22);
        jPanel8.add(edtIPRelay);
        edtIPRelay.setBounds(80, 70, 190, 22);

        jLabel7.setText(":");
        jPanel8.add(jLabel7);
        jLabel7.setBounds(270, 70, 10, 16);

        edtPortaRelay.setEditable(false);
        jPanel8.add(edtPortaRelay);
        edtPortaRelay.setBounds(280, 70, 60, 22);

        lstIPRelay.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(lstIPRelay);

        jPanel8.add(jScrollPane1);
        jScrollPane1.setBounds(10, 100, 330, 50);

        btnDelRelay.setText("-");
        btnDelRelay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelRelayActionPerformed(evt);
            }
        });
        jPanel8.add(btnDelRelay);
        btnDelRelay.setBounds(290, 20, 50, 25);

        btnAddRelay.setText("+");
        btnAddRelay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRelayActionPerformed(evt);
            }
        });
        jPanel8.add(btnAddRelay);
        btnAddRelay.setBounds(240, 20, 50, 25);

        jPanel3.add(jPanel8);
        jPanel8.setBounds(10, 160, 350, 160);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(" Rendezvous "));
        jPanel9.setLayout(null);

        cbkRendezvous.setText("Atuar como peer Rendezvous");
        jPanel9.add(cbkRendezvous);
        cbkRendezvous.setBounds(10, 20, 190, 25);

        jLabel8.setText("Lista");
        jPanel9.add(jLabel8);
        jLabel8.setBounds(10, 50, 26, 16);

        cmbRendezvous.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TCP", "HTTP" }));
        cmbRendezvous.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cmbRendezvousPropertyChange(evt);
            }
        });
        jPanel9.add(cmbRendezvous);
        cmbRendezvous.setBounds(10, 70, 58, 22);
        jPanel9.add(edtIPRendezvous);
        edtIPRendezvous.setBounds(80, 70, 190, 22);

        jLabel9.setText(":");
        jPanel9.add(jLabel9);
        jLabel9.setBounds(270, 70, 10, 16);

        edtPortaRendezvous.setEditable(false);
        jPanel9.add(edtPortaRendezvous);
        edtPortaRendezvous.setBounds(280, 70, 60, 22);

        lstIPRendezvous.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(lstIPRendezvous);

        jPanel9.add(jScrollPane2);
        jScrollPane2.setBounds(10, 100, 330, 50);

        btnDelRedezvous.setText("-");
        btnDelRedezvous.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelRedezvousActionPerformed(evt);
            }
        });
        jPanel9.add(btnDelRedezvous);
        btnDelRedezvous.setBounds(290, 20, 50, 25);

        btnAddRedezvous.setText("+");
        btnAddRedezvous.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRedezvousActionPerformed(evt);
            }
        });
        jPanel9.add(btnAddRedezvous);
        btnAddRedezvous.setBounds(240, 20, 50, 25);

        jPanel3.add(jPanel9);
        jPanel9.setBounds(10, 0, 350, 160);

        jTabbedPane1.addTab("Config. Peer", jPanel3);

        jPanel5.setBackground(new java.awt.Color(178, 168, 168));

        btnOK.setBackground(new java.awt.Color(178, 168, 168));
        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancela.setBackground(new java.awt.Color(178, 168, 168));
        btnCancela.setText("Cancela");
        btnCancela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(178, Short.MAX_VALUE)
                .addComponent(btnCancela, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancela))
                .addContainerGap(79, Short.MAX_VALUE))
        );

        jPanel7.setLayout(null);

        jLabel6.setText("Nome do peer");
        jPanel7.add(jLabel6);
        jLabel6.setBounds(10, 10, 90, 16);
        jPanel7.add(edtNome);
        edtNome.setBounds(10, 30, 350, 22);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setBounds(0, 0, 379, 504);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        
    }//GEN-LAST:event_formWindowClosed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        //chamo o metodo de processamento
        processar();       
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelaActionPerformed
        //saio do sistema quanto o botão cancelar for acionado
        System.exit(0);
    }//GEN-LAST:event_btnCancelaActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //saio do sistema quanto a janela for fechada
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void cbkInternetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbkInternetActionPerformed
        
    }//GEN-LAST:event_cbkInternetActionPerformed

    private void btnDelRelayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelRelayActionPerformed
        //retiro o ip de relay que está selecionado
        listaRelay.removeElement(lstIPRelay.getSelectedValue());
    }//GEN-LAST:event_btnDelRelayActionPerformed

    private void btnDelRedezvousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelRedezvousActionPerformed
        //retiro o ip de rendezVous que esta selecionado
        listaRendezvous.removeElement(lstIPRendezvous.getSelectedValue());
    }//GEN-LAST:event_btnDelRedezvousActionPerformed

    private void cmbRendezvousPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cmbRendezvousPropertyChange
        //seto a porta no campo de porta dependendo do que for selecionado na combobox
        edtPortaRendezvous.setText(cmbRendezvous.getSelectedIndex() == 0 ? "9701" : "9700");        
    }//GEN-LAST:event_cmbRendezvousPropertyChange

    private void cmbRelayPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cmbRelayPropertyChange
        //seto a porta no campo de porta dependendo do que for selecionado na combobox
        edtPortaRelay.setText(cmbRelay.getSelectedIndex() == 0 ? "9701" : "9700");
    }//GEN-LAST:event_cmbRelayPropertyChange

    private void btnAddRedezvousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRedezvousActionPerformed
        //adiciono um novo item na lista rendezvous
        listaRendezvous.addElement(cmbRendezvous.getSelectedItem().toString().toLowerCase() + "://" + edtIPRendezvous.getText() + ":" + edtPortaRendezvous.getText());
        lstIPRendezvous.setModel(listaRendezvous);        
    }//GEN-LAST:event_btnAddRedezvousActionPerformed

    private void btnAddRelayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRelayActionPerformed
        //adiciono um novo item na lista relay
        listaRelay.addElement(cmbRelay.getSelectedItem().toString().toLowerCase() + "://" + edtIPRelay.getText() + ":" + edtPortaRelay.getText());
        lstIPRelay.setModel(listaRelay);  
    }//GEN-LAST:event_btnAddRelayActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddRedezvous;
    private javax.swing.JButton btnAddRelay;
    private javax.swing.JButton btnCancela;
    private javax.swing.JButton btnDelRedezvous;
    private javax.swing.JButton btnDelRelay;
    private javax.swing.JButton btnOK;
    private javax.swing.JCheckBox cbkInternet;
    private javax.swing.JCheckBox cbkRedeLocal;
    private javax.swing.JCheckBox cbkRelay;
    private javax.swing.JCheckBox cbkRendezvous;
    private javax.swing.JComboBox cmbRelay;
    private javax.swing.JComboBox cmbRendezvous;
    private javax.swing.JTextField edtIPHTTP;
    private javax.swing.JTextField edtIPRelay;
    private javax.swing.JTextField edtIPRendezvous;
    private javax.swing.JTextField edtIPTCP;
    private javax.swing.JTextField edtNome;
    private javax.swing.JTextField edtPortaRelay;
    private javax.swing.JTextField edtPortaRendezvous;
    private javax.swing.JTextField edtSenha;
    private javax.swing.JTextField edtUsuario;
    private javax.swing.JPanel gpbSeguraca;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList lstIPRelay;
    private javax.swing.JList lstIPRendezvous;
    // End of variables declaration//GEN-END:variables

}
