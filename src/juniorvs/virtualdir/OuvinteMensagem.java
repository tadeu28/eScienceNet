/*
 * Created on 26/06/2004
 */
package juniorvs.virtualdir;

/**
 * Interface de Ouvinte de Mensagem entre os Peers
 * 
 * @author Tadeu Classe
 */
public interface OuvinteMensagem {
    
    /**
     * Método de mensagens entre os peers
     * 
     * @param user usuário
     * @param msg mensagem
     */
    void eventoMensagem(String user, String msg);
    
    /**
     * Método de entrada de peers
     * 
     * @param name nome do peer
     */
    void eventoEntradaPeer(String name);
    
    /**
     * Método de saída de peer
     * 
     * @param name nome do peer
     */
    void eventoSaidaPeer(String name);
}
