package juniorvs.virtualdir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import juniorvs.virtualdir.ext.PeerGroupManager;
import net.jxta.exception.PeerGroupException;
import net.jxta.instantp2p.Search;
import net.jxta.peergroup.PeerGroup;
import net.jxta.share.CMS;
import net.jxta.share.Content;
import net.jxta.share.ContentAdvertisement;
import net.jxta.share.ContentManager;
import net.jxta.share.UUID;

/**
 * Classe responsável pela procura de elementos na rede
 * 
 * @author Tadeu Classe
 */
public class GerenciaProcura implements OuvinteProcura, Runnable {

    //atributos estáticos da classe
    public final static String ANUNCIO_P2P = "AnuncioArquivos";
    private final static int PROCURA_ARQUIVO = 0, ARQUIVO_ADICIONADO = 1;
    private final static int DefaultBufferSize = 64 * 1024;
    
    //atributos da classe
    private ConteudoDownload conteudoDownload;
    private PeerGroupManager peerGroupManager;
    private Search search = null;
    private net.jxta.instantp2p.SearchResult[] searchResults = null;
    private String searchString;
    private Vector requests = new Vector();
    private Vector listeners = new Vector();
    private boolean conteudoRemotoAtualizado = false;
    private Vector itensRemovidos = new Vector();
    private ContentManager contentManager = null;
    private Content[] content = null;
    private Vector contentItems = new Vector();
    private boolean conteudoLocalAtualizado = false;
    private int type = PROCURA_ARQUIVO;
    private File adicaoArquivos[] = null;
    private Hashtable cmsTable = new Hashtable();
    private Hashtable searchTable = new Hashtable();

    /**
     * Método contrutor da classe
     * 
     * @param manager gerente do grupo de peers
     * @param getContentDownload conteúdo de download
     */   
    public GerenciaProcura(PeerGroupManager manager, ConteudoDownload getContentDownload) {
        //seto o gerente do grupo de peers    
        this.peerGroupManager = manager;
        //seto o objeto de conteúdo de download
        this.conteudoDownload = getContentDownload;
        //altero o grupo passando o grupo selecionado
        grupoAlterado(manager.getSelectedPeerGroup());
    }
    
    /**
     * Atualioz o PeerGroupManager
     * 
     * @param manager Objeto de compartilhamento de arquivos
     */
    public void updatePeerGroupManager(PeerGroupManager manager){
        this.peerGroupManager = manager;
    }
    
    /**
     * Método responsável por obtero o diretório do grupo
     * 
     * @param pg grupo do peer
     * @return arquivo com o diretório do grupo
     */
    private static File getDiretorioGrupo(PeerGroup pg){
        //crio a variavel de diretorio
        File dir = null;
        //crio o objeto de diretorio principal
        File rootDir = new File(ANUNCIO_P2P);
        
        //verifico se o diretorio de anuncio de arquivos existe
        if (!rootDir.exists()) {
            //caso não exista eu crio o diretório
            rootDir.mkdir();
        }
        
        //pego o diretório de anuncio  de arquivos
        dir = new File(rootDir, getNomeAbreviado(pg));
        
        //verifico a existencia do diretorio
        if (!dir.exists()) {
            //caso o diretório não existe, eu o crio
            dir.mkdir();
        }
        
        //retorno o diretório
        return dir;
    }
    
    /**
     * Método responsável por retornar o nome do grupo abreviado
     * 
     * @param pg grupo do peer
     * @return nome abreviado do grupo
     */
    private static String getNomeAbreviado(PeerGroup pg) {
        //retorno o nome abreviado do grupo
        return pg.getPeerGroupID().getUniqueValue().toString();
    }
    
    /**
     * Método responsável por recuperar o conteúdo remoto de cada peer
     * 
     * @return conjunto de conteúdo recuperado
     */
    public Enumeration recuperaConteudoRemoto() {
        
        //verifico se a lista de retornados não está vazia
        if (searchResults != null) {
            //verifico se o conteúdo remoto não está atualizado
            if (!conteudoRemotoAtualizado) {
                //verifico se não há items removidos
                itensRemovidos.removeAllElements();
                //percorro a lista de resultados da busca
                for (int i = 0; i < searchResults.length; i++) {
                    //adiciono o elemento na lista de removidos
                    itensRemovidos.addElement(searchResults[i]);
                }
                //seto que o conteúdo remoto foi atualizado
                conteudoRemotoAtualizado = true;
            }
        } else {
            //removo todos os elementos da lista de removidos
            itensRemovidos.removeAllElements();
            //informo que o conteúdo remoto foi atualizado
            conteudoRemotoAtualizado = true;
        }
        
        //retorno a lista de elementos removidos
        return itensRemovidos.elements();
    }
    
    /**
     * Método responsável pela recuperação do conteúdo local de cada peer
     * 
     * @return conjunto de arquivos do conteúdo local
     */
    public Enumeration recuperaConteudoLocal() {
        //verifico se a lista de conteuno não esta vazia    
        if (content != null) {
            //verifico se o conteúdo local não está atualizado
            if (!conteudoLocalAtualizado) {
                //removo todos os elementos da lista de conteúdo
                contentItems.removeAllElements();
                //percorro os arquivos da lista de conteúdo
                for (int i = 0; i < content.length; i++) {
                    //adiciono os arquivo a lista de conteudo
                    contentItems.addElement(content[i]);
                }
                
                //informo que o conteudo local foi atualizado
                conteudoLocalAtualizado = true;
            }
        } else {
            //removo os elementos da lista de conteudo
            contentItems.removeAllElements();
            //informo que o conteúdo local foi atualizado
            conteudoLocalAtualizado = true;
        }
        
        //retorno o conteúdo local
        return contentItems.elements();
    }
    
    /**
     * Método responsável por setar o ounvinte de conteúdo na classe
     * 
     * @param l Ouvinte de Conteudo 
     */
    public void addListener(OuvinteConteudo l) {
        //adiciono o ouvinte na lista de ouvintes
        listeners.addElement(l);
    }
    
    /**
     * Método responsável por remover o ouvinte de conteudo da classe
     * 
     * @param l Ouvinte de Conteudo
     */
    public void removeListener(OuvinteConteudo l) {
        //removo o ouvinte de conteúdo da lista
        listeners.removeElement(l);
    }
    
    /**
     * Método responsável por iniciar a pesquisa de conteudo
     * 
     * @param searchString string de busca de arquivos
     */
    public void iniciaProcura(String searchString) {
        //pego a string de busca de arquivos
        this.searchString = searchString;
        //verifico se a string é nula
        if (this.searchString == null) {
            //então seto a como uma string vazia
            this.searchString = "";
        }
        //seto o tipo como procura de arquivos
        type = PROCURA_ARQUIVO;
        //adiciono a tarefa no gerenciador de grupos
        peerGroupManager.getWorkerThread().addData(this);
    }
    
    /**
     * Método responsável por executar esta classe
     */
    @Override
    public void run() {
        //crio um objeto para exceções
        Exception exp = null;
        //crio um contador
        int i;

        //verifico se o tipo é de procura de arquivos
        if (type == PROCURA_ARQUIVO) {
            //realizo a busca
            search.search(searchString);
            //atualizo a lista de conteúdo
            atualizaListaConteudo();

        //verifico se é o tipo de arquivos adicionados    
        } else if (type == ARQUIVO_ADICIONADO) {
            try {
                //verifico se a adição de arquivos é diferente de null
                if (adicaoArquivos != null) {
                    //percorro a lista de arquivos
                    for (i = 0; i < adicaoArquivos.length; i++) {
                        //adiciono o arquivo
                        addFile(adicaoArquivos[i]);
                    }
                }
                
            }catch (Exception e) {
                if (exp != null) {
                    exp = new Exception("Não foi possível adicinar o arquivo pois houve algum tipo de falha.");
                }
            }
            
            //pego o objeto de contetudo
            content = contentManager.getContent();
            //seto que o conteúdo local não foi atualizado
            conteudoLocalAtualizado = false;
        }

    }
    
    /**
     * Método responsãvel por fazer alterações nos grupos de peers
     * 
     * @param group Grupo de peers
     */
    public final void grupoAlterado(PeerGroup group) {
        
        //verifico se o grupo não está vazio
        if (null != group) {
            //verifico se a pesquisa não está vazia
            if (null != search) {
                //cancelo a busca
                search.cancel();
                //removo o ouvinte da lista de pesquisa
                search.removeListener(this);
            }
            
            //pesquiso o grupo
            search = getSearch(group);
            //adiciono novamente o grupo na lista de pesquisa
            search.addListener(this);

            //crio o objeto de notificação de arquivos
            CMS cms = getCMS(group);
            //pego o gerenciador de conteudo do objeto de notificação
            contentManager = cms.getContentManager();
            //pego o conteudo do gerenciador de conteudo
            content = contentManager.getContent();
        }
        
        //seto que o conteúdo remoto e local não foi atualizado
        conteudoRemotoAtualizado = false;
        conteudoLocalAtualizado = false;
    }

    /**
     * Método responsável pela adição de conteudo na rede
     * 
     * @param path caminho do arquico que foi adicionado na rede
     */
    public void addContent(String path) {
        //verifico se o caminho passado não é vazio
        if (path != null && !path.trim().equals("")) {
            //pego o arquivo    
            File file[] = { new File(path)};
            //adiciono o conteúdo
            addContent(file);
        }
    }
    
    /**
     * Método responsável pela adição de conteudo na rede
     * 
     * @param addFiles Lista de arquivos
     */
    public void addContent(File addFiles[]) {
        //seto o tipo como adição de aquivo
        type = ARQUIVO_ADICIONADO;
        //seto a lista de arquivos adicionados
        this.adicaoArquivos = addFiles;
        //insiro os dados no servido dentro do gerenciados do grupo
        peerGroupManager.getWorkerThread().addData(this);
    }
    
    /**
     * Método responsável pela recuperação do conteúdo remoto
     * 
     * @param adv anúncio do arquivo
     * @param fileName nome do arquivo
     * @param listener ouvinte de conteúdo
     */
    public synchronized void recuperaConteudo(ContentAdvertisement adv, String fileName, OuvinteConteudo listener) {
        //crio um novo arquivo temporário com o nome do arquivo
        File tmpFile = new File(fileName);
        try {
            //crio o objeto de requisição do arquivo
            GetRequestor request = new GetRequestor(peerGroupManager.getSelectedPeerGroup(), adv, tmpFile, conteudoDownload);
            //insito o objeto de requisição do arquivo na lista de requisições
            getRequests().addElement(request);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
        //atualizo a lista de resuisições
        atualizaListaRequisicao();
    }
    
    /**
     * Método resposável por realizar a gravação do arquivo tranferido
     * 
     * @param contentFile conteudo do arquivo
     * @param path caminho do arquivo
     */
    public void salvaConteudo(File contentFile, String path) {

        //crio streams de entrada e saida de arquivos    
        FileInputStream ip = null;
        FileOutputStream op = null;

        try {
            //crio os objetos de entrada e saida dos arquivos
            ip = new FileInputStream(contentFile);
            op = new FileOutputStream(path);
            
            //crio o buffer de gravação do arquico
            byte[] buffer = new byte[DefaultBufferSize];

            //enquanto o arquivo de entrada estiver em transferência
            while (ip.available() > 0) {
                //leio a posição no buffer
                int res = ip.read(buffer);
                //gravo o arquivo com o objeto de saída
                op.write(buffer, 0, res);
            }
            
            //fecho os objetos de entrada e saída de arquivos
            ip.close();
            op.close();
        } catch (IOException e) {}
    }
    
    /**
     * Método responsável por mostrar o conteúdo de um peer
     * 
     * @param cAdv anúncio dos arquivos
     * @param listener ouvinte de conteúdo dos arquivos
     */
    public synchronized void mostraConteudo(ContentAdvertisement cAdv, OuvinteConteudo listener) {
            
        //verifico se o diretório de anuncio de arquivos está criado
        File rootDir = new File(ANUNCIO_P2P);
        if (!rootDir.exists()) {
            //crio o diretorio de anuncio de arquivos
            rootDir.mkdir();
        }

        //verifico se o diretorio temporário está criado
        File tmpDir = new File(rootDir, "tmp");
        if (!tmpDir.exists()) {
            //crio o diretório temporário
            tmpDir.mkdir();
        }

        //pego o nome do arquivo
        String name = cAdv.getName();
        //crio uma variável para receber a extensão do arquivo
        String ext = "";
        //pego o indice do ponto final
        int index = name.lastIndexOf('.');
        //verifico se o indice foi encontrado
        if (index != -1) {
            //pego a extensão do arquivo
            ext = name.substring(name.lastIndexOf('.'));
        }
        
        //crio o arquivo temporário
        File tmpFile = new File(tmpDir, Integer.toHexString(new UUID().hashCode()) + ext);
        try {
            //instancio o objeto de requisição
            GetRequestor requestor = new GetRequestor(peerGroupManager.getSelectedPeerGroup(), cAdv, tmpFile, conteudoDownload);
            //adiciono o elemento de resuisição na lista de requisiçoes
            getRequests().addElement(requestor);
        } catch (InvocationTargetException e) {}
    }
    
    /**
     * Método responsável por atualizar a lista de conteúdo
     */
    @Override
    public void atualizaBusca() {
        //atualioz a lista de conteúdo
        atualizaListaConteudo();
    }

    /**
     * Método responsável por obter a instancia do objeto de transferencia de arquivos
     * 
     * @param group grupo de peers
     * @return CMS
     */
    public CMS getCMS(PeerGroup group) {
        //instancio o objeto de transferencia a partir da lista de arquivos
        CMS cms = (CMS) cmsTable.get(getNomeAbreviado(group));
        
        //verifico se o objeto é nulo
        if (null == cms) {
            //crio um novo objeto de tranferencia
            cms = new CMS();
            try {
                //inicializo o objteo de transferencia para o grupo
                cms.init(group, null, null);
            } catch (PeerGroupException e) {
                e.printStackTrace();
            }
            
            //starto o objeto de transferência de arquivos
            cms.startApp(new File(getDiretorioGrupo(group), CMS.DEFAULT_DIR));
        }
        
        //verifico se o objeto não está vazio
        if (null != cms) {
            //coloco o na tabela de objetos de tranferencia
            cmsTable.put(getNomeAbreviado(group), cms);
        }
        
        //retorno o objeto de transferencia
        return cms;
    }
    
    /**
     * Método responsável por retornar um objeto de busca
     * 
     * @param group
     * @return 
     */
    private Search getSearch(PeerGroup group) {
        //crio o objeto de busca das informaçõs pegando o da tabela de objetos de busca
        Search src = (Search) searchTable.get(getNomeAbreviado(group));
        
        //verifiico se o objeto não esta vazio
        if (null == src) {
            //crio um novo objeto de busca
            src = new Search(group, new File(getDiretorioGrupo(group), Search.DEFAULT_DIR));
        }
        
        //verifico se o objeto de busca esta vazio
        if (null != src) {
            //insiro o objeto de busca criado na tabela hash para tal fim
            searchTable.put(getNomeAbreviado(group), src);
        }
        
        //retorno o objeto de busca
        return src;
    }
    
    /**
     * Método responsável pela adição de arquivos
     * 
     * @param file arquivos a ser adicionado
     * @throws Exception 
     */
    public void addFile(File file) throws Exception {
        
        //verifico se é um arquivo
        if (file.isFile()) {
            //compartilho o arquivo com o gerenciador de conteudo
            contentManager.share(file, peerGroupManager.getMyPeerName());
        } else {
            //pego a lista de arquivos
            File fileList[] = file.listFiles();
            //percorro a lista de arquivos
            for (int i = 0; i < fileList.length; i++) {
                //adiciono o arquivo
                addFile(fileList[i]);
            }
        }
    }
    
    /**
     * Método responsável por atualizar o conteúdo remoto
     */
    private synchronized void atualizaListaConteudo() {
        //verifico se o obejto de pesquisa está instanciado
        if (search == null) {
            return;
        }
        
        //retorno os resultados da pesquisa
        searchResults = search.getResults();
        //seto que o contenudo remoto não está atualizado
        conteudoRemotoAtualizado = false;
    }
    
    /**
     * Método responsável por atualizar a lista de requisições
     */
    private void atualizaListaRequisicao() {
        //pego a lista de requisições
        Enumeration en = getRequests().elements();
        //percorro a lista de requisições
        while (en.hasMoreElements()) {
            //crio a instancia da requicições
            GetRequestor request = (GetRequestor) en.nextElement();
        }
    }

    /**
     * Método responsável pelo cancelamento da transferência de arquivos
     */
    public void cancelar(){
        //obtenho a requisição
        GetRequestor requestor = (GetRequestor) requests.get(0);
        //notifivo que a transferência foi cancelada
        requestor.notificarCancelamento(requestor.getFile().toString());
        //cancelo a transferêcia do arquivo
        requestor.cancel();
        //apago o arquivo da pasta
        requestor.getFile().delete();            
        //limpo a lista de requisições
        requests.clear();
    }
    
    /**
     * Método responsável por retornar um vetor de requisições
     * 
     * @return Vector
     */
    public Vector getRequests() {
        //retorna a lista de requisições
        return requests;
    }

}
