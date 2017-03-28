/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.semanticmanager;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.esciencenet.compositionmanager.CompositionManager;
import com.esciencenet.forms.FrmAtualizar;
import com.esciencenet.interestmanager.*;
import com.esciencenet.models.*;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jxta.ext.config.Configurator;
import net.jxta.ext.config.TcpTransportAddress;
import net.jxta.ext.config.Transport;
import net.jxta.peergroup.PeerGroup;
import com.esciencenet.datamanager.*;
import com.esciencenet.searchmanager.*;
import com.esciencenet.servicemanager.OWLSOperation;
import com.esciencenet.servicemanager.OWLSParam;
import com.esciencenet.servicemanager.ServiceManager;
import com.esciencenet.servicemanager.wsdlmodels.WSDLRecoverParams;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerException;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.XSD;
import impl.jena.OWLDataPropertyImpl;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import juniorvs.virtualdir.ControlaMensagens;
import juniorvs.virtualdir.desktop.VirtualDir;
import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.grounding.AtomicGrounding;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * Gerente de Semântica responsável pela a criação de semântica na e-ScienceNet
 * @author Tadeu Classe
 */
public class SemanticManager {
 
    //atributos da classe
    public static final String GROUP_ONTOLOGY = "PeerOntology.owl";
    public static final String URI_PEERGROUPS_ONTOLOGY = "http://www.esciencenet.com.br/tadeuclasse/esciencenet/PeerOntology.owl#";
    public static final String DIRETORIO = "OntologyRepository";
    public static final String SERVICES_OWL_DIR = "Services";
    public static final String DOMAINS_OWL_DIR = "Domains";    
    public static final String DOMAIN_OWL_REPOSITORY = "DomainOntologyRepository";
    
    private static final SemanticManager semanticManager = new SemanticManager();    
    
    private String dirOntology;
    private InterestManager interestManager; 
    private DataManager dataManager;
    private SearchManager searchManager;
    private CompositionManager compositionManager;
    private String nomePeer;
    private SemanticChat semanticChat;
    private ServiceManager serviceManager;
    private FrmAtualizar frmAtualizar = null;
    private PeerGroup netPeerGroup = null;
    private OntModel modelMain = null;
    private String domainOntologyPath = "";
    
    /**
     * Método construtor da classe
     */
    private SemanticManager(){
        //crio o gerente de interesse
        this.interestManager = new InterestManager();
        //crio o gerente de dados
        this.dataManager = new DataManager();
        //crio o gerente de composição
        this.compositionManager = new CompositionManager();
    }
    
    /**
     * Retorna o objeto da classe através do Padrão de Projeto Singleton
     * 
     * @return SemanticManager
     */
    public static SemanticManager getInstance(){
        return semanticManager;
    }
    
    /**
     * Crio o repositório de ontologias
     * @param diretorio diretorio do JXTA
     */
    public void criarRepositorio(String diretorio){        
        try{
            //crio a string para a criação do diretorio
            String path = diretorio + File.separator + DIRETORIO;
            //crio o objeto de gravacao do diretorio        
            File ontologyPath = new File(path);

            //verifico se o diretorio ja existe
            if (!ontologyPath.exists()){
                //crio o repositorio de ontologias
                ontologyPath.mkdir();
            }
            
            //seto o a ontologia
            this.dirOntology = path;
            
            //repasso ao gerente de dados o diretorio do repositorio
            this.dataManager.carregarDownloadedFilesModel(diretorio);
            
            //crio o repositorio de OWL-s
            this.criarOWLSRepository(diretorio);  
            //crio o diretorio de dominio
            this.criarDomainRepository(diretorio);
            //crio o diretório de dominios
            this.criarSNDomainRepository(diretorio);
        }catch(Exception e){
            System.out.printf(e.getMessage());
        }
    }  
    
    private void criarSNDomainRepository(String diretorio){
        try{
            //crio a string para a criação do diretorio
            String path = diretorio + File.separator + DOMAIN_OWL_REPOSITORY;
            //crio o objeto de gravacao do diretorio        
            File domainDirectory = new File(path);

            //verifico se o diretorio ja existe
            if (!domainDirectory.exists()){
                //crio o repositorio de ontologias
                domainDirectory.mkdir();
            }
            
            this.domainOntologyPath = path;
        }catch(Exception e){
            System.out.printf(e.getMessage());
        }
    }
    
    /**
     * Crio o repositorio de OWL-S
     */
    private void criarOWLSRepository(String diretorio){
        try{
            //crio a string para a criação do diretorio
            String path = diretorio + File.separator + DIRETORIO + File.separator + SERVICES_OWL_DIR;
            //crio o objeto de gravacao do diretorio        
            File ontologyPath = new File(path);

            //verifico se o diretorio ja existe
            if (!ontologyPath.exists()){
                //crio o repositorio de ontologias
                ontologyPath.mkdir();
            }
        }catch(Exception e){
            System.out.printf(e.getMessage());
        }
    }
    
    /*
     * Crio o diretório de domínio
     */
    private void criarDomainRepository(String diretorio){
        try{
            //crio a string para a criação do diretorio
            String path = diretorio + File.separator + DIRETORIO + File.separator + DOMAINS_OWL_DIR;
            //crio o objeto de gravacao do diretorio        
            File ontologyPath = new File(path);

            //verifico se o diretorio ja existe
            if (!ontologyPath.exists()){
                //crio o repositorio de ontologias
                ontologyPath.mkdir();
            }
        }catch(Exception e){
            System.out.printf(e.getMessage());
        }
    }
    
    /**
     * Grava as informações do nós nas ontologias que precisam
     * 
     * @param config Configurador do JXTA com os dados para o Peer
     */
    public void gravarPeerOnOntology(Configurator config){
        try {
            //pego o protocolo de transporte do JXTA para a obtenção do IP do Peer
            List listTransports = config.getTransports();
            Transport transportTCP = (Transport) listTransports.get(0);
            TcpTransportAddress ts = (TcpTransportAddress) transportTCP.getAddresses().get(0);        

            //seto os dados do IP do peer para serem salvos na ontologia
            String peerID = config.getPeerId().toString();
            String peerName = config.getName();
            String peerIP = ts.getAddress().getHost();
            
            //seto o nome do peer para a classe
            this.setNomePeer(getNomePeer());

            //gravo as informações nas ontologias de grupo
            if (! gravarPeerOnOntologyGroup(peerID, peerName, peerIP)){            
                throw new Exception("Não foi possível realizar a gravação dos dados do peer na ontologia.");            
            }          
        } catch (Exception ex) {
            Logger.getLogger(SemanticManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Verifico se o peer ja existe na ontologia atraves de SPARQL
     * @param nome nome do peer
     * @return retorna se o peer ja existe na ontologia
     */
    private boolean verificaExistenciaDoPeerGroup(String nome){
        try{
            //crio o modelo ontologico
            OntModel model = this.getPeerOntModel();
            
            //monto uma query SparQL para a consulta na ontologia
            String sql = "PREFIX t: <"+ URI_PEERGROUPS_ONTOLOGY +">\n" +
                         "SELECT ?name ?id ?ip\n" +
                         "WHERE {\n" +
                         "\t?x t:peerName \""+ nome +"\".\n" +
                         "\t?x t:peerName ?name\n" +
                         " }";
            
            //Crio a query de consulta a ontologia
            Query query = QueryFactory.create(sql);

            //Executo a consulta na ontologia
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet results =  qe.execSelect();
            
            //percorro os resultados encontrados
            while(results.hasNext()){
                QuerySolution row = results.next();
                Literal colunm = row.getLiteral("name");
                
                //verifico se o nome encontrado bate com o nome do peer, se sim, eu saio e não preciso gravar os dados na ontologia novamente
                if (nome.equals(colunm.getString())){
                    return true;
                }
            }
            
            //fecho o componente de pesquisa
            qe.close();
            
            return false;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    /**
     * Gravo as informações na ontologia de grupos
     * 
     * @param peerID id do peer
     * @param nome nome do peer
     * @param ip ip do peer
     * @return retorna se foi possivel gravar corretamente na ontologia
     */
    private boolean gravarPeerOnOntologyGroup(String peerID, String nome, String ip){
        try{
            //abro o modelo de ontologia a partir do arquivo
            OntModel model = this.getPeerOntModel();
            
            //verifico se o peer existe na ontologia, isto consiste que eu não preciso gravar novamente
            if (verificaExistenciaDoPeerGroup(nome)) {
                return true;
            }
           
            //crio o novo individio
            Resource individuo = model.createResource(URI_PEERGROUPS_ONTOLOGY + nome);
            
            //seto a propriedade de nome do peer
            Property peerName = model.createProperty(URI_PEERGROUPS_ONTOLOGY + "peerName");            
            model.add(individuo, peerName, nome, XSDDatatype.XSDstring);
            
            //seto a propriedade de id do peer
            Property idPeer = model.createProperty(URI_PEERGROUPS_ONTOLOGY + "peerIdJXTA");                   
            model.add(individuo, idPeer, peerID, XSDDatatype.XSDstring);
            
            //seto a propriedade de IP do peer
            Property peerIP = model.createProperty(URI_PEERGROUPS_ONTOLOGY + "peerIP");                       
            model.add(individuo, peerIP, ip, XSDDatatype.XSDstring);
            
            //seto a propriedade de tipo do peer
            Resource tipoNamedIndividual = model.createResource("http://www.w3.org/2002/07/owl#NamedIndividual"); 
            Resource tipoClass = model.createResource(URI_PEERGROUPS_ONTOLOGY + "Peer");             
            model.add(individuo, RDF.type, tipoNamedIndividual);
            model.add(individuo, RDF.type, tipoClass);
            
            //gravo a ontologia com o novo peer criado em disco
            FileOutputStream fileOutputStream = new FileOutputStream(this.getPeerOWLPath());            
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    /**
     * Remove um grupo da ontologia de grupos
     * @param grupo nome do grupo a ser removido
     * @return se o grupo foi removido com sucesso
     */
    public boolean removerGrupoOWL(String grupo){
        try{
            //pego o modelo da ontologia do peer
            OntModel model = this.getPeerOntModel();
            
            //pego o individo da ontologia
            Individual group = model.getIndividual(URI_PEERGROUPS_ONTOLOGY + grupo);
            //removo o individuo da ontologia
            group.remove();
            
            PeerGroupModel peerGroupModel = this.verificarPeerGroup();
            if (peerGroupModel != null){
                Individual individual = model.getIndividual(URI_PEERGROUPS_ONTOLOGY + nomePeer);

                Property hasGroup = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "hasGroup" );

                if (individual.getProperty(hasGroup) != null){            
                    individual.getProperty(hasGroup).remove();
                }
            }
                        
            //gravo a ontologia novamente em arquivo
            FileOutputStream fileOutputStream = new FileOutputStream(this.getPeerOWLPath());            
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            
            return true;
        }catch(Exception e){
            System.out.printf(e.getMessage());
            return false;
        }
    }

    public boolean removerServico(String servico){
        try{
            //pego o modelo da ontologia do peer
            OntModel model = this.getPeerOntModel();
            
            //pego o individo da ontologia
            Individual domain = model.getIndividual(URI_PEERGROUPS_ONTOLOGY + servico.replace("Service", ""));
            
            Property serviceOWLSPath = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "serviceOWLSPath");            
            File owlsFile = new File(domain.getPropertyValue(serviceOWLSPath).asLiteral().getString());
            if(owlsFile.exists()){
                owlsFile.delete();
            }
            
            Property serviceWSDLPath = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "serviceWSDLPath");            
            File wsdlFile = new File(domain.getPropertyValue(serviceWSDLPath).asLiteral().getString());
            if(wsdlFile.exists()){
                wsdlFile.delete();
            }
            
            //removo o individuo da ontologia
            domain.remove();
            
            //gravo a ontologia novamente em arquivo
            FileOutputStream fileOutputStream = new FileOutputStream(this.getPeerOWLPath());            
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean removerDomainInOWL(String domainName){
        try{
            //pego o modelo da ontologia do peer
            OntModel model = this.getPeerOntModel();
            
            //pego o individo da ontologia
            Individual domain = model.getIndividual(URI_PEERGROUPS_ONTOLOGY + domainName);
            //removo o individuo da ontologia
            domain.remove();
            
            //gravo a ontologia novamente em arquivo
            FileOutputStream fileOutputStream = new FileOutputStream(this.getPeerOWLPath());            
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            
            return true;
        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean removerArquivoInOWL(String nome){
        try{
            String arquivoOWL = nome.replaceAll(" ", "").trim();
            
            //pego o modelo da ontologia do peer
            OntModel model = this.getPeerOntModel();
            
            //pego o individo da ontologia
            Individual arquivo = model.getIndividual(URI_PEERGROUPS_ONTOLOGY + arquivoOWL);
            //removo o individuo da ontologia
            arquivo.remove();
            
            //gravo a ontologia novamente em arquivo
            FileOutputStream fileOutputStream = new FileOutputStream(this.getPeerOWLPath());            
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Pesquisa os grupos de peers dentro de uma ontologia
     * @return Retorno uma lista com os grupos encontrados na ontologia
     */
    public ArrayList<PeerGroupModel> obterGrupos(){
        try{
            ArrayList<PeerGroupModel> peerGroupLst = new ArrayList<>();
            
            //pego o modelo da ontologia do peer
            OntModel model = this.getPeerOntModel();

            //monto uma query SparQL para a consulta na ontologia
            String sql = "PREFIX t: <"+ URI_PEERGROUPS_ONTOLOGY +">\n" +
                         "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                         "SELECT ?class ?member\n" +
                         "WHERE {?class a owl:Class.\n" +
                         "\t?member a ?class.\n"+
                         "\tfilter (?class in (t:Group))"+
                         "}";
            
            //Crio a query de consulta a ontologia
            Query query = QueryFactory.create(sql);

            //Executo a consulta na ontologia
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet results =  qe.execSelect();
            
            //percorro os resultados encontrados
            while(results.hasNext()){
                //pego a linha da ontologia retornada
                QuerySolution row = results.next();
                //pego a classe menbro
                Resource colunm = row.getResource("member");                
                
                //verifico se o recurso não é null
                if (colunm.getLocalName() != null){
                    //crio o modelo de grupos
                    PeerGroupModel group = new PeerGroupModel();                
                    group.setGroupName(colunm.getLocalName());                

                    //obtenho o restante das informações da ontologia de grupo
                    obterPeerGroupOnOntology(group);

                    //verifico se o grupo retornado não é nulo
                    if (group.getGroupName() != null){        
                        //salvo o grupo na lista de grupos
                        peerGroupLst.add(group);
                    }
                }
            }
            
            //fecho o objeto de pesquisa
            qe.close();
            
            //retorno a lista de grupos
            return peerGroupLst;
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Obtem um modelo de grupo para informações do mesmo
     * @param peerGroupModel modelo de grupo inicial
     * @param ontologia caminho da ontologia
     */
    private void obterPeerGroupOnOntology(PeerGroupModel peerGroupModel){
        try{           
            //pego o modelo da ontologia do peer
            OntModel model = this.getPeerOntModel();
           
            //monto uma query SparQL para a consulta na ontologia
            String sql = "PREFIX t: <"+ URI_PEERGROUPS_ONTOLOGY +">\n" +                         
                         "SELECT ?nomeGrupo ?id ?area ?desc ?autor ?data\n" +
                         "WHERE {?x t:groupName \""+ peerGroupModel.getGroupName() +"\".\n" +
                         "\t?x t:groupName ?nomeGrupo.\n" +
                         "\t?x t:groupIdJXTA ?id.\n" +
                         "\t?x t:groupArea ?area.\n" +
                         "\t?x t:groupDescription ?desc.\n" +
                         "\t?x t:groupWasCreated ?autor.\n" +
                         "\t?x t:groupCreationDate ?data.\n" +
                         "}";
            
            //Crio a query de consulta a ontologia
            Query query = QueryFactory.create(sql);

            //Executo a consulta na ontologia
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet results =  qe.execSelect();
                        
            //percorro os resultados encontrados
            while(results.hasNext()){
                //pego a lista da pesquisa
                QuerySolution row = results.next();
                
                //pego o nome do grupo
                Literal nome = row.getLiteral("nomeGrupo");                
                peerGroupModel.setGroupName(nome.getString());
                
                //pego o id do grupo
                Literal id = row.getLiteral("id");                
                peerGroupModel.setGroupID(id.getString());
                
                //pego a area do grupo
                Literal area = row.getLiteral("area");                
                peerGroupModel.setGroupArea(area.getString());
                
                //pego a descricao do grupo
                Literal desc = row.getLiteral("desc");                
                peerGroupModel.setGroupDescription(desc.getString());
                
                //pego a data de criacao
                Literal data = row.getLiteral("data");                
                peerGroupModel.setDataCriacao(data.getString());
                
                //pego o autor do grupo
                Resource autor = row.getResource("autor");                
                peerGroupModel.setGroupCreator(autor.getLocalName());
            }
            
            //fecho o objeto de pesquisa
            qe.close();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Gravo o grupo dentro da ontologia de grupo
     * @param peerGroup modelo de grupos para gravação
     * @param isToCreateJXTAGroupna ontologia
     */
    public void gravarGrupoInOWL(PeerGroupModel peerGroup, boolean isToCreateJXTAGroup){
        try{
            if (isToCreateJXTAGroup){
                //crio o grupo no jxta
                getInterestManager().criarGrupoJXTA(peerGroup);
            }

            //pego o modelo da ontologia do peer
            OntModel model = this.getPeerOntModel();

            Resource individuo = model.createResource(URI_PEERGROUPS_ONTOLOGY + peerGroup.getGroupName());

            Resource tp3 = model.createResource( "http://www.w3.org/2002/07/owl#NamedIndividual" ); 
            Resource tp4 = model.createResource( URI_PEERGROUPS_ONTOLOGY + "Group" ); 
            model.add(individuo, RDF.type, tp3);
            model.add(individuo, RDF.type, tp4);

            Property groupName = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "groupName" );
            model.add(individuo, groupName, peerGroup.getGroupName(), XSDDatatype.XSDstring);

            Property groupArea = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "groupArea" );                   
            model.add(individuo, groupArea, peerGroup.getGroupArea(), XSDDatatype.XSDstring);

            Property groupDescription = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "groupDescription" );                   
            model.add(individuo, groupDescription, peerGroup.getGroupDescription(), XSDDatatype.XSDstring);

            Property idGroup = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "groupIdJXTA" );                   
            model.add(individuo, idGroup, peerGroup.getGroupID(), XSDDatatype.XSDstring);
            
            if (peerGroup.getDataCriacao() != null){
                Property creationDate = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "groupCreationDate" );                   
                model.add(individuo, creationDate, (peerGroup.getDataCriacao()), XSDDatatype.XSDstring);
            }

            Property groupWasCreatedBy = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "groupWasCreated" );
            Resource groupWasCreatedByValue = model.createResource( URI_PEERGROUPS_ONTOLOGY + peerGroup.getGroupCreator());            
            model.add(individuo, groupWasCreatedBy, groupWasCreatedByValue);

            FileOutputStream fileOutputStream = new FileOutputStream(this.getPeerOWLPath());
            model.write(fileOutputStream, "RDF/XML-ABBREV");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public String obterXMLGruposOnOWL(){
        String retorno = "";
        
        //pego o modelo da ontologia do peer
        OntModel model = this.getPeerOntModel();
        
        OntClass group = model.getOntClass(URI_PEERGROUPS_ONTOLOGY + "Group");
        List<PeerGroupModel> lstPeerGroupModels = new ArrayList<PeerGroupModel>();

        Iterator i = null;
        for(i = group.listInstances(); i.hasNext();){
            Individual individuo = (Individual) i.next();

            PeerGroupModel peerGroup = new PeerGroupModel();

            Property groupName = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "groupName");
            peerGroup.setGroupName(individuo.getPropertyValue(groupName).asLiteral().getString());

            Property groupDescription = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "groupDescription");
            peerGroup.setGroupDescription(individuo.getPropertyValue(groupDescription).asLiteral().getString());

            Property groupArea = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "groupArea");
            peerGroup.setGroupArea(individuo.getPropertyValue(groupArea).asLiteral().getString());

            Property groupIdJXTA = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "groupIdJXTA");
            peerGroup.setGroupID(individuo.getPropertyValue(groupIdJXTA).asLiteral().getString());

            Property groupCreationDate = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "groupCreationDate");
            peerGroup.setDataCriacao(individuo.getPropertyValue(groupCreationDate) == null ? null : individuo.getPropertyValue(groupCreationDate).asLiteral().getString());

            Property groupWasCreated = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "groupWasCreated");
            peerGroup.setGroupCreator(individuo.getPropertyValue(groupWasCreated).asResource().toString().replace(URI_PEERGROUPS_ONTOLOGY, ""));

            lstPeerGroupModels.add(peerGroup);
        }
        
        XStream xstream = new XStream(new DomDriver());
        retorno = xstream.toXML(lstPeerGroupModels);
        
        return retorno;
    }
    
    public void processoAtualizacao(String msg, int progresso){
        frmAtualizar.atualizarProgresso(msg, progresso);
    }
    
    public void createSemanticChat(PeerGroup netPeerGroup){    
        //ontologia do grupo
        String ontologia = dirOntology + File.separator + GROUP_ONTOLOGY;
        ontologia = ontologia.replace(File.separator + File.separator, File.separator);
        ontologia = ontologia.replace(File.separator, "/");
        
        this.semanticChat = new SemanticChat(netPeerGroup, ontologia);        
    }   
        
    public boolean atualizarGrupos(){
        try{
            String path = dirOntology + File.separator + "TempOntology" + File.separator;
        
            XStream xstream = new XStream(new DomDriver());
            File diretorio = new File(path);
            ArrayList<PeerGroupModel> lstPeerGroupModels = null;

            if(diretorio.listFiles() == null){
                JOptionPane.showMessageDialog(null, "Any group finded or this peer no found groups in JXTA.", ".: e-ScienceNet :.", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            
            for (File file : diretorio.listFiles()){
                if (file.isFile()){
                    if (file.getName().indexOf(".xml") >= 0){                    

                        FileReader reader = new FileReader(file);  
                        String xml = "";
                        
                        try (BufferedReader input = new BufferedReader(reader)) {
                            String linha;  
                            while ((linha = input.readLine()) != null) {  
                                xml = xml + linha + "\n";  
                            }
                        } 

                        lstPeerGroupModels = (ArrayList<PeerGroupModel>) xstream.fromXML(xml);
                        
                        for (PeerGroupModel peerGroupModel : lstPeerGroupModels){
                            this.gravarGrupoInOWL(peerGroupModel, false);
                        }
                    }
                    
                    file.delete();
                }
            }
            
            diretorio.delete();
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }   
    
    public void gravarGrupoSelecionadoOWL(String grupo){
        try{
            //pego o modelo da ontologia do peer
            OntModel model = this.getPeerOntModel();

            Individual individual = model.getIndividual(URI_PEERGROUPS_ONTOLOGY + nomePeer);

            Property hasGroup = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "hasGroup" );
            
            if (individual.getProperty(hasGroup) != null){            
                individual.getProperty(hasGroup).remove();
            }

            Resource hasGroupValue = model.createResource( URI_PEERGROUPS_ONTOLOGY + grupo);            
            model.add(individual, hasGroup, hasGroupValue);
            
            FileOutputStream fileOutputStream = new FileOutputStream(this.getPeerOWLPath());
            model.write(fileOutputStream, "RDF/XML-ABBREV");
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
    public PeerGroupModel verificarPeerGroup(){
        try{
            //pego o modelo da ontologia do peer
            OntModel model = this.getPeerOntModel();

            Individual individual = model.getIndividual(URI_PEERGROUPS_ONTOLOGY + nomePeer);
            
            Property hasGroup = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "hasGroup" );
            
            if (individual.getProperty(hasGroup) == null){            
                return null;
            }else{
                PeerGroupModel peerGroupModel = new PeerGroupModel();
                String peerGroup = individual.getPropertyValue(hasGroup).asResource().getURI().replace(URI_PEERGROUPS_ONTOLOGY, "");
                peerGroupModel.setGroupName(peerGroup);
                return peerGroupModel;
            }
        }catch(Exception e){
            System.err.println(e);
            return null;
        }        
    }
    
    public List<DomainFileModel> obterDomainFiles(String groupName){
        try{            
            List<DomainFileModel> lstDomainFileModels = new ArrayList<>();
            
            OntModel data = this.getPeerOntModel();
            
            if (!groupName.equals("")){
            
                OntModel model = this.getPeerOntModel();
                
                Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();
                reasoner = reasoner.bindSchema(model);

                InfModel infmodel = ModelFactory.createInfModel(reasoner, data);


                String sql = "PREFIX t: <"+ URI_PEERGROUPS_ONTOLOGY +">\n" +
                         "SELECT ?hasDomain\n" +
                         "WHERE {?x t:groupName \""+ groupName +"\".\n" +                         
                         "\t?x t:hasDomain ?hasDomain.\n" +
                         "}";
                QueryExecution queryExec = QueryExecutionFactory.create(sql, infmodel); 
                ResultSet rs = queryExec.execSelect(); 

                while(rs.hasNext()) {
                    QuerySolution row = rs.nextSolution(); 
                    Resource domain = row.getResource("hasDomain");

                    if (domain.isResource()){
                        Individual ind = data.getIndividual(domain.toString());                        
                        
                        DomainFileModel domanFileModel = new DomainFileModel();
                        PeerGroupModel peerGroupModel = new PeerGroupModel();
                        peerGroupModel.setGroupName(groupName);
                        domanFileModel.setGroup(peerGroupModel);

                        Property domainName = data.getProperty(URI_PEERGROUPS_ONTOLOGY + "domainName");
                        domanFileModel.setDomainName(ind.getPropertyValue(domainName).asLiteral().getString());

                        Property fileOWLDomain = data.getProperty(URI_PEERGROUPS_ONTOLOGY + "fileOWLDomain");
                        domanFileModel.setDomainOWLFile(ind.getPropertyValue(fileOWLDomain).asLiteral().getString());
                        
                        Property domainURI = data.getProperty(URI_PEERGROUPS_ONTOLOGY + "domainURI");
                        domanFileModel.setDomainURI(ind.getPropertyValue(domainURI).asLiteral().getString());

                        Property domainDescription = data.getProperty(URI_PEERGROUPS_ONTOLOGY + "domainDescription");
                        domanFileModel.setDomainDescription(ind.getPropertyValue(domainDescription).asLiteral().getString());
                        
                        lstDomainFileModels.add(domanFileModel);
                    }
                }
            }else{
                
                OntClass ontClass = data.getOntClass(URI_PEERGROUPS_ONTOLOGY + "Domain");
                for (Iterator i = ontClass.listInstances(); i.hasNext();){
                    Individual ind = (Individual) i.next();
                    
                    DomainFileModel domanFileModel = new DomainFileModel();                    
                    domanFileModel.setGroup(SemanticManager.getInstance().getInterestManager().getGrupoSelecionado());

                    Property domainName = data.getProperty(URI_PEERGROUPS_ONTOLOGY + "domainName");
                    domanFileModel.setDomainName(ind.getPropertyValue(domainName).asLiteral().getString());

                    Property fileOWLDomain = data.getProperty(URI_PEERGROUPS_ONTOLOGY + "fileOWLDomain");
                    domanFileModel.setDomainOWLFile(ind.getPropertyValue(fileOWLDomain).asLiteral().getString());

                    Property domainDescription = data.getProperty(URI_PEERGROUPS_ONTOLOGY + "domainDescription");
                    domanFileModel.setDomainDescription(ind.getPropertyValue(domainDescription).asLiteral().getString());  
                    
                    lstDomainFileModels.add(domanFileModel);
                }                
            }
            
            return lstDomainFileModels;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public boolean gravarDomainInPeerOntology(DomainFileModel domainFileModel){
        try{           
            OntModel model = this.getPeerOntModel();
            Resource domain = model.createResource(URI_PEERGROUPS_ONTOLOGY + 
                              domainFileModel.getDomainName().replaceAll(" ", "").trim());

            Resource tp1 = model.createResource( "http://www.w3.org/2002/07/owl#NamedIndividual" ); 
            Resource tp2 = model.createResource( URI_PEERGROUPS_ONTOLOGY + "Domain" ); 
            model.add(domain, RDF.type, tp1);
            model.add(domain, RDF.type, tp2); 
            
            Property domainName = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "domainName" );
            model.add(domain, domainName, domainFileModel.getDomainName(), XSDDatatype.XSDstring);
            
            Property domainDescription = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "domainDescription" );
            model.add(domain, domainDescription, domainFileModel.getDomainDescription(), XSDDatatype.XSDstring);
            
            Property fileOWLDomain = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "fileOWLDomain" );
            model.add(domain, fileOWLDomain, domainFileModel.getDomainOWLFile(), XSDDatatype.XSDstring);
            
            Property domainURI = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "domainURI" );
            model.add(domain, domainURI, domainFileModel.getDomainURI(), XSDDatatype.XSDanyURI);
            
            Property isDomainOf = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "isDomainOf" );
            Resource isDomainOfValue = model.createResource( URI_PEERGROUPS_ONTOLOGY + 
                                                             domainFileModel.getGroup().getGroupName());            
            model.add(domain, isDomainOf, isDomainOfValue);
            
            FileOutputStream fileOutputStream = new FileOutputStream(this.getPeerOWLPath());
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public HashMap<String, String> recoveryWF(boolean isAWF, boolean isResults){
        try{
            HashMap<String, String> arquivos = new HashMap<>();
            
            OntModel model = this.getPeerOntModel();
            
            OntClass classe = model.getOntClass(URI_PEERGROUPS_ONTOLOGY + "Files");
                        
            for(Iterator ind = classe.listInstances(); ind.hasNext();){
                Individual individuo = (Individual) ind.next();
                
                Property fileExtension = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "fileExtension");
                String fileExtensionValue = individuo.getPropertyValue(fileExtension).asLiteral().getString();
                
                if(!isResults){
                    if(fileExtensionValue.equals(isAWF ? ".awf" : ".wkf")){

                        Property fileName = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "fileName");
                        String fileNameValue = individuo.getPropertyValue(fileName).asLiteral().getString();

                        Property filePath = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "filePath");
                        String filePathValue = individuo.getPropertyValue(filePath).asLiteral().getString();

                        arquivos.put(fileNameValue + fileExtensionValue, filePathValue);                    
                    }
                }else{
                    if(fileExtensionValue.equals(".wfr")){
                        Property fileName = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "fileName");
                        String fileNameValue = individuo.getPropertyValue(fileName).asLiteral().getString();

                        Property filePath = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "filePath");
                        String filePathValue = individuo.getPropertyValue(filePath).asLiteral().getString();

                        arquivos.put(fileNameValue + fileExtensionValue, filePathValue);  
                    }
                }
            }
            
            return arquivos;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public boolean gravarArquivoInOWL(FileModel fileModel){
        try{
            //pego o modelo da ontologia do peer
            OntModel model = this.getPeerOntModel();

            Resource arquivo = null;
            if(model.getIndividual(URI_PEERGROUPS_ONTOLOGY + fileModel.getName().replaceAll(" ", "").trim()) != null){
                                
                Individual individuo = model.getIndividual(URI_PEERGROUPS_ONTOLOGY + fileModel.getName().replaceAll(" ", "").trim());
                individuo.remove();
                
                arquivo = model.createResource(URI_PEERGROUPS_ONTOLOGY + fileModel.getName().replaceAll(" ", "").trim());
            }else{
                arquivo = model.createResource(URI_PEERGROUPS_ONTOLOGY + fileModel.getName().replaceAll(" ", "").trim());
            }
            
            Resource tp1 = model.createResource( "http://www.w3.org/2002/07/owl#NamedIndividual" ); 
            Resource tp2 = model.createResource( URI_PEERGROUPS_ONTOLOGY + "Files" ); 
            model.add(arquivo, RDF.type, tp1);
            model.add(arquivo, RDF.type, tp2);

            Property fileName = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "fileName" );
            model.add(arquivo, fileName, fileModel.getName(), XSDDatatype.XSDstring);
            
            Property filePath = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "filePath" );
            model.add(arquivo, filePath, fileModel.getPath(), XSDDatatype.XSDstring);
            
            Property fileDescription = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "fileDescription" );
            model.add(arquivo, fileDescription, fileModel.getDescription(), XSDDatatype.XSDstring);
            
            Property fileType = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "fileType" );
            model.add(arquivo, fileType, fileModel.getType(), XSDDatatype.XSDstring);
            
            Property fileSize = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "fileSize" );
            model.add(arquivo, fileSize, Double.toString(fileModel.getSize()), XSDDatatype.XSDdecimal);
            
            Property fileCreationDate = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "fileCreationDate" );
            model.add(arquivo, fileCreationDate, fileModel.getDate(), XSDDatatype.XSDstring);
            
            Property fileExtension = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "fileExtension" );
            model.add(arquivo, fileExtension, fileModel.getExtension(), XSDDatatype.XSDstring);
            
            Property pertenceOneGroup = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "pertenceOneGroup" );
            Resource pertenceOneGroupByValue = model.createResource( URI_PEERGROUPS_ONTOLOGY + fileModel.getPeerGroup().getGroupName());            
            model.add(arquivo, pertenceOneGroup, pertenceOneGroupByValue);
            
            Property isFileOf = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "isFileOf" );
            Resource isFileOfByValue = model.createResource( URI_PEERGROUPS_ONTOLOGY + fileModel.getPeerName());            
            model.add(arquivo, isFileOf, isFileOfByValue);
            
            FileOutputStream fileOutputStream = new FileOutputStream(this.getPeerOWLPath());
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public String getPeerOWLPath(){
        //localização da ontologia
        String ontologia = dirOntology + File.separator + GROUP_ONTOLOGY;
        ontologia = ontologia.replace(File.separator + File.separator, File.separator);
        ontologia = ontologia.replace(File.separator, "/");
        return ontologia;
    }

    public String getOntologyURI(String ontology){        
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(URI.create(ontology).getPath());                        
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
                        
            return ont.getOntologyID().getOntologyIRI().toString().concat("#").replaceAll("##", "#");
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return "";
        }
        
    }
    
    public OntModel getPeerOntModel(){
        try{
            //verifico se ja foi instanciado
            if (modelMain == null){
                //crio o modelo da ontologia
                modelMain = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
                modelMain.read("file:///" + this.getPeerOWLPath());
            }
            
            return modelMain;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public PeerGroupModel getPeerGroup(String peerName){
        try{
            //pego o modelo da ontologia de peers
            OntModel model = this.getPeerOntModel();
            
            //monto uma queri para pesquisa o grupo de um determinado peer
            String sql = "PREFIX t: <http://www.esciencenet.com.br/tadeuclasse/esciencenet/PeerOntology.owl#>\n" +
                         "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                         "SELECT * WHERE { \n" +
                         "\t	{\n" +
                         "\t\t		SELECT ?group where {\n" +
                         "\t\t\t		?x rdf:type t:Peer . \n" +  
                         "\t\t\t		?x t:peerName \""+ peerName +"\" . \n" +
                         "\t\t\t		?x t:hasGroup ?group . \n" +
                         "\t\t      	}\n" +
                         "\t	}\n" +
                         "\t	?group t:groupCreationDate ?date ; \n" +
                         "\t\t      t:groupName ?name ; \n" +
                         "\t\t      t:groupArea ?area ; \n" +
                         "\t\t      t:groupDescription ?desc ; \n" +
                         "\t\t      t:groupWasCreated ?creator ; \n" +
                         "\t\t      t:groupIdJXTA ?jxta \n" +
                         "}";
            
             //Executo a consulta na ontologia
            QueryExecution qe = QueryExecutionFactory.create(sql, model);
            ResultSet results =  qe.execSelect();
                        
            //crio o modelo
            PeerGroupModel peerGroupModel = new PeerGroupModel();
            
            //percorro os resultados encontrados
            while(results.hasNext()){
                //pego a lista da pesquisa
                QuerySolution row = results.next();
                
                //pego o nome do grupo
                Literal nome = row.getLiteral("name");                
                peerGroupModel.setGroupName(nome.getString());
                
                Literal date = row.getLiteral("date");                
                peerGroupModel.setDataCriacao(date.getString());
                
                Literal area = row.getLiteral("area");                
                peerGroupModel.setGroupArea(area.getString());
                
                Literal desc = row.getLiteral("desc");                
                peerGroupModel.setGroupDescription(desc.getString());
                
                Literal jxta = row.getLiteral("jxta");                
                peerGroupModel.setGroupID(jxta.getString());
                
                Resource autor = row.getResource("creator");                
                peerGroupModel.setGroupCreator(autor.getLocalName());
            }
            
            //fecho o objeto de pesquisa
            qe.close();
            
            return peerGroupModel;
        }catch(Exception e){            
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public List<FileModel> buscarArquivos(String termoBusca, String localPeerName, List<String> lstOpcoes){
        try{
            //crio o modelo ontologico
            OntModel model = this.getPeerOntModel();
            
            //tento obter o modelo do grupo
            PeerGroupModel peerGroupModel = getPeerGroup(localPeerName);
            
            //variavel com os filtros
            String filter = "";
            
            //percorro a lista de fitros
            for (String item : lstOpcoes){
                //verifico qual o tipo de filtro montando dentro do SPARQL
                switch (item.toLowerCase()){
                    case "size":{
                        try{
                            //o Sparql não aceita integer como sendo string, então faço essa gabiarra só para verificar se o termo é um numero
                            Double.parseDouble(termoBusca);
                            filter = (filter.isEmpty() ? filter + "\n (?"+ item.toLowerCase() +" = "+ termoBusca +")" : filter + " || \n (?"+ item.toLowerCase() +" = "+ termoBusca +")");
                            break;
                        }catch(Exception e){
                            break;
                        }                        
                    } 
                    default:{
                        filter = (filter.isEmpty() ? filter + "\n regex(str(?"+ item.toLowerCase() +"), \""+ termoBusca +"\", \"i\")" : filter + " || \n regex(str(?"+ item.toLowerCase() +"), \""+ termoBusca +"\", \"i\")"); 
                        break;
                    }
                }
                
            }
            
            //monto uma query SparQL para a consulta na ontologia           
            String sql = "PREFIX t: <http://www.esciencenet.com.br/tadeuclasse/esciencenet/PeerOntology.owl#>\n" +                                                  
                         "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"+
                         "SELECT ?name ?extension ?date ?size ?description ?type ?path where {"
                    + "?group t:pertenceOneGroup t:"+ peerGroupModel.getGroupName() +" .\n"
                    + "?group rdf:type t:Files . \n"
                    +" ?group t:fileName ?name . \n"
                    +" ?group t:fileExtension ?extension . \n"
                    +" ?group t:fileCreationDate ?date . \n"
                    +" ?group t:fileDescription ?description . \n"
                    +" ?group t:fileSize ?size . \n"                    
                    +" ?group t:fileType ?type . \n"     
                    +" ?group t:filePath ?path . \n"     
                    +" ?group t:isFileOf t:"+ localPeerName +" . \n"   
                    +" FILTER ("+ filter +") \n"
                    + "}\n ";
            
            //Crio a query de consulta a ontologia
            Query query = QueryFactory.create(sql);

            //Executo a consulta na ontologia
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet results =  qe.execSelect();
    
            //crio uma lista com os arquivos encontrados pela pesquisa
            List<FileModel> lstFilesFinded = new ArrayList<>();
            
            //percorro os resultados encontrados
            while(results.hasNext()){
                
                //crio um modelo de arquivos para retornar da pesquisa
                FileModel fileModel = new FileModel();
                
                //pego a lista da pesquisa
                QuerySolution row = results.next();
                
                //pego a propriedade de nome
                Literal name = row.getLiteral("name");
                fileModel.setName(name.getString());
                
                //pego a propriedade de data
                Literal date = row.getLiteral("date");
                fileModel.setDate(date.getString());
                
                //pego a propriedade de tamanho
                Literal size = row.getLiteral("size");
                fileModel.setSize(size.getDouble());
                
                //pego a propriedade de descricao
                Literal description = row.getLiteral("description");
                fileModel.setDescription(description.getString());
                
                //pego a propriedade de tipo
                Literal type = row.getLiteral("type");
                fileModel.setType(type.getString());
                
                //pego a propriedade de path
                Literal path = row.getLiteral("path");
                fileModel.setPath(path.getString());
                
                //pego a propriedade de path
                Literal extension = row.getLiteral("extension");
                fileModel.setExtension(extension.getString());                
                
                //coloco o nome do peer
                fileModel.setPeerName(localPeerName);
                //seto o grupo
                fileModel.setPeerGroup(peerGroupModel);
                
                //adiciono na lista de arquivos encontrados
                lstFilesFinded.add(fileModel);                
            }
            
            //fecho o componente de pesquisa
            qe.close();
            
            return lstFilesFinded;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
         
    public void verificarClass(String fileName, DomainFileModel domainFileModel, String serviceDesc, List<DomainFileModel> lstDomainFileModels ){
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(fileName);            
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph );
           
            String filter = "";
            if (!serviceDesc.equals("")){
                
                filter = "FILTER (\n";
                
                StringTokenizer suggestTok = new StringTokenizer(serviceDesc, ";");                
                while(suggestTok.hasMoreTokens()){
                    filter = filter + "\t regex(str(?class), \""+ suggestTok.nextToken() +"\", \"i\") || \n";
                }
                
                if(filter.lastIndexOf("||") != -1){
                    filter = filter.trim();
                    filter = filter.substring(0, filter.length() - ("||").length());
                    filter = filter.trim();
                }
                
                filter = filter + "\n\t)\n";
            }
            
            String sql = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +                         
                         "PREFIX t: <"+ domainFileModel.getDomainURI() +">\n" +
                         "SELECT ?equiv \n" +
                         "WHERE {?class owl:equivalentClass ?equiv.\n "+ filter+" }\n"+
                         "ORDER BY (?equiv)";
                
            //Executo a consulta na ontologia
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
            
            while(results.hasNext()){
                //pego a lista da pesquisa
                QuerySolution row = results.next();
                
                Resource equiv = row.getResource("equiv");
                
                if (equiv.getLocalName() != null){
                    DomainFileModel domainRelated = new DomainFileModel();
                    
                    domainRelated.setDomainURI(equiv.getURI().toString());
                    domainRelated.setDomainName(equiv.getLocalName());
                    domainRelated.setDomainOWLFile(fileName);
                    
                    lstDomainFileModels.add(domainRelated);
                }
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public List<DomainFileModel> searchServiceDomain(DomainFileModel domainFileModel, String serviceDesc){
        try{

            List<DomainFileModel> lstDomainFileModels = new ArrayList<>();
            this.verificarClass(domainFileModel.getDomainOWLFile(), domainFileModel, serviceDesc, lstDomainFileModels);
            
            return lstDomainFileModels;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public List<OWLClassesModel> searchInFullInferredDomain(DomainFileModel domainFileModel){
        try{
            List<OWLClassesModel> lstOWLClassesModels = new ArrayList<>();
            
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(domainFileModel.getDomainOWLFile());            
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(owlFile);
            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner( ontology );
            Node<OWLClass> owlClasses = pelletReasoner.getTopClassNode();            
            
            this.preencheListaClassesDominio(lstOWLClassesModels, owlClasses, pelletReasoner, "", domainFileModel.getDomainURI());
            
            return lstOWLClassesModels;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    private void preencheListaClassesDominio(List<OWLClassesModel> classes, Node<OWLClass> parent, OWLReasoner reasoner, String superClasse, String domainUri) {
        
        DefaultPrefixManager prefixManager = new DefaultPrefixManager(domainUri);
        
        //verifico se é o ultimo nível da classe
        if (parent.isBottomNode()) {
            return;
        }
        
        //crio a string da classe
        String strClasse  = "";
        String strClassURI = "";
        //percorro as classes desse no
        for (Iterator<OWLClass> it = parent.getEntities().iterator(); it.hasNext();) {
            //pego a descricao da classe
            OWLClass cls = it.next();
                       
            //encurto a mesma
            strClasse = strClasse + prefixManager.getShortForm(cls).replace(":", "").trim();
            //pego a URI das classes
            strClassURI = strClassURI + cls.getIRI().toString();
            
            //verifico se a classe tem sua equivalente
            if (it.hasNext()) {
                //seto o sinal de que elas são equivalentes
                strClasse = strClasse + " ≡ ";
                strClassURI = strClassURI + " ≡ ";
            }
        }  

        //obtenho a classe a partir de seu nome. Neste caso tento obter a classe pai
        OWLClassesModel classe = OWLClassesModel.verificarOWLClass(classes, superClasse);
        //verifico se foi retornada a classe pai
        if (classe.getName() != null){
            
            //crio a subClasse
            OWLClassesModel subClass = null;
            
            //tento pegar a classe
            OWLClassesModel classExists = OWLClassesModel.verificarOWLClass(classes, strClasse);
            //verifico se a classe já existe em algum local da ontologia
            if (classExists.getName() != null){
                //seto a subClasse para a classe existente
                subClass = classExists;
            }else{
                //crio a subClasse
                subClass = new OWLClassesModel();
                //salvo o nome da classe
                subClass.setName(strClasse);
                subClass.setSuperClass(superClasse);
                subClass.setUri(strClassURI);
            }   
            
            //tento pegar a classe dentro da superClasse corrente
            OWLClassesModel classExistsInParent = OWLClassesModel.verificarOWLClass(OWLClassesModel.verificarOWLClass(classes, superClasse).getSubClasses(), strClasse);
            //verifico se essa classe já existe dentro da superClasse
            if (classExistsInParent.getName() == null){
                //gravo a classe na lista
                classe.getSubClasses().add(subClass);
            }            
        }else{
            //salvo o nome da classe
            classe.setName(strClasse);
            classe.setSuperClass(superClasse);
            classe.setUri(strClassURI);
            //gravo a classe na lista
            classes.add(classe);
        }
        
        //seto como superClasse a classe atual
        superClasse = strClasse;
        
        //verifico se o pai tem subClasses, percorrendo-as
        for (Node<OWLClass> child : reasoner.getSubClasses(parent.getRepresentativeElement(), true)) {
            
            //chamo a função recursivamente para as outras classes
            preencheListaClassesDominio(classes, child, reasoner, superClasse, domainUri);
        }
    }
    
    public boolean persistServiceInPeerOntology(ServicesInfoModel servicesInfoModel, boolean isConnector){
        try{
            OntModel model = this.getPeerOntModel();
            
            Resource ws = model.createResource(URI_PEERGROUPS_ONTOLOGY + servicesInfoModel.getWsdlServiceName());

            Resource tp2 = null;
            if (servicesInfoModel.getWsdlFile().contains("wsdl")){            
                tp2 = model.createResource( URI_PEERGROUPS_ONTOLOGY + "WSDL" ); 
            }else{
                tp2 = model.createResource( URI_PEERGROUPS_ONTOLOGY + "REST" ); 
            }
            Resource tp1 = model.createResource( "http://www.w3.org/2002/07/owl#NamedIndividual" );
            
            model.add(ws, RDF.type, tp1);
            model.add(ws, RDF.type, tp2);
            
            if(isConnector){
                Resource tp3 = model.createResource( URI_PEERGROUPS_ONTOLOGY + "Connectors" );
                model.add(ws, RDF.type, tp3);
            }

            Property pertenceOneGroup = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "pertenceOneGroup" );
            Resource pertenceOneGroupByValue = model.createResource( URI_PEERGROUPS_ONTOLOGY + this.getInterestManager().getGrupoSelecionado().getGroupName());            
            model.add(ws, pertenceOneGroup, pertenceOneGroupByValue);
            
            Property isServiceOf = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "isServiceOf" );
            Resource isServiceOfValue = model.createResource( URI_PEERGROUPS_ONTOLOGY + this.getNomePeer());            
            model.add(ws, isServiceOf, isServiceOfValue);
            
            Property serviceOWLSPath = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "serviceOWLSPath" );
            model.add(ws, serviceOWLSPath, servicesInfoModel.getOwlsFile(), XSDDatatype.XSDstring);
            
            Property serviceWSDLPath = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "serviceWSDLPath" );
            model.add(ws, serviceWSDLPath, servicesInfoModel.getWsdlFile(), XSDDatatype.XSDstring);
            
            Property serviceWSDLURL = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "serviceWSDLURL" );
            model.add(ws, serviceWSDLURL, servicesInfoModel.getWsdlAddress(), XSDDatatype.XSDstring);
            
            if(isConnector){
                String serviceName = servicesInfoModel.getDomainName();
                if(serviceName.indexOf("-") != -1){
                    serviceName = serviceName.substring(0, serviceName.indexOf("-")).trim();

                    Property isConnectorOf = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "isConnectorOf" );
                    Resource isConnectorOfValue = model.createResource( URI_PEERGROUPS_ONTOLOGY + serviceName);
                    model.add(ws, isConnectorOf, isConnectorOfValue);
                }
            }else{
                Property serviceDomainMainTerm = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "serviceDomainMainTerm" );
                model.add(ws, serviceDomainMainTerm, servicesInfoModel.getDomainName(), XSDDatatype.XSDstring);
            }
            
            FileOutputStream fileOutputStream = new FileOutputStream(this.getPeerOWLPath());
            model.write(fileOutputStream, "RDF/XML-ABBREV");           
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Wasn't possible record service information on PeerOntology.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public List<ServicesModelView> searchServiceInfoInPeerOWL(String group, boolean isConnectorShow){
        try{
            List<ServicesModelView> lstServicesInfoModels = new ArrayList<>();
            
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(this.getPeerOWLPath());            
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);            

            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph );
            
            String sql = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX t: <"+ URI_PEERGROUPS_ONTOLOGY +">\n" +
                        "SELECT ?service ?owlPath ?wsdlPath \n" +
                        "WHERE {t:"+ this.getNomePeer() +" t:hasService ?service. \n"+
                        "\t     ?service t:serviceOWLSPath ?owlPath. \n"+
                        "\t     ?service t:serviceWSDLPath ?wsdlPath. \n";
            
            if(!group.equals("")){
                sql = sql + "\t t:"+ group +" t:groupHasResources ?service. \n" ;
            }
            
            sql = sql + "}";
                
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
            
            //ResultSetFormatter.out(System.out, results);
            
            while(results.hasNext()){
                
                //pego a lista da pesquisa
                QuerySolution row = results.next();
                
                if(row.get("service") instanceof Resource){                
                    Resource service = row.getResource("service");
                    
                    OntModel model = this.getPeerOntModel();
                    OntClass connectors = model.getOntClass(URI_PEERGROUPS_ONTOLOGY + "Connectors");
                    
                    boolean connectorFinded = false;                    
                    String serviceRelated = ""    ;
                    for(Iterator it = connectors.listInstances(); it.hasNext();){
                        Resource individual = (Resource) it.next();

                        if(individual.equals(service)){
                            
                            Property isConnectorOf = model.createProperty( URI_PEERGROUPS_ONTOLOGY + "isConnectorOf" );                            
                            Resource resource = individual.getPropertyResourceValue(isConnectorOf);
                            if(resource != null){
                                serviceRelated = resource.getLocalName();
                            }
                            
                            connectorFinded = true;
                            break;
                        }
                    }
                
                    if(!isConnectorShow){
                        if(connectorFinded){
                            continue;
                        }
                    }
                        
                    String owlsPath = row.getLiteral("owlPath").getString();
                    
                    ServicesModelView servicesModelView = new ServicesModelView();
                    servicesModelView.setServiceRelated(serviceRelated);
                    
                    if(! this.owlsData(owlsPath, service.getLocalName(), servicesModelView, connectorFinded)){
                        return null;
                    }
                    
                    lstServicesInfoModels.add(servicesModelView);
                }
            }
            
            return lstServicesInfoModels;
        }catch(OWLOntologyCreationException | ReasonerException e){
            JOptionPane.showMessageDialog(null, "Wasn't possible to find Service information at "+ group +".\n\n" + e, 
                    ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }        
    }
    
    public List<OWLSOperation> searchByConnectorsInPeerOWL(String groupName, String serviceName, List<OWLSParam> owlsParams, boolean isToCompareInput){
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(this.getPeerOWLPath());            
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);            

            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );
            
            InfModel infModel = ModelFactory.createInfModel( graph );
            
            String sql = "PREFIX t: <"+ URI_PEERGROUPS_ONTOLOGY +">\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "SELECT  *\n" +
                        "WHERE\n" +
                        "  { t:"+ groupName +"  t:groupHasResources  ?connector.\n" +
                        "    ?connector rdf:type t:Connectors.\n" +
                        "    ?connector t:serviceOWLSPath ?owlsPath.\n" +
                        (serviceName.equals("") ? "" : "    ?connector t:isConnectorOf t:"+ serviceName +".\n")+                        
                        "}";
           
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
           
            List<OWLSOperation> lstOWLSOp = new ArrayList<>();
            
            while(results.hasNext()){
                
                QuerySolution row = results.next();
                
                OWLSOperation owlsOperation = this.serviceSemanticResearch(row.getLiteral("owlsPath").getString(), "", false);
                
                if(owlsOperation != null){
                    
                    List<OWLSParam> lstSubParamenters = new ArrayList<>();
                    for(OWLSParam input : owlsOperation.getInputs()){
                        if(input.getParamDomainTerm().contains("Thing")){
                            List<OWLSParam> subParams = SemanticManager.getInstance().getSubParameterOnSemanticSearch(input, row.getLiteral("owlsPath").getString());
                            if(subParams != null){
                                if(!subParams.isEmpty()){
                                    lstSubParamenters.addAll(subParams);
                                }
                            }
                        }
                    }

                    if(!lstSubParamenters.isEmpty()){                                
                        owlsOperation.getInputs().addAll(lstSubParamenters);                                
                    }

                    lstSubParamenters.clear();
                    for(OWLSParam output : owlsOperation.getOutputs()){
                        if(output.getParamDomainTerm().contains("Thing")){
                            List<OWLSParam> subParams = SemanticManager.getInstance().getSubParameterOnSemanticSearch(output, row.getLiteral("owlsPath").getString());
                            if(subParams != null){
                                if(!subParams.isEmpty()){
                                    lstSubParamenters.addAll(subParams);
                                }
                            }
                        }
                    }

                    this.removerThing(owlsOperation);
                    
                    if(!lstSubParamenters.isEmpty()){                                
                        owlsOperation.getOutputs().addAll(lstSubParamenters);                                
                    }
                    
                    if(isToCompareInput){
                        String compatibility = this.getCompositionManager().compareSemanticConnectorParams(owlsOperation.getOutputs(), owlsParams);
                        if(!compatibility.equals("")){
                            owlsOperation.setCompatibility(compatibility);
                            lstOWLSOp.add(owlsOperation);
                        }
                    }else{ 
                        String compatibility = this.getCompositionManager().compareSemanticConnectorParams(owlsParams, owlsOperation.getInputs());
                        if(!compatibility.equals("")){
                            owlsOperation.setCompatibility(compatibility);
                            lstOWLSOp.add(owlsOperation);
                        }
                    }
                }else{
                    return null;
                }                
            }
            
            return lstOWLSOp;
        }catch(OWLOntologyCreationException | ReasonerException e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to find Connectors information.\n\n" + e, 
                    ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }        
    }
    
    private void removerThing(OWLSOperation owlsOperation){
        boolean thingFind = true;
        while(thingFind){    
            thingFind = false;

            for(OWLSParam input : owlsOperation.getInputs()){
                if(input.getParamDomainTerm().contains("Thing")){
                    owlsOperation.getInputs().remove(input);
                    thingFind = true;
                    break;                                        
                }
            }

            for(OWLSParam output : owlsOperation.getOutputs()){
                if(output.getParamDomainTerm().contains("Thing")){
                    owlsOperation.getOutputs().remove(output);
                    thingFind = true;
                    break;                                        
                }
            }
        }
    }
    
    public boolean owlsData(String owlsPath, String serviceName, ServicesModelView servicesModelView, boolean connectorFinded){
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(owlsPath);
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);            

            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph );                      
            
            String sql = "";
            if(connectorFinded){
                sql = "PREFIX service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>\n" +
                         "PREFIX profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>\n" +
                         "PREFIX process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>\n" +
                         "PREFIX owls: <"+ ont.getOntologyID().getOntologyIRI().toString() +"#>\n" +
                         "SELECT ?service ?domainServiceTerm ?profile ?parameter ?parameterType\n" +
                         "	WHERE {?individual a service:Service.\n" +
                         "\t ?service  service:presents ?profile.\n" +
                         "\t ?profile profile:hasParameter ?parameter.\n" +
                         "\t ?parameter process:parameterType ?parameterType}";
            }else{
            
                sql = "PREFIX service: <http://www.daml.org/services/owl-s/1.1/Service.owl#>\n" +
                             "PREFIX profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>\n" +
                             "PREFIX process: <http://www.daml.org/services/owl-s/1.1/Process.owl#>\n" +
                             "PREFIX owls: <"+ ont.getOntologyID().getOntologyIRI().toString() +"#>\n" +
                             "SELECT ?service ?domainServiceTerm ?profile ?parameter ?parameterType\n" +
                             "	WHERE {?individual a service:Service.\n" +
                             "\t ?service owls:domainTerm ?domainServiceTerm.\n" +
                             "\t ?service  service:presents ?profile.\n" +
                             "\t ?profile profile:hasParameter ?parameter.\n" +
                             "\t ?parameter process:parameterType ?parameterType}";
            }
                
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
            
            while(results.hasNext()){
                QuerySolution row = results.next();
                
                if(servicesModelView.getServiceName().equals("")){
                    servicesModelView.setServiceName(row.getResource("service").getLocalName());
                }
                
                if(servicesModelView.getOwlsName().equals("")){
                    servicesModelView.setOwlsName(serviceName + ".owl");
                }
                
                servicesModelView.setConnector(true);                
                if(row.getLiteral("domainServiceTerm") != null){
                    String domainCompleteTerm = row.getLiteral("domainServiceTerm").getString();                
                    DomainsRelations domainsRelations = new DomainsRelations();
                    domainsRelations.setDomainTermName(domainCompleteTerm.substring(domainCompleteTerm.lastIndexOf("#") + 1));
                    domainsRelations.setDomainOntology(domainCompleteTerm.substring(domainCompleteTerm.lastIndexOf("/") + 1, domainCompleteTerm.lastIndexOf("#")));                

                    if(servicesModelView.indexOf(domainsRelations) == -1){
                        servicesModelView.getDomainsRelations().add(domainsRelations);
                    }                    
                    
                    servicesModelView.setConnector(false);
                }
                
                ParamServiceModel param = new ParamServiceModel();
                param.setParamName(row.getResource("parameter").getLocalName());                
                
                String parameterType = row.getLiteral("parameterType").getString();                
                if (! parameterType.contains("XMLSchema")){
                    DomainsRelations domainsRelationsParam = new DomainsRelations();
                    domainsRelationsParam.setDomainTermName(parameterType.substring(parameterType.lastIndexOf("#") + 1));
                    domainsRelationsParam.setDomainOntology(parameterType.substring(parameterType.lastIndexOf("/") + 1, parameterType.lastIndexOf("#")));

                    int indexOf = servicesModelView.indexOf(domainsRelationsParam);
                    if(indexOf == -1){
                        servicesModelView.getDomainsRelations().add(domainsRelationsParam);
                        param.setParamType(domainsRelationsParam);
                    }else{
                        param.setParamType(servicesModelView.getDomainsRelations().get(indexOf));
                    }
                }
                
                servicesModelView.getLstParameters().add(param);
            }
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Wasn't possible to get the owls informations. \n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public String getSuperNoByGroup(String group){
        try{            
            OntModel model = this.getPeerOntModel();
            
            Individual peerGroup = model.getIndividual(URI_PEERGROUPS_ONTOLOGY + group);
            
            if(peerGroup != null){
                
                Property groupWasCreated = model.getProperty(URI_PEERGROUPS_ONTOLOGY + "groupWasCreated");
                return peerGroup.getPropertyValue(groupWasCreated).asResource().getLocalName();                
            }else{
                throw new Exception("Any peer was finded!");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Wasn't possible to find Super node of "+ group +".\n\n" + e, 
                    ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return "";
        }
    }
    
    public boolean owlValidation(File owlFile){
        try{
            boolean valid = false;
                        
            OWLKnowledgeBase kb = OWLFactory.createKB();
            kb.setReasoner("Pellet");            
            kb.read(owlFile.toURI());
            valid = kb.isConsistent();

            if(!valid){
                throw new Exception("The semantic service description (OWL-s) wasn't generated with success!");
            }
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage(), ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE, 
                                (new javax.swing.ImageIcon(getClass().getResource("/images/error.png"))));
            return false;
        }           
    }
    
    public boolean gravarOntologiaByOWLAPI(File owl){
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology domainOntology = manager.loadOntologyFromOntologyDocument(owl);            
            
            owl.delete();
            
            OWLOntologyFormat format = manager.getOntologyFormat(domainOntology);
            manager.saveOntology(domainOntology, format);
            
            return true;
        }catch(OWLOntologyCreationException | UnknownOWLOntologyException | OWLOntologyStorageException e){
            JOptionPane.showMessageDialog(null, e.getMessage(), ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }  
    }            
    
    public boolean gernerateSubClassComponentInOWLS(String owls){
        try{
            String URI_PROCESS_OWLS = "http://www.daml.org/services/owl-s/1.1/Process.owl";
            
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(owls);
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            model.read("file:///" + owls);
            
            OntClass parameterClass = model.getOntClass(URI_PROCESS_OWLS + "#Parameter");
            OntClass subParameterClass = model.createClass(ont.getOntologyID().getOntologyIRI().toString() + "#SubParameter");
            parameterClass.addSubClass(subParameterClass);
            
            if(generateSubClassObjectPropertieInOWLS(ont, model)){            
                FileOutputStream fileOutputStream = new FileOutputStream(owlFile);
                model.write(fileOutputStream, "RDF/XML-ABBREV");
            }
            
            return true;
        }catch(OWLOntologyCreationException | FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to generate the Classes in OWL-S.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean createSubParameter(String owls, String parameter, WSDLRecoverParams subParameter, OWLOntology ont, OntModel model, boolean isInput){
        try{    
            String URI_PROCESS_OWLS = "http://www.daml.org/services/owl-s/1.1/Process.owl";
            
            Individual parameterInd = model.getIndividual(ont.getOntologyID().getOntologyIRI().toString() + "#" + parameter);
            
            Resource subParameterRes = model.createResource(ont.getOntologyID().getOntologyIRI().toString() + "#" + subParameter.getName());
            Resource tipoNamedIndividual = model.createResource("http://www.w3.org/2002/07/owl#NamedIndividual"); 
                        
            model.add(subParameterRes, RDF.type, tipoNamedIndividual);
                        
            Property isSubParameterOf = model.getProperty(ont.getOntologyID().getOntologyIRI().toString() + "#isSubParameterOf" );            
            model.add(subParameterRes, isSubParameterOf, parameterInd);
            
            Property parameterType = model.getProperty( URI_PROCESS_OWLS + "#parameterType" );
            model.add(subParameterRes, parameterType, subParameter.getNameSpaceURI(), XSDDatatype.XSDanyURI);
            
            if(!this.generateParameterTypeXSDInOWLS(owls, subParameter, ont, model)){
                return false;
            }
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to create the sub-parameter in OWL-S.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean generateParameterTypeXSDInOWLS(String owls, WSDLRecoverParams parameter, OWLOntology ont, OntModel model){
        try{    
            Individual parameterInd = model.getIndividual(ont.getOntologyID().getOntologyIRI().toString() + "#" + parameter.getName());
                                  
            Property parameterTypeXSD = model.getProperty( ont.getOntologyID().getOntologyIRI().toString() + "#parameterTypeXSD" );
            model.add(parameterInd, parameterTypeXSD, parameter.getParameterTypeXSD(), XSDDatatype.XSDanyURI);
            
            FileOutputStream fileOutputStream = new FileOutputStream(owls);
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to create the parameterTypeXSD in OWL-S.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean generateSubClassObjectPropertieInOWLS(OWLOntology ont, OntModel model){
        try{
            String URI_PROCESS_OWLS = "http://www.daml.org/services/owl-s/1.1/Process.owl";
            
            OntClass parameterClass = model.getOntClass(URI_PROCESS_OWLS + "#Parameter");
            OntClass subParameterClass = model.getOntClass(ont.getOntologyID().getOntologyIRI().toString() + "#SubParameter");
            
            ObjectProperty hasSubParameterProperty = model.createObjectProperty(ont.getOntologyID().getOntologyIRI().toString() + "#hasSubParameter");
            ObjectProperty isSubParameterOfProperty = model.createObjectProperty(ont.getOntologyID().getOntologyIRI().toString() + "#isSubParameterOf");
            
            hasSubParameterProperty.addDomain(parameterClass);
            hasSubParameterProperty.addRange(subParameterClass);
            
            isSubParameterOfProperty.addDomain(subParameterClass);
            isSubParameterOfProperty.addRange(parameterClass);                        
            
            hasSubParameterProperty.addProperty(OWL.inverseOf, isSubParameterOfProperty);            
            hasSubParameterProperty.addRDFType(OWL.TransitiveProperty);
            isSubParameterOfProperty.addProperty(OWL.inverseOf, hasSubParameterProperty);
            
            DatatypeProperty parameterTypeXSD = model.createDatatypeProperty(ont.getOntologyID().getOntologyIRI().toString() + "#parameterTypeXSD");
            parameterTypeXSD.addDomain(parameterClass);
            parameterTypeXSD.addRange(XSD.anyURI);
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to generate the Class Properties in OWL-S.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean generateServiceTermInOWLS(String owlsPath, String owlsURI, String domainTerm, String serviceName){
        try{
            final String URI_OWLS_SERVICE = "http://www.daml.org/services/owl-s/1.1/Service.owl#";
            
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            model.read("file:///" + owlsPath);
                     
            Resource servico = model.getResource(owlsURI + "#" + serviceName + "Service");
            DatatypeProperty domainTermProperty = model.createDatatypeProperty(owlsURI + "#domainTerm");
                        
            domainTermProperty.addDomain(model.getResource(URI_OWLS_SERVICE + "Service"));
            domainTermProperty.addRange(XSD.anyURI);
            
            model.add(servico, domainTermProperty, domainTerm, XSDDatatype.XSDanyURI);            
                     
            FileOutputStream fileOutputStream = new FileOutputStream(owlsPath);
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Wasn't possible to create the domain annotation in OWL-S file.", ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean generateServiceRealNameInOWLS(String owlsPath, String owlsURI, String serviceRealName, String serviceName){
        try{
            final String URI_OWLS_PROFILE = "http://www.daml.org/services/owl-s/1.1/Profile.owl#";
            
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            model.read("file:///" + owlsPath);
                     
            Resource profile = model.getResource(owlsURI + "#" + serviceName + "Profile");
            DatatypeProperty serviceRealNameProperty = model.createDatatypeProperty(owlsURI + "#serviceRealName");
                        
            serviceRealNameProperty.addDomain(model.getResource(URI_OWLS_PROFILE + "Profile"));
            serviceRealNameProperty.addRange(XSD.xstring);
            
            model.add(profile, serviceRealNameProperty, serviceRealName, XSDDatatype.XSDstring);            
                     
            FileOutputStream fileOutputStream = new FileOutputStream(owlsPath);
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Wasn't possible to create the domain annotation in OWL-S file.", ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public List<String> getObjectPropertyByClass(String owlDomainClass, String owlPath) {
        try{
            List<String> lstPropriedades = new ArrayList<>();
            
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(owlPath);
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);            

            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph );                      
             
            String sql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                         "PREFIX t: <"+ ont.getOntologyID().getOntologyIRI().toString() +"#>\n" +
                         "SELECT DISTINCT * \n"+
                         "WHERE {\n"+
                         "\t {\n"+
                                        "\t\t SELECT ?superClass \n "+
                                        "\t\t WHERE {t:"+ owlDomainClass +" rdfs:subClassOf ?superClass.}\n"+
                         "\t }\n"+ 
                         " ?propriedade rdfs:domain ?superClass.\n"+
                         "}";
                
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
            
            while(results.hasNext()){
                
                QuerySolution row = results.next();
                
                if(! row.getResource("propriedade").getLocalName().contains("bottom")){
                    lstPropriedades.add(row.getResource("propriedade").getLocalName());
                }
            }
            
            sql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                         "PREFIX t: <"+ ont.getOntologyID().getOntologyIRI().toString() +"#>\n" +
                         "SELECT DISTINCT * \n"+
                         "WHERE {\n"+
                         " ?propriedade rdfs:domain t:"+ owlDomainClass +".\n"+
                         "}";
                
            qe = QueryExecutionFactory.create(sql, infModel);
            results =  qe.execSelect();
            
            while(results.hasNext()){
                
                QuerySolution row = results.next();
                
                if(! row.getResource("propriedade").getLocalName().contains("bottom")){
                    if(lstPropriedades.indexOf(row.getResource("propriedade").getLocalName()) == -1){
                        lstPropriedades.add(row.getResource("propriedade").getLocalName());
                    }
                }
            }
            
            return lstPropriedades;
        }catch(OWLOntologyCreationException | ReasonerException e){            
            JOptionPane.showMessageDialog(null, "Wasn't possible to get object properties.", ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public List<String> searchTermsRelated(String owlDomainPath, String classProperty, String owlClass){
        try{
            List<String> lstClasses = new ArrayList<>();
            List<String> lstAllClasses = this.getAllSubAndEquivalentClasses(owlDomainPath, owlClass);
            List<String> lstServices = new ArrayList<>();
            
            for(String classe : lstAllClasses){
                
                OWLClassesModel owlClassesModel = new OWLClassesModel();
                SemanticManager.getInstance().getRangeClasseByObjProperty(classe, classProperty, owlDomainPath, 
                                                                          owlClassesModel.getSubClasses(), 
                                                                          SemanticRestrictionEnum.some);
                
                for(OWLClassesModel owlModel : owlClassesModel.getSubClasses()){
                    
                    if(lstClasses.indexOf(owlModel.getName()) == -1){
                        lstClasses.add(owlModel.getName());
                    }                    
                }      
                
                owlClassesModel = new OWLClassesModel();
                SemanticManager.getInstance().getRangeClasseByObjProperty(classe, classProperty, owlDomainPath, 
                                                                          owlClassesModel.getSubClasses(), 
                                                                          SemanticRestrictionEnum.only);
                
                for(OWLClassesModel owlModel : owlClassesModel.getSubClasses()){
                    
                    if(lstClasses.indexOf(owlModel.getName()) == -1){
                        lstClasses.add(owlModel.getName());
                    }                    
                }  
            }   
            
            for(String service : lstClasses){
                
                List<String> lstServicesTemp = this.getAllSubAndEquivalentClasses(owlDomainPath, service);
                
                for(String term : lstServicesTemp){
                    
                    if(lstServices.indexOf(term) == -1){
                        lstServices.add(term);
                    }
                }
            }
            
            return lstServices;
        }catch(Exception e){            
            JOptionPane.showMessageDialog(null, "Wasn't possible to related terms.", ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public List<String> getAllSubAndEquivalentClasses(String owlPath, String owlClass){
        try{
            List<String> lstClasses = new ArrayList<>();
            
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(owlPath);
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);            

            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph );                      
             
            String sql = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
                         "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                         "PREFIX t: <"+ ont.getOntologyID().getOntologyIRI().toString() +"#>\n" +
                         "SELECT * \n" +
                         "WHERE {?subject ?property t:"+ owlClass +"}";
                
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
            
            while(results.hasNext()){
                QuerySolution row = results.next();
                
                if(!row.getResource("subject").getLocalName().contains("Nothing")){
                    if((row.getResource("property").getLocalName().contains("subClassOf")) || (row.getResource("property").getLocalName().contains("equivalentClass"))){
                        if(lstClasses.indexOf(row.getResource("subject").getLocalName()) == -1){
                            lstClasses.add(row.getResource("subject").getLocalName());
                        }
                    }
                }                
            }
            
            return lstClasses;
        }catch(ReasonerException | OWLOntologyCreationException e){            
            JOptionPane.showMessageDialog(null, "Wasn't possible to get all classes terms.", ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public void getRangeClasseByObjProperty(String domainClass, String objProperty, String owlPath, List<OWLClassesModel> lstOWLClassesModels, 
                                            SemanticRestrictionEnum restriction) {
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(owlPath);
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            OWLDataFactory factory = manager.getOWLDataFactory();
            
            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);            
            OWLClass classe = factory.getOWLClass(IRI.create(ont.getOntologyID().getOntologyIRI().toString() + "#" + domainClass));            
            
            OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);   
            ontModel.read("file:///" + owlFile);
            
            DefaultPrefixManager pm = new DefaultPrefixManager(ont.getOntologyID().getOntologyIRI().toString() + "#");
            
            String sql = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
                         "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                         "PREFIX t: <"+ ont.getOntologyID().getOntologyIRI().toString() +"#>\n" +
                         "SELECT * \n" +
                         "WHERE {t:%s ?y ?z.\n"+
                         "?z owl:onProperty t:%s.\n"+
                         "?z owl:"+ restriction.toString() +" ?classRange}";
            
            String sqlQuery = String.format(sql, pm.getShortForm(classe).replace(":", ""), objProperty);
            
            QueryExecution qe = QueryExecutionFactory.create(sqlQuery, ontModel);
            ResultSet results =  qe.execSelect();

            while(results.hasNext()){
                
                QuerySolution row = results.next();
                
                OWLClassesModel owlClassModel = new OWLClassesModel();
                owlClassModel.setFile(owlPath);
                owlClassModel.setName(row.getResource("classRange").getLocalName());
                owlClassModel.setUri(row.getResource("classRange").getURI().toString());
                
                lstOWLClassesModels.add(owlClassModel);                
            }
            
            NodeSet<OWLClass> subClasses = pelletReasoner.getSuperClasses(classe, false);
            Set<OWLClass> classes = subClasses.getFlattened();
            for (OWLClass cls : classes) {
                                
                if(cls.isOWLThing()){
                    continue;
                }
                
                sqlQuery = String.format(sql, pm.getShortForm(cls).replace(":", "") , objProperty); 
                
                qe = QueryExecutionFactory.create(sqlQuery, ontModel);
                results =  qe.execSelect();

                while(results.hasNext()){
                
                    QuerySolution row = results.next();

                    OWLClassesModel owlClassModel = new OWLClassesModel();
                    owlClassModel.setFile(owlPath);
                    owlClassModel.setName(row.getResource("classRange").getLocalName());
                    owlClassModel.setUri(row.getResource("classRange").getURI().toString());

                    if(lstOWLClassesModels.indexOf(owlClassModel) == -1){
                        lstOWLClassesModels.add(owlClassModel);              
                    }
                }             
            }  

        }catch(OWLOntologyCreationException | ReasonerException e){            
            JOptionPane.showMessageDialog(null, "Wasn't possible to get range class.\n\n" + e , ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void discoverNextTask(String domainClass, String objProperty, String owlPath, List<OWLClassesModel> lstOWLClassesModels, SemanticRestrictionEnum restriction){
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(owlPath);
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            OWLDataFactory factory = manager.getOWLDataFactory();
            
            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);            
            OWLClass classe = factory.getOWLClass(IRI.create(ont.getOntologyID().getOntologyIRI().toString() + "#" + domainClass));            
            
            OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);   
            ontModel.read("file:///" + owlFile);
            
            DefaultPrefixManager pm = new DefaultPrefixManager(ont.getOntologyID().getOntologyIRI().toString() + "#");
            
            String sql = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
                         "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                         "PREFIX t: <"+ ont.getOntologyID().getOntologyIRI().toString() +"#>\n" +
                         "SELECT ?task \n" +
                         "WHERE {?task ?y ?z.\n" +
                         "?z owl:onProperty t:%s.\n" +
                         "?z owl:someValuesFrom t:%s}";
            
            String sqlQuery = String.format(sql, objProperty, pm.getShortForm(classe).replace(":", ""));
            
            QueryExecution qe = QueryExecutionFactory.create(sqlQuery, ontModel);
            ResultSet results =  qe.execSelect();
            
            while(results.hasNext()){
                
                QuerySolution row = results.next();    
                
                OWLClassesModel owlClassModel = new OWLClassesModel();
                owlClassModel.setFile(owlPath);
                owlClassModel.setName(row.getResource("task").getLocalName());
                owlClassModel.setUri(row.getResource("task").getURI().toString());

                if(lstOWLClassesModels.indexOf(owlClassModel) == -1){
                    lstOWLClassesModels.add(owlClassModel);              
                }
            }
            
            NodeSet<OWLClass> subClasses = pelletReasoner.getSuperClasses(classe, false);
            Set<OWLClass> classes = subClasses.getFlattened();
            for (OWLClass cls : classes) {
                                
                if(cls.isOWLThing()){
                    continue;
                }
                
                sqlQuery = String.format(sql, pm.getShortForm(cls).replace(":", "") , objProperty); 
                
                qe = QueryExecutionFactory.create(sqlQuery, ontModel);
                results =  qe.execSelect();

                while(results.hasNext()){
                
                    QuerySolution row = results.next();

                    OWLClassesModel owlClassModel = new OWLClassesModel();
                    owlClassModel.setFile(owlPath);
                    owlClassModel.setName(row.getResource("classRange").getLocalName());
                    owlClassModel.setUri(row.getResource("classRange").getURI().toString());

                    if(lstOWLClassesModels.indexOf(owlClassModel) == -1){
                        lstOWLClassesModels.add(owlClassModel);              
                    }
                }             
            }  

        }catch(OWLOntologyCreationException | ReasonerException e){            
            JOptionPane.showMessageDialog(null, "Wasn't possible to discover the next task.\n\n" + e , ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public int countTask(String domainClass, String objProperty, String owlPath, SemanticRestrictionEnum restriction){
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(owlPath);
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            OWLDataFactory factory = manager.getOWLDataFactory();
                         
            OWLClass classe = factory.getOWLClass(IRI.create(ont.getOntologyID().getOntologyIRI().toString() + "#" + domainClass));            
            
            OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);   
            ontModel.read("file:///" + owlFile);
            
            DefaultPrefixManager pm = new DefaultPrefixManager(ont.getOntologyID().getOntologyIRI().toString() + "#");
            
            String sql = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
                         "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                         "PREFIX t: <"+ ont.getOntologyID().getOntologyIRI().toString() +"#>\n" +
                         "SELECT * \n" +
                         "WHERE {?x ?y ?z.\n"+
                         "t:%s ?y ?z.\n"+
                         "?z owl:onProperty t:%s.}";
            
            String sqlQuery = String.format(sql, pm.getShortForm(classe).replace(":", ""), objProperty);
            
            QueryExecution qe = QueryExecutionFactory.create(sqlQuery, ontModel);
            ResultSet results =  qe.execSelect();

            ResultSetFormatter.out(System.out, results);
            
            while(results.hasNext()){
                
                QuerySolution row = results.next();
                
                return row.getLiteral("qtde").getInt();
            }
            
            return -1;
        }catch(OWLOntologyCreationException | ReasonerException e){            
            JOptionPane.showMessageDialog(null, "Wasn't possible to count tasks.\n\n" + e , ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return -1;
        }        
    }
    
    public List<ServicesInfoModel> searchServices(List<String> lstTerms, boolean isToSearchConnectors){
        try{
            List<ServicesInfoModel> lstServices = new ArrayList<>();

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(this.getPeerOWLPath());
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            
            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph ); 
            
            String filter = "";
            if(lstTerms != null){
                for(String term : lstTerms){
                    filter = filter + "(str(?serviceDomainMainTerm) = \""+ term +"\") ||\n";
                }
            }
            
            if(!filter.equals("")){
                filter = filter.substring(0, filter.lastIndexOf("||"));
            }
                
            String sql = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX t: <"+ ont.getOntologyID().getOntologyIRI().toString() +"#>\n" +
                        "SELECT *\n" +
                        "WHERE { ?service t:pertenceOneGroup t:"+ this.getInterestManager().getGrupoSelecionado().getGroupName() +".\n" +                        
                        "        ?service rdf:type t:Services.\n" +
                        (isToSearchConnectors ? "" : "        ?service t:serviceDomainMainTerm ?serviceDomainMainTerm.\n") +                        
                        "        ?service t:serviceOWLSPath ?serviceOWLSPath.\n" +
                        "        ?service t:serviceWSDLPath ?serviceWSDLPath.\n"+
                        "        ?service t:serviceWSDLURL ?serviceWSDLURL.\n"+
                        (filter.equals("") ? "" : "        FILTER ("+ filter +")\n") +                        
                        "}";
            
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
            
            while(results.hasNext()){
                
                QuerySolution row = results.next();
                
                String service = row.get("service").asResource().getLocalName();
                
                String serviceDomainMainTerm = "";
                if(row.contains("serviceDomainMainTerm")){
                    serviceDomainMainTerm = row.get("serviceDomainMainTerm").asLiteral().getString();
                }
                
                String serviceOWLSPath = row.get("serviceOWLSPath").asLiteral().getString();
                String serviceWSDLPath = row.get("serviceWSDLPath").asLiteral().getString();
                String serviceWSDLURL = row.get("serviceWSDLURL").asLiteral().getString();
                
                ServicesInfoModel servicesInfoModel = new ServicesInfoModel();
                servicesInfoModel.setDomainName(serviceDomainMainTerm);
                servicesInfoModel.setWsdlFile(serviceWSDLPath);
                servicesInfoModel.setOwlsFile(serviceOWLSPath);
                servicesInfoModel.setWsdlOperationName(service);
                servicesInfoModel.setWsdlAddress(serviceWSDLURL);
                
                lstServices.add(servicesInfoModel);                
            }
            
            return lstServices;
        }catch(OWLOntologyCreationException | ReasonerException e){
            JOptionPane.showMessageDialog(null, "Wasn't possible to get all services.\n\n" + e , ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public List<OWLSParam> getSubParameterOnSemanticSearch(OWLSParam owlsParamenter, String owlsPath){
        try{
            List<OWLSParam> lstParams = new ArrayList<>();
            
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(owlsPath);
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            
            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph ); 
                
            String sql = "PREFIX t: <"+ ont.getOntologyID().getOntologyIRI().toString() +"#>\n" +
                        "PREFIX proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>\n" +
                        "SELECT  *\n" +
                        "WHERE\n" +
                        "{ \n" +
                        "     t:"+ owlsParamenter.getParamName() +"  t:hasSubParameter  ?subParameter.\n" +
                        "     ?subParameter proc:parameterType ?parameterType.\n" +
                        "     ?subParameter t:parameterTypeXSD ?parameterTypeXSD.\n" +
                        "}";
            
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
            
            while(results.hasNext()){
                
                QuerySolution row = results.next();
                
                String paramName = row.get("subParameter").asResource().getLocalName();
                String parameterType = row.get("parameterType").asLiteral().getString();
                String parameterTypeXSD = row.get("parameterTypeXSD").asLiteral().getString();
                
                OWLSParam owlsParam = new OWLSParam();
                owlsParam.setParamName(paramName);
                owlsParam.setParamDomainTerm(parameterType);
                owlsParam.setParamType(parameterTypeXSD);
                
                lstParams.add(owlsParam);
            }
            
            return lstParams; 
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public OWLSOperation serviceSemanticResearch(String owlsPath, String serviceDomainTerm, boolean isToLoadAll){
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(owlsPath);
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            
            OWLKnowledgeBase kb = OWLFactory.createKB();
            kb.setReasoner("Pellet");
            
            Service service = kb.readService(owlFile.toURI().toURL().toExternalForm());            
            Property domainTermProp = new PropertyImpl(ont.getOntologyID().getOntologyIRI().toString() + "#domainTerm");
            OWLDataPropertyImpl dataProperty = new OWLDataPropertyImpl(service.getOntology(), domainTermProp);    
            OWLDataValue domainTermValue = service.getProperty(dataProperty);
            
            String domainTerm = "";            
            if(domainTermValue != null){
                domainTerm = domainTermValue.getValue().toString().substring(domainTermValue.getValue().toString().lastIndexOf("#") + 1, 
                                                                                domainTermValue.getValue().toString().length());
            }
            
            String tempDomainTerm = domainTerm;
            domainTerm = (isToLoadAll ? "" : domainTerm);
            
            OWLSOperation owlsOperation = null;
            
            if(domainTerm.equals(serviceDomainTerm)){
                final String URI_OWLS_PROFILE = "http://www.daml.org/services/owl-s/1.1/Profile.owl#";
                Profile profile = service.getProfile();
                
                Property serviceRealNameProp = new PropertyImpl(ont.getOntologyID().getOntologyIRI().toString() + "#serviceRealName");
                OWLDataPropertyImpl serviceRealNameProperty = new OWLDataPropertyImpl(profile.getOntology(), serviceRealNameProp);    
                OWLDataValue serviceRealNameValue = profile.getProperty(serviceRealNameProperty);
                
                Property textDescriptionProp = new PropertyImpl(URI_OWLS_PROFILE + "textDescription");
                OWLDataPropertyImpl textDescriptionProperty = new OWLDataPropertyImpl(profile.getOntology(), textDescriptionProp);    
                OWLDataValue textDescriptionValue = profile.getProperty(textDescriptionProperty);
                
                owlsOperation = new OWLSOperation();
                owlsOperation.setOperationName(service.getLocalName().replace("Service", ""));
                owlsOperation.setOperationDomainTerm(tempDomainTerm);
                if(serviceRealNameValue != null){
                    owlsOperation.setOperationRealName(serviceRealNameValue.getValue().toString());
                }
                if(textDescriptionValue != null){
                    owlsOperation.setServiceDescription(textDescriptionValue.getValue().toString());
                }
                               
                OWLOntology owlsProcess = this.getOWLSClass(ont, "process");
                
                for(Input input : profile.getInputs()){
                    Property parameterTypeProp = new PropertyImpl(owlsProcess.getOntologyID().getOntologyIRI().toString() + "#parameterType");
                    OWLDataPropertyImpl parameterType = new OWLDataPropertyImpl(input.getOntology(), parameterTypeProp);    
                    OWLDataValue parameterTypeValue = input.getProperty(parameterType);
                    
                    Property parameterTypeXSDProp = new PropertyImpl(ont.getOntologyID().getOntologyIRI().toString() + "#parameterTypeXSD");
                    OWLDataPropertyImpl parameterTypeXSD = new OWLDataPropertyImpl(input.getOntology(), parameterTypeXSDProp);    
                    OWLDataValue parameterTypeXSDValue = input.getProperty(parameterTypeXSD);
                            
                    if(parameterTypeValue != null){
                        OWLSParam owlsParam = new OWLSParam();
                        owlsParam.setParamName(input.getLabel());
                        owlsParam.setParamDomainTerm(parameterTypeValue.getValue().toString());
                        owlsParam.setParamType(parameterTypeXSDValue.getValue().toString());
                        
                        owlsOperation.getInputs().add(owlsParam);
                    }
                }
                
                for(Output output : profile.getOutputs()){
                    Property parameterTypeProp = new PropertyImpl(owlsProcess.getOntologyID().getOntologyIRI().toString() + "#parameterType");
                    OWLDataPropertyImpl parameterType = new OWLDataPropertyImpl(output.getOntology(), parameterTypeProp);    
                    OWLDataValue parameterTypeValue = output.getProperty(parameterType);
                    
                    Property parameterTypeXSDProp = new PropertyImpl(ont.getOntologyID().getOntologyIRI().toString() + "#parameterTypeXSD");
                    OWLDataPropertyImpl parameterTypeXSD = new OWLDataPropertyImpl(output.getOntology(), parameterTypeXSDProp);    
                    OWLDataValue parameterTypeXSDValue = output.getProperty(parameterTypeXSD);
                    
                    if(parameterTypeValue != null){
                        OWLSParam owlsParam = new OWLSParam();
                        owlsParam.setParamName(output.getLabel());
                        owlsParam.setParamDomainTerm(parameterTypeValue.getValue().toString());
                        owlsParam.setParamType(parameterTypeXSDValue.getValue().toString());
                        
                        owlsOperation.getOutputs().add(owlsParam);
                    }
                }
                
                for(AtomicGrounding atomicGrounding : service.getGrounding().getAtomicGroundings()){
                    owlsOperation.setServiceURL(atomicGrounding.getDescriptionURL().toString());
                }
            }
            
            return owlsOperation;
        }catch(OWLOntologyCreationException | IOException | URISyntaxException e){
            JOptionPane.showMessageDialog(null, 
                    "Wasn't possible to get all information about the servince in OWL-S file.\n\n" + e, 
                                          ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    private OWLOntology getOWLSClass(OWLOntology ontology, String owl){
        
        for(OWLOntology ont : ontology.getImports()){
            
            if(ont.getOntologyID().getOntologyIRI().toString().toLowerCase().contains(owl.toLowerCase())){
                return ont;
            }
            
        }
        
        return null;
    }
    
    public HashMap<String, String> servicesRelatedWithConnector(String adress){
        try{
            HashMap<String, String> lstServices = new HashMap<>();

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(this.getPeerOWLPath());
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            
            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph ); 
                
            String sql = "PREFIX t: <"+ URI_PEERGROUPS_ONTOLOGY +">\n" +
                         "SELECT *\n" +
                         "WHERE {?subject t:serviceWSDLURL ?object.\n" +
                         "\t     ?subject t:serviceDomainMainTerm ?serviceDomainMainTerm.\n"+
                         "\t FILTER ( regex(str(?object), \""+ adress +"\", \"i\")) }";
            
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();            
            
            while(results.hasNext()){
                
                QuerySolution row = results.next();
                
                String service = row.get("subject").asResource().getLocalName();
                String serviceDomainMainTerm = row.get("serviceDomainMainTerm").asLiteral().getString();
                
                lstServices.put(service, serviceDomainMainTerm);
            }
            
            return lstServices;
        }catch(OWLOntologyCreationException | ReasonerException e){
            JOptionPane.showMessageDialog(null, "Wasn't possible to get all services.\n\n" + e , ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public boolean checkServiceExists(String service){
        try{            
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(this.getPeerOWLPath());
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            
            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph ); 
                
            String sql = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                         "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                         "PREFIX t: <"+ URI_PEERGROUPS_ONTOLOGY +">\n" +
                         "SELECT * WHERE  {\n" +                        
                         "	?service rdf:type t:Services.\n" +
                         "}";
            
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
            
            while(results.hasNext()){
                
                QuerySolution row = results.next();
                
                if(row.getResource("service").getLocalName().equals(service)){
                    return true;
                }                
            }
            
            return false;
        }catch(OWLOntologyCreationException | ReasonerException e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to check service.\n\n" + e , ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return true;
        }
    }
    
    
    public boolean deleteAllConnector(String serviceName){
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(this.getPeerOWLPath());
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            
            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph ); 
                
            String sql = "PREFIX t: <"+ URI_PEERGROUPS_ONTOLOGY +">\n" +
                         "SELECT  *\n" +
                         "WHERE\n" +
                         "  { t:BlastP  t:hasConnector  ?connector.\n" +
                         "    ?connector t:serviceOWLSPath ?owlsPath.\n" +
                         "    ?connector t:serviceWSDLPath ?wsdlPath.}";
            
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
            
            while(results.hasNext()){
                
                QuerySolution row = results.next();
                
                File owlsFile = new File(row.getLiteral("owlsPath").getString());
                if(owlsFile.exists()){
                    owlsFile.delete();
                }
                
                File wsdlFile = new File(row.getLiteral("wsdlPath").getString());
                if(wsdlFile.exists()){
                    wsdlFile.delete();
                }
                
                OntModel model = this.getPeerOntModel();
                //pego o individo da ontologia
                Individual connector = model.getIndividual(row.getResource("connector").getURI());
                //removo o individuo da ontologia
                connector.remove();

                //gravo a ontologia novamente em arquivo
                FileOutputStream fileOutputStream = new FileOutputStream(this.getPeerOWLPath());            
                model.write(fileOutputStream, "RDF/XML-ABBREV");
            }           
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "It wasn't possible to delete all connectors.\n\n" + e , ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public SemanticChat getSemanticChat(){
        return this.semanticChat;
    }
       
    public String getDirOntology() {
        return dirOntology;
    }

    public InterestManager getInterestManager() {
        return interestManager;
    }

    public void setInterestManager(InterestManager interestManager) {
        this.interestManager = interestManager;
    }

    public String getURI_PEERGROUPS_ONTOLOGY() {
        return URI_PEERGROUPS_ONTOLOGY;
    }

    public String getNomePeer() {
        return nomePeer;
    }

    public void setNomePeer(String nomePeer) {
        this.nomePeer = nomePeer;
    }

    public PeerGroup getNetPeerGroup() {
        return netPeerGroup;
    }

    public void setNetPeerGroup(PeerGroup netPeerGroup) {
        this.netPeerGroup = netPeerGroup;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public SearchManager getSearchManager() {
        return searchManager;
    }

    public void criarSearchManager(ControlaMensagens controlaMensagens, VirtualDir app) {
        searchManager = new SearchManager(controlaMensagens, app);
    }

    /**
     * @return the serviceManager
     */
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    /**
     * @param serviceManager the serviceManager to set
     */
    public void setServiceManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * @return the domainOntologyPath
     */
    public String getDomainOntologyPath() {
        return domainOntologyPath;
    }

    /**
     * @return the compositionManager
     */
    public CompositionManager getCompositionManager() {
        return compositionManager;
    }
}
