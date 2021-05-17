/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Apis;

import Main.Configuration;
import static Main.Hikari.getDataSource;
import Main.MLBotila;
import bots.TeleBot;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Mauro
 * This bot will recive HTTP URLs to fetch data from.
 */
public class obtieneDatos {
    

           static String chatID = null;
           
            //DB
            static LinkedBlockingQueue<String> colaPreguntas = new LinkedBlockingQueue<String>();
            static LinkedBlockingQueue<String> colaVentas = new LinkedBlockingQueue<String>();
            static LinkedBlockingQueue<String> colaMensajes = new LinkedBlockingQueue<String>();
            static Connection connThread = null;
            static Statement stmntThreadDB = null;
            static String sql = null;
        
    public static  void obtengoDatos(String Api_URL, String api_type, String Token, String Seller_id) throws IOException, SQLException{
        Document doc = null;
            try {
                
				dbpasswd=""
				userpasswd=""
                connThread = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/DATA",userpasswd,dbpasswd); 
                //connThread = DriverManager.getConnection(Data.getInstance().getDB_URL(),Data.getInstance().getUSER(),Data.getInstance().getPASS());
                connThread.setAutoCommit(false);
               
                stmntThreadDB = connThread.createStatement();
            
              
            long id = 0;

                    doc = Jsoup.connect(Api_URL).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")
                            .referrer("http://www.google.com")
                            .timeout(180000)
                            .ignoreContentType(true)
                            //.header("Accept", "*/*")
                            //.header("Cache-Control", "no-cache")
                            //.header("Host", "api.mercadolibre.com")
                            //.header("Accept-Encoding", "gzip, deflate")
                            //.header("Connection", "keep-alive")
                            //.header("cache-control", "no-cache")
                            .get();
                        
                        //System.out.println(doc.getElementsByTag("pre").text());
                        //JSONObject obj = new JSONObject(doc.getElementsByTag("pre").text().replaceAll("[^a-zA-Z0-9\\,\\{\\}\\(\\)\\=\\$\\?\\:\\[\\]\\-\\_\\ \\.]", "").replaceAll("\\:\\ ", "\\:\\'").replaceAll("\\,\\ ", "\\'\\,\\ "));
                        JSONObject obj = new JSONObject(doc.getElementsByTag("pre").text());
                        //obj = optJSONObject(doc.getElementsByTag("pre").text());
                        
                        
                        //System.out.println(obj);
                        JSONArray arr = null;
                        //System.out.println("2");
                        switch(api_type){
                            case "questions":
                                        arr = obj.getJSONArray("questions");
                                        if(arr.length() == 0){
                                            System.out.println("JSON Questions Vacio con URL: "+Api_URL);
                                        }else{
                                            for (int i = 0; i < arr.length(); i++)
                                            {
                                                //System.out.println(arr.getJSONObject(i));
                                                //System.out.println("preguntas");
                                                id = arr.getJSONObject(i).getLong("id");
                                                String item_id = arr.getJSONObject(i).getString("item_id");
                                                String status = arr.getJSONObject(i).getString("status");
                                                String message = arr.getJSONObject(i).optString("text");
                                                String date = arr.getJSONObject(i).getString("date_created").replaceAll("T.+", "");
                                                
                                                //System.out.println(message);

                                               //System.out.println("id: "+id+" item_id: "+item_id+" status: "+status+" item_id: "+message);
                                               String pregunta = String.format("('%s',\"%s\",\"%s\",\"%s\",\"%s\")",id,item_id,status,message.replace("\\", ""),date);
                                               colaPreguntas.add(pregunta);
                                               MLBotila.Write2Log("ColaPreguntas: "+colaPreguntas.size());
                                               //System.out.println(pregunta);
                                            }  
                                        //Escribo en la DB
                                        //Preguntas
                                        String valores = colaPreguntas.toString();
                                        valores = valores.substring(1, valores.length() - 1);
                                        sql = "INSERT INTO preguntas (id, item_id, status, mensaje, qdate) VALUES " + valores + 
                                                " ON DUPLICATE KEY UPDATE id = VALUES(id),item_id = VALUES(item_id),status = VALUES(status),mensaje = VALUES(mensaje),qdate = VALUES(qdate);"; 
                                        //System.out.println(sql);
                                        stmntThreadDB.execute(sql);
                                        connThread.commit();
                                        colaPreguntas.clear();
                                        }
                                        break;
                                        
                            case "sales":
                                        arr = obj.getJSONArray("results");
                                        //System.out.println(arr);
                                        if(arr.length() == 0){
                                            System.out.println("JSON Sales Vacio con URL: "+Api_URL+"\n"+obj);
                                        }else{
                                            for (int i = 0; i < arr.length(); i++)
                                            {
                                                id = arr.getJSONObject(i).getLong("id");
                                                String status = arr.getJSONObject(i).getString("status");
                                                String date_closed = arr.getJSONObject(i).getString("date_closed").replaceAll("T.+", "");
                                                //System.out.println(arr.getJSONObject(i).getJSONObject("shipping"));
                                                String item_id = arr.getJSONObject(i).getJSONArray("order_items").getJSONObject(0).getJSONObject("item").getString("id");
                                                String title = arr.getJSONObject(i).getJSONArray("order_items").getJSONObject(0).getJSONObject("item").getString("title");
                                                int quantity = arr.getJSONObject(i).getJSONArray("order_items").getJSONObject(0).getInt("quantity");
                                                String shipping = arr.getJSONObject(i).getJSONObject("shipping").optString("status","null");
                                                long shipping_id = arr.getJSONObject(i).getJSONObject("shipping").optLong("id");
                                                long buyer_id = arr.getJSONObject(i).getJSONObject("buyer").optLong("id"); //user_id = buyer_id
                                                //System.out.println(arr.getJSONObject(i).getJSONObject("shipping").getLong("id"));

                                               //System.out.println("id: "+id+" item_id: "+item_id+" status: "+status+" Date: "+date_closed+" title: "+title+" quantity: "+quantity
                                               //                   +" shipping_status: "+shipping+" shipping_id: "+shipping_id+" buyer_id: "+buyer_id);
                                               
                                               
                                                //Traemos el Shipping Label
                                                String shipping_label = "null";
                                                if(shipping_id != 0 && (!shipping.contains("delivered") && !shipping.contains("cancelled") && !shipping.contains("to_be_agreed") && !shipping.contains("null"))){
                                                shipping_label = String.format("https://api.mercadolibre.com/shipment_labels?shipment_ids=%s&savePdf=Y&access_token=", shipping_id);
                                                    //System.out.println(shipping_label);
                                                }//Hay que guardarla sin el TOKEN
                                                
                                                //Obtenemos el Pack_ID para traer los mensajes
                                                String pack_api = String.format("https://api.mercadolibre.com/orders/%s?&access_token=%s",id,Token);
                                                Document pack_doc = Jsoup.connect(pack_api).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")
                                                .referrer("http://www.google.com")
                                                .timeout(180000)
                                                .ignoreContentType(true)
                                                .get();
                                                
                                                JSONObject pack_obj = new JSONObject(pack_doc.getElementsByTag("pre").text());
                                                long pack_id = pack_obj.optLong("pack_id",0);
                                                //System.out.println(pack_id);
                                                //Si el pack_id es 0, entonces buscamos por el sale_id
                                                if(pack_id == 0){
                                                    pack_id = id;
                                                    
                                                }
                                                
                                                //Obtenemos los Mensajes de la Venta
                                                String msg_api = String.format("https://api.mercadolibre.com/messages/packs/%s/sellers/%s?access_token=%s",pack_id,Seller_id,Token);
                                                Document msg_doc = Jsoup.connect(msg_api).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")
                                                .referrer("http://www.google.com")
                                                .timeout(180000)
                                                .ignoreContentType(true)
                                                .get();
                                                //System.out.println(msg_doc.getElementsByTag("pre").text().replaceAll("<a href=.+</a>", ""));
                                                JSONObject msg_obj = new JSONObject(msg_doc.getElementsByTag("pre").text().replaceAll("<a href=.+</a>", ""));
                                                JSONArray msg_arr = msg_obj.getJSONArray("messages");
                                                
                                                if(msg_arr.length() == 0){
                                                        System.out.println("JSON Mensajes Vacio con URL: "+msg_api);
                                                    }else{
                                                        for (int j = 0; j < msg_arr.length(); j++)
                                                        {   //System.out.println(msg_arr.getJSONObject(j));
                                                            String msg_id = msg_arr.getJSONObject(j).getString("id");
                                                            String user_name = msg_arr.getJSONObject(j).getJSONObject("from").optString("name","null");
                                                            long user_id = msg_arr.getJSONObject(j).getJSONObject("from").optLong("user_id"); //user_id = buyer_id
                                                                /*if(String.valueOf(user_id).contains(Seller_id)){                                                  //Si el mensaje es nuestro cambio el user
                                                                    user_id = msg_arr.getJSONObject(j).getJSONObject("to").optLong("user_id");// para poder relacionar todos los mensajes
                                                                    user_name = user_name.concat("35907719");
                                                                }*/
                                                            String text = msg_arr.getJSONObject(j).getString("text");
                                                            String email = msg_arr.getJSONObject(j).getJSONObject("from").optString("email");
                                                            String msg_date = msg_arr.getJSONObject(j).getJSONObject("message_date").getString("created").replaceAll("T.+", "");
                                                            
                                                            if(!String.valueOf(user_id).contains(Seller_id)){
                                                            //System.out.println("msg_id: "+msg_id+" user_id: "+user_id+" user_name: "+user_name+" text: "+text+" subject: "+email+" msg_date: "+msg_date);
                                                            String mensajes = String.format("('%s','%s',\"%s\",'%s','%s',\"%s\",\"%s\",\"%s\",\"%s\")",msg_id,id,item_id,buyer_id,pack_id,user_name,email,text.replace("\\", ""),msg_date);
                                                            colaMensajes.add(mensajes);
                                                            MLBotila.Write2Log("colaMensajes: "+colaMensajes.size());
                                                            //System.out.println(mensajes);
                                                            }
                                                        }

                                                        }
                                                
                                                String venta = String.format("('%s',\"%s\",\"%s\",\"%s\",\"%s\",'%s','%s',\"%s\",'%s','%s',\"%s\")",id,item_id,status,date_closed,title,quantity,shipping_id,shipping,buyer_id,pack_id,shipping_label);
                                                colaVentas.add(venta);
                                                MLBotila.Write2Log("colaVentas: "+colaVentas.size());
                                                //System.out.println(venta);
                                                
                                            }
                                           
                                            //Mensajes
                                            String valores = colaMensajes.toString();
                                            valores = valores.substring(1, valores.length() - 1);
                                            sql = "INSERT INTO mensajes (id, sale_id, item_id, buyer_id, pack_id, buyer_name, email, message, msg_date) VALUES " 
                                                    + valores + " ON DUPLICATE KEY UPDATE id = VALUES(id),item_id = VALUES(item_id),buyer_id = VALUES(buyer_id),"
                                                    + "pack_id = VALUES(pack_id),buyer_name = VALUES(buyer_name),email = VALUES(email),message = VALUES(message),msg_date = VALUES(msg_date);"; 
                                            stmntThreadDB.execute(sql);
                                            connThread.commit();
                                            colaMensajes.clear();

                                            //Ventas
                                            valores = colaVentas.toString();
                                            valores = valores.substring(1, valores.length() - 1);
                                            sql = "INSERT INTO ventas (id, item_id, status, date, title, quantity, shipping_id, shipping_status, buyer_id, pack_id, shipping_label) VALUES " + valores + 
                                                    " ON DUPLICATE KEY UPDATE id = VALUES(id),item_id = VALUES(item_id),status = VALUES(status),date = VALUES(date),title = VALUES(title)"
                                                    + ",quantity = VALUES(quantity),shipping_id = VALUES(shipping_id),shipping_status = VALUES(shipping_status),buyer_id = VALUES(buyer_id)"
                                                    + ",pack_id = VALUES(pack_id),shipping_label = VALUES(shipping_label);"; 
                                            stmntThreadDB.execute(sql);
                                            connThread.commit();
                                            colaVentas.clear();
                                        }
                                        break;

                        }//End Case
                        
                        //Mandamos al Chat los items nuevos
                        /* Deprecated. New class "updater"
                        sql = "select * from temp;";
                        ResultSet rs = stmntThreadDB.executeQuery(sql); 
                    
                        if(!rs.next()) {    
                            //TeleBot.sendMessage("ID Inexistente!","bot_token", chatID);
                            rs.close();
                            //stmntThreadDB.close();
                        }else{
                                rs.previous();
                                while(rs.next()) {
                                    //String t_id = rs.getString("id"); 
                                    String t_text = rs.getString("text");
                                    String t_type = rs.getString("type");
                                    String t_item = rs.getString("item");
                                    String t_id = null;
                                        switch(t_type){
                                            case "pregunta":
                                                t_id = String.format("Q%s", rs.getString("id"));
                                                break;
                                            case "venta":
                                                 t_id = String.format("V%s", rs.getString("id"));
                                                break;
                                            case "mensaje":
                                                 t_id = String.format("M%s", rs.getString("id"));
                                                break;
                                        }                       //FALTA: Armarel BOT
                                    String t_msg = String.format("La %s con ID: %s:\n %s: \n %s", t_type,t_id,t_item,t_text);
                                    
                                    TeleBot.PushMSG(t_msg);
                                }
                             }
                       rs.close();
                        */
                        
                        //Cerramos la Conexion a la base
                        stmntThreadDB.close();
                        connThread.close();
                        //dataSource.close();
                        
                        //TeleBot.sendMessage("Respúesta", "694993539:AAF0tw-n_x3cYImubWE88I2LkQLhKOdlWIk", "-315765887");
                    
            //Thread.sleep(3000);
           
        } catch (SQLException ex) {
            
            System.out.println("Begining Catch Debug in ObtieneDatos");
            System.out.println(ex);
            System.out.println(sql);
            
            //System.out.println(doc.text().replaceAll(".+\\{", "\\{").replaceAll("\\}.+", "\\}"));
            System.out.println(Api_URL);     

        }

        catch (JSONException ex){
                System.out.println(ex);
                //System.out.println(doc.text().replaceAll(".+\\{", "\\{").replaceAll("\\}.+", "\\}"));
                        if(!colaMensajes.isEmpty()){
                        String valores = colaMensajes.toString();
                        valores = valores.substring(1, valores.length() - 1);
                        sql = "INSERT INTO mensajes (id, sale_id, item_id, buyer_id, pack_id, buyer_name, email, message, msg_date) VALUES " 
                                + valores + " ON DUPLICATE KEY UPDATE id = VALUES(id),item_id = VALUES(item_id),buyer_id = VALUES(buyer_id),"
                                + "pack_id = VALUES(pack_id),buyer_name = VALUES(buyer_name),email = VALUES(email),message = VALUES(message),msg_date = VALUES(msg_date);"; 
                        stmntThreadDB.execute(sql);
                        connThread.commit();
                        colaMensajes.clear(); 
                        }
                        if(!colaVentas.isEmpty()){
                            String valores = colaVentas.toString();
                            valores = valores.substring(1, valores.length() - 1);
                            sql = "INSERT INTO ventas (id, item_id, status, date, title, quantity, shipping_id, shipping_status, buyer_id, pack_id, shipping_label) VALUES " + valores + 
                                    " ON DUPLICATE KEY UPDATE id = VALUES(id),item_id = VALUES(item_id),status = VALUES(status),date = VALUES(date),title = VALUES(title)"
                                    + ",quantity = VALUES(quantity),shipping_id = VALUES(shipping_id),shipping_status = VALUES(shipping_status),buyer_id = VALUES(buyer_id)"
                                    + ",pack_id = VALUES(pack_id),shipping_label = VALUES(shipping_label);"; 
                            stmntThreadDB.execute(sql);
                            connThread.commit();
                            colaVentas.clear();
                        }
                        if(!colaPreguntas.isEmpty()){
                            String valores = colaPreguntas.toString();
                            valores = valores.substring(1, valores.length() - 1);
                            sql = "INSERT INTO preguntas (id, item_id, status, mensaje, qdate) VALUES " + valores + 
                                    " ON DUPLICATE KEY UPDATE id = VALUES(id),item_id = VALUES(item_id),status = VALUES(status),mensaje = VALUES(mensaje),qdate = VALUES(qdate);"; 
                            //System.out.println(sql);
                            stmntThreadDB.execute(sql);
                            connThread.commit();
                            colaPreguntas.clear();
                        }
                        stmntThreadDB.close();
                        connThread.close();

                try {
                        JSONObject http_status = new JSONObject(doc.text().replaceAll(".+\\{", "\\{").replaceAll("\\}.+", "\\}"));
                        //http_status.optJSONObject(doc.text().replaceAll(".+\\{", "\\{").replaceAll("\\}.+", "\\}"));
                        if(http_status.optInt("status") >= 400){
                                System.out.println("Code: "+http_status.optString("message"));
                                MLBotila.getInstance().setAccess_Token("");
                        }
                } catch (Exception e) {
                        System.out.println(e);
                  }	
        }
        catch(IOException ex){
                System.out.println(ex);
        }
            
            //return true;
    }
    
    /*
    static public void searchQuery(String txt, String user, String chatID) throws SQLException, InterruptedException, UnsupportedEncodingException, IOException{
        Connection connThread; 
        connThread = DriverManager.getConnection(Configuration.getConfig().getProperty("db_url").replace("\"",""),Configuration.getConfig().getProperty("db_user").replace("\"",""),Configuration.getConfig().getProperty("db_passwd").replace("\"","")); 
        connThread.setAutoCommit(false);
                
        String sql = "SELECT * FROM productos where articulo like '%"+txt+"%' group by articulo order by price ASC limit "+Configuration.getConfig().getProperty("searcher_limit_query").replace("\"","") +";";
        Statement stmntThread = connThread.createStatement();
        ResultSet rs = stmntThread.executeQuery(sql);  
        
                        int rowCount = 0;
                        if (rs.last()) {//make cursor to point to the last row in the ResultSet object
                          rowCount = rs.getRow();
                          rs.beforeFirst(); //make cursor to point to the front of the ResultSet object, just before the first row.
                            System.out.println("RowCount= "+rowCount);
                        }
            TeleBot.PushMSG("@"+user);
            if(!rs.next()) {    
                        TeleBot.PushMSG("No hay Productos que Coincidan con la Busqueda");
                        rs.close();
                        stmntThread.close();
                    }else{
                        rs.previous();
                        while(rs.next()) {
                          //String product = String.format("<%s>: [%s] precio $%.2f\n %s\n",Data.getVendorDisplay().get(rs.getString("vendor_id")),rs.getString("articulo"),rs.getFloat("price"),rs.getString("url"));
                          //TeleBot.sendMessage(product, Configuration.getConfig().getProperty("searcher_token").replace("\"",""), chatID);  
                        }
                   rs.close();
                  }
    
            stmntThread.close();
            connThread.close();
    }
    */
            
}
