/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.unicarioca.gmailinteraction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author Guilherme
 */
public class Debug {

    public static void log(String msg) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        
        System.out.println("[" + sdf.format(calendar.getTime()) + "][DEBUG]" + msg);
    }
    
    public static void listArray(ArrayList<MailData> array){
        log("Listando array...");
        
        for(int i = 0; i < array.size(); i++){
            log(array.get(i).getFrom() + " / " + array.get(i).getSubject());
        }
        
        log("Fim da listagem do array.");
    }
}
