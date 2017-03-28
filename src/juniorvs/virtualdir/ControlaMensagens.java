package juniorvs.virtualdir;

import java.io.InputStream;
import java.util.Hashtable;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

/*
 * Classe que faz o controle da troca de mensagens entre os peers da rede
 * 
 * @author Tadeu Classe
 */
public final class ControlaMensagens implements PipeMsgListener {

    //atributos privados da classe controladora de mensagens
    private final static String CHAT_NOME_TAG = "JxtaTalkUserName";
    private final static String CHAT_NOME = "IP2PGRP";
    private final static String ENVIAR_NOME = "JxtaTalkSenderName";
    private final static String ENVIAR_GRUPO_NAME = "GrpName";
    private final static String ENVIAR_MENSAGEM = "JxtaTalkSenderMessage";
    private final static String ENVIAR_MENSAGEM_TIPO = "type";
    private final static String ENVIAR_MENSAGEM_PEER_NOVO = "userNew";
    private final static String ENVIAR_MENSAGEM_PEER_SAIDA = "userExit";

    //atributos da classe controladora de mensagem
    private PeerGroup group = null;
    private PipeService pipe = null;
    private DiscoveryService discoveryService = null;
    private InputPipe inputPipe = null;
    private OutputPipe outputPipe = null;
    private String peerNome = "anonymous";
    private OuvinteMensagem ouvinteMensagem;
    private Hashtable opTable = new Hashtable();
    private Hashtable ipTable = new Hashtable();

    /**
     * Método construtor da classe
     * 
     * @param group grupo de peers da rede
     * @param name nome do peer
     */
    public ControlaMensagens(PeerGroup group, String name) {
        //pego o nome do peer
        this.peerNome = name;
        //seto o peer grupo na classe
        setGrupo(group);
    }

    /**
     * Método responsável pela pesquisa de peers na rede p2p
     */
    public void procuraPeers() {
        try {
            //crio o objeto de mensagem
            Message msg = new Message();
            //adiciono o elemento de envio de mensagem de um novo peer
            msg.addMessageElement(null, new StringMessageElement(ENVIAR_MENSAGEM_TIPO, ENVIAR_MENSAGEM_PEER_NOVO, null));
            //adiciono o elemento de envio de mensagem com o nome do peer
            msg.addMessageElement(null, new StringMessageElement(ENVIAR_MENSAGEM, peerNome, null));
            //adiciono o elemento de envio de mensagem com o nome do peer
            msg.addMessageElement(null, new StringMessageElement(ENVIAR_NOME, peerNome, null));
            //adiciono o elemento de envio de mensagem com o nome do grupo
            msg.addMessageElement(null, new StringMessageElement(ENVIAR_GRUPO_NAME, group.getPeerGroupName(), null));
            //envio a mensagem para os peers
            outputPipe.send(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**     
     * Método responsável por inserir um grupo
     * 
     * @param group grupo contendo os peers
     */
    public synchronized void setGrupo(PeerGroup group) {
        //seto o grupo
        this.group = group;
        //seto o canal de envio do grupo
        this.pipe = group.getPipeService();
        //seto o obbjeto de descoberta de serviços
        this.discoveryService = group.getDiscoveryService();
        
        //verifico se a chave do grupo está localizada na tabela de saida
        if (!opTable.containsKey(group.getPeerGroupID().toString())) {
            //realiza o login no grupo
            efetuarLogin(group);
        } else {
            //senão, crio um pipe de saida para o novo grupo
            outputPipe = (OutputPipe) opTable.get(group.getPeerGroupID().toString());
        }
        
        //atualizo a lista de peers do nós
        atualizaLista(peerNome);
    }
    
    /**
     * Método responsável pela saída de peers da rede
     */
    public void saidaPeer() {
        try {
            //crio o objeto de envio de mensagem
            Message msg = new Message();
            //adiciono o elemento para envio de mensagem da saída de um peer
            msg.addMessageElement(null, new StringMessageElement(ENVIAR_MENSAGEM_TIPO, ENVIAR_MENSAGEM_PEER_SAIDA, null));
            //adiciono o elemento de envio de mensagem do nome do peer
            msg.addMessageElement(null, new StringMessageElement(ENVIAR_MENSAGEM, peerNome, null));
            //adiciono o elemento de envio de mensagem do nome do peer
            msg.addMessageElement(null, new StringMessageElement(ENVIAR_NOME, peerNome, null));
            //adiciono o elemento de envio de mensagem do nome do grupo
            msg.addMessageElement(null, new StringMessageElement(ENVIAR_GRUPO_NAME, group.getPeerGroupName(), null));
            //envio a mensagem para os peers
            outputPipe.send(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Método responsável por efetuar login no grupo
     */
    public synchronized void efetuarLogin(PeerGroup peerGroup) {
        //crio o anuncio para o canal canal de anúncio do grupo
        PipeAdvertisement myPipeAdvt = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());

        //crio o id para o canal
        myPipeAdvt.setPipeID(getUniquePipeID(peerGroup));
        //crio o nome para conversa no 
        myPipeAdvt.setName(CHAT_NOME_TAG + "." + CHAT_NOME);
        //seto o tipo de serviço do canal
        myPipeAdvt.setType(PipeService.PropagateType);

        try {
            //publico o canal para o descobridor de serviço
            discoveryService.publish(myPipeAdvt);
            //crio o canal de entrada
            inputPipe = pipe.createInputPipe(myPipeAdvt, this);
            //crio o canal de saída
            outputPipe = pipe.createOutputPipe(myPipeAdvt, 100);
            //coloco o canal de entrada no hash de entrada
            opTable.put(group.getPeerGroupID().toString(), outputPipe);
            //coloco o canal de saída no has de entrada
            ipTable.put(group.getPeerGroupID().toString(), inputPipe);
            //envio a mensagem para os demais peers da rede
            enviarMensagem("Peer juntou-se " + group.getPeerGroupName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Método responsável por setar o nome do peer
     * 
     * @param name 
     */
    public void setPeerNome(String name) {
        //seto o nome do peer
        peerNome = name;
    }
    
    /**
     * Método responsável por retornar o grupo
     * 
     * @return PeerGroup
     */
    public PeerGroup getGrupo() {
        return group;
    }
    
    /**
     * Método responsável por retornar o nome do Peer
     * 
     * @return String
     */
    public String getPeerNome() {
        return peerNome;
    }
    
    /**
     * Mpetodo responsável pelos eventos dos canais de comunicação
     * 
     * @param event evento do canal
     */
    @Override
    public void pipeMsgEvent(PipeMsgEvent event) {
        
        //recebo a mensagem do canal
        Message msg = event.getMessage();
        try {
            //crio o tipo de mensagem
            String type = getTagString(msg, ENVIAR_MENSAGEM_TIPO, " ");
            
            //verifico o tipo de mensagens
            switch (type) {
                //verifico se é saída do peer
                case ENVIAR_MENSAGEM_PEER_SAIDA:{
                    //crio o envio de mensagem
                    String sender = getTagString(msg, ENVIAR_NOME, "anonymous");
                    //disparo o evendo de saída de peer
                    ouvinteMensagem.eventoSaidaPeer(sender);
                    break;
                }
                //verifico se é entrada de peer    
                case ENVIAR_MENSAGEM_PEER_NOVO:{
                    //crio o envio da mensagem
                    String sender = getTagString(msg, ENVIAR_NOME, "anonymous");
                    //disparo o envento de entrada de peer
                    ouvinteMensagem.eventoEntradaPeer(sender);
                    break;
                }
                default:{
                    //crio o envio da mensagem
                    String sender = getTagString(msg, ENVIAR_NOME, "anonymous");
                    String senderMessage = getTagString(msg, ENVIAR_MENSAGEM, null);
                    //disparo o evendo de envio de mensagem
                    ouvinteMensagem.eventoMensagem(sender, senderMessage);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Método responsável pelo envio de mensagens
     * 
     * @param gram mensagem a ser enviada
     */
    public void enviarMensagem(String gram) {
        try {
            //crio o objeto de envio de mensagem
            Message msg = new Message();
            //adiciono o elemento de envio de mensagem
            msg.addMessageElement(null, new StringMessageElement(ENVIAR_MENSAGEM, gram, null));
            //adiciono o elemento de nome do peer
            msg.addMessageElement(null, new StringMessageElement(ENVIAR_NOME, peerNome, null));
            //adiciono o elemento de nome do grupo
            msg.addMessageElement(null, new StringMessageElement(ENVIAR_GRUPO_NAME, group.getPeerGroupName(), null));
            //envio a mensagem pelo canal
            outputPipe.send(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Método responsável pela criação do Id do canal
     * 
     * @return o id do Canal
     */    
    private PipeID getUniquePipeID(PeerGroup peerGrop) {

        //crio um id previo
        byte[] preCookedPID =
                {
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1,
                        (byte) 0xD1 };

        //gero o id passando o grupo
        PipeID id = (PipeID) IDFactory.newPipeID(peerGrop.getPeerGroupID(), preCookedPID);

        //retonor o id
        return id;
    }
    
    /**
     * Método responsável por obter o valor da tag da mensagem
     * 
     * @param msg mensagem de envio
     * @param tag tag de envio
     * @return array de bites
     * @throws Exception Exceção
     */
    protected byte[] getTagValue(Message msg, String tag) throws Exception {
        //crio o buffer
        byte[] buffer = null;
        //crio o elemento da mensagem
        MessageElement elem = msg.getMessageElement(null, tag);
        //verifico se o elemento não está nulo
        if (elem != null) {
            //remove o elemento da mensagem
            msg.removeMessageElement(elem);
            //pego o stream de entrada
            InputStream ip = elem.getStream();
            //verifico se o stream não está nulo
            if (ip != null) {
                //salvo no buffer
                buffer = new byte[ip.available()];
                //leio o buffer
                ip.read(buffer);
            }
            //retorno o buffer
            return buffer;
        } else {
            //retorno o buffer nulo
            return null;
        }

    }
    
    /**
     * Método responsável por obter a string da tag
     * 
     * @param msg mensagem de envio
     * @param tag tag de envio
     * @param defaultValue valor default da tag
     * @return tag em formato string
     * @throws Exception exceção
     */
    protected String getTagString(Message msg, String tag, String defaultValue) throws Exception {
        
        //armazeno a tag no buffer
        byte[] buffer = getTagValue(msg, tag);
        //crio a string de resultado
        String result;
        //verifico se o buffer não está vazio
        if (buffer != null) {
            //retorno a tag em formato string
            result = new String(buffer);
        } else {
            //retorno o valor default
            result = defaultValue;
        }

        //retorno a string
        return result;
    }
    
    /**
     * Método responsável por atualizar a lista de peers
     * 
     * @param name nome do peer
     */
    private void atualizaLista(String name) {
        //verifico se o ouvinte das mensagens não é nulo
        if (ouvinteMensagem != null) {
            //chamo o evento de entrada do peer
            ouvinteMensagem.eventoEntradaPeer(name);
        }
    }
    
    /**
     * Método responsável por inserir o ouvinte de mensagem no controlador
     * 
     * @param listener ouvinte do controlador de mensagem
     */
    public void setListener(OuvinteMensagem listener) {
        //seto o ouvinte de mensagem
        this.ouvinteMensagem = listener;
    }

}

