

package juniorvs.virtualdir;

/**
 * Interface de estrutura do grupo
 * 
 * @author Tadeu Classe
 */
public interface OuvinteEstruturaGrupo {
    
    /**
     * Método de estrutura do grupo alterado
     * 
     * @param event evento de descoberta
     */
    public void estruturaGrupoAlterado(EventoDescoberta event);
    
    /**
     * Método de dados de peers alterados
     * 
     * @param event evento de descoberta
     */
    public void dadosPeerAlterado(EventoDescoberta event);
}


