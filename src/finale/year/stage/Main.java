package finale.year.stage;

import finale.year.stage.util.Util;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by newton on 5/1/15.
 */
public class Main {

    public static void main(String[] argv){

        String password = "newton";;
        String digested = "fd216818cecbc78c0aeb274521b1501a01a2226a23a9a6922abb824b12dd86c4";

        StringBuilder bd = new StringBuilder();


        /*//convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        System.out.println("Hex format : " + sb.toString());

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<mdbytes.length;i++) {
            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
        }

        System.out.println("Hex format : " + hexString.toString());

        byte[] digest = null ;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes(Charset.forName("UTF-8")));
             digest = md.digest();
            for(byte ch : digest){
                bd.append(Integer.toString((ch & 0xff) + 0x100, 16).substring(1));//covert to hexadecimal string
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        System.out.println("fd216818cecbc78c0aeb274521b1501a01a2226a23a9a6922abb824b12dd86c4");
        System.out.println(digested);
        System.out.println(DatatypeConverter.printHexBinary(digest).toLowerCase());
        System.out.println(bd.toString().length());
        System.out.println(digested.length());
        int hex = 0xff;
        System.out.println(0x100);*/

        //System.out.println("code : "+Util.generateRecoveryCode("igotti47@gmail.com"));
       /* System.out.println("password : " + password);
        System.out.println("in base 64 : "+Util.encodeToBase64(password));
        String[] arr = Util.encryptPassword(password);
        System.out.println("encrypted : "+arr[0]);
        System.out.println("salt : "+arr[1]);


        Util.sendEmail("chamaphilip@gmail.com","Hi!","Attention...!");
        Util.sendEmail("igotti47@gmail.com","Hi!","Attention...!");*/

       // System.out.println(password.length());
       // System.out.println(password.getBytes().length);
        Date date  = new Date("11/1/2008");
        Date date2 = new Date("11/10/2009");
        int day = Calendar.getInstance().get(Calendar.DATE);

        java.sql.Date sqlD = new java.sql.Date(date.getTime());
        java.sql.Date sql2 = new java.sql.Date(date2.getTime());

        System.out.println(day);

        Calendar dat = Calendar.getInstance();
        dat.getTime();
        System.out.println(new SimpleDateFormat("YYYY/MM/dd").format(sqlD));

        for(int i = 1; i <=4 ; ++i) {
            System.out.println(i);
        }


    }
}
