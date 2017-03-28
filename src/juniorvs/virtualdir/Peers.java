/*
 * Created on 04/04/2004
 *
 */
package juniorvs.virtualdir;

import java.util.HashMap;
import net.jxta.share.ContentAdvertisement;

/**
 * Classe responsável pelos peers do grupo
 * 
 * @author Tadeu Classe
 */
public class Peers {
    
    /**
     * Atributos da classe
     */
    private HashMap listPeers = new HashMap();

    /**
     * Método de retorno da quantidade de peers
     * @return inteiro
     */
    public int getQtdPeers() {
        //quantidade de peers
        return listPeers.size();
    }
    
    /**
     * Métode de retorno de um peer pelo seu nome
     * @param name nome do peer
     * @return Peer
     */
    public Peer getPeer(String name) {
        //retorna o peer
        return (Peer) listPeers.get(name);
    }
    
    /**
     * Método de atualização de conteudo de anuncio
     * 
     * @param nameUser nome do usuario
     * @param adv anuncio
     * @return booleano
     */
    public boolean atualizaAnuncioConteudo(String nameUser, ContentAdvertisement adv) {
        //pego o peer pelo nome
        Peer user = (Peer) listPeers.get(nameUser);
        //verifico se o usuário não é nulo
        if (user != null) {
            //retorno o anuncio de conteudo
            return user.addAnuncioConteudo(adv);
        }
        return false;
    }
    
    /**
     * Método de retorno de peer pelo índice
     * @param index índice 
     * @return Peer
     */
    public Peer getPeer(int index) {
        //retorna o peer pelo índice
        return (Peer) listPeers.values().toArray()[index];
    }

    /**
     * Método de atualização da lista de peer
     * @param nameUser nome do usuário
     * @return booleano
     */
    public boolean atualizaPeer(String nameUser) {
        //pego o peer pelo nome
        Peer user = (Peer) listPeers.get(nameUser);
        //verifico se o usuário não e nulo
        if (user == null) {
            //crio um novo peer
            user = new Peer();
            //seto o nome do usuário
            user.setNome(nameUser);
            //insito na lista de peers
            listPeers.put(nameUser, user);
            return true;
        }
        return false;
    }
    
    /**
     * Método de remoção de peers da lista
     * @param nameUser nome do peer
     * @return booleando
     */
    public boolean removePeer(String nameUser) {
        //pego o peer pelo nome
        Peer user = (Peer) listPeers.get(nameUser);
        //verifico se o usuário não é vazio
        if (user != null) {
            //removo o peer da lista
            return listPeers.remove(nameUser) == null ? false : true;
        }
        return false;
    }
}
