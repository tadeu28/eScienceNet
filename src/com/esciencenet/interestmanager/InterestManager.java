package com.esciencenet.interestmanager;

import com.esciencenet.semanticmanager.*;
import com.esciencenet.forms.*;
import com.esciencenet.models.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JOptionPane;
import juniorvs.virtualdir.ControlaMensagens;
import juniorvs.virtualdir.Peers;
import juniorvs.virtualdir.ext.PeerGroupManager;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PeerGroupAdvertisement;

/**
 * Classe responsável por métodos de interesse de cada peer, gerenciando grupos
 * de interesses em conjunto com o Gerente de Semântica (Semantic Manager)
 * 
 * @author Tadeu Classe
 */
public class InterestManager {
    
    private PeerGroupManager pgm = null;
    private PeerGroupModel grupoSelecionado;
    private boolean cancelado = false;
    private ControlaMensagens controlaMensagens;
    private FWaitingResponse frmWatingReponse;
    private FDomainAvailable frmDomainAvailable;
    private FIncludeNewDomain frmIncludeNewDomain;
    private Peers peers;
    private InterestOperationEnum interestOperations;
    
    public static final String GIVEME_DOMAINS = "[GIVEME_DOMAINS]";
    public static final String RESPONSE_DOMAINS = "[RESPONSE_DOMAINS]";
    public static final String GIVEME_OWL = "[GIVEME_OWL]";
    public static final String RESPONSE_OWL = "[RESPONSE_OWL]";
    public static final String UP_DOMAIN_SN = "[UP_DOMAIN_SN]";
    public static final String UP_DOMAIN_SN_OK = "[UP_DOMAIN_SN_OK]";
    public static final String UP_DOMAIN_SN_ERROR = "[UP_DOMAIN_SN_ERROR]";
    
    /**
     * metodo construtor da classe
     */
    public InterestManager(){
        frmWatingReponse = new FWaitingResponse(null, true);
        frmDomainAvailable = new FDomainAvailable(null, true);
        frmIncludeNewDomain = new FIncludeNewDomain(null, true);
    } 
    
    /**
     * Método para exibição da tela de seleção e criação dos grupos
     */
    public void exibirGrupos(){
        //crio a lista de modelos grupos
        ArrayList<PeerGroupModel> peerGroupModels = SemanticManager.getInstance().obterGrupos();
        //crio o formulário de exibição
        FrmGrupos frmGrupos = new FrmGrupos(null, true, peerGroupModels);
        //exibo o formulário de exibição
        frmGrupos.setVisible(true);
        //verifico se foi cancelado
        this.cancelado = frmGrupos.isCancelado();
        if (!this.cancelado){
            //pego o grupo selecionado
            this.grupoSelecionado = frmGrupos.getGrupoSelecionado();        
        }
    }
    
    /**
     * Método para a criação de novos grupos 
     */
    public void criarGrupo(){
        //crio o formulario de novos grupos
        FrmCriarGrupos frmCriarGrupos = new FrmCriarGrupos(null, true);
        //exibo o formulário de novos grupos
        frmCriarGrupos.setVisible(true);
    }

    public PeerGroupAdvertisement obterGruposJXTA(PeerGroup  netPeerGroup, String nomeGrupo) {
        PeerGroupAdvertisement adv = null;
        try {            
            Enumeration en = netPeerGroup.getDiscoveryService().getLocalAdvertisements(netPeerGroup.getDiscoveryService().GROUP, null, null);
            
            if (en != null) {
                while (en.hasMoreElements()) {                    
                    adv = (PeerGroupAdvertisement) en.nextElement();
                    if (nomeGrupo.equals(adv.getName())){                       
                        break;
                    }
                    adv = null;
                 }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        return adv;
    }
    
    public void criarGrupoJXTA(PeerGroupModel peerGroupModel){
        
        peerGroupModel.setGroupCreator(SemanticManager.getInstance().getNomePeer());
        pgm.createPeerGroup(peerGroupModel.getGroupName(), true, "");        
        
        PeerGroupAdvertisement adv = this.obterGruposJXTA(pgm.getSelectedPeerGroup(), peerGroupModel.getGroupName());
        peerGroupModel.setGroupID(adv.getPeerGroupID().toString());
    }

    public void showDomains(){    
        String nomeSuperNo = SemanticManager.getInstance().getSuperNoByGroup(this.getGrupoSelecionado().getGroupName());
        
        this.interestOperations = InterestOperationEnum.showDomain;
        this.getControlaMensagens().enviarMensagem("<" + nomeSuperNo + ">#" + GIVEME_DOMAINS);
        this.getFrmWatingReponse().setVisible(true);
    }
        
    public void includeNewDomains(){
        String nomeSuperNo = SemanticManager.getInstance().getSuperNoByGroup(this.getGrupoSelecionado().getGroupName());

        this.interestOperations = InterestOperationEnum.newDomain;
        this.getControlaMensagens().enviarMensagem("<" + nomeSuperNo + ">#" + GIVEME_DOMAINS);
        this.getFrmWatingReponse().setVisible(true);
    }
    
    public void downloadDomain(String owl){
        String nomeSuperNo = SemanticManager.getInstance().getSuperNoByGroup(this.getGrupoSelecionado().getGroupName());
        
        this.getControlaMensagens().enviarMensagem("<" + nomeSuperNo + ">#" + GIVEME_OWL + "#" + owl);
        this.getFrmWatingReponse().setVisible(true);
    }
    
    public FindDomainFileModel checkExistenceDomainFile(FindDomainFileModel findDomainFileModel){
       
        File directory = new File(SemanticManager.getInstance().getDirOntology() + File.separator + SemanticManager.DOMAINS_OWL_DIR);
        if(directory.exists()){
            
            String[] arquivos = directory.list();
            
            for(String arquivo : arquivos){
                
                File owl = new File(directory.getAbsoluteFile() + File.separator + arquivo);
                
                if(findDomainFileModel.getNome().equals(owl.getName())){
                    
                    FindDomainFileModel findDomainFile = new FindDomainFileModel();
                    findDomainFile.setNome(owl.getName());
                    findDomainFile.setSize(owl.length() / 1024);
                    
                    return findDomainFile;
                }
            }
        }
        
        return null;
    }
    
    public void processNewDomain(String domainPath){
        try{
            String nomeSuperNo = SemanticManager.getInstance().getSuperNoByGroup(SemanticManager.getInstance().getInterestManager().getGrupoSelecionado().getGroupName());

            domainPath = domainPath.substring(domainPath.lastIndexOf("\\") + 1);

            File owlPath =  new File(SemanticManager.getInstance().getDirOntology() + File.separator  + SemanticManager.DOMAINS_OWL_DIR + File.separator + domainPath);

            if(!SemanticManager.getInstance().getDataManager().getGerenciaProcura().localContentExists(domainPath)){            
                SemanticManager.getInstance().getDataManager().getGerenciaProcura().addFile(owlPath);
            }
                        
            getControlaMensagens().enviarMensagem("<"+ nomeSuperNo +">#" + UP_DOMAIN_SN + "#" + domainPath);
            this.getFrmWatingReponse().setVisible(true);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void showDomainForm(){        
        FDomainOntology frmDomainOntology = new FDomainOntology(null, true);        
        frmDomainOntology.setVisible(true);
    }
    
    public void manageDomainInformationReceived(String infoReceived){
        try{
            infoReceived = infoReceived.substring(infoReceived.lastIndexOf("#") + 1, infoReceived.length());

            String owlFileStr = SemanticManager.getInstance().getDirOntology() + File.separator + SemanticManager.DOMAINS_OWL_DIR + File.separator + infoReceived;                

            File owlFile = new File(owlFileStr);        

            String ontologyURI = SemanticManager.getInstance().getOntologyURI(owlFile.toURI().toURL().toExternalForm());

            if (ontologyURI.equals("")){
                JOptionPane.showMessageDialog(null, "Did't is possible to persiste OWL file on repository, because the URI was lost.", ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PeerGroupModel peerGroupModel = new PeerGroupModel();
            peerGroupModel.setGroupName(this.getGrupoSelecionado().getGroupName());

            DomainFileModel domainFileModel = new DomainFileModel();
            domainFileModel.setDomainDescription("No Description, becaus this ontology was generated of automatic form.");
            domainFileModel.setDomainOWLFile(owlFileStr);
            domainFileModel.setGroup(peerGroupModel);
            domainFileModel.setDomainURI(ontologyURI);

            //String domainName = edtOWL.getText().substring(edtOWL.getText().lastIndexOf("/") + 1, edtOWL.getText().length());
            String domainName = infoReceived.replaceAll(".owl", "").replaceAll(" ", "").replaceAll("%20", "");
            domainFileModel.setDomainName(domainName);

            if (!SemanticManager.getInstance().gravarDomainInPeerOntology(domainFileModel)){            
                JOptionPane.showMessageDialog(null, "Did't is possible to save domain informations on PeerOntology.", ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            }   
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Did't is possible to save domain informations on PeerOntology.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void showMyDomainsOntologies(){
        FDomainOntology frmDomainOntology = new FDomainOntology(null, true);
        frmDomainOntology.setVisible(true);
    }            
            
    public void setPgm(PeerGroupManager pgm) {
        this.pgm = pgm;
    }

    public PeerGroupModel getGrupoSelecionado() {
        return grupoSelecionado;
    }
    
    public void setGrupoSelecionado(PeerGroupModel peerGroupModel){
        this.grupoSelecionado = peerGroupModel;
    }

    public boolean isCancelado() {
        return cancelado;
    }

    /**
     * @param controlaMensagens the controlaMensagens to set
     */
    public void setControlaMensagens(ControlaMensagens controlaMensagens) {
        this.controlaMensagens = controlaMensagens;
    }

    /**
     * @return the frmWatingReponse
     */
    public FWaitingResponse getFrmWatingReponse() {
        return frmWatingReponse;
    }

    /**
     * @return the controlaMensagens
     */
    public ControlaMensagens getControlaMensagens() {
        return controlaMensagens;
    }

    /**
     * @return the frmDomainAvailable
     */
    public FDomainAvailable getFrmDomainAvailable() {
        return frmDomainAvailable;
    }

    /**
     * @return the peers
     */
    public Peers getPeers() {
        return peers;
    }

    /**
     * @param peers the peers to set
     */
    public void setPeers(Peers peers) {
        this.peers = peers;
    }

    /**
     * @return the interestOperations
     */
    public InterestOperationEnum getInterestOperations() {
        return interestOperations;
    }

    /**
     * @return the frmIncludeNewDomain
     */
    public FIncludeNewDomain getFrmIncludeNewDomain() {
        return frmIncludeNewDomain;
    }
    
}
