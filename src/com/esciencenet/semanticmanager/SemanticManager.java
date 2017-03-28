/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.semanticmanager;

import com.esciencenet.interestmanager.*;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
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
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jxta.ext.config.Configurator;
import net.jxta.ext.config.TcpTransportAddress;
import net.jxta.ext.config.Transport;
import com.esciencenet.models.*;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;

/**
 * Gerente de Semântica responsável pela a criação de semântica na e-ScienceNet
 * 
 * @author Tadeu Classe
 */
public class SemanticManager {
 
    //atributos da classe
    private final String DIRETORIO = "OntologyRepository";
    private final String GROUP_ONTOLOGY = "PeerGroups.owl";
    private final String URI_PEERGROUPS_ONTOLOGY = "http://www.esciencenet.com.br/tadeuclasse/esciencenet/PeerGroups.owl#";
    private static final SemanticManager semanticManager = new SemanticManager();    
    
    private String dirOntology;
    private InterestManager interestManager;   
    
    /**
     * Método construtor da classe
     */
    private SemanticManager(){
        //crio o gerente de interesse
        this.interestManager = new InterestManager();
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
    private boolean verificaExistenciaDoPeerGroup(String nome, String ontologia){
        try{

            //crio o modelo ontologico
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            //associo o arquivo da ontologia ao modelo
            model.read("file:///" + ontologia);
            
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
            String ontologia = getDirOntology() + File.separator + GROUP_ONTOLOGY;
            ontologia = ontologia.replace(File.separator + File.separator, File.separator);
            ontologia = ontologia.replace(File.separator, "/");
            
            //verifico se o peer existe na ontologia, isto consiste que eu não preciso gravar novamente
            if (verificaExistenciaDoPeerGroup(nome, ontologia)) {
                return true;
            }
            
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            model.read("file:///" + ontologia);
           
            Resource individuo = model.createResource(URI_PEERGROUPS_ONTOLOGY + nome);
            
            Property peerName = model.createProperty(URI_PEERGROUPS_ONTOLOGY + "peerName");            
            model.add(individuo, peerName, nome, XSDDatatype.XSDstring);
            
            Property idPeer = model.createProperty(URI_PEERGROUPS_ONTOLOGY + "idPeer");                   
            model.add(individuo, idPeer, peerID, XSDDatatype.XSDstring);
            
            Property peerIP = model.createProperty(URI_PEERGROUPS_ONTOLOGY + "peerIP");                       
            model.add(individuo, peerIP, ip, XSDDatatype.XSDstring);
            
            Resource tipoNamedIndividual = model.createResource("http://www.w3.org/2002/07/owl#NamedIndividual"); 
            Resource tipoClass = model.createResource(URI_PEERGROUPS_ONTOLOGY + "Peer"); 
            
            model.add(individuo, RDF.type, tipoNamedIndividual);
            model.add(individuo, RDF.type, tipoClass);
            
            FileOutputStream fileOutputStream = new FileOutputStream(ontologia);            
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public boolean removerGrupoOWL(String grupo){
        try{
            String ontologia = dirOntology + File.separator + GROUP_ONTOLOGY;
            ontologia = ontologia.replace(File.separator + File.separator, File.separator);
            ontologia = ontologia.replace(File.separator, "/");
            
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            model.read("file:///" + ontologia);
            
            Individual group = model.getIndividual(URI_PEERGROUPS_ONTOLOGY + grupo);
            group.remove();
            
            FileOutputStream fileOutputStream = new FileOutputStream(ontologia);            
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            
            return true;
        }catch(Exception e){
            System.out.printf(e.getMessage());
            return false;
        }
    }
    
    private boolean criarOntologiaGrupos(String diretorio){
        
        try{
            String ontologia = diretorio + File.separator + GROUP_ONTOLOGY;
            ontologia = ontologia.replace(File.separator + File.separator, File.separator);
            ontologia = ontologia.replace(File.separator, "/");
            
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            model.read("file:///" + ontologia);
            
            String URI = "http://www.esciencenet.com.br/tadeuclasse/esciencenet/PeerGroups.owl#";
            
            Resource individuo = model.createResource("http://www.esciencenet.com.br/tadeuclasse/esciencenet/PeerGroups.owl#VIRTUALW8");
            
            Property peerName = model.createProperty( URI + "peerName" );            
            model.add(individuo, peerName, "VirtualW8", XSDDatatype.XSDstring);
            
            Property idPeer = model.createProperty( URI + "idPeer" );                   
            model.add(individuo, idPeer, "jxta:92139812398n219837293", XSDDatatype.XSDstring);
            
            Property peerIP = model.createProperty( URI + "peerIP" );                       
            model.add(individuo, peerIP, "192.168.0.1", XSDDatatype.XSDstring);
            
            Resource tp1 = model.createResource( "http://www.w3.org/2002/07/owl#NamedIndividual" ); 
            Resource tp2 = model.createResource( URI + "Peer" ); 
            
            model.add(individuo, RDF.type, tp1);
            model.add(individuo, RDF.type, tp2);
            
            
            Resource individuo2 = model.createResource("http://www.esciencenet.com.br/tadeuclasse/esciencenet/PeerGroups.owl#Mathematics");
            
            Resource tp3 = model.createResource( "http://www.w3.org/2002/07/owl#NamedIndividual" ); 
            Resource tp4 = model.createResource( URI + "Grupo" ); 
            model.add(individuo2, RDF.type, tp3);
            model.add(individuo2, RDF.type, tp4);
            
            Property groupName = model.createProperty( URI + "groupName" );                   
            model.add(individuo2, groupName, "Mathematics", XSDDatatype.XSDstring);
            
            Property groupArea = model.createProperty( URI + "groupArea" );                   
            model.add(individuo2, groupArea, "Matematica", XSDDatatype.XSDstring);
            
            Property groupDescription = model.createProperty( URI + "groupDescription" );                   
            model.add(individuo2, groupDescription, "Grupo relacionario a troca de arquivos e servicos relacionados com a area de bioinformatica", XSDDatatype.XSDstring);
            
            Property idGroup = model.createProperty( URI + "idGroup" );                   
            model.add(individuo2, idGroup, "uuid-EB2993B5E74B4C0B873AC66BB5E3651C02", XSDDatatype.XSDstring);
            
            Property groupWasCreatedBy = model.createProperty( URI + "groupWasCreatedBy" );
            Resource groupWasCreatedByValue = model.createResource( URI + "VIRTUALW8" );            
            model.add(individuo2, groupWasCreatedBy, groupWasCreatedByValue);
            
            FileOutputStream fileOutputStream = new FileOutputStream(ontologia);
            
            model.write(fileOutputStream, "RDF/XML-ABBREV");
            return true;
        }catch(Exception e){
            e.printStackTrace();
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
            
            //ontologia do grupo
            String ontologia = dirOntology + File.separator + GROUP_ONTOLOGY;
            ontologia = ontologia.replace(File.separator + File.separator, File.separator);
            ontologia = ontologia.replace(File.separator, "/");
            
            //crio o modelo ontologico
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            //associo o arquivo da ontologia ao modelo
            model.read("file:///" + ontologia);
            
            //crio os modelo de dados
            OntModel data = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            //associo o arquivo da ontologia ao modelo
            data.read("file:///" + ontologia);
            
            //seto o motor de inferencia e passo o modelo a ser inferido
            Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
            reasoner = reasoner.bindSchema(model);
            
            //pego o modelo inferido
            
            InfModel infmodel = ModelFactory.createInfModel(reasoner, data);                        
            
            //monto uma query SparQL para a consulta na ontologia
            String sql = "PREFIX t: <http://www.esciencenet.com.br/tadeuclasse/esciencenet/PeerGroups.owl#>\n" +
                         "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                         "SELECT ?class ?member\n" +
                         "WHERE {?class a owl:Class.\n" +
                         "\t?member a ?class.\n"+
                         "\tfilter (?class in (t:Grupo))"+
                         "}";
            
            //Crio a query de consulta a ontologia
            Query query = QueryFactory.create(sql);

            //Executo a consulta na ontologia
            QueryExecution qe = QueryExecutionFactory.create(query, infmodel);
            ResultSet results =  qe.execSelect();
            
            //percorro os resultados encontrados
            while(results.hasNext()){
                QuerySolution row = results.next();
                Resource colunm = row.getResource("member");                
                
                PeerGroupModel group = new PeerGroupModel();                
                group.setGroupName(colunm.getLocalName());                
                
                obterPeerGroupOnOntology(group, ontologia);
                
                if (group.getGroupName() != null){                
                    peerGroupLst.add(group);
                }
            }
            
            qe.close();
            
            return peerGroupLst;
        }catch(Exception e){ 
            e.printStackTrace();
            return null;
        }
    }
    
    private void obterPeerGroupOnOntology(PeerGroupModel peerGroupModel, String ontologia){
        try{           
            //crio o modelo ontologico
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            //associo o arquivo da ontologia ao modelo
            model.read("file:///" + ontologia);
           
            //monto uma query SparQL para a consulta na ontologia
            String sql = "PREFIX t: <http://www.esciencenet.com.br/tadeuclasse/esciencenet/PeerGroups.owl#>\n" +                         
                         "SELECT ?nomeGrupo ?id ?area ?desc ?autor\n" +
                         "WHERE {?x t:groupName \""+ peerGroupModel.getGroupName() +"\".\n" +
                         "\t?x t:groupName ?nomeGrupo.\n" +
                         "\t?x t:idGroup ?id.\n" +
                         "\t?x t:groupArea ?area.\n" +
                         "\t?x t:groupDescription ?desc.\n" +
                         "\t?x t:groupWasCreatedBy ?autor.\n" +
                         "}";
            
            //Crio a query de consulta a ontologia
            Query query = QueryFactory.create(sql);

            //Executo a consulta na ontologia
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet results =  qe.execSelect();
                        
            //percorro os resultados encontrados
            while(results.hasNext()){
                QuerySolution row = results.next();
                
                Literal nome = row.getLiteral("nomeGrupo");                
                peerGroupModel.setGroupName(nome.getString());
                
                Literal id = row.getLiteral("id");                
                peerGroupModel.setGroupID(id.getString());
                
                Literal area = row.getLiteral("area");                
                peerGroupModel.setGroupArea(area.getString());
                
                Literal desc = row.getLiteral("desc");                
                peerGroupModel.setGroupDescription(desc.getString());
                
                Resource autor = row.getResource("autor");                
                peerGroupModel.setGroupCreator(autor.getLocalName());
            }
            
            qe.close();
        }catch(Exception e){
            e.printStackTrace();
        }
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
}
