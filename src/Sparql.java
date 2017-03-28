
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import static com.esciencenet.semanticmanager.SemanticManager.URI_PEERGROUPS_ONTOLOGY;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.File;
import net.jxta.impl.util.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.service.Service;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.query.ValueMap;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Tadeu
 */
public class Sparql {

    public static void teste(){
        try{
            
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File("C://Users//Tadeu//Documents//NetBeansProjects//eScienceNet//OntologyRepository//Services//Blast.owl");
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            
            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph ); 
                
            String sql = "PREFIX t: <http://www.esciencenet.com.br/tadeuclasse/esciencenet/Blast.owl#>\n" +
"PREFIX proc: <http://www.daml.org/services/owl-s/1.1/Process.owl#>\n" +
"SELECT ?parameter ?typeParameter \n" +
"WHERE\n" +
"{ \n" +
"     ?process proc:hasParameter t:parameters.\n"                
  //+ " ?process proc:hasParameter t:parameters."
  + " ?parameter proc:parameterType ?typeParameter" +
"}";
            
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
            
            ResultSetFormatter.out(System.out, results);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            String s = "Tadeu Classe";
            
            
            
            
    //        teste();
            /*
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File("C://Users//Tadeu Classe//Documents//NetBeansProjects//eScienceNet//OntologyRepository//PeerOntology.owl");
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);            
            
            OWLDataFactory factory = manager.getOWLDataFactory();
            
            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel(graph);
            OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);   
            ontModel.read("file:///C://Users//Tadeu Classe//Documents//NetBeansProjects//eScienceNet//OntologyRepository//Domains//SequenceAligningOntology.owl");
            
            String sql = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>//n" +
                        "PREFIX t: <http://www.esciencenet.com.br/tadeuclasse/esciencenet/PeerOntology.owl#>//n" +
                        "SELECT *n" +
                        "WHERE { ?service t:pertenceOneGroup t:BioInformatics.//n" +                        
                        "        ?service rdf:type t:Services.//n" +
                        "        ?service t:serviceDomainMainTerm ?serviceDomainMainTerm.//n" +
                        "        ?service t:serviceOWLSPath ?serviceOWLSPath.//n" +
                        "        ?service t:serviceWSDLPath ?serviceWSDLPath.//n"+
                        "        FILTER ((str(?serviceDomainMainTerm) = //"BLASTN//") ||//n" +
                        "                (str(?serviceDomainMainTerm) = //"TBLASTX//"))//n"+
                        "}";
            
            System.out.println(sql);
            
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();

            ResultSetFormatter.out(System.out, results);
            */
        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static ResultSet getDataPropertyByClass(String owlDomainClass) {
        try{
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File("C://Users//Tadeu Classe//Documents//NetBeansProjects//eScienceNet//OntologyRepository//Domains//SequenceAligningOntology.owl");
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);            

            PelletReasoner pelletReasoner = PelletReasonerFactory.getInstance().createNonBufferingReasoner(ont);
            KnowledgeBase kb = pelletReasoner.getKB();
            PelletInfGraph graph = new org.mindswap.pellet.jena.PelletReasoner().bind( kb );

            InfModel infModel = ModelFactory.createInfModel( graph );
             
            String sql = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>//n" +
                         "PREFIX t: <http://gabriellacastro.com.br/SequenceAligningOntology#>//n" +
                         "SELECT * //n"+
                         "WHERE {//n"+
                         "//t {//n"+
                                        "//t//t SELECT ?superClass //n "+
                                        "//t//t WHERE {t:"+ owlDomainClass +" rdfs:subClassOf ?superClass.}//n"+
                         "//t }//n"+ 
                         " ?propriedade rdfs:domain ?superClass.//n"+
                         " ?propriedade rdfs:range ?range.//n"+
                         " ?propriedade rdfs:domain ?domain.//n"+
                         "}";
                
            QueryExecution qe = QueryExecutionFactory.create(sql, infModel);
            ResultSet results =  qe.execSelect();
            
            //ResultSetFormatter.out(System.out, results);
            return results;
        }catch(Exception e){            
            e.printStackTrace();
            return null;
        }
    }
}
