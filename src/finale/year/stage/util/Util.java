package finale.year.stage.util;


import org.json.JSONObject;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Random;

/**
 * Created by newton on 4/30/15.
 */
public class Util {

    private static final String SHA_256 = "SHA-256";
    private static final String EMAIL_SENDER = "gestionstage2015@gmail.com";
    private static final String EMAIL_SENDER_PASSWORD = "2015gestionstage";
    private static final String HOST_GMAIL = "smtp.gmail.com"; //smtp server




    /**
     * Encodes a string using Base64Encoding
     *
     * @param str a string to be encoded
     * @return a string encoded using base64
     */
    public static String encodeToBase64(String str){
        //Base64 encoder = new  BASE64Encoder();//get encoder instance
        return DatatypeConverter.printBase64Binary(str.getBytes());//encode its bytes representation and return a string


    }
    public static String encodeToBase64(byte[] byteArray){
        //Base64 encoder = new  BASE64Encoder();//get encoder instance
        return DatatypeConverter.printBase64Binary(byteArray);//encode its bytes representation and return a string


    }

    /**
     * Encrypts a given password using custom algorithm
     *
     * @param password a string to be encrypted
     * @return an array containing salt and encrypted password
     */
    public static String[] encryptPassword(String password){
        String[] array = new String[2];
        try {
            Random rand = new Random();
            int temp = rand.nextInt();
            byte[] digestedPassword = digestPassword(password);//hash using SHA-256
            String salt = Util.encodeToBase64(digestedPassword)+temp;//encode and concatenate to get salt
            password = formatDigestedPassword(digestedPassword, "%02x")+salt;//generate encrypted password
            digestedPassword = digestPassword(password);
            password = formatDigestedPassword(digestedPassword, "%03x");
            array[0] = password;
            array[1] = salt;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return array;
    }


    /**
     * Encrypts a given password using salt so it can be compared to another one especially
     * during login
     *
     * @param password a string to be decrypted
     * @param salt security addition
     * @return decrypted password
     */
    public static String encryptPassword(String password, String salt){
        try {
            byte[] digestedPassword = digestPassword(password);//hash the password using SHA-256
            password = formatDigestedPassword(digestedPassword, "%02x")+salt;//format it to generate an encrypted version
            digestedPassword = digestPassword(password);//hash the salted password
            password = formatDigestedPassword(digestedPassword, "%03x");//format it to get final string
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return password;
    }

    /**
     * Digests a given string into an array of bytes
     *
     * @param undigested a string to be digested into  array of bytes
     * @return array of bytes of digested string
     *
     * @throws NoSuchAlgorithmException
     */
    public static  byte[] digestPassword(String undigested) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance(SHA_256);
        md.update(undigested.getBytes());
        return md.digest();


    }

    /**
     * Formats a byte array into a string using a provided format
     * @param digestArray a byte array to be formated
     * @param format a format string
     * @return a formatted string
     */
    public static String formatDigestedPassword(byte[] digestArray, String format){
        StringBuilder builder = new StringBuilder();
        for(byte character : digestArray){
            //this could be replaced by DatatypeConverter.printHexBinary(digest).toLowerCase()
            //if no format is needed

            //builder.append(Integer.toString((character & 0xff) + 0x100, 16).substring(1));//covert to hexadecimal string
            builder.append(String.format(format,character));
        }
        return builder.toString();
    }

    public static String generateRecoveryCode(String email) {
        Random random = new Random();
        email =  (random.nextLong()+email.hashCode()) + email ;
        String code = Util.encodeToBase64(email);

        return code.substring(0,8).toUpperCase();
    }

    public static void sendEmail(String recipient, String subject, String content){


        //1.create a properties object used to create a session
        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host",HOST_GMAIL);
        properties.put("mail.smtp.port","587");

        //2.Get a session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //parameters for authentification
                return new PasswordAuthentication(EMAIL_SENDER,EMAIL_SENDER_PASSWORD);
            }
        });
        //3.Create a MimeMessage object
        Message message = new MimeMessage(session);

        try {
            //set From : header field of the header
            message.setFrom(new InternetAddress(EMAIL_SENDER));

            //set To : header field of the header
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

            //set Subject : header field
            message.setSubject(subject);

            //set the message content
            message.setText(content);

            //3.Send the message
            Transport.send(message);


        } catch (MessagingException e) {
            e.printStackTrace();
        }

       // System.out.println("Email sent successful");


    }



}
