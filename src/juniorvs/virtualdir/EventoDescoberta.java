package juniorvs.virtualdir;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.List;

/**
 * Classe responsável pela descoberta de anúncios
 * 
 * @author Tadeu Classe
 */
public class EventoDescoberta extends EventObject {
    
    //atributos estaticos da classe    
    public static final int ANUNCIO_ADICIONADO = 1;
    public static final int TODOS_ANUNCIOS_DELETADOS = 2;
    public static final int ANUNCIO_ALTERADO = 3;
    
    //atributos da classe
    private int tipo;
    private List listaAnuncios;
    
    /**
     * Método construtor da classe
     * 
     * @param source objeto de recurso
     * @param tipo tipo de descoberta
     * @param en enumeração de eventos de descoberta
     */
    public EventoDescoberta(Object source, int tipo, Enumeration en) {
        //passo o parâmetro para a classe pai
        super(source);
        //seto o tipo
        this.tipo = tipo;
        //crio a lista de anúncios
        listaAnuncios = new ArrayList();
        
        //percorro os elementos que foram encontrados no enumerado
        while (en.hasMoreElements()) {
            //adiciono os elementos encontrados na lista
            listaAnuncios.add(en.nextElement());
        }
    }
    
    /**
     * Método construtor da classe
     * 
     * @param source objeto de recurso
     * @param tipo tipo de descoberta
     * @param listaAnuncios lista de anuncios de descorberta
     */
    public EventoDescoberta(Object source, int tipo, List listaAnuncios) {
        //passo o parâmetro para a classe pai    
        super(source);
        //seto o tipo de descoberto
        this.tipo = tipo;
        //passo a lista de anúncios
        this.listaAnuncios = listaAnuncios;
        
        //verifico se a lista de anuncios não está nula
        if (this.listaAnuncios == null){
            //crio a lista de anûncios
            this.listaAnuncios = new ArrayList();
        }        
    }
    
    /**
     * Método responsável por retornar o tipo de evento de descoberta
     * 
     * @return tipo de evento
     */
    public int getTipo() {
        return tipo;
    }
    
    /**
     * Método responsável por recuperar a lista de anuncios
     * 
     * @return lista de anuncioas de eventos
     */
    public List recuperaAnuncios() {
        return listaAnuncios;
    }
}


