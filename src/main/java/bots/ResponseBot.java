/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bots;

import Apis.Default_APIs;
import Main.Configuration;
import static Main.Hikari.getDataSource;
import Main.MLBotila;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.SQLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.json.*;
/**
 *
 * @author Mauro
 */
public class ResponseBot{
        //static String Configuration.getConfig().getProperty("searcher_token") = Configuration.getConfig().getProperty("searcher_Configuration.getConfig().getProperty("searcher_token")");

        static String chatID = null;
         
        
    public static void searcher(){
            String url = null;
            boolean flag_stop = true;
            
            try {
                //System.out.println("start");
            Document doc;
            long offset = 0, id = 0;

            while(flag_stop){
            url = String.format("https://api.telegram.org/bot%s/getUpdates?offset=%d",Configuration.getConfig().getProperty("bot_Token").replace("\"",""),offset);
                    //System.out.println(url);
                    doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")
                            .referrer("http://www.google.com")
                            .timeout(180000)
                            .ignoreContentType(true)
                            .get();
                        
                    
                    
                    JSONObject obj = new JSONObject(doc.getElementsByTag("body").text());
                    boolean status = obj.getBoolean("ok");
                   
                    if(status){
                        
                        JSONArray arr = obj.getJSONArray("result");
                        if(arr.length() == 0){
                            flag_stop = false;
                        }else{
                            for (int i = 0; i < arr.length(); i++)
                            {
                                id = arr.getJSONObject(i).getLong("update_id");
                                String user = arr.getJSONObject(i).getJSONObject("message").getJSONObject("from").getString("first_name");
                                chatID = Long.toString(arr.getJSONObject(i).getJSONObject("message").getJSONObject("chat").getLong("id"));
                                String message = arr.getJSONObject(i).getJSONObject("message").getString("text").replaceAll("\\;","");
                                
                                
                                
                                if(message.split("#").length == 2){
                                    //Q1-RESPUESTA
                                    String code_id = message.split("#")[0];
                                    String text = message.split("#")[1];
                                    //System.out.println(code_id+" ---- "+text);
                                    
                                    if(code_id.startsWith("Q")){
                                        //TeleBot.PushMSG("Q:"+text);
                                        preparoRespuesta("Q",code_id, text);
                                    }else if(code_id.startsWith("M")){
                                        //TeleBot.PushMSG("M:"+text);
                                        preparoRespuesta("M",code_id, text);
                                    }else if(code_id.startsWith("T")){
                                        //TeleBot.PushMSG("T:"+text);
                                        preparoRespuesta("T",code_id, text);
                                    }else if(code_id.startsWith("G")){
                                        //TeleBot.PushMSG("G:"+text);
                                        preparoRespuesta("G",code_id, text);
                                    }else if(code_id.startsWith("V")){
                                        //TeleBot.PushMSG("G:"+text);
                                        preparoRespuesta("V",code_id, text);
                                    }else{
                                        //Invalido
                                    }
                                    
                                }else{
                                    if(message.contains("help")){
                                        String help = "Comandos: \n"
                                                    + "G#{preguntas|ventas|ventasall|mensajes}\n"
                                                    + "Devolvera las Preguntas o Mensajes sin responder. En el caso de las ventas devuelve todas por el momento.\n"
                                                    + "Q{ID}#{TEXTO}\n"
                                                    + "EJ: Q11#Efectivamente, podes hacer la compra. Saluda\n"
                                                    + "Metodo para responder las Preguntas segun el ID\n"
                                                    + "M{ID}#{TEXTO}\n"
                                                    + "EJ: M11#Si necesitas otro , tenemos 2!\n"
                                                    + "Metodo para responder los Mensajes de las VENTAS segun el ID\n"
                                                    + "V{ID}#{TEXTO}\n"
                                                    + "Envia un Mensaje a la Venta Elegida\n"
                                                    + "EJ: V22#Gracias por tu Compra!\n"
                                                    + "T#{TOKEN}\n"
                                                    + "Deben entrar en la web que se envia, con al APP ID que se pasa y obtener el nuevo token. Luego responderlo:\n"
                                                    + "T#APP_USR-TOKEN..\n";
                                        TeleBot.PushMSG(help);
                                    }
                                }

                               offset = id+1;
                            }  
                        }
                    }
            
           }
            
          Thread.sleep(3000);  
          //System.out.println("Finish Search"); 
        } catch (Exception ex) {
            System.out.println(ex);
            System.out.println(url);
            
        }
    }
    
    static public void preparoRespuesta(String type,String id, String text) throws SQLException, InterruptedException, UnsupportedEncodingException, IOException{
            
        Connection connThread = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/DATA","USER","PASSWD"); 
        //connThread = DriverManager.getConnection(Data.getInstance().getDB_URL(),Data.getInstance().getUSER(),Data.getInstance().getPASS());
        
        Statement stmntThread = connThread.createStatement();
        Statement stmntThread2 = connThread.createStatement();
        connThread.setAutoCommit(false);

        switch(type){
            case "Q":
                     //Buscamos el Q_ID de la pregunta a responder
                    String sql = "select id from preguntas where p_id='"+id.replaceAll("Q", "")+"';";
                    ResultSet rs = stmntThread.executeQuery(sql); 
                    
                        if(!rs.next()) {    
                            TeleBot.PushMSG("ID Inexistente!");
                            rs.close();
                            stmntThread.close();
                        }else{
                            rs.previous();
                            while(rs.next()) {
                                String q_id = rs.getString("id");
                                Default_APIs.responderPreguntas(q_id, text);
                                //Marcamos la pregunta como respondida
                                sql = "update preguntas set marked='1' where id='"+q_id+"';";
                                stmntThread2.execute(sql);
                                connThread.commit();
                            }
                       rs.close();
                       //Una vez respondido marcar la pregunta en la base como respondido para que no vuelva a saltar
                      }


                    break;
                    
            case "M":
                     //Buscamos el Q_ID de la pregunta a responder
                    sql = "select id, pack_id,buyer_id from mensajes where m_id='"+id.replaceAll("M", "")+"';";
                    stmntThread = connThread.createStatement();
                    rs = stmntThread.executeQuery(sql); 
                    
                        if(!rs.next()) {    
                            TeleBot.PushMSG("ID Inexistente!");
                            rs.close();
                            stmntThread.close();
                        }else{
                            rs.previous();
                            while(rs.next()) {
                                String m_id = rs.getString("id");
                                String pack = rs.getString("pack_id");
                                String buyer_id = rs.getString("buyer_id");
                                Default_APIs.responderVentas(buyer_id,pack, Configuration.getConfig().getProperty("SellerID").replace("\"",""), Configuration.getConfig().getProperty("seller_email").replace("\"",""), text);
                                //Marcamos el mensaje como respondio
                                sql = "update mensajes set marked='1' where id='"+m_id+"';";
                                stmntThread2.execute(sql);
                                connThread.commit();
                            }
                       rs.close();
                       
                       //Una vez respondido marcar el mesaje en la base como respondido para que no vuelva a saltar
                      }

                    
                    break;
                    
            case "G":
                     //String seccion = id.replaceAll("G", "");
                     switch(text){
                         case "ventas":case "Ventas":case "VENTAS":
                             sql = "select v_id, id, title, status, quantity, shipping_status, date, shipping_label from ventas where shipping_status not in ('cancelled','delivered');";
                                stmntThread = connThread.createStatement();
                                rs = stmntThread.executeQuery(sql); 

                                    if(!rs.next()){    
                                        TeleBot.PushMSG("No hay Ventas!");
                                        rs.close();
                                        stmntThread.close();
                                    }else{
                                        rs.previous();
                                        while(rs.next()) {
                                            String v_id = rs.getString("id");
                                            String v_id1 = rs.getString("v_id");
                                            String title = rs.getString("title");
                                            String status = rs.getString("status");
                                            String quantity = rs.getString("quantity");
                                            String shipping_status = rs.getString("shipping_status");
                                            String date = rs.getString("date");
                                            String shipping_label = rs.getString("shipping_label");
                                            
                                            if(!shipping_label.contains("null")){
                                                String label = String.format("%s%s", shipping_label,MLBotila.getInstance().getAccess_Token());
                                            }
                                            
                                            String sale = String.format("Venta: \nID:%s\nITEM:%s\nStatus:%s\nCantidad:%s\nFecha:%s\nEstado de Envio:%s\nEtiqueta:%s%s", 
                                                          v_id1,title,status,quantity,date,shipping_status,shipping_label,MLBotila.getInstance().getAccess_Token());
                                            TeleBot.PushMSG(sale);
                                        }
                                   rs.close();

                                  }
                             break;
                         case "preguntas":case "Preguntas":case "PREGUNTAS":
                                sql = "select a.p_id, a.item_id,b.title, a.status, a.mensaje, a.qdate from preguntas as a\n" +
                                      "join articulos b on a.item_id=b.item_id\n" +
                                      "where marked='0';";
                                stmntThread = connThread.createStatement();
                                rs = stmntThread.executeQuery(sql); 

                                    if(!rs.next()){    
                                        TeleBot.PushMSG("No hay Preguntas sin Responder!");
                                        rs.close();
                                        stmntThread.close();
                                    }else{
                                        rs.previous();
                                        while(rs.next()) {
                                            String q_id = rs.getString("p_id");
                                            String title = rs.getString("title");
                                            String status = rs.getString("status");
                                            String mensaje = rs.getString("mensaje");
                                            String qdate = rs.getString("qdate");
                                            
                                            String preg = String.format("Pregunta: \nID:%s\nITEM:%s\nStatus:%s\nMensaje:%s\nFecha:%s", q_id,title,status,mensaje,qdate);
                                            TeleBot.PushMSG(preg);
                                        }
                                   rs.close();

                                  }
                             break;
                         case "mensajes":case "Mensajes":case "MENSAJES":
                                sql = "select a.m_id, a.item_id,b.title, a.buyer_name, a.message, a.msg_date from mensajes as a\n" +
                                      "join articulos b on a.item_id=b.item_id\n" +
                                      "where marked='0';";
                                stmntThread = connThread.createStatement();
                                rs = stmntThread.executeQuery(sql); 

                                    if(!rs.next()){    
                                        TeleBot.PushMSG("No hay mensajes para responder!");
                                        rs.close();
                                        stmntThread.close();
                                    }else{
                                        rs.previous();
                                        while(rs.next()) {
                                            String q_id = rs.getString("m_id");
                                            String title = rs.getString("title");
                                            String buyer = rs.getString("buyer_name");
                                            String mensaje = rs.getString("message");
                                            String qdate = rs.getString("msg_date");
                                            
                                            String preg = String.format("Mensaje: \nID:%s\nITEM:%s\nUsuario:%s\nMensaje:%s\nFecha:%s", q_id,title,buyer,mensaje,qdate);
                                            TeleBot.PushMSG(preg);
                                        }
                                   rs.close();

                          
                                  }
                             break;
                         case "ventasall":case "VentasAll":case "VENTASALL":
                             sql = "select id, title, status, quantity, shipping_status, date, shipping_label from ventas;";
                                stmntThread = connThread.createStatement();
                                rs = stmntThread.executeQuery(sql); 

                                    if(!rs.next()){    
                                        TeleBot.PushMSG("No hay Ventas!");
                                        rs.close();
                                        stmntThread.close();
                                    }else{
                                        rs.previous();
                                        while(rs.next()) {
                                            String v_id = rs.getString("id");
                                            String title = rs.getString("title");
                                            String status = rs.getString("status");
                                            String quantity = rs.getString("quantity");
                                            String shipping_status = rs.getString("shipping_status");
                                            String date = rs.getString("date");
                                            String shipping_label = rs.getString("shipping_label");
                                            
                                            if(!shipping_label.contains("null")){
                                                String label = String.format("%s%s", shipping_label,MLBotila.getInstance().getAccess_Token());
                                            }
                                            
                                            String sale = String.format("Venta: \nID:%s\nITEM:%s\nStatus:%s\nCantidad:%s\nFecha:%s\nEstado de Envio:%s\nEtiqueta:%s%s", 
                                                          v_id,title,status,quantity,date,shipping_status,shipping_label,MLBotila.getInstance().getAccess_Token());
                                            TeleBot.PushMSG(sale);
                                        }
                                   rs.close();

                                  }
                             break;  
                     }
       
                    break;        
                    
                    
            case "T":
                     //Actualizamos el Token
                    //System.out.println("Actualizamos Token "+text);
                    MLBotila.getInstance().setAccess_Token(text);
                    TeleBot.PushMSG("Token Actualizado");
                    
                    //Activar un flag para que vuelva a procesar
                    
                    break;
                    
            case "V":
                     //Buscamos el Q_ID de la pregunta a responder
                    sql = "select buyer_id, pack_id from ventas where v_id='"+id.replaceAll("V", "")+"';";
                    stmntThread = connThread.createStatement();
                    rs = stmntThread.executeQuery(sql); 
                    
                        if(!rs.next()) {    
                            TeleBot.PushMSG("ID Inexistente!");
                            rs.close();
                            stmntThread.close();
                        }else{
                            rs.previous();
                            while(rs.next()) {
                                //String m_id = rs.getString("id");
                                String pack = rs.getString("pack_id");
                                String buyer_id = rs.getString("buyer_id");
                                Default_APIs.responderVentas(buyer_id,pack, Configuration.getConfig().getProperty("SellerID").replace("\"",""), Configuration.getConfig().getProperty("seller_email").replace("\"",""), text);
                                //Marcamos el mensaje como respondio
                                //sql = "update mensajes set marked='1' where id='"+m_id+"';";
                                //stmntThread2.execute(sql);
                                //connThread.commit();
                            }
                       rs.close();
                       
                       //Una vez respondido marcar el mesaje en la base como respondido para que no vuelva a saltar
                      }
                    
                    break;
        }//End Switch
        stmntThread2.close();
        stmntThread.close();
        connThread.close();
        //dataSource.close();
            
         
       
    }//End preparoRespuesta

}//End Class
