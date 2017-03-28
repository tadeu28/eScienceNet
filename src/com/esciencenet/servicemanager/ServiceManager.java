/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.servicemanager;

import com.esciencenet.forms.FComposeAbstractWorkflow;
import com.esciencenet.forms.FExecuteSingleService;
import com.esciencenet.forms.FShowServices;
import com.esciencenet.models.ServicesInfoModel;
import com.esciencenet.semanticmanager.SemanticManager;
import com.esciencenet.servicemanager.wsdlmodels.WSDLRecoverParams;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.utils.QNameProvider;
import org.mindswap.wsdl.WSDLOperation;
import org.mindswap.wsdl.WSDLParameter;
import org.mindswap.wsdl.WSDLTranslator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;


/**
 *
 * @author Tadeu
 */
public class ServiceManager{
    
    private final String SERVICE_REPOSITORY = "ServiceRepository";
    
    private String appDir;
    private String owlsDir;
    private String owlsFilePath;
    
    private String dirServiceRepository;    
    private String wsdlLocalFileName = "";    
    
    public ServiceManager(String dirOntology, String owlsDir){
        this.owlsDir = owlsDir;
        this.createServiceRepository(dirOntology);
        this.appDir = dirOntology;
    }
    
    private boolean createServiceRepository(String diretorio){        
        try{
            //crio a string para a criação do diretorio
            String path = diretorio + File.separator + SERVICE_REPOSITORY; 
            
            //crio o arquivo
            File dirService = new File(path);
            
            //verifico se o mesmo não existe
            if (! dirService.exists()){
                //crio o diretorio
                dirService.mkdir();
            }
            
            //seto o parametro de diretorio
            dirServiceRepository = path;
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public void executeWSDLToOWLs(boolean isConnector){
        ServicesWSDLToOWLS wSDLToOWLS = new ServicesWSDLToOWLS();
        wSDLToOWLS.setAppDir(this.appDir);
        wSDLToOWLS.setOwlsDir(this.getOwlsDir());
        wSDLToOWLS.setIsConnector(isConnector);
        wSDLToOWLS.start();
    }
    
    public File checkXMLInformationFile(){
        try{
            File xmlFile = new File(this.getAppDir() + SERVICE_REPOSITORY + File.separator + "OWLManager.xml");
            
            if (xmlFile.exists()){
                return xmlFile;
            }
            
            return null;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public ServicesInfoModel readOWLManagerXML(){
        try{
            ServicesInfoModel servicesInfoModel = null;
                        
            //crio o objeto de verificaça da existencia do XML
            File xmlFile = this.checkXMLInformationFile();
            
            if (xmlFile != null){
                FileInputStream xml = new FileInputStream(xmlFile);                
                XStream xstream = new XStream(new DomDriver());
                
                servicesInfoModel = (ServicesInfoModel) xstream.fromXML(xml);

                xmlFile.delete();
            }
            
            return servicesInfoModel;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public void showServices(){
        FShowServices frmShowServices = new FShowServices(null, true);
        frmShowServices.setVisible(true);
    }
    
    public boolean deleteWSDLFile(String wsdlPath){
        try{
            File wsdlFile = new File(wsdlPath);
            
            if (wsdlFile.exists()){                
                return wsdlFile.delete();                
            }
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean deleteOWLSFile(String owlsPath){
        try{
            File owlsFile = new File(owlsPath);
            
            if (owlsFile.exists()){                
                return owlsFile.delete();                
            }
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean saveWSDLInServiceRepository(URI wsdlURI, String opName){
        try{    
            //crio o nome do arquivo local
            String nomeArquivoLocal = wsdlURI.getPath();              
            /*nomeArquivoLocal = nomeArquivoLocal.substring(nomeArquivoLocal.lastIndexOf("/") + 1);
            
            if (nomeArquivoLocal.contains("?")){
                if(nomeArquivoLocal.contains(".")){
                    nomeArquivoLocal = nomeArquivoLocal.substring(0, nomeArquivoLocal.indexOf(".")) + ".wsdl";
                }else{
                    nomeArquivoLocal = nomeArquivoLocal.replace("?", ".");
                }
            }else{
                if(!nomeArquivoLocal.contains(".")){   
                    if (nomeArquivoLocal.contains("wsdl")){
                        nomeArquivoLocal = opName + ".wsdl";
                    }else{
                        nomeArquivoLocal = nomeArquivoLocal + ".wsdl";
                    }                    
                }else{
                    nomeArquivoLocal = nomeArquivoLocal.substring(0, nomeArquivoLocal.indexOf(".")) + ".wsdl";
                }
            }*/
                        
            nomeArquivoLocal = getDirServiceRepository() + File.separator + opName + ".wsdl";
            this.wsdlLocalFileName = nomeArquivoLocal;
            
            File wsdlFile = new File(nomeArquivoLocal);
            
            //verifico se o arquivo existe no diretorio
            if (wsdlFile.exists()){          
                wsdlFile.delete();
                wsdlFile.createNewFile();
            }
            
            //gravo o arquivo em disco 
            FileOutputStream fos;
            try (InputStream is = wsdlURI.toURL().openStream()) {
                fos = new FileOutputStream(nomeArquivoLocal);
                int umByte = 0;
                while ((umByte = is.read()) != -1){  
                    fos.write(umByte);  
                }
            }  
            fos.close();
            
            return true;
        }catch(Exception e){
            System.out.println(e.getMessage());
            wsdlLocalFileName = "";
            return false;
        }
    }
    
    private boolean verificarAnotacaoParametros(List<WSDLRecoverParams> lstParameters){
        try{
            for(WSDLRecoverParams parameter : lstParameters){
            
                if((parameter.isMandatory()) && (parameter.getLocalPart().equals(""))){
                    JOptionPane.showMessageDialog(null, "The parameter ["+ parameter.getName() +
                                "] is mandatory. Please, fill the parameter.", ".: e-ScienceNet :.", JOptionPane.WARNING_MESSAGE);
                    return false;
                }else{
                    if(parameter.getLocalPart() != null){
                        if(parameter.getLocalPart().contains("Thing")){
                            if(parameter.getSubParams().isEmpty()){
                                JOptionPane.showMessageDialog(null, "The parameter ["+ parameter.getName() +
                                        "] is a complex type, but there isn't some sub-parameter associeted. Please, fill the parameter", ".: e-ScienceNet :.", 
                                        JOptionPane.WARNING_MESSAGE);
                                return false;
                            }
                        }
                    }
                }

                boolean retorno = verificarAnotacaoParametros(parameter.getSubParams());
                if(!retorno){
                    return retorno;
                }
            }

            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error in process the parameter.\n\n" + e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean gerarOWLS(String serviceName, WSDLOperation wsdlOperation, String serviceDescription, String logicalURI, 
                             List<WSDLRecoverParams> inputParams, List<WSDLRecoverParams> outputParams, QNameProvider qNames, 
                             String serviceDomainTerm, ServicesInfoModel servicesInfoModel, boolean isConnector){
        
        File owlsFile = null;
        
        try{            
            this.owlsFilePath = "";
            String name = serviceName.replaceAll(" ", "_");
            
            WSDLTranslator translator = new WSDLTranslator(wsdlOperation, logicalURI, name);

            translator.setServiceName(serviceName);
            translator.setTextDescription(serviceDescription);

            if(!verificarAnotacaoParametros(inputParams)){
                return false;
            }
            
            if(!verificarAnotacaoParametros(outputParams)){
                return false;
            }
            
            Set usedNames = new HashSet();
            if (! generateOWLsInputs(inputParams, translator, wsdlOperation, usedNames, qNames)){
                return false;
            }
            
            if (! generateOWLsOutputs(outputParams, translator, wsdlOperation, usedNames, qNames)){
                return false;
            }

            String owls = this.getOwlsDir() + File.separatorChar + serviceName + ".owl";
            
            owlsFile = new File(owls);
            if (owlsFile.exists()){
                owlsFile.delete();
                owlsFile.createNewFile();
                this.owlsFilePath = owls; 
            }
            
            try (FileOutputStream fos = new FileOutputStream(owlsFile)) {
                translator.writeOWLS(fos);
            }            

            if (! this.saveWSDLInServiceRepository(wsdlOperation.getService().getFileURI(), serviceName)){
                throw new Exception("Don't was possible to make download of the WSDL file relative to service "+ serviceName);
            }

            if(!SemanticManager.getInstance().gernerateSubClassComponentInOWLS(owls)){
                return false;
            }
            
            //TODO Talvez remover isso daqui
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File owlFile = new File(owls);
            OWLOntology ont = manager.loadOntologyFromOntologyDocument(owlFile);
            
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
            model.read("file:///" + owls);
            
            if(!this.runSubParameters(inputParams, ont, model, owls, true)){
                return false;
            }
            
            if(!this.runSubParameters(outputParams, ont, model, owls, false)){
                return false;
            }
            
            if(!isConnector){
                if (SemanticManager.getInstance().generateServiceTermInOWLS(owls, logicalURI, serviceDomainTerm, serviceName)){ 
                    if (SemanticManager.getInstance().generateServiceRealNameInOWLS(owls, logicalURI, wsdlOperation.getName(), serviceName)){ 
                        if (! owlsValidation(owlsFile)){
                            return false;
                        }
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }else{
                if (SemanticManager.getInstance().generateServiceRealNameInOWLS(owls, logicalURI, wsdlOperation.getName(), serviceName)){ 
                    if (! owlsValidation(owlsFile)){
                        return false;
                    }
                }else{
                    return false;
                }
            }
            
            servicesInfoModel.setWsdlFile(this.wsdlLocalFileName);
            servicesInfoModel.setWsdlServiceName(serviceName);
            servicesInfoModel.setOwlsFile(owls);
            servicesInfoModel.setDomainName(serviceDomainTerm.substring(serviceDomainTerm.lastIndexOf("#") + 1));
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage(), ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE, 
                                (new javax.swing.ImageIcon(getClass().getResource("/images/error.png"))));
            
            if(owlsFile.exists()){
                owlsFile.delete();
            }
            
            return false;
        }            
    }
    
    private boolean runSubParameters(List<WSDLRecoverParams> lstParameters, OWLOntology ont, OntModel model, String owls, boolean isInput){
        
        for(WSDLRecoverParams parameter : lstParameters){                
            
            if(parameter.getParameterTypeXSD() != null){
                if(!SemanticManager.getInstance().generateParameterTypeXSDInOWLS(owls, parameter, ont, model)){
                    return false;
                }
            }
            
            if(parameter.getNameSpaceURI() != null){
                if(!createSubParameterInOWLS(parameter, ont, model, owls, isInput)){
                    return false;
                }
            }                
        }

        return true;        
    }
    
    private boolean createSubParameterInOWLS(WSDLRecoverParams parameter, OWLOntology ont, OntModel model, String owls, boolean isInput){
        
        for(WSDLRecoverParams subParameter : parameter.getSubParams()){
            if(subParameter.getNameSpaceURI() != null){
                if(!SemanticManager.getInstance().createSubParameter(owls, parameter.getName(), subParameter, ont, model, isInput)){
                    return false;
                }
            }

            if(!subParameter.getSubParams().isEmpty()){
                if(!this.createSubParameterInOWLS(subParameter, ont, model, owls, isInput)){
                    return false;
                }
            }
        }            

        return true;
    }
    
    private boolean owlsValidation(File owlsFile){
        try{
            boolean valid = false;
                        
            OWLKnowledgeBase kb = OWLFactory.createKB();
            kb.setReasoner("Pellet");            
            kb.read(owlsFile.toURI());
            valid = kb.isConsistent();

            if(!valid)
                throw new Exception("The semantic service description (OWL-s) wasn't generated with success!");
            else {
                JOptionPane.showConfirmDialog(null, "Semantic service description (OWL-s) was generated with success!", 
                                              ".: e-ScienceNet :.", JOptionPane.DEFAULT_OPTION, 
                                              JOptionPane.INFORMATION_MESSAGE, 
                                          (new javax.swing.ImageIcon(getClass().getResource("/images/information.png"))));
            }
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e.getMessage(), ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE, 
                                (new javax.swing.ImageIcon(getClass().getResource("/images/error.png"))));
            return false;
        }           
    }
    
    private boolean generateOWLsInputs(List<WSDLRecoverParams> inputParams, WSDLTranslator translator, WSDLOperation wsdlOperation, Set usedNames, QNameProvider qNames){
        
        for(WSDLRecoverParams parameter : inputParams){
            WSDLParameter param = wsdlOperation.getInput(parameter.getName());
            
            String paramName = parameter.getName();
            String paramType = parameter.getLocalPart();
            
            String prefix = paramName;
            for( int count = 1; usedNames.contains( paramName ); count++ ){
                paramName = prefix + count;
            }
            
            usedNames.add( paramName );
            
            URI paramTypeURI;
            try {
                paramType = qNames.longForm(paramType);
                paramTypeURI = new URI(paramType);
            } catch(Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE, 
                                (new javax.swing.ImageIcon(getClass().getResource("/images/error.png"))));
                return false;
            }

            translator.addInput(param, paramName, paramTypeURI, null);
        }
        
        return true;
    }
    
    private boolean generateOWLsOutputs(List<WSDLRecoverParams> outputParams, WSDLTranslator translator, WSDLOperation wsdlOperation, Set usedNames, QNameProvider qNames){
        
        for(WSDLRecoverParams parameter : outputParams){
            WSDLParameter param = wsdlOperation.getOutput(parameter.getName());
            
            String paramName = parameter.getName();
            String paramType = parameter.getLocalPart();
            
            String prefix = paramName;
            for( int count = 1; usedNames.contains( paramName ); count++ ){
                paramName = prefix + count;
            }
            
            usedNames.add( paramName );
            
            URI paramTypeURI;
            try {
                paramType = qNames.longForm(paramType);
                paramTypeURI = new URI(paramType);
            } catch(Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE, 
                                (new javax.swing.ImageIcon(getClass().getResource("/images/error.png"))));
                return false;
            }

            translator.addOutput(param, paramName, paramTypeURI, null);
        }
        
        return true;
    }
    
    public void executeSingleService(){
        FExecuteSingleService frmExecuteSingleService = new FExecuteSingleService(null, true);
        frmExecuteSingleService.setVisible(true);
    }
    
    public List<OWLSOperation> getAllServices(){
        try{
            List<OWLSOperation> lstOWLSOp = new ArrayList<>();
            
            List<ServicesInfoModel> lstServicesInfoModels = SemanticManager.getInstance().searchServices(null, true);
            
            if(lstServicesInfoModels == null){
                return null;
            }
            
            for(ServicesInfoModel servicesInfoModel : lstServicesInfoModels){
                
                OWLSOperation owlsOperation = SemanticManager.getInstance().serviceSemanticResearch(servicesInfoModel.getOwlsFile(), "", true);
                if(owlsOperation != null){
                    lstOWLSOp.add(owlsOperation);
                }else{
                    return null;
                }                
            }            
            
            return lstOWLSOp;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Wasn't possible to get all services.\n\n" + e , ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * @return the appDir
     */
    public String getAppDir() {
        return appDir;
    }

    /**
     * @param appDir the appDir to set
     */
    public void setAppDir(String appDir) {
        this.appDir = appDir;
    }

    /**
     * @return the owlsDir
     */
    public String getOwlsDir() {
        return owlsDir;
    }

    /**
     * @param owlsDir the owlsDir to set
     */
    public void setOwlsDir(String owlsDir) {
        this.owlsDir = owlsDir;
    }

    /**
     * @return the dirServiceRepository
     */
    public String getDirServiceRepository() {
        return dirServiceRepository;
    }

    /**
     * @return the wsdlLocalFileName
     */
    public String getWsdlLocalFileName() {
        return wsdlLocalFileName;
    }

    /**
     * @return the owlsFilePath
     */
    public String getOwlsFilePath() {
        return owlsFilePath;
    }
}
