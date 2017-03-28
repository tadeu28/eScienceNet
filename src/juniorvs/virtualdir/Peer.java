/*
 * Created on 04/04/2004
 *
 */
package juniorvs.virtualdir;

import java.util.HashMap;
import net.jxta.share.ContentAdvertisement;

/**
 * Classe responsável pela criação de peer na rede
 * 
 * @author Tadeu Classe
 */
public class Peer {
    
    //atributos da classe
    private HashMap listAnuncioConteudo = new HashMap();
    private String name;
    
    /**
     * Métodos responsável por retornar a quantidade de anuncioas do peer
     * 
     * @return quantidade de anuncioas
     */
    public int getQtdAnuncioConteudo(){
        //retorna a quantidade de anuncios
        return listAnuncioConteudo.size();
    }
    
    /**
     * Método de adição de anuncios
     * 
     * @param adv conteúdo do anuncio
     * @return boleano
     */
    public boolean addAnuncioConteudo(ContentAdvertisement adv) {
        //verifico se é possivel adicionar o nome do anuncion na lista
        if (listAnuncioConteudo.get(adv.getName()) == null){
            //adiciono o anuncio na lista
            listAnuncioConteudo.put(adv.getName(),adv);
            return true;
        }
        return false;
    }
    
    /**
     * Método de retorno de conteúdos de anuncio
     * 
     * @param index índice do anuncio
     * @return conteúdo do anuncio
     */
    public ContentAdvertisement getAnuncioConteudo(int index){
        //retorna o conteudo do anuncio
        return (ContentAdvertisement) listAnuncioConteudo.values().toArray()[index];
    }
    
    public ContentAdvertisement getAnuncioConteudo(String content){
        
        for (Object obj : listAnuncioConteudo.values().toArray()){
            
            ContentAdvertisement contentAdvertisement = (ContentAdvertisement) obj;
            
            if (contentAdvertisement != null){
                
                if (contentAdvertisement.getName().equals(content)){
                    return contentAdvertisement;
                }                
            }
        }
        return null;        
    }
    
    /**
     * Método de retorno do nome do peer
     * @return nome do peer
     */
    public String getNome() {
        //retorna o nome do peer
        return name;
    }
    
    /**
     * Método de inclusão do nome do peer
     * @param string nome do peer
     */
    public void setNome(String string) {
        name = string;
    }
}
