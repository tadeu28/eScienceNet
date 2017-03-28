/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.semanticmanager;

import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import juniorvs.virtualdir.ControlaMensagens;
import juniorvs.virtualdir.EventoDescoberta;
import juniorvs.virtualdir.Mensagem;
import juniorvs.virtualdir.OuvinteDescoberta;
import juniorvs.virtualdir.OuvinteMensagem;
import juniorvs.virtualdir.ext.*;
import net.jxta.peergroup.PeerGroup;
import juniorvs.virtualdir.*;
import net.jxta.protocol.PeerGroupAdvertisement;

/**
 *
 * @author Tadeu Classe
 */
public class SemanticChat implements OuvinteMensagem, 
                                     Runnable,
                                     Mensagem,
                                     OuvinteDescoberta{

    public static final String GIVE_GROUPS = "givemeGroups";
    public static final String DELETE_GROUP = "deleteGroup";
    
    private PeerGroupManager peerGroupManager;
    private Chat chat;
    private ControlaMensagens controlaMensagens;
    private Peers peers;
    private String ontologyPath;
    private javax.swing.JTextArea jTextAreaInfo;
    private PeerGroup netPeerGroup;
    
    public SemanticChat(PeerGroup netPeerGroup, String ontologia){
        this.ontologyPath = ontologia;
        
        this.peerGroupManager = new PeerGroupManager(netPeerGroup, netPeerGroup);
        this.netPeerGroup = netPeerGroup;
        
        this.chat = new Chat(peerGroupManager, this);
        this.chat.addListener(this);
        
        controlaMensagens = new ControlaMensagens(netPeerGroup, peerGroupManager.getMyPeerName());
        controlaMensagens.setListener(this);
        
        this.peers = new Peers();
        
        Thread thread = new Thread(this);
        thread.start();
    }
    
    public boolean verificarExistenciaGrupoJxta(String nomeGrupo) {        
        try {
            PeerGroupAdvertisement adv = null;
            Enumeration en = netPeerGroup.getDiscoveryService().getLocalAdvertisements(netPeerGroup.getDiscoveryService().GROUP, null, null);
            
            if (en != null) {
                while (en.hasMoreElements()) {                    
                    adv = (PeerGroupAdvertisement) en.nextElement();
                    
                    if (nomeGrupo.equals(adv.getName())){
                        return true;
                    }
                 }
            }            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void sincronizarGruposRecepcao(String peerName, String owl){        
        try{             
            owl = owl.replace("[GROUP_ONTOLOGY]", "");        
            String path = ontologyPath.replace(SemanticManager.GROUP_ONTOLOGY, "TempOntology" + File.separator);
            
            File file = new File(path);             
            if (!file.exists()){
                file.mkdir();
            }
            
            try (FileWriter fw = new FileWriter(path + File.separator + peerName + ".xml")) {
                fw.write(owl);
            }
        }catch(Exception e){}
        
    }
    
    public void sincronizarGruposEnvio(){        
        try{
            String strOntology = "[GROUP_ONTOLOGY]";
            
            strOntology = strOntology + "\n" + SemanticManager.getInstance().obterXMLGruposOnOWL();
            
            controlaMensagens.enviarMensagem(strOntology.trim());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public void sincronizarGrupoExclusao(String msg, String user){
        String nomeGrupo = msg.replace(DELETE_GROUP, "").trim();        
        try{
            if (! SemanticManager.getInstance().removerGrupoOWL(nomeGrupo)){
                throw new Exception("Não foi possível realizar a exclusão do grupo " + nomeGrupo);
            }
            
            jTextAreaInfo.append(user.toUpperCase() + " (Mensagem): O Grupo " + nomeGrupo + " foi excluído da e-ScienceNet. "+
                                                      "Se seu peer se encontra nele, ao iniciar novamente o sistema você deverá selecionar um novo grupo de interesse. \n");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    @Override
    public void eventoMensagem(String user, String msg) {
        try{
            if (!SemanticManager.getInstance().getNomePeer().equals(user)){
                
                if (msg.equals(GIVE_GROUPS)){
                    this.sincronizarGruposEnvio();
                }else if (msg.indexOf(DELETE_GROUP) >= 0){
                    this.sincronizarGrupoExclusao(msg, user);
                }else if (msg.indexOf("[GROUP_ONTOLOGY]") >= 0){            
                    this.sincronizarGruposRecepcao(user, msg);
                }
            }
            
            System.out.println(user.toUpperCase() + " (Mensagem): " + msg + "\n");
        }catch(Exception e){}
    }

    @Override
    public void eventoEntradaPeer(String name) {
        if (! name.equals(peerGroupManager.getMyPeerName())) {
            if (this.peers.getPeer(name) == null){
                this.peers.atualizaPeer(name);
            }
        }
    }

    @Override
    public void eventoSaidaPeer(String name) {
        //removo o nó
        peers.removePeer(name);
    }

    @Override
    public void run() {
        while (true) {
            try {
                controlaMensagens.procuraPeers();
                Thread.sleep(10000);
            } catch (Exception iox) {
                    System.out.println("ERRO de execução: " + iox.getMessage() + " \n ");
            }
        }
    }

    @Override
    public void info(String str) {
        System.out.println(str + "\n");
    }

    @Override
    public void exibirMensagem(String str) {
        System.out.println(str + "\n");
    }

    @Override
    public void exibirMensagem(String str, String user) {
        System.out.println(str + " ->" + user + "\n");
    }
    
    public void requisitarGrupos(){        
        try {            
            controlaMensagens.enviarMensagem(GIVE_GROUPS);
            Thread.sleep(10000);            
            
            SemanticManager.getInstance().atualizarGrupos();
        } catch (InterruptedException ex) {
            Logger.getLogger(SemanticChat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void enviarExclusaoGrupo(String msg){
        controlaMensagens.enviarMensagem(DELETE_GROUP + "\n" + msg);
    }
    
    public void enviarMSG(String msg){
        controlaMensagens.enviarMensagem(msg);
    }
    
    public void exitOnSemanticChat(){
        controlaMensagens.saidaPeer();
    }

    @Override
    public void eventoDescoberta(EventoDescoberta event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setjTextAreaInfo(javax.swing.JTextArea jTextAreaInfo) {
        this.jTextAreaInfo = jTextAreaInfo;
    }
}
