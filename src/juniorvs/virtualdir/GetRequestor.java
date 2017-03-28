package juniorvs.virtualdir;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.share.ContentAdvertisement;
import net.jxta.share.client.GetContentRequest;

/**
 * Classe responável pela exibição do notificações acerca da transferência de arquivos
 * 
 * @author Tadeu Classe
 */
public class GetRequestor extends GetContentRequest {
    
    //atributos da classe
    private ContentAdvertisement adv = null;
    private String url = null;
    private ConteudoDownload conteudoDownload = null;

    /**
     * Método construtor da classe
     * 
     * @param pg grupo do peer
     * @param adv anúncioas
     * @param tmpFile arquivo temporário
     * @param conteudoDownload objeto de conteúdo de download
     * @throws InvocationTargetException exceção
     */
    public GetRequestor(PeerGroup pg, ContentAdvertisement adv, File tmpFile, ConteudoDownload conteudoDownload) throws InvocationTargetException {
        //passo os parâmetros para a classe pai
        super(pg, adv, tmpFile);
        //passo o objeto de conteudo de downloas
        this.conteudoDownload = conteudoDownload;
        //passo os anuncioas
        this.adv = adv;
        //seto o caminho do arquivo temporário
        url = tmpFile.getAbsolutePath();
    }

    /**
     * Método responsável por retornar os anúncios
     * 
     * @return ContentAdvertisement
     */
    public ContentAdvertisement getContentAdvertisement() {
        return adv;
    }
    
    /**
     * Método responsável por notificar o final da transição do arquivo
     */
    @Override
    public void notifyDone() {
        //notifica o final da transferência
        conteudoDownload.notificarDownloadCompleto(url);
    }
    
    /**
     * Método responsável por notificar a falha no download
     */
    @Override
    public void notifyFailure() {
        //notifica a falha no downloa
        conteudoDownload.notificarDownloadFalha();
    }
    
    /**
     * Método responsável por notificar o andamento do download
     * 
     * @param percentage porcentagem de transferência do arquivo
     */
    @Override
    public void notifyUpdate(int percentage) {                        
        //notifica o conteúdo que foi transferido
        conteudoDownload.notificarDownloadAtualiza(percentage, adv.getName());                        
    }

    /**
     * Método responsável por notificar o cancelamento do download
     * 
     * @param msg 
     */
    public void notificarCancelamento(String msg){
        //notifica o cancelamento do download
        conteudoDownload.notificarCancelamento(adv.getName());
    }
    
}
