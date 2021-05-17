/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Apis;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Mauro
 * This bot will recive HTTP URLs to fetch data from.
 */
public class Conexion {
    

           static String chatID = null;
         
        
    public static  void searcher(String Api_URL, String api_type, String Token) throws IOException{
        Document doc = null;
            try {
                //System.out.println("start");
            our_user_id=""
            long id = 0;

                    doc = Jsoup.connect(Api_URL).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")
                            .referrer("http://www.google.com")
                            .timeout(180000)
                            .ignoreContentType(true)
                            .get();
                        
                        //System.out.println(doc);
                        
                        JSONObject obj = new JSONObject(doc.getElementsByTag("pre").text());
                        
                        //System.out.println(obj);
                        JSONArray arr = null;
                        
                        switch(api_type){
                            case "questions":
                                        arr = obj.getJSONArray("questions");
                                        if(arr.length() == 0){
                                            System.out.println("JSON Vacio con URL: "+Api_URL);
                                        }else{
                                            for (int i = 0; i < arr.length(); i++)
                                            {
                                                id = arr.getJSONObject(i).getLong("id");
                                                String item_id = arr.getJSONObject(i).getString("item_id");
                                                String status = arr.getJSONObject(i).getString("status");
                                                String message = arr.getJSONObject(i).getString("text");

                                               System.out.println("id: "+id+" item_id: "+item_id+" status: "+status+" item_id: "+message);

                                            }  
                                        }
                                        break;
                                        
                            case "sales":
                                        arr = obj.getJSONArray("results");
                                        //System.out.println(arr);
                                        if(arr.length() == 0){
                                            System.out.println("JSON Vacio con URL: "+Api_URL);
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
                                                String shipping = arr.getJSONObject(i).getJSONObject("shipping").getString("status");
                                                long shipping_id = arr.getJSONObject(i).getJSONObject("shipping").optLong("id");
                                                long buyer_id = arr.getJSONObject(i).getJSONObject("buyer").optLong("id"); //user_id = buyer_id
                                                //System.out.println(arr.getJSONObject(i).getJSONObject("shipping").getLong("id"));

                                               System.out.println("id: "+id+" item_id: "+item_id+" status: "+status+" Date: "+date_closed+" title: "+title+" quantity: "+quantity
                                                                  +" shipping: "+shipping+" shipping_id: "+shipping_id+" buyer_id: "+buyer_id);

                                            }  
                                        }
                                        break;
                            case "mensajes":
                                        arr = obj.getJSONArray("results");
                                        ArrayList<String> result = new ArrayList<String>();
                                        if(arr.length() == 0){
                                            System.out.println("JSON Vacio con URL: "+Api_URL);
                                        }else{
                                            for (int i = 0; i < arr.length(); i++)
                                            {   
                                                //id = arr.getJSONObject(i).getLong("id");
                                                String pack_id[] = arr.getJSONObject(i).getString("resource").split("/");  
                                                result.add(pack_id[2]);
                                                System.out.println("pack_id: "+pack_id[2]);
                                               

                                            }
                                            for (int i = 0; i < result.size(); i++)
                                            {
                                                String msg_api = String.format("https://api.mercadolibre.com/messages/packs/%s/sellers/%s?access_token=%s", result.get(i),our_user_id,Token);
                                                Document new_doc = Jsoup.connect(msg_api).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")
                                                .referrer("http://www.google.com")
                                                .timeout(180000)
                                                .ignoreContentType(true)
                                                .get();
                                                
                                                //System.out.println(new_doc);
                                                    JSONObject obj1 = new JSONObject(new_doc.getElementsByTag("pre").text().replaceAll("<a href=.+</a>", ""));
                                                    //System.out.println(obj1);
                                                    JSONArray arr1 = obj1.getJSONArray("messages"); //fix json url double quotes
                                                    
                                                    if(arr1.length() == 0){
                                                        System.out.println("JSON Vacio con URL: "+Api_URL);
                                                    }else{
                                                        for (int j = 0; j < arr1.length(); j++)
                                                        {   
                                                            String msg_id = arr1.getJSONObject(j).getString("id");
                                                            long user_id = arr1.getJSONObject(j).getJSONObject("from").optLong("user_id"); //user_id = buyer_id
                                                                if(user_id == our_user_id){                                                  //Si el mensaje es nuestro cambio el user
                                                                    user_id = arr1.getJSONObject(j).getJSONObject("to").optLong("user_id");// para poder relacionar todos los mensajes
                                                                }
                                                            String user_name = arr1.getJSONObject(j).getJSONObject("from").optString("name");
                                                            String text = arr1.getJSONObject(j).getString("text");
                                                            String subject = arr1.getJSONObject(j).optString("subject");
                                                            String msg_date = arr1.getJSONObject(j).getJSONObject("message_date").getString("created").replaceAll("T.+", "");
                                                            
                                                            System.out.println("msg_id: "+msg_id+" user_id: "+user_id+" user_name: "+user_name+" text: "+text+" subject: "+subject+" msg_date: "+msg_date);
                                                        }

                                                        }
                                            }
                                                //return result;
                                        }
                                        break;
                        
                        }
                        
                        
                       
                    
            //Thread.sleep(3000);
           
        } catch (Exception ex) {
            System.out.println(ex);
            System.out.println(Api_URL);          
            
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
            bots.TeleBot.sendMessage("@"+user, Configuration.getConfig().getProperty("searcher_token").replace("\"",""), chatID);      
            if(!rs.next()) {    
                        bots.TeleBot.sendMessage("No hay Productos que Coincidan con la Busqueda", Configuration.getConfig().getProperty("searcher_token").replace("\"",""), chatID);
                        rs.close();
                        stmntThread.close();
                    }else{
                        rs.previous();
                        while(rs.next()) {
                          //String product = String.format("<%s>: [%s] precio $%.2f\n %s\n",Data.getVendorDisplay().get(rs.getString("vendor_id")),rs.getString("articulo"),rs.getFloat("price"),rs.getString("url"));
                          //bots.TeleBot.sendMessage(product, Configuration.getConfig().getProperty("searcher_token").replace("\"",""), chatID);  
                        }
                   rs.close();
                  }
    
            stmntThread.close();
            connThread.close();
    }
*/
            
}
