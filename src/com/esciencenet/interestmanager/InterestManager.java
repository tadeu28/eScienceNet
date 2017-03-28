package com.esciencenet.interestmanager;

import com.esciencenet.semanticmanager.*;
import com.esciencenet.forms.*;
import com.esciencenet.models.*;
import java.util.ArrayList;

/**
 * Classe responsável por métodos de interesse de cada peer, gerenciando grupos
 * de interesses em conjunto com o Gerente de Semântica (Semantic Manager)
 * 
 * @author Tadeu Classe
 */
public class InterestManager {
    
    /**
     * metodo construtor da classe
     */
    public InterestManager(){
        
    } 
    
    /**
     * Método para exibição da tela de seleção e criação dos grupos
     */
    public void exibirGrupos(){
        
        ArrayList<PeerGroupModel> peerGroupModels = SemanticManager.getInstance().obterGrupos();
        FrmGrupos frmGrupos = new FrmGrupos(null, true, peerGroupModels);
        frmGrupos.setVisible(true);
        
    }
}
