
package juniorvs.virtualdir;

/**
 * Interface de exibição de mensagens e informações
 * 
 * @author Tadeu Classe
 */
public interface Mensagem {
    /**
     * Método de exibição de informações
     * 
     * @param str Informações
     */
    public void info (String str);
    
    /**
     * Método de exibição de mensagens
     * 
     * @param str mensagens
     */
    public void exibirMensagem (String str);
    
    /**
     * Método de exibição de mensagens
     * 
     * @param str mensagem
     * @param user usuário
     */
    public void exibirMensagem (String str, String user);
}


