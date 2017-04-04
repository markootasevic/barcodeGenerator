/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barcodegenerator;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class BarcodeGenerator {
    
    

    
    public static ArrayList<String> createCombinations(ArrayList<String> chars, int size, ArrayList<String> combinations){
        
        if(combinations.isEmpty()){
            combinations = chars;
        }
        
        if(size == 1){
            return combinations;
        }
        
        ArrayList<String> newCombinations = new ArrayList<>();
        
        for (String combination : combinations) {
            for (String oneChar : chars) {
                newCombinations.add(combination+oneChar);
            }
        }
       
        return createCombinations(chars, size - 1, newCombinations);
       
    }
    
     public static int getCheckDigit(String barcode){
        
         int sum = 0;
         
         for (int i = 1; i <= 11; i+=2) {
             int number = Integer.parseInt(barcode.charAt(i)+"");
             sum += 3*number;
         }
         
        for (int j = 0; j <= 10; j+=2) {
             int num = Integer.parseInt(barcode.charAt(j)+"");
             sum += num;
         }
        int rez = sum % 10;
        
        if(rez > 0){
            rez = 10 - rez;
        }
         
        return rez;
    }
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String url = "jdbc:mysql://192.168.1.109:3306/tickets_dev";
          Class.forName ("com.mysql.jdbc.Driver").newInstance ();
           Connection conn = (Connection) DriverManager.getConnection (url, "ticketdev", "j6oH9n6UxbVKYkJQ");
           conn.setAutoCommit(false);
         
            ArrayList<String> chars = new ArrayList<>();
            chars.add("0");
            chars.add("1");
            chars.add("2");
            chars.add("3");
            chars.add("4");
            chars.add("5");
            chars.add("6");
            chars.add("7");
            chars.add("8");
            chars.add("9");
            ArrayList<String> allCombinations = createCombinations(chars,6, new ArrayList<String>());

            
            ArrayList<String> barcodes = new ArrayList<>();
            String firstPart = "860737";
            for(String combination : allCombinations) {
               String bc = firstPart + combination;
               int checkDigit = getCheckDigit(bc);
               String finalBarcode = bc + checkDigit;
               barcodes.add(finalBarcode);
            }
         
            for (int i = 1; i < 810000; i++) {
            String updateTableSQL = "UPDATE entrance_code SET barcode = ? WHERE id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateTableSQL);
            preparedStatement.setString(1, barcodes.get(i+ 1 ));
            preparedStatement.setInt(2, i);
            preparedStatement .executeUpdate();
            }
            
            conn.commit();
            conn.close();
            System.out.println("uspelo");
            
            // TODO code application logic here
        } catch (Exception ex) {
            Logger.getLogger(BarcodeGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
