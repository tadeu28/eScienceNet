

package juniorvs.virtualdir;

/**
 * Interface responsável pela descoberta de eventos na rede
 * 
 * @author Tadeu Classe
 */
public interface OuvinteDescoberta {
    
    /**
     * Método de descoberta de eventos da rede
     * 
     * @param event Eventos de descoberta
     */
    public void eventoDescoberta(EventoDescoberta event);
}


