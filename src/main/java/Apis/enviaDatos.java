/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Apis;

import bots.TeleBot;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * @author Mauro
 */
public class enviaDatos {

    public static void envioRespuesta(String url, String q_id, String text) throws IOException{
                            try{
                                
                            OkHttpClient client = new OkHttpClient();

                            MediaType mediaType = MediaType.parse("application/json");
                            String Sbody = String.format("{\r\n\"question_id\": %s, \r\n \"text\":\"%s\" \r\n}\r\n", q_id,text);
                            RequestBody body = RequestBody.create(mediaType, Sbody);
                            Request request = new Request.Builder()
                              .url(url)
                              .post(body)
                              .addHeader("Content-Type", "application/json")
                              .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")
                              .addHeader("Accept", "*/*")
                              .addHeader("Cache-Control", "no-cache")
                              //.addHeader("Postman-Token", "87ed9020-e89b-486c-8fa1-6f22c3f50122,6ecbe441-23c1-4e39-bae2-aed9b9a95751")
                              .addHeader("Host", "api.mercadolibre.com")
                              .addHeader("Accept-Encoding", "gzip, deflate")
                              .addHeader("Content-Length", "52")
                              .addHeader("Connection", "keep-alive")
                              .addHeader("cache-control", "no-cache")
                              .build();

                              Response response = client.newCall(request).execute();
                              
                                System.out.println(response);
                                TeleBot.PushMSG("Code: "+response.code()+"\nMessage: "+response.message());
                            
                            }
                            
                            catch(Exception ex){
                                System.out.println(ex);
                            }
        
        
    }

    public static void envioMensaje(String url, String buyer_id, String seller_id, String email, String text) throws IOException{
                            try{ //ARREGLAR LA URL DE LA APIm  
                                
                            OkHttpClient client = new OkHttpClient();

                            MediaType mediaType = MediaType.parse("application/octet-stream");
                            String Sbody = String.format("{\r\n\"from\" : {\r\n\"user_id\": \"%s\",\r\n\"email\" : \"%s\"\r\n},"
                                    + "\r\n\"to\": {\r\n\t\t\"user_id\" : \"%s\"\r\n},\r\n   \t\"text\": \"%s\"\r\n}\r\n", seller_id,email,buyer_id,text);
                            RequestBody body = RequestBody.create(mediaType,Sbody);
                            Request request = new Request.Builder()
                              .url(url)
                              .post(body)
                              .addHeader("cache-control", "no-cache,no-cache")
                              .addHeader("content-type", "application/json")
                              .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")
                              .addHeader("Accept", "*/*")
                              .addHeader("Host", "api.mercadolibre.com")
                              .addHeader("Accept-Encoding", "gzip, deflate")
                              .addHeader("Content-Length", "159")
                              .addHeader("Connection", "keep-alive")
                              .build();

                            Response response = client.newCall(request).execute();
                              
                                System.out.println(response);
                                TeleBot.PushMSG("Code: "+response.code()+"\nMessage: "+response.message());
                            }
                            
                            catch(Exception ex){
                                System.out.println(ex);
                            }
        
        
    }
    
}
