/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Apis;

import Main.Configuration;
import Main.MLBotila;
import java.io.IOException;
import java.sql.SQLException;

/**
 * APP_USR-
 * @author Mauro
 */
public class Default_APIs {
    
    
    public static void ObtenerPregunta() throws IOException, SQLException{
        String url = String.format("https://api.mercadolibre.com/my/received_questions/search?status=UNANSWERED&access_token=%s",MLBotila.getInstance().getAccess_Token());   
        obtieneDatos.obtengoDatos(url,"questions","null","null"); 
        
    }
    
    public static void ObtenerVentas() throws IOException, SQLException{
        String url = String.format("https://api.mercadolibre.com/orders/search/recent?seller=%s&access_token=%s",Configuration.getConfig().getProperty("SellerID").replace("\"",""),MLBotila.getInstance().getAccess_Token());
        obtieneDatos.obtengoDatos(url,"sales",MLBotila.getInstance().getAccess_Token(),Configuration.getConfig().getProperty("SellerID").replace("\"",""));
        
    }
    
    public static void ObtenerMensajes() throws IOException, SQLException{
        String url = String.format("https://api.mercadolibre.com/messages/unread?access_token=%s&role=%s",MLBotila.getInstance().getAccess_Token(),"seller");
        obtieneDatos.obtengoDatos(url,"mensajes",MLBotila.getInstance().getAccess_Token(),"null");
        
    }
        
    public static void responderPreguntas(String q_id, String text) throws IOException{
        String url = String.format("https://api.mercadolibre.com/answers?access_token=%s",MLBotila.getInstance().getAccess_Token());
        enviaDatos.envioRespuesta(url,q_id,text);
        
    }
    
    public static void responderVentas(String buyer_id,String pack, String seller_id, String email, String text) throws IOException{
        String url = String.format("https://api.mercadolibre.com/messages/packs/%s/sellers/%s?access_token=%s",pack,Configuration.getConfig().getProperty("SellerID").replace("\"",""),MLBotila.getInstance().getAccess_Token());
        enviaDatos.envioMensaje(url,buyer_id,seller_id,email,text);
        
    }
    
}
