/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import static Main.Hikari.getDataSource;
import bots.TeleBot;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Mauro
 */
public class updater {
    
    static ResultSet rs; static ResultSet tmp;
    
    static Connection connThread = null;
    static Statement stmntThread = null;
    static Statement stmntThread2 = null;
    static String sql = null;
    
    public static void updater() throws IOException{

        try {
			dbpasswd=""
			dbsuer=""
            connThread = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/DATA",dbsuer,dbpasswd); 
            //connThread = DriverManager.getConnection(Data.getInstance().getDB_URL(),Data.getInstance().getUSER(),Data.getInstance().getPASS());
           
            stmntThread = connThread.createStatement();
            stmntThread2 = connThread.createStatement();
            connThread.setAutoCommit(false);
            //System.out.println("Updater start");
            
            BiMap<String, String>VendorDisplay = HashBiMap.create();
            VendorDisplay.put("pregunta", "Q");VendorDisplay.put("venta", "V");VendorDisplay.put("mensaje", "M");

                sql = "SELECT * FROM temp;";
                rs = stmntThread.executeQuery(sql);
                    if(!rs.next()){
                        //Thread.sleep(1000);
                    }else{
                        rs.previous();
                        System.out.println(rs.toString());
                        while(rs.next()){
                            //System.out.println("RS");
                            String id = rs.getString("id");
                            String type = rs.getString("type");
                            String item = rs.getString("item");
                            String text = rs.getString("text");
                            
                            TeleBot.PushMSG("Nuevo:\nID:"+VendorDisplay.get(type)+id+"\nItem:"+item+"\n"+text);
                          
                          sql = "DELETE FROM temp WHERE id='" +id +"';";
                          stmntThread2.execute(sql);
                          connThread.commit();
                        }

                  }
            stmntThread.close();     
            stmntThread2.close();   
            rs.close();       
            connThread.close();
            //dataSource.close();
        
            //System.out.println("Offer Sekker Finished");  
            //Data.getInstance().setColaLog("Offer Sekker Finished\n");
            //Data.getInstance().setOSFlag(true);
            
            
        } catch (SQLException ex) {
           System.out.println("Updater: "+ex);  
    }
  }  
}
