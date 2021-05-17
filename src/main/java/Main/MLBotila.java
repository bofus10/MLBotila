/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Apis.Default_APIs;
import bots.ResponseBot;
import bots.TeleBot;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/*
 * BD
 * @author Mauro
 */


public class MLBotila {
    
    private static String Access_Token = "";
    static Logger vendors = Logger.getLogger("MyLog");
    static SimpleFormatter formatter = new SimpleFormatter();
    private static boolean flag_token = false;
    private static int timer = 0;
    
    private static MLBotila instance = null;
    
public static void main(String[] args) throws InterruptedException, IOException, SQLException {
    
     FileHandler fh_vendors = new FileHandler("./log/updates.log"); 
    vendors.addHandler(fh_vendors);
    vendors.setUseParentHandlers(false);
    fh_vendors.setFormatter(formatter);
            
        while(true){
          if(getInstance().getAccess_Token().isEmpty()){
               //Pedimos el token 
               //System.out.println(getInstance().getAccess_Token());
                TeleBot.PushMSG("Enviar el Token para la API!\nURL: https://developers.mercadolibre.com.ar/es_ar/producto-autenticacion-autorizacion/ \n");
                TeleBot.PushMSG("APP_ID:"+Configuration.getConfig().getProperty("APP_ID").replace("\"",""));
                    while(getInstance().getAccess_Token().isEmpty()){
                        ResponseBot.searcher();
                        Thread.sleep(5000);
                        //System.out.println("Finish Main");
                    }           
            }else{
                System.out.println("Obtenemos Datos");
                Default_APIs.ObtenerPregunta();
                Default_APIs.ObtenerVentas();
                updater.updater();
                
                    while(timer < 10){
                        ResponseBot.searcher();
                        timer++;
                    }
                timer = 0;        
            }    
          Thread.sleep(60000);
        }
          
       
    }

/* ########## DATA ########### */
    public static MLBotila getInstance(){
       if(instance==null){
       instance = new MLBotila();
      }
      return instance;
    }

    public String getAccess_Token() {
        return Access_Token;
    }

    public void setAccess_Token(String Access_Token) {
        MLBotila.Access_Token = Access_Token;
    }
    
    public boolean getFlag_token() {
        return flag_token;
    }

    public void setFlag_token(boolean state) {
        MLBotila.flag_token = state;
    }
    
  /* ########## Logger ########### */  
    public static void Write2Log(String file) throws IOException{
                vendors.info(file);

    }


}

/*
        Default_APIs.ObtenerPregunta(Access_Token);
        System.out.println("Ventas");
        Default_APIs.ObtenerVentas(Access_Token, seller_id);
        System.out.println("Mensajes");
        Default_APIs.ObtenerMensajes(Access_Token, seller_id);


*/