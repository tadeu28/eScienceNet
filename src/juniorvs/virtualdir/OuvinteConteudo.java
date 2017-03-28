
package juniorvs.virtualdir;

/**
 * Interface responsável por ouvir conteúdos na rede
 * 
 * @author Tadeu Classe
 */
public interface OuvinteConteudo{
    
    /**
     * Método de recuperação de conteúdo da rede
     * 
     * @param url conteúdo (caminho)
     */
    public void recuperarConteudo(String url);
}
