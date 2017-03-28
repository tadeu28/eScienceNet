/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.utils;

import com.hp.hpl.jena.reasoner.rulesys.builtins.Regex;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import javax.naming.spi.DirectoryManager;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;
import net.jxta.impl.util.Base64;
import org.apache.commons.codec.binary.StringUtils;

/**
 *
 * @author Tadeu Classe
 */
public class EScienceNetUtils {
    
    public static String RemoveSpecialCharacters(String str){	
        str = Normalizer.normalize(str, Normalizer.Form.NFD);  
        str = str.replaceAll("[^\\p{ASCII}]", ""); 
        return str;
    } 
    
    public static String retornarStringBuscaFiles(String termo, TreePath[] opcoes){
    
        String busca = termo;
                        
        for (TreePath tree : opcoes){                       
                        
            busca = busca + "#" + tree.getLastPathComponent().toString().replaceAll(" ", "_");
            
        }
        return busca;
    }

    public static String decodeBase64(String text){
        try{
            return StringUtils.newStringUtf8(Base64.decodeBase64(text));
        }catch(IOException e){
            return text;
        }
    }
    
    public static String encodeBase64(String text){
        try{
            String encoded = Base64.encodeBase64(text.getBytes());
            return encoded;
        }catch(Exception e){
            return text;
        }
    }
    
    public static String retornarStringBuscaFiles(String termo, List<String> opcoes){
    
        String busca = termo;
                        
        for (String term : opcoes){                       
                        
            busca = busca + "#" + term.trim().replace(" ", "_");
            
        }
        return busca;
    }
    
    public static boolean doCopy(String file, String fileTo){     
        try{            
            File fileToName = new File(fileTo);
            
            //verifico se o arquivo existe no diretorio
            if (!fileToName.exists()){
                fileToName.createNewFile();
            }else{
                return true;
            }

            URL url = new URL(file);

            //gravo o arquivo em disco 
            FileOutputStream fos;
            try (InputStream is = url.openStream()) {
                fos = new FileOutputStream(fileTo);
                int umByte = 0;
                while ((umByte = is.read()) != -1){  
                    fos.write(umByte);  
                }
            }  
            fos.close();
            
            return true;
        }catch(Exception e){ 
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            (new File(fileTo)).delete();
            return false;
        }        
    }   
    
    public static String readFileToString(File file, boolean isToDelete){
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            
            String resultado = sb.toString();
            
            br.close();
            
            if(isToDelete){
                file.delete();
            }
            
            return resultado;
        }catch(IOException e){ 
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            file.delete();
            return "";
        } 
    }
    
    public static boolean copyLocalFiles(String fileOrigin, String fileDestiny){
        try{            
            try (FileWriter fw = new FileWriter(fileDestiny, true)) {
                try (BufferedWriter bw = new BufferedWriter(fw);FileReader fr = new FileReader(fileOrigin)) {
                    try (BufferedReader br = new BufferedReader( fr )) {
                        while(br.ready()){                   
                            bw.write(br.readLine());
                        }
                    }
                    fr.close();
                }
                fw.close();
            }
            
            return true;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            (new File(fileDestiny)).delete();
            return false;
        }
    }
    
    public static String getCurrentDateTime(String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);  
        return simpleDateFormat.format(new Date());          
    }
    
    public static String formatMilisecond(String format, long millis){
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d:%d", hour, minute, second, millis);
    }
}
