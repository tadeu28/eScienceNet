package juniorvs.virtualdir;

/**
 * Interface para as mensagens de download de arquivos
 * 
 * @author Tadeu Classe
 */
public interface ConteudoDownload {
	
    /**
     * Notifica percentagem atual do download
     * 
     * @param percentage porcentagem do download
     */
    public void notificarDownloadAtualiza(int percentage, String descricao);

    /**
     * Notifica Falha de um download
     */
    public void notificarDownloadFalha();

    /**
     * Notifica quando o download esta completo         * 
     * 
     * @param url arquivo local que foi concluido
     */
    public void notificarDownloadCompleto(String url);

    /**
     * Notifica o cancelamento do download de um arquivo
     * 
     * @param msg mensagem de cancelamento de download
     */
    public void notificarCancelamento(String msg);
}
