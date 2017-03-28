package juniorvs.virtualdir;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import juniorvs.virtualdir.desktop.VirtualDir;
import net.jxta.instantp2p.util.PreferenceReader;
import net.jxta.share.ContentAdvertisement;
import net.jxta.share.MimeInfo;
import net.jxta.share.MimeTable;
import com.esciencenet.forms.*;


/**
 * Classe para representação de conteúdo remoto
 * 
 * @author Tadeu Classe
 */
public class ConteudoRemoto implements OuvinteConteudo {

    //atributos staticos da classe
    public static final String DIRETORIO_PREFERENCIA = ".PreferredDirectory";
    private static final String COMPARTILHADO = ".autoshare";

    //atributos da classe
    private FrmSalvarRecurso salvarConteudoDialog = null;
    private GerenciaProcura gerenciaProcura;
    private boolean verConteudo = false;
    private boolean compartilhar = true;

    /**
     * Realiza a leitura do diretório local obtendo os arquivos
     * 
     * @return diretório local dos arquivos
     */
    public String lerDiretorioPreferencia() {
        //crio o objeto de arquivos
        File dir = new File(".");        
        //crio a instâncio do objeto de preferência de arquivos
        PreferenceReader prefs = PreferenceReader.getInstance();
        //abro as propriedades do arquivo        
        String cwd = prefs.getProperty(getClass().getName() + DIRETORIO_PREFERENCIA, null);
        //verifico se o arquivo com o diretório foi encontrado
        if (cwd == null) {
            try {
                //pego o diretório do sistemas
                cwd = dir.getCanonicalPath() + System.getProperty("file.separator");
            } catch (IOException e) {
                cwd = "";
            }
        }
        
        //retorno o diretório do sistema
        return cwd;
    }
    
    /**
     * Método cosntrutor da classe responsável pelo conteúdo remoto
     * 
     * @param app classe fundamental para a rede p2p
     * @param gerenciaProcura objeto de gerencia de procura
     */
    public ConteudoRemoto(VirtualDir app, GerenciaProcura gerenciaProcura){		
        //leio as preferências do diretório compartilhado
        lerPreferencias();
        //pego o objeto referente a procura
        this.gerenciaProcura = gerenciaProcura;
    }
    
    /**
     * Mostra ou salva conteudo remoto
     * 
     * @param cAdv anuncio de conteudo do arquivo remoto
     */
    public void mostrarConteudo(ContentAdvertisement cAdv) {
        //verifico se o tipo de anuncio é nulo
        if (cAdv.getType() != null) {
            //se for, eu pego os tipo da informação e salvo o conteúdo            
            salvarConteudo(cAdv, cAdv.getName());
        } else {
            //se não for eu salvo o conteúdo
            salvarConteudo(cAdv, cAdv.getName());
        }
    }
    
    public void salvarDomain(ContentAdvertisement cAdv, String filePath) {
        //verifico se o tipo de anuncio é nulo
        if (cAdv.getType() != null) {
            //se for, eu pego os tipo da informação e salvo o conteúdo            
            salvarConteudo(cAdv, cAdv.getName());
        } else {
            //se não for eu salvo o conteúdo
            salvarConteudo(cAdv, cAdv.getName());
        }
    }
    
    /**
     * Recupera o conteúdo remoto
     * 
     * @param url caminho do arquivo a ser recuperado
     */
    @Override
    public void recuperarConteudo(String url) {
        //verifico se o conteúdo é para ser visto
        if (verConteudo) {
            //mostro o caminho do arquivo
            mostrarURL(url);
            //seto a varíavel de mostrar conteúdo para false
            verConteudo = false;
        } else {
            //verifico se é para compratilhar o conteúdo
            if (compartilhar) {
                //adiciono o conteúdo para o gerente de procura
                gerenciaProcura.addContent(url);
            }
        }
    }
    
    /**
     * Método responsável pela exibição do formulário de gravação de conteúdo
     * 
     * @param adv anúncio do arquivo a ser salvo
     * @param name nome do arquivo a ser salvo
     * @param mInfo informações sobre o arquivo a ser salvo
     * @param viewEnabled verifico se é para a visão do arquivos ser exibida
     */
    private void salvarConteudo(ContentAdvertisement adv, String name) {        
        
        //verifico se o objeto de gravação do arquivo é para ser exibido
        if (salvarConteudoDialog == null) {
            //crio o formulário de gravação de conteúdo
            salvarConteudoDialog = new FrmSalvarRecurso(null, true, lerDiretorioPreferencia(), gerenciaProcura, this, adv, name);
        }
        
        //seto o resultado no formulário para a exibição
        salvarConteudoDialog.setResult(adv, name);
        //abro o formulário
        salvarConteudoDialog.setVisible(true);
    }
    
    /**
     * Mostra o conteúdo dos arquivos
     * 
     * @param url caminho do arquivo a ser baixado
     */
    private void mostrarURL(String url){
        
        //crio um array de string
        String[] command = null;
        //crio o objeto de execução
        Runtime rt = Runtime.getRuntime();
        
        //verifico os separadores do arquivo
        switch (File.separator) {
            //verifico o separador de linux
            case "/":
                command = new String[3];
                command[0] = "netscape";
                command[1] = "-remote";
                command[2] = "openURL(" + url + ")";
                break;
            //verifico o separador de windows
            case "\\":
                command = new String[2];
                command[0] = "viewfile";
                command[1] = url;
                break;
        }
        
        
        try {
            //executo o compando
            Process process = rt.exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
   
    /**
     * Ler preferencias locais
     *
     */
    private void lerPreferencias() {
            PreferenceReader prefs = PreferenceReader.getInstance();
            String value = (String) prefs.getProperty(getClass().getName() + COMPARTILHADO, null);
            if (value != null && value.equals("true")) {
                    compartilhar = true;
            } else {
                    compartilhar = false;
            }
    }
        
        
}
