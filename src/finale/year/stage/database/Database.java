package finale.year.stage.database;

import finale.year.stage.models.*;
import finale.year.stage.util.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import javax.xml.transform.Result;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Created by newton on 4/30/15.
 */
public class Database {
    //database specifics
    private static final String DATABASE_NAME = "stage";
    private static final String DATABASE_URL = "jdbc:mysql://127.0.0.1/"+DATABASE_NAME;
    //private static final String DATABASE_URL = "jdbc:mysql://127.7.116.2:3306/"+DATABASE_NAME;
    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";

    private static final String DATABASE_USER = "stagiere";
    private static final String DATABASE_USER_PASSWORD = "stagiere";
    //private static final String DATABASE_USER = "adminqyuXbuX";
   //private static final String DATABASE_USER_PASSWORD = "i8KGtij_XPtz";


    // table specifics
    private static final String TABLE_LOGIN = "login";
    private static final String TABLE_COLUMN_EMAIL = "email";
    private static final String TABLE_COLUMN_TYPE = "type";
    private static final String TABLE_COLUMN_PASSWORD = "password";
    private static final String TABLE_COLUMN_SALT = "salt";

    private static final String TABLE_IDENTIFICATION = "identification";
    private static final String TABLE_COLUMN_IDENTIFIER ="identifier";
    private static final String TABLE_RECOVERY = "recovery";
    private static final String TABLE_COLUMN_CODE = "code";
    private static final String TABLE_COLUMN_ACTIVE = "active";
    private static final String TABLE_COLUMN_DATE = "date";


    private static final String TABLE_THEME = "theme";
    private static final String TABLE_COLUMN_TITLE = "titre";
    private static final String TABLE_COLUMN_DESCRIPTION = "description";
    private static final String TABLE_COLUMN_PROPOSEUR_TYPE = "proposeur_type";
    private static final String TABLE_COLUMN_PROPOSEUR_EMAIL = "proposeur_email";
    private static final String TABLE_COLUMN_ID = "id";

    // Reclammation Specifics
    private static final String TABLE_RECLAMATION = "reclamation";
    private static final String TABLE_COLUMN_EMAIL_RECEPTEUR = "email_recepteur";
    private static final String TABLE_COLUMN_EMAIL_SENDER = "email_sender";
    private static final String TABLE_COLUMN_ID_ETAT ="id_etat";
    private static final String TABLE_COLUMN_READ_STATUS ="read_status";
    private static final String TABLE_COLUMN_SWITCH ="switch";


    // Phase Specifics
    private static final String TABLE_PHASE = "phase";
    private static final String TABLE_PHASE_ID = "id";
    private static final String TABLE_COLUMN_NUMBER_PHASE ="number_phase";
    private static final String TABLE_COLUMN_PHASESTATE = "state";
    private static final String TABLE_COLUMN_START= "date_start";
    private static final String TABLE_COLUMN_END= "date_end";
    private static final int    TABLE_PHASE_STATE_IDLE = 0;
    private static final int    TABLE_PHASE_STATE_RUNNNING = 1;
    private static final int    TABLE_PHASE_STATE_ENDED = 2;


    // Etat Specifics
    private static final String TABLE_ETAT ="etat";
    private static final String TABLE_COLUMN_NOTE_ENS = "note_enseignant";
    private static final String TABLE_COLUMN_NOTE_TUT = "note_tuteur";
    private static final String TABLE_COLUMN_ID_PHASE = "id_phase";
    private static final String TABLE_COLUMN_EMAIL_ETUDIANT = "email_etudiant";


    private Connection connection;
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;

    private Database() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class.forName(DATABASE_DRIVER).newInstance();
        connection = DriverManager.getConnection(DATABASE_URL,DATABASE_USER,DATABASE_USER_PASSWORD);
    }

    public static Database connect(){
        Database db = null;
        try {
            db = new Database();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return db;
    }

    //user general
    public String register(String id, String email, String password){

        JSONObject response = new JSONObject();
        //check if the id exists
        String query = "S" +
                "ELECT COUNT(*) FROM "+TABLE_IDENTIFICATION +
                " WHERE " + TABLE_COLUMN_IDENTIFIER +"=?";
        try {


            if(valueExists(id, query)){
                //id exists now get the type
                query = "SELECT "+TABLE_COLUMN_TYPE + " FROM "+
                        TABLE_IDENTIFICATION +" WHERE "+ TABLE_COLUMN_IDENTIFIER +" = ?";
                preparedStatement = null;
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1,id);
                resultSet = preparedStatement.executeQuery();
                resultSet.next();//move to first row
                int userType = resultSet.getInt(TABLE_COLUMN_TYPE);
                //get an array containing encrypted password and salt
                String[] array = Util.encryptPassword(password);

                //check if user is already in the login table
                query = "SELECT COUNT(*) FROM "+TABLE_LOGIN +
                        " WHERE " + TABLE_COLUMN_EMAIL +"=?";

                if(valueExists(email,query)){
                    //user already registered
                    //send back error message
                    try {
                        response.put("response code", 0);
                        response.put("response message","User already registered");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    disconnect();
                    return response.toString();//failure

                }


                //user has a valid id and has not been registered
                //now register to login table
                query = "INSERT INTO "+TABLE_LOGIN+"("+ TABLE_COLUMN_EMAIL +","+ TABLE_COLUMN_PASSWORD +","+
                        TABLE_COLUMN_SALT +","+ TABLE_COLUMN_TYPE +") VALUES "+"(?,?,?,?)";
                preparedStatement = null;
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1,email);
                preparedStatement.setString(2,array[0]);
                preparedStatement.setString(3,array[1]);
                preparedStatement.setInt(4, userType);
                int rows = preparedStatement.executeUpdate();
                if(rows == 1){
                    //registration was successful
                    //query the user information and send them back
                    JSONObject user = fetchUserInfo(email,userType);
                    try {
                        response.put("response code",1);
                        response.put("response message","User successful registered");
                        response.put("user",user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    disconnect();
                    return  response.toString();//success
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //id doesn't exist
        try {
            response.put("response code",2);
            response.put("response message","Invalid user id! Please contact the system admin!");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        disconnect();
        return response.toString();

    }

    public String login(String email, String password){

        JSONObject response = new JSONObject();
        //check if the email exists i.e the user has been registered already
        String query = "SELECT COUNT(*) FROM "+TABLE_LOGIN +" WHERE "+ TABLE_COLUMN_EMAIL +" = ?";
        try {
            if(valueExists(email,query)){
                //the user is registered
                //get user info

                query = "SELECT "+ TABLE_COLUMN_PASSWORD +","+ TABLE_COLUMN_SALT +","+ TABLE_COLUMN_TYPE +
                        " FROM "+TABLE_LOGIN+" WHERE "+ TABLE_COLUMN_EMAIL +" = ?";
                preparedStatement = null;
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1,email);
                resultSet = preparedStatement.executeQuery();
                resultSet.next();
                //get salt
                String salt = resultSet.getString(TABLE_COLUMN_SALT);
                //concatenate to make password from database salted
                String dbSaltedPassword = resultSet.getString(TABLE_COLUMN_PASSWORD) + salt;
                ;
                //encrypt the supplied password with salt to get equivalent string
                //as dbSaltedPassword
                String encryptedPassword = Util.encryptPassword(password,salt) + salt;

                //encode the two and check for equality
                if(Util.encodeToBase64(dbSaltedPassword).equals(Util.encodeToBase64(encryptedPassword))){
                    //the two strings are equal, valid login
                    //fetch user information
                    JSONObject user = fetchUserInfo(email,resultSet.getInt(TABLE_COLUMN_TYPE));

                    try {
                        response.put("response code",1);
                        response.put("response message","Login successful");
                        response.put("from db",Util.encodeToBase64(dbSaltedPassword));
                        response.put("supplied",Util.encodeToBase64(encryptedPassword));
                        response.put("user",user);
                        disconnect();
                        return  response.toString();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //wrong password
                try {
                    response.put("response code",2);
                    response.put("response message","Wrong credentials");
                    disconnect();
                    return response.toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            //email does not exist
            try {
                response.put("response code",0);
                response.put("response message","Unregistered user");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        disconnect();
        return response.toString();
    }

    public String recoverPassword(String email){
        JSONObject response = new JSONObject();
        //check if the email exists i.e the user has been registered already
        String query = "SELECT COUNT(*) FROM "+TABLE_LOGIN +" WHERE "+ TABLE_COLUMN_EMAIL +" = ?";
        try {
            if (valueExists(email, query)) {
                //check if user has an active request for recovery
                query = "SELECT COUNT(*) FROM "+TABLE_RECOVERY +" WHERE "+ TABLE_COLUMN_EMAIL +" = ? AND "
                        +TABLE_COLUMN_ACTIVE +" = ?";

                if(valueExists(email,1,query)){
                    //there is an active request
                    //send it back
                    //retrieve value
                    query = "SELECT "+TABLE_COLUMN_CODE+" FROM "+TABLE_RECOVERY +" WHERE "+ TABLE_COLUMN_EMAIL
                            +" = ? AND "+TABLE_COLUMN_ACTIVE +" = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1,email);
                    preparedStatement.setInt(2,1);
                    resultSet = preparedStatement.executeQuery();
                    resultSet.next();
                    String code = resultSet.getString(TABLE_COLUMN_CODE);
                    try {
                        //send email here
                        String message = "You recently requested for a code to change your password." +
                                "Please use this code and don't send anymore requests until you use it.\n\n\n" +
                                "RECOVERY CODE : " +code+"\n\n\n" +
                                "Use this code to change your password.\n" +
                                "Ignore this e-mail if " + "you didn't request for a recovery code";
                        Util.sendEmail(email,"Recovery code",message);
                        response.put("response code",3);
                        response.put("response message","An email with recovery code has been sent again to "+email);
                        response.put("code",code);
                        disconnect();
                        return response.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }

                //valid email generate code
                String code = Util.generateRecoveryCode(email);
                //insert the code into the recovery table
                query = "INSERT INTO "+ TABLE_RECOVERY +"("+ TABLE_COLUMN_EMAIL +","+TABLE_COLUMN_CODE+")" +
                        " VALUES "+"(?,?)";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1,email);
                preparedStatement.setString(2,code);
                int rows = preparedStatement.executeUpdate();
                if(rows == 1){
                    //code successful added
                    //send email here
                    String message = "You recently requested for a code to recover your password.\n\n\n" +
                            "RECOVERY CODE : " +code+ "\n\n\n" +
                            "Use this code to change your password.\n" +
                            "Ignore this e-mail if " + "you didn't request for a recovery code";
                    Util.sendEmail(email,"Recovery code",message);
                    //send response back
                    try {
                        response.put("response code",1);
                        response.put("response message","An email with recovery code has been sent to "+email);
                        response.put("code",code);
                        disconnect();
                        return response.toString();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            //unregistered user
            try {
                response.put("response code",0);
                response.put("response message","Unknown user with email : "+email);

            } catch (JSONException e) {

                e.printStackTrace();

            }


        } catch (SQLException e) {
            e.printStackTrace();
            try {
                response.put("response code",2);
                response.put("response message","Unknown error occured");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        disconnect();
        return response.toString();//
    }

    public String changePassword(String email,String code,String password){

        JSONObject response = new JSONObject();
        //check if the id exists
        String query = "SELECT COUNT(*) FROM "+TABLE_RECOVERY +
                " WHERE " + TABLE_COLUMN_EMAIL +" =? AND "+TABLE_COLUMN_CODE+" =? AND "+TABLE_COLUMN_ACTIVE+ " =? ";
        try {

            if (valueExists(email, code, 1, query)) {
                //valid email,code and active
                //now deactivate code
                query = "UPDATE " + TABLE_RECOVERY + " SET " +
                        TABLE_COLUMN_ACTIVE + " =? WHERE " + TABLE_COLUMN_EMAIL + " = ?";
                preparedStatement = null;
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, 0);
                preparedStatement.setString(2, email);
                preparedStatement.executeUpdate();

                //encrypt new password
                String[] credentials = Util.encryptPassword(password);

                //now insert new password
                query = "UPDATE " + TABLE_LOGIN + " SET " +
                        TABLE_COLUMN_PASSWORD + " =?, "+TABLE_COLUMN_SALT+" =? "+" WHERE " + TABLE_COLUMN_EMAIL + " = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, credentials[0]);
                preparedStatement.setString(2, credentials[1]);
                preparedStatement.setString(3, email);
                int rows = preparedStatement.executeUpdate();

                if(rows == 1){
                    try {
                        response.put("response code",1);
                        response.put("response message","Password changed successfully!");
                        return response.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }else{
                //code wrong,email wrong, or used
                try {
                    response.put("response code",0);
                    response.put("response message","Error : Expired code or wrong email");
                    return response.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }catch (SQLException e){
            e.printStackTrace();

        }

        try {
            response.put("response code",2);
            response.put("response message","Something went terribly wrong");
            return response.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response.toString();

    }

    //theme
    public String addTheme(String theme){
        JSONObject response = new JSONObject();
        JSONObject themeObject = null;
        String title = null;
        try {
            themeObject = new JSONObject(theme);//change the json string to object
            title = themeObject.getString("title");

        } catch (JSONException e) {
            e.printStackTrace();
            //return error here
        }
        //check if the theme exists
        String query =  "SELECT COUNT(*) FROM "+TABLE_THEME +
                " WHERE " + TABLE_COLUMN_TITLE +"=?";
        try {
            if(valueExists(title,query)){
                //the theme title exists refuse it
                try {
                    response.put("response code",2);
                    response.put("response message","Theme with the same title exists already");
                    disconnect();
                    return response.toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            //theme doesn't exist insert into the theme table

            query = "INSERT INTO "+TABLE_THEME+"("+ TABLE_COLUMN_TITLE +","+ TABLE_COLUMN_DESCRIPTION + "," +
                    TABLE_COLUMN_PROPOSEUR_TYPE + ","+ TABLE_COLUMN_PROPOSEUR_EMAIL + ") VALUES " +"(?,?,?,?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,title);
            preparedStatement.setString(2,themeObject.getString("description"));
            preparedStatement.setInt(3, themeObject.getInt("type"));
            preparedStatement.setString(4,themeObject.getString("email"));

            int rows = preparedStatement.executeUpdate();

            if(rows == 1){
                //successful inserted
                //send a response
                response.put("response code",1);
                response.put("response message","Theme successfully added");
                disconnect();
                return response.toString();
            }

            //theme was not added
            //database error
            response.put("response code",3);
            response.put("response message","Theme could not be added at this time!");

        } catch (SQLException e) {
            e.printStackTrace();

          /*  if(e instanceof MySQLIntegrityConstraintViolationException){
                //UNIQUE constraint violation

                try {
                    response.put("response code",2);
                    response.put("response message","Theme with the same title exists already");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnect();
        return response.toString();



    }

    public String listThemes(){
        JSONObject response = new JSONObject();

        //get all themes and send them back as an array of json objects
         String query =  "SELECT * FROM "+TABLE_THEME;
        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            JSONObject theme = null;
            int id = 0;
            while( resultSet.next()){
                //add info to a json object
                theme = new JSONObject();
                id = resultSet.getInt(TABLE_COLUMN_ID);
                theme.put(TABLE_COLUMN_ID,id);
                theme.put(TABLE_COLUMN_TITLE,resultSet.getString(TABLE_COLUMN_TITLE));
                theme.put(TABLE_COLUMN_DESCRIPTION,resultSet.getString(TABLE_COLUMN_DESCRIPTION));
                theme.put(TABLE_COLUMN_TYPE,resultSet.getString(TABLE_COLUMN_PROPOSEUR_TYPE));
                theme.put(TABLE_COLUMN_EMAIL,resultSet.getString(TABLE_COLUMN_PROPOSEUR_EMAIL));
                //add the theme to the response
                response.put(String.valueOf(id), theme);

            }
            disconnect();
            //send response back
            return response.toString();
        } catch (SQLException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            response.put("response code",0);
            response.put("response message"," Something went terribly wrong while fetching themes");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnect();

        return response.toString();


    }

    public String modifyTheme(String theme){

        JSONObject response = new JSONObject();
        JSONObject themeObject = null;
        int id = 0 ;
        try {
            themeObject = new JSONObject(theme);//change the json string to object
            id = Integer.parseInt(themeObject.getString("id"));


        } catch (JSONException e) {
            e.printStackTrace();
            //return error here
        }

        //update theme
        String query =  " UPDATE "+TABLE_THEME + " SET " + TABLE_COLUMN_TITLE +" = ? ,"
                + TABLE_COLUMN_DESCRIPTION +" = ?" + " WHERE " + TABLE_COLUMN_ID +" = ?";

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,themeObject.getString("title"));
            preparedStatement.setString(2,themeObject.getString("description"));
            preparedStatement.setInt(3, id);
            int rows = preparedStatement.executeUpdate();
            if(rows == 1){

                //successfully modified
                response.put("response code",1);
                response.put("response message","Theme modified successfully");
                disconnect();
                return response.toString();

            }

            //an error occurred

            response.put("response code",2);
            response.put("response message","Theme modification failed");
            disconnect();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //a terrible error occurred
        //likely database exception
        try {
            response.put("response code",0);
            response.put("response message","An error occurred during theme modification");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        disconnect();

        return response.toString();

    }

    public String deleteTheme(int id){
        JSONObject response = new JSONObject();

        //check if id is valid
        String query =  " SELECT COUNT(*) FROM "+TABLE_THEME + " WHERE " + TABLE_COLUMN_ID +" = ?";
        try {
            if(valueExists(id,query)){
                //id exists now delete it
                query = "DELETE FROM "+TABLE_THEME + " WHERE "+TABLE_COLUMN_ID +" = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1,id);
                int rows = preparedStatement.executeUpdate();
                if(rows == 1){
                    //successfully deleted
                    response.put("response code",1);
                    response.put("response message","Theme successfully deleted");
                    disconnect();
                    return response.toString();

                }

            }
            //invalid id

            response.put("response code",2);
            response.put("response message","Invalid theme id : Deletion failed!");
            disconnect();
            return response.toString();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //error
        //probably exception from the database
        try {
            response.put("response code",0);
            response.put("response message","An error occured during deletion!");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        disconnect();
        return response.toString();

    }

    private JSONObject fetchUserInfo(String email, int userType) throws SQLException {
        String query;//determine table to query by type
        String table = getUserType(userType);

        query = "SELECT * FROM "+table +
                " WHERE " + User.COLUMN_EMAIL +" = ?";
        preparedStatement = null;
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, email);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        JSONObject user = new JSONObject();
        try {
            user.put("firstname",resultSet.getString(User.COLUMN_PRENOM));
            user.put("lastname",resultSet.getString(User.COLUMN_NOM));
            user.put("sex",resultSet.getString(User.COLUMN_SEXE));
            user.put("email",resultSet.getString(User.COLUMN_EMAIL));
            user.put("tel",resultSet.getString(User.COLUMN_TEL));
            user.put("address",resultSet.getString(User.COLUMN_ADRESSE));
            user.put("type",userType);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    private String getUserType(int userType) {
        String table = null;
        switch (userType){

            case 0:
                table = Etudiant.TABLE_ETUDIANT;
                break;
            case 1:
                table = Tuteur.TABLE_TUTEUR;
                break;
            case 2:
                table = Enseignant.TABLE_ENSEIGNANT;
                break;
            case 3:
                table= Agent.TABLE_AGENT;
                break;
            case 4:
                table = Responsable.TABLE_RESPONSABLE;
                break;

        }
        return table;
    }

    private boolean valueExists(int value,String query) throws SQLException {
        boolean bool = false;
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, value);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();//move to first row
        if(resultSet.getInt("COUNT(*)") == 1){
            bool = true;
        }
        return bool;
    }

    private boolean valueExists(String value, String query) throws SQLException {
        boolean bool = false;
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, value);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();//move to first row
        if(resultSet.getInt("COUNT(*)") >= 1){
            bool = true;
        }
        return bool;
    }

    private boolean valueExists(String email,int active, String query) throws SQLException {
        boolean bool = false;
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,email);
        preparedStatement.setInt(2,active);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();//move to first row
        if(resultSet.getInt("COUNT(*)") == 1){
            bool = true;
        }
        return bool;
    }

    private boolean valueExists(String email,String code,int active, String query) throws SQLException {
        boolean bool = false;
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,email);
        preparedStatement.setString(2,code);
        preparedStatement.setInt(3,active);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();//move to first row
        if(resultSet.getInt("COUNT(*)") == 1){
            bool = true;
        }
        return bool;
    }
    //

    public String listReclamation(){
        JSONObject response = new JSONObject();

        //get all reclamations which have not been processed
        String query =  "SELECT * FROM "+ TABLE_RECLAMATION +" WHERE "+TABLE_COLUMN_SWITCH +" = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,0);
            resultSet = preparedStatement.executeQuery();
            JSONObject reclamation = null;
            JSONObject reclamations = new JSONObject();
            int id = 0;
            while( resultSet.next()){
                //add info to a json object
                reclamation = new JSONObject();
                id = resultSet.getInt(TABLE_COLUMN_ID);
                reclamation.put(TABLE_COLUMN_ID, id);
                reclamation.put(TABLE_COLUMN_ID_ETAT, resultSet.getString(TABLE_COLUMN_ID_ETAT));
                reclamation.put(TABLE_COLUMN_DESCRIPTION, resultSet.getString(TABLE_COLUMN_DESCRIPTION));
                reclamation.put(TABLE_COLUMN_EMAIL_RECEPTEUR, resultSet.getString(TABLE_COLUMN_EMAIL_RECEPTEUR));
                //add the reclamation to the response
                reclamations.put(String.valueOf(id), reclamation);

            }
            //create a response message
            response.put("response code",1);
            response.put("response message","Les reclamations ont été sélectionnés avec succès");
            response.put("reclamations",reclamations);
            disconnect();
            //send response back
            return response.toString();
        } catch (SQLException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            response.put("response code",0);
            response.put("response message","Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnect();

        return response.toString();


    }



    public  String consulterReclamations(String emailRecepteur){

        JSONObject response = new JSONObject();

        //get all reclamations which have not been processed
        String query =  "SELECT * FROM "+ TABLE_RECLAMATION +" WHERE "+TABLE_COLUMN_SWITCH +" = ? AND "+
                TABLE_COLUMN_EMAIL_RECEPTEUR+" = ? ORDER BY "+TABLE_COLUMN_READ_STATUS;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,1);
            preparedStatement.setString(2, emailRecepteur);
            resultSet = preparedStatement.executeQuery();
            JSONObject reclamation = null;
            JSONArray reclamations = new JSONArray();
            //for getting student info
            query = "SELECT nom,prenom"+" FROM "+ Etudiant.TABLE_ETUDIANT +" WHERE "+TABLE_COLUMN_EMAIL +" = ? ";
            PreparedStatement prepSt = connection.prepareStatement(query);
            ResultSet set = null;
            int id = 0;
            while( resultSet.next()){
                //add info to a json object
                reclamation = new JSONObject();
                reclamation.put("index", id);
                reclamation.put(TABLE_COLUMN_ID_ETAT, resultSet.getString(TABLE_COLUMN_ID_ETAT));
                reclamation.put(TABLE_COLUMN_EMAIL_SENDER, resultSet.getString(TABLE_COLUMN_EMAIL_SENDER));
                reclamation.put(TABLE_COLUMN_DATE,resultSet.getDate(TABLE_COLUMN_DATE));
                reclamation.put(TABLE_COLUMN_READ_STATUS, resultSet.getInt(TABLE_COLUMN_READ_STATUS));
                prepSt.setString(1, resultSet.getString(TABLE_COLUMN_EMAIL_SENDER));
                set = prepSt.executeQuery();
                /***********/
                if(set.next()){
                    reclamation.put("fullname",set.getString(Etudiant.TABLE_COLUMN_NOM)+" "
                            +set.getString(Etudiant.TABLE_COLUMN_PRENOM));
                }
                /******/
                //reclamation.put("fullname",set.getString("nom")+" "+set.getString("prenom"));
                prepSt.close();
                set.close();
                //get scores
                query = "SELECT note_tuteur,note_enseignant,id_phase FROM "+ TABLE_ETAT +" WHERE "+TABLE_COLUMN_ID +" = ? ";
                prepSt = connection.prepareStatement(query);
                prepSt.setInt(1,resultSet.getInt(TABLE_COLUMN_ID_ETAT));
                set = prepSt.executeQuery();
                if(set.next())
                reclamation.put("note_enseignant", set.getDouble("note_enseignant"));
                reclamation.put("note_tuteur",set.getDouble("note_tuteur"));
                reclamation.put("id_phase",set.getDouble("id_phase"));
                reclamation.put(TABLE_COLUMN_DESCRIPTION, resultSet.getString(TABLE_COLUMN_DESCRIPTION));
                reclamation.put(TABLE_COLUMN_EMAIL_RECEPTEUR, resultSet.getString(TABLE_COLUMN_EMAIL_RECEPTEUR));
                //add the reclamation to the response
                reclamations.put(id++, reclamation);

            }
            prepSt.close();
            set.close();
            //create a response message
            response.put("response code",1);
            response.put("response message","Les reclamations ont été sélectionnés avec succès");
            response.put("reclamations",reclamations);
            disconnect();
            //send response back
            return response.toString();
        } catch (SQLException e) {
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            response.put("response code",0);
            response.put("response message","Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnect();

        return response.toString();



    }

    public void disconnect (){
        if (resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (preparedStatement != null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

/**************************************************************************************************************************/
    public ArrayList<Affectation> affecter = new ArrayList<>();
    public ArrayList<Affectation> affecterMain = new ArrayList<>();
    //
    public ArrayList<Deliberation> delibList = new ArrayList<>();
    //
    //Table affectation
    public static final String TABLE_AFFECTATION = "affectation";
    public static final String TABLE_COLUMN_EMAIL_ENSEIGNANT = "email_enseignant";
    public static final String TABLE_COLUMN_EMAIL_TUTEUR ="email_tuteur";
    public static final String TABLE_COLUMN_ID_THEME ="id_theme";

    //Choix
    public static final String TABLE_CHOIX = "choix";
    public static final String TABLE_COLUMN_CHOIX_ID = "id";
    public static final String TABLE_COLUMN_CHOIX_1 = "choix1";
    public static final String TABLE_COLUMN_CHOIX_2 = "choix2";
    public static final String TABLE_COLUMN_CHOIX_3 = "choix3";


    //Tuteur
    public static final String TABLE_TUTEUR ="tuteur";

    //Deliberation
    public static final String TABLE_DELIBERATION = "deliberation";
    public static final String COLUMN_PHASE_1     = "phase1";
    public static final String COLUMN_PHASE_2     = "phase2";
    public static final String COLUMN_PHASE_3     = "phase3";
    public static final String COLUMN_PHASE_4     = "phase4";
    public static final String COLUMN_MOYENNE     = "moyenne";
    public static final String COLUMN_DECISION    = "decision";

    //Reclamation
    public String submitReclamation(int idEtat,String description,String emailRecepteur,String senderEMail){
        //
        JSONObject response = new JSONObject();
        //check if id exist and email exists
        String queryIdEtat = " SELECT COUNT(*) FROM "+TABLE_ETAT+" WHERE "+TABLE_COLUMN_ID+" = ?";
        String emailExist  = " SELECT COUNT(*) FROM "+Enseignant.TABLE_ENSEIGNANT
                            +" WHERE "+TABLE_COLUMN_EMAIL+" = ?";
        String emailTut    = " SELECT COUNT(*) FROM "+Tuteur.TABLE_TUTEUR
                +" WHERE "+TABLE_COLUMN_EMAIL+" = ?";

        boolean proceed = true;
        try {

            if(valueExists(idEtat,queryIdEtat)) {
                if(!valueExists(emailRecepteur,emailExist)) {
                    if(!valueExists(emailRecepteur,emailTut)){
                        proceed = false;
                        //something went wrong
                        response.put("response code",2);
                        response.put("response message", "Votre demande n'a pas été effectuée : Email invalide !");

                        disconnect();
                        return response.toString();
                    }
                }

            }else{
                proceed = false;
                //something went wrong
                response.put("response code",2);
                response.put("response message", "Votre demande n'a pas été effectuée : ID Invalide ! ");

                disconnect();
                return response.toString();
            }
        //Check dates to verify if reclamation can be made
        // 1 . Get current Phase
        int phaseNow = currentPhase();
        if(phaseNow != -1)  {
            // 2. Get date of current Phase
            java.sql.Date dateEnd = getPhaseDate(phaseNow);
            if(!(dateNow().before(dateEnd))){
                //Reclamtion impossible :
                response.put("response code",2);
                response.put("response message", "Votre demande n'a pas été effectuée : (Phase) Termine; Dates sont depasser ! ");

                disconnect();
                return response.toString();
            }


        }else{
            //something went wrong
            response.put("response code",2);
            response.put("response message", "Votre demande n'a pas été effectuée : Stage Termine ou Non Commence ! ");

            disconnect();
            return response.toString();
        }
        //Everything is OKya he can reclammer
        String query = "INSERT INTO "+ TABLE_RECLAMATION + "("+ TABLE_COLUMN_ID_ETAT+","+TABLE_COLUMN_DESCRIPTION+","+
                TABLE_COLUMN_EMAIL_RECEPTEUR + ","+ TABLE_COLUMN_EMAIL_SENDER+ ") VALUES ( ?, ? , ?,?)";

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1,idEtat);
            preparedStatement.setString(2,description);
            preparedStatement.setString(3,emailRecepteur);
            preparedStatement.setString(4,senderEMail);
            int rows = preparedStatement.executeUpdate();
            if(rows == 1){
                //success
                response.put("response code", 1);
                response.put("response message", "Votre reclamation a été déposée avec succès");

                disconnect();
                return response.toString();
            }

            //something went wrong
            response.put("response code",2);
            response.put("response message", "Votre demande n'a pas été effectuée");

            disconnect();
            return response.toString();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            response.put("response code",0);
            response.put("response message","Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        disconnect();
        return response.toString();

    }

    //Check id
    public String transferReclamation(int id,int state){
        JSONObject response = new JSONObject();
        //check if id  exists
        String query = "SELECT COUNT(*) FROM "+TABLE_RECLAMATION +" WHERE "+TABLE_COLUMN_ID +" = ?";

        try {
            if(valueExists(id,query)){
                //id exists
                //update it to processed by switching to 1
                //get the email of the receptor and send them an email
                query = "UPDATE "+TABLE_RECLAMATION +" SET "+TABLE_COLUMN_SWITCH + " = ? " +" WHERE "+
                        TABLE_COLUMN_ID +" = ?";
                preparedStatement = connection.prepareStatement(query);

                if(state == 0)
                    preparedStatement.setInt(1,2);
                else
                    preparedStatement.setInt(1,1);

                preparedStatement.setInt(2,id);
                int rows = preparedStatement.executeUpdate();
                if(rows == 1){
                    //successfully processed
                    if(state == 1 ) {//valid request

                        response.put("response code", 1);
                        response.put("response message", "Reclamation transferée avec succès");
                        //get email
                        // query = "SELECT "+TABLE_COLUMN_EMAIL_RECEPTEUR+","+TABLE_COLUMN_DATE +" FROM "+TABLE_RECLAMATION + " WHERE "+
                        //  TABLE_COLUMN_ID + " = ?";
                        ////get email
                        query = "SELECT "+TABLE_COLUMN_EMAIL_RECEPTEUR+","+TABLE_COLUMN_DATE +" , "
                                +TABLE_COLUMN_EMAIL_SENDER+" FROM "+TABLE_RECLAMATION + " WHERE "+
                                TABLE_COLUMN_ID + " = ?";
                        preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setInt(1,id);
                        resultSet = preparedStatement.executeQuery();
                        resultSet.next();
                        String recipient = resultSet.getString(TABLE_COLUMN_EMAIL_RECEPTEUR);
                        String date = resultSet.getTimestamp(TABLE_COLUMN_DATE).toString();
                        //
                        String sender = resultSet.getString(TABLE_COLUMN_EMAIL_SENDER).toString();
                        //send email
                        Util.sendEmail(recipient,"Une nouvelle reclamation","Vous avez une nouvelle reclamation déposée à "+
                               date + " par un de vos étudiants.");
                        //Send FeedBack to Student
                        Util.sendEmail(sender, "Etat de reclamation", "Vous avez déposée un reclamation success ");

                        disconnect();
                        return  response.toString();
                    }else{//invalid
                        response.put("response code", 1);
                        response.put("response message", "Reclamation transferée avec succès");
                        //get email
                        // query = "SELECT "+TABLE_COLUMN_EMAIL_RECEPTEUR+","+TABLE_COLUMN_DATE +" FROM "+TABLE_RECLAMATION + " WHERE "+
                        //  TABLE_COLUMN_ID + " = ?";
                        ////get email
                        query = "SELECT "+TABLE_COLUMN_EMAIL_RECEPTEUR+","+TABLE_COLUMN_DATE +" , "
                                +TABLE_COLUMN_EMAIL_SENDER+" FROM "+TABLE_RECLAMATION + " WHERE "+
                                TABLE_COLUMN_ID + " = ?";
                        preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setInt(1,id);
                        resultSet = preparedStatement.executeQuery();
                        resultSet.next();
                        String recipient = resultSet.getString(TABLE_COLUMN_EMAIL_RECEPTEUR);
                        String date = resultSet.getTimestamp(TABLE_COLUMN_DATE).toString();
                        //
                        String sender = resultSet.getString(TABLE_COLUMN_EMAIL_SENDER).toString();
                        //send email
                        Util.sendEmail(sender,"Etat reclamation","Cher Etudiant, \n Votre reclamation etait invalide et non transferable.\n Bon Journee  ");


                        disconnect();
                        return  response.toString();
                    }

                }

            }

            //invalid id
            response.put("response code",2);
            response.put("response message","ID Reclamation inconnu");
            disconnect();
            return  response.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            response.put("response code",0);
            response.put("response message","Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        disconnect();
        return  response.toString();


    }


    //Modify a Users Information
    public String modifierProfile(String oldEmail,String password,
                                  String firstname, String lastname, String address,
                                  String sex, String tel,int userType) {

        //Get Users Current Information
        String currentEmail = "";     String currentPassword= "";
        String currentName  = "";     String currentLastN= "";
        String currentAddress = "";    String currentSex= "";
        String currentTel= "";        String salt= "";

        //Pull Info from Database
        try {
            JSONObject user = fetchUserInfo(oldEmail,userType);
            //put in fields
            currentEmail   = user.getString("email");
            currentName    = user.getString("firstname");
            currentLastN   = user.getString("lastname");
            currentAddress = user.getString("address");
            currentSex     = user.getString("sex");
            currentTel     = user.getString("tel");
            //
            System.out.println("Modify "+user.toString());
            //Fetch Passwords and Salt from Login Table
            String query  = "SELECT * FROM "+TABLE_LOGIN+" WHERE "+TABLE_COLUMN_EMAIL+" = ? ";
            preparedStatement = null;
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,oldEmail);
            resultSet = null;
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            //
            salt = resultSet.getString(TABLE_COLUMN_SALT);
            currentPassword = resultSet.getString(TABLE_COLUMN_PASSWORD)+salt;
            resultSet.close();
            System.out.println(salt+"    "+currentPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //response object
        JSONObject response = new JSONObject();
                //query to update user info
        String query = "";
        if(userType == 2) {
            //Enseignant
            query = "UPDATE " + Enseignant.TABLE_ENSEIGNANT + " SET "
                    + Enseignant.TABLE_COLUMN_NOM + " = ? ,"
                    + Enseignant.TABLE_COLUMN_PRENOM + " = ? ," + Enseignant.TABLE_COLUMN_ADDR + " = ? ,"
                    + Enseignant.TABLE_COLUMN_SEXE + " = ? ," + Enseignant.TABLE_COLUMN_TEL + "= ?  WHERE "
                    + Enseignant.TABLE_COLUMN_EMAIL + " = ?";
        }else if(userType == 1){
            //Tuteur
            query = "UPDATE " + Tuteur.TABLE_TUTEUR + " SET "
                    + Enseignant.TABLE_COLUMN_NOM + " = ? ,"
                    + Enseignant.TABLE_COLUMN_PRENOM + " = ? ," + Enseignant.TABLE_COLUMN_ADDR + " = ? ,"
                    + Enseignant.TABLE_COLUMN_SEXE + " = ? ," + Enseignant.TABLE_COLUMN_TEL + "= ?  WHERE "
                    + Enseignant.TABLE_COLUMN_EMAIL + " = ?";

        }else if(userType == 3){
            //Agent
            query = "UPDATE " + Agent.TABLE_AGENT + " SET "
                    + Enseignant.TABLE_COLUMN_NOM + " = ? ,"
                    + Enseignant.TABLE_COLUMN_PRENOM + " = ? ," + Enseignant.TABLE_COLUMN_ADDR + " = ? ,"
                    + Enseignant.TABLE_COLUMN_SEXE + " = ? ," + Enseignant.TABLE_COLUMN_TEL + "= ?  WHERE "
                    + Enseignant.TABLE_COLUMN_EMAIL + " = ?";

        }else if(userType == 4){
            //Responsable
            query = "UPDATE " + Responsable.TABLE_RESPONSABLE + " SET "
                    + Enseignant.TABLE_COLUMN_NOM + " = ? ,"
                    + Enseignant.TABLE_COLUMN_PRENOM + " = ? ," + Enseignant.TABLE_COLUMN_ADDR + " = ? ,"
                    + Enseignant.TABLE_COLUMN_SEXE + " = ? ," + Enseignant.TABLE_COLUMN_TEL + "= ?  WHERE "
                    + Enseignant.TABLE_COLUMN_EMAIL + " = ?";

        }else if(userType == 0){
            //Etudiant
            query = "UPDATE " + Etudiant.TABLE_ETUDIANT + " SET "
                    + Enseignant.TABLE_COLUMN_NOM + " = ? ,"
                    + Enseignant.TABLE_COLUMN_PRENOM + " = ? ," + Enseignant.TABLE_COLUMN_ADDR + " = ? ,"
                    + Enseignant.TABLE_COLUMN_SEXE + " = ? ," + Enseignant.TABLE_COLUMN_TEL + "= ?  WHERE "
                    + Enseignant.TABLE_COLUMN_EMAIL + " = ?";

        }
        //
                try {
                    preparedStatement = null;
                    preparedStatement = connection.prepareStatement(query);
                    //Check for Added filleds
                    if (!(firstname.equals(""))) {
                        preparedStatement.setString(1, firstname);
                    } else {
                        preparedStatement.setString(1, currentName);
                    }
                    if (!(lastname.equals(""))) {
                        preparedStatement.setString(2, lastname);
                    } else {
                        preparedStatement.setString(2, currentLastN);
                    }
                    if (!(address.equals(""))) {
                        preparedStatement.setString(3, address);
                    } else {
                        preparedStatement.setString(3, currentAddress);
                    }
                    if (!(sex.equals(currentSex))) {
                        preparedStatement.setString(4, sex);
                    } else {
                        preparedStatement.setString(4, currentSex);
                    }
                    if (!(tel.equals(""))) {
                        preparedStatement.setString(5, tel);
                    } else {
                        preparedStatement.setString(5, currentTel);
                    }

                    //required old email
                    preparedStatement.setString(6, oldEmail);

                    int rows = preparedStatement.executeUpdate();

                    if( password != "") {

                        //Update Password in LOGIN TABLE IF NON EMPTY
                        int rowsLogin = updateLoginTable(currentEmail,password);
                        if(rowsLogin == 0 ){
                            rows = 0;
                        }
                    }


                    if (rows == 1 ) {
                        try {
                            response.put("response code", 1);
                            response.put("response message", "Profile a ete mise a jour!");
                            disconnect();
                            //
                            return response.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            response.put("response code", 0);
                            response.put("response message", "Erreur : Profile non mise a jour!");
                            disconnect();
                            //
                            return response.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

        try {
            response.put("response code",2);
            response.put("response message", "un erruer s'est produite");
            disconnect();
            //
            return response.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        disconnect();
        //
        return response.toString();
    }

    //Update the Login Table
    public int updateLoginTable(String email,String newpassword){
        int rows = 0;
        //query
        String queryLogin = "UPDATE " + TABLE_LOGIN + " SET "
                           +TABLE_COLUMN_PASSWORD+ " = ? ";
        queryLogin += ", "+ TABLE_COLUMN_SALT+ " = ? ";
        queryLogin += " WHERE " + TABLE_COLUMN_EMAIL + " = ? ";
        preparedStatement = null;

        try {
            int index = 0;

            // not empty
            if(!(newpassword.equals("")) && newpassword != null ) {
                preparedStatement = connection.prepareStatement(queryLogin);

                String[] arraysP  = Util.encryptPassword(newpassword);

                preparedStatement.setString(1, arraysP[0]);
                preparedStatement.setString(2, arraysP[1]);
                preparedStatement.setString(3, email);
                //
                rows = preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        return rows;
    }

    //Fetch Students Information
    public String fetchStudents(String emailUser,int type){
        JSONObject response = new JSONObject();


        //Get all student Who Belong users Theme Group
        String query = "";
        if(type == 2){
            System.out.println("ENS :"+type);
            query = "SELECT "+TABLE_COLUMN_EMAIL_ETUDIANT+" FROM "+TABLE_AFFECTATION+
                    " WHERE "+TABLE_COLUMN_EMAIL_ENSEIGNANT+" = ? ";
        }else if(type == 1){
            System.out.println("TUT :"+type);
            query = "SELECT "+TABLE_COLUMN_EMAIL_ETUDIANT+" FROM "+TABLE_AFFECTATION+
                    " WHERE "+TABLE_COLUMN_EMAIL_TUTEUR+" = ? ";
        }


        JSONArray students = new JSONArray();
        JSONObject student = null;
        try {
            preparedStatement = null;
            preparedStatement = connection.prepareStatement(query);
            resultSet = null;
            preparedStatement.setString(1,emailUser);
            resultSet = preparedStatement.executeQuery();

            //Get Student Info from Student Table
            String queryStudent = "SELECT "+User.COLUMN_NOM+","+User.COLUMN_PRENOM+" FROM "
                                    +Etudiant.TABLE_ETUDIANT+" WHERE "+TABLE_COLUMN_EMAIL
                                    +" = ? ";
            PreparedStatement prep = null;
            ResultSet rs = null;
            //Loop Through to Get Information
            int index = 0;

            while(resultSet.next()){
                student = new JSONObject();
                //
                String email = resultSet.getString(TABLE_COLUMN_EMAIL_ETUDIANT);
                student.put(TABLE_COLUMN_EMAIL_ETUDIANT, email);
                //Get name and lastname
                prep = connection.prepareStatement(queryStudent);
                prep.setString(1, email);
                //Execute
                rs = prep.executeQuery();
                if(rs.next()){
                    student.put(Etudiant.TABLE_COLUMN_NOM,rs.getString(Etudiant.TABLE_COLUMN_NOM));
                    student.put(Etudiant.TABLE_COLUMN_PRENOM,rs.getString(Etudiant.TABLE_COLUMN_PRENOM));
                }
                //
                students.put(index,student);
                index += 1;

            }
            rs.close();
            prep.close();

            //Add to response
            //create a response message
            response.put("response code",1);
            response.put("response message","Les etudiants ont été sélectionnés avec succès");
            response.put("students",students);
            disconnect();
            //send response back
            return response.toString();

        } catch(SQLException e){
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            response.put("response code",0);
            response.put("response message","Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnect();
        return response.toString();
    }

    //Fill State ( Etat ) Table for easy State Monitoring( Suivre Etat )
    public int fillEtat(int phase){
        //
        int res = -1;
        //Get students from Etat
        String studentQ = "SELECT "+TABLE_COLUMN_EMAIL_ETUDIANT+" FROM "+TABLE_AFFECTATION;
        String insertQ  = "INSERT INTO "+TABLE_ETAT+" ("+TABLE_COLUMN_EMAIL_ETUDIANT+" , "
                            +TABLE_COLUMN_NOTE_ENS+" , "+TABLE_COLUMN_NOTE_TUT+" , "+TABLE_COLUMN_ID_PHASE+" ) "
                            +" VALUES "+"(?,?,?,?)";

        String student = null;
        preparedStatement = null;

        try{
            PreparedStatement prepS = null;
            preparedStatement = connection.prepareStatement(studentQ);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){

                student = resultSet.getString(TABLE_COLUMN_EMAIL_ETUDIANT);
                //
                //Insert into ETAT
                prepS = connection.prepareStatement(insertQ);
                //
                prepS.setString(1,student);
                prepS.setDouble(2,0.0);
                prepS.setDouble(3,0.0);
                prepS.setInt(4,phase);
                //
                if(prepS.executeUpdate() > 0) {
                    res += 1;
                    prepS.close();
                }else{
                    break;
                }
                //
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
        //
        return res;
    }

    //Mentionner un note
    public String mentionnerNote(String emailEtudiant, double note,int userType){
        JSONObject response = new JSONObject();
        //Query to get current Phase
        int currentPhase = currentPhase();

        String query = null;
        if(currentPhase > 0) {
            //Stage is in Process
            //note_enseignant
            if (userType == 2) {

                query = "UPDATE " + TABLE_ETAT + " SET " + TABLE_COLUMN_NOTE_ENS + "= ? WHERE "
                        + TABLE_COLUMN_EMAIL_ETUDIANT + " = ? " + "AND " + TABLE_COLUMN_ID_PHASE + "= ?";

            } else {
                //note_tuteur
                query = "UPDATE " + TABLE_ETAT + " SET " + TABLE_COLUMN_NOTE_TUT + "= ? WHERE "
                        + TABLE_COLUMN_EMAIL_ETUDIANT + " = ? " + "AND " + TABLE_COLUMN_ID_PHASE + "= ?";
            }
            //
            try {
                preparedStatement = connection.prepareStatement(query);
                //
                preparedStatement.setDouble(1, note);
                preparedStatement.setString(2, emailEtudiant);
                preparedStatement.setInt(3, currentPhase);
                //
                int rows = preparedStatement.executeUpdate();
                System.out.println("result " + rows);
                if (rows == 1) {
                    try {
                        response.put("response code", 1);
                        response.put("response message", "Note Mentionner avec succes!");
                        disconnect();
                        //
                        return response.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        response.put("response code", 0);
                        response.put("response message", "Erreur : Note non mentionner!");
                        disconnect();
                        //
                        return response.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else {
            try {
                response.put("response code", 1);
                response.put("response message", "Erreur : Stage n'est pas commence !");
                disconnect();
                //
                return response.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        disconnect();
        //

        return response.toString();
    }

    //Get current Phase
    public int currentPhase(){
        int res = -1;
        //Query
        String query = "SELECT "+TABLE_COLUMN_NUMBER_PHASE+" FROM "+TABLE_PHASE
                        +" WHERE "+TABLE_COLUMN_PHASESTATE+" = ? ";

        preparedStatement = null;
        try{
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, 1); //Running State
            resultSet = null;
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                res = resultSet.getInt(TABLE_COLUMN_NUMBER_PHASE);
                return res;
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        return res;
    }

    //Launchh a Phase
    public String launchPhase(int phase,Date start, Date end){
        JSONObject response = new JSONObject();


        //Phase state ( 0 : Idle, 1: Running, 2: Destroyed )
        int state = -1;
        if(phase == 1){
            //Check state
            state = checkState(phase);

            //
            switch(state){
                //Idle
                case 0:
                        int result = launchPhase(phase,start,end,TABLE_PHASE_STATE_RUNNNING);
                        if(result == 1){
                            try {
                                response.put("response code", 1);
                                response.put("response message", "Phase 1 lancer avec succes!");
                                //Launch fillEtat to update States
                                int res = fillEtat(phase);
                                disconnect();
                                //
                                return response.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if(result == -1){
                            try {
                                response.put("response code", 0);
                                response.put("response message", "Erreur : Phase non lancer!");
                                disconnect();
                                //
                                return response.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                response.put("response code",2);
                                response.put("response message", "un erruer s'est produite");
                                disconnect();
                                //
                                return response.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        break;
                //Runnning
                case 1:
                        try {
                            response.put("response code",1);
                            response.put("response message", "Phase deja en cours!");
                            disconnect();
                            //
                            return response.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                //Ended
                case 2:
                        try {
                            response.put("response code",1);
                            response.put("response message", "Phase a deja Termine !");
                            disconnect();
                            //
                            return response.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

            }
        }else { //if(phase == 2)
            state = checkState(phase);
            switch(state){
                case 0: //Check if Phase 1 is still running
                        //if phase 1 state = 0; Abort! s
                        // tate = 1 ;check date(s);
                        // dates is ended; make phase one end!
                        int prevPhase = checkState((phase-1));
                        if(prevPhase == 0){
                            try {
                                response.put("response code",1);
                                response.put("response message",
                                        "On ne peut pas Lancer Phase "+phase+" avant "+(phase-1));
                                disconnect();
                                //
                                return response.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if(prevPhase == 1){
                            Date currentDate = new Date(dateNow().getTime());
                            Date phaseDate   = getPhaseDate((phase-1));
                            if(phaseDate != null){
                                if(currentDate.before(phaseDate)){
                                    try {
                                        response.put("response code",1);
                                        response.put("response message",
                                                "Non permit! Phase "+(phase-1)+" en cours! ");
                                        disconnect();
                                        //
                                        return response.toString();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    //End phase 1
                                    //Start phase 2
                                    int result = endPhase((phase-1));
                                    if(result == 1){
                                        //Start Phase 2
                                        int startPhase = launchPhase(phase,start,end,TABLE_PHASE_STATE_RUNNNING);
                                        if(startPhase == 1){
                                            try {
                                                response.put("response code", 1);
                                                response.put("response message", "Phase "+phase+" lancer avec succes!");
                                                //Launch fillEtat to update States
                                                int res = fillEtat(phase);
                                                disconnect();
                                                //
                                                return response.toString();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else if(startPhase == -1){
                                            try {
                                                response.put("response code", 0);
                                                response.put("response message", "Erreur : Phase "+phase+" non lancer!");
                                                disconnect();
                                                //
                                                return response.toString();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            try {
                                                response.put("response code",2);
                                                response.put("response message", "un erruer s'est produite");
                                                disconnect();
                                                //
                                                return response.toString();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                }
                            }else{
                                //Error retrieving phase date
                                try {
                                    response.put("response code",0);
                                    response.put("response message", "Erreur cote serveur! ");
                                    disconnect();
                                    //
                                    return response.toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }else if(prevPhase == 2) {
                            //Phase has Ended ! Launch
                            int result = launchPhase(phase, start, end, TABLE_PHASE_STATE_RUNNNING);
                            if (result == 1) {
                                try {
                                    response.put("response code", 1);
                                    response.put("response message", "Phase "+phase+" lancer avec succes!");
                                    //Launch fillEtat to update States
                                    int res = fillEtat(phase);
                                    disconnect();
                                    //
                                    return response.toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                case 1:
                        try {
                            response.put("response code",1);
                            response.put("response message", "Phase deja en cours!");
                            disconnect();
                            //
                            return response.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                case 2:
                    try {
                        response.put("response code",1);
                        response.put("response message", "Phase a deja Termine !");
                        disconnect();
                        //
                        return response.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }
        //End of phase tests
        try {
            response.put("response code",2);
            response.put("response message", "un erruer s'est produite");
            disconnect();
            //
            return response.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response.toString();
    }


    //Phase state ( 0 : Idle, 1: Running, 2: Destroyed )
    public int checkState(int phaseNo){
        String query = "SELECT "+TABLE_COLUMN_PHASESTATE
                +" FROM "+TABLE_PHASE+" WHERE "
                +TABLE_COLUMN_NUMBER_PHASE+"= ? ";
        int state = -1; //Default Error Signature
        //Assuming Phase number has been verified at CLIENT FRONT-END
        try {
            preparedStatement = null;
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, phaseNo);

            resultSet = null;
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                state = resultSet.getInt(TABLE_COLUMN_PHASESTATE);

                return state;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Send Status
        return state;
    }

    //End Phase
    public int endPhase(int phase){
        String query = "UPDATE "+TABLE_PHASE+" SET "+TABLE_COLUMN_PHASESTATE+" = ? "
                    +" WHERE "+TABLE_PHASE_ID+" = ? ";
        int result = -1;
        try{
            preparedStatement = null;
          preparedStatement = connection.prepareStatement(query);
          preparedStatement.setInt(1, TABLE_PHASE_STATE_ENDED);
          preparedStatement.setInt(2, phase);

          result = preparedStatement.executeUpdate();
          if(result == 1){

              return result;
          }

        } catch(SQLException e){
            e.printStackTrace();
        }
        //

        return result;
    }


    //End Phase ( Responsable )
    public String endPhaseNow(int phase){
        JSONObject response = new JSONObject();
        //
        String query = "UPDATE "+TABLE_PHASE+" SET "+TABLE_COLUMN_PHASESTATE+" = ? "
                +" WHERE "+TABLE_PHASE_ID+" = ? ";
        int result = -1;
        try{
            preparedStatement = null;
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, TABLE_PHASE_STATE_ENDED);
            preparedStatement.setInt(2, phase);

            result = preparedStatement.executeUpdate();
            if(result == 1){

                //Add to response
                //create a response message
                try {
                    response.put("response code", 1);
                    response.put("response message", "Le phase a ete Termine avec success ! ");

                    disconnect();
                    //send response back
                    return response.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        } catch(SQLException e){
            e.printStackTrace();
        }
        //
        try {
            response.put("response code", 0);
            response.put("response message","Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnect();

        return response.toString();



    }
    //
    public int launchPhase(int phase,Date start,Date end,int state){
        String query = "UPDATE "+TABLE_PHASE+" SET "+TABLE_COLUMN_START+"= ? ,"+
                TABLE_COLUMN_END+" = ? ,"+TABLE_COLUMN_PHASESTATE+" = ?  WHERE "
                +TABLE_PHASE_ID +" = ? ";

        int result = -1;
        try {
            preparedStatement = null;
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDate(1, start);
            preparedStatement.setDate(2, end);
            preparedStatement.setInt(3, state);
            preparedStatement.setInt(4,phase);

            result = preparedStatement.executeUpdate();
            //

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //
        return result;
    }

    //Current date
    public java.util.Date dateNow(){
        String day   = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
        String month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
        String year  = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        return (new java.util.Date(day+"/"+month+"/"+year));
    }

    //Phase Date
    public Date getPhaseDate(int phase){
        String query  = "SELECT "+TABLE_COLUMN_END+" FROM "+TABLE_PHASE
                        +" WHERE "+TABLE_COLUMN_NUMBER_PHASE+" = ?";
        try {
            preparedStatement = null;
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, phase);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                Date date = resultSet.getDate(TABLE_COLUMN_END);

                return date;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }

    //Get Etat of Students
    public String fetchEtat(String emailUser,int type) {
        JSONObject response = new JSONObject();
        JSONObject student  = null;
        JSONArray students  = new JSONArray();
        JSONArray phases    = null;
        JSONObject phase    = null;
        //
        String query = "";
        try{

        //Check type here
        if(type == 2) {
            //Get all student Who Belong users Theme Group
            query = "SELECT " + TABLE_COLUMN_EMAIL_ETUDIANT + " FROM " + TABLE_AFFECTATION +
                    " WHERE " + TABLE_COLUMN_EMAIL_ENSEIGNANT + " = ? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,emailUser);
            resultSet = preparedStatement.executeQuery();
        }else if(type == 1){
            query = "SELECT " + TABLE_COLUMN_EMAIL_ETUDIANT + " FROM " + TABLE_AFFECTATION +
                    " WHERE " + TABLE_COLUMN_EMAIL_TUTEUR + " = ? ";


            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,emailUser);
            resultSet = preparedStatement.executeQuery();
            //
        }else if(type == 4){
            query = "SELECT " + TABLE_COLUMN_EMAIL_ETUDIANT + " FROM " + TABLE_AFFECTATION;


            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            //
        }

            //Get student Names
            //Get Student Info from Student Table
            String queryStudent = "SELECT "+User.COLUMN_NOM+","+User.COLUMN_PRENOM+" FROM "
                    +Etudiant.TABLE_ETUDIANT+" WHERE "+TABLE_COLUMN_EMAIL
                    +" = ? ";

            //Get Etat of that Student
            String queryEtat = "SELECT "+TABLE_COLUMN_NOTE_ENS+","
                    +TABLE_COLUMN_NOTE_TUT +" FROM "
                    +TABLE_ETAT+" WHERE "+TABLE_COLUMN_EMAIL_ETUDIANT+" = ? AND "
                    +TABLE_COLUMN_ID_PHASE +" = ? ";
            //
            PreparedStatement prepEtat = null;
            ResultSet rsEtat = null;
            PreparedStatement prep = null;
            ResultSet rs = null;
            //Loop Through to Get Information
            int index = 0;

            while(resultSet.next()){
                student = new JSONObject();
                phases  = new JSONArray();
                //
                String email = resultSet.getString(TABLE_COLUMN_EMAIL_ETUDIANT);
                student.put(TABLE_COLUMN_EMAIL_ETUDIANT, email);
                //Get name and lastname
                prep = connection.prepareStatement(queryStudent);
                prep.setString(1, email);
                //Execute
                rs = prep.executeQuery();
                if(rs.next()){
                    student.put(Etudiant.TABLE_COLUMN_NOM,rs.getString(Etudiant.TABLE_COLUMN_NOM));
                    student.put(Etudiant.TABLE_COLUMN_PRENOM,rs.getString(Etudiant.TABLE_COLUMN_PRENOM));
                }
                //
                //Get Etat
                for(int i = 1; i <= 4 ; ++i) {
                    prepEtat = connection.prepareStatement(queryEtat);
                    prepEtat.setString(1, email);
                    prepEtat.setInt(2,i);
                    //
                    rsEtat = prepEtat.executeQuery();
                    //
                    if(rsEtat.next()){
                        phase = new JSONObject();
                        phase.put("note_ens",rsEtat.getDouble(TABLE_COLUMN_NOTE_ENS));
                        phase.put("note_tut",rsEtat.getDouble(TABLE_COLUMN_NOTE_TUT));
                        phases.put((i-1),phase);
                    }else{
                        //No other Phases : Stop!
                        break;
                    }
                    rsEtat.close();
                    prepEtat.close();
                }
                //Add phases to Student
                student.put("phases",phases);
                students.put(index, student);
                index += 1;

            }

            rs.close();
            prep.close();


            //
            //Add to response
            //create a response message
            response.put("response code", 1);
            response.put("response message", "Les etudiants ont été sélectionnés avec succès");
            response.put("students", students);
            disconnect();
            //send response back
            return response.toString();


        }catch(SQLException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            response.put("response code", 0);
            response.put("response message","Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnect();

        return response.toString();
    }

    //Empty deliberation Table
    public int emptyGrouping(){
        int result = -1;
        String query = " TRUNCATE "+TABLE_AFFECTATION;

        preparedStatement = null;

        //
        try {
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    //Lancher affectation
    public String launchGrouping(){
        JSONObject response = new JSONObject();
        //

        int trunc = emptyGrouping();
        int studentPerProf     = -1;
        int themesLimit        =  3;
        int theme1             = getThemes(1);
        int theme2             = getThemes(2);
        int theme3             = getThemes(3);
        try {
        if(!checkChoix()){

            response.put("response code",1);
            response.put("response message","Affectation non permit! Pas de themes choisit  ");

            disconnect();
            //send response back
            return response.toString();
        }


        //Stage 1: Get Ratio
        studentPerProf = students()/professors();

        //Stage 2 : Give profs a Theme to Manage Each
        int resultStage2 = affectThemesProfs(theme1,theme2,theme3);

        //Stage 3 : Affect Students to Profs with theme choices
        int resultStage3 = affectStudentsToThemes(theme1,theme2,theme3,studentPerProf);

        //Stage 4 : Launch Affectation
        String query = "INSERT INTO "+TABLE_AFFECTATION+" ( "+TABLE_COLUMN_EMAIL_ETUDIANT+" , "
                +TABLE_COLUMN_EMAIL_ENSEIGNANT+" , "+TABLE_COLUMN_EMAIL_TUTEUR+" , "+TABLE_COLUMN_ID_THEME
                +") VALUES (?,?,?,?) ";

            //
            int result = -1;
            for (int i = 0; i < affecterMain.size(); i++) {
                preparedStatement = null;
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1,affecterMain.get(i).student);
                preparedStatement.setString(2,affecterMain.get(i).enseignant);
                preparedStatement.setString(3, affecterMain.get(i).tuteur);
                preparedStatement.setInt(4, affecterMain.get(i).theme);
                result = preparedStatement.executeUpdate();
            }
            if(result == 1){
                response.put("response code",1);
                response.put("response message","Affectation reussit! ");

                disconnect();
                //send response back
                return response.toString();
            }else{
                response.put("response code",2);
                response.put("response message","Affectation a re-contre un erruer ");

                disconnect();
                //send response back
                return response.toString();
            }

        }catch(SQLException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //End
        try {
            response.put("response code", 0);
            response.put("response message","Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnect();
        //
        return response.toString();
    }

    //affect students to profs with themes
    public int affectStudentsToThemes(int t1,int t2,int t3,int maxStudents){
        //
        int result = -1;
        String query = "SELECT "+TABLE_COLUMN_EMAIL_ETUDIANT+" , "+TABLE_COLUMN_CHOIX_1
                +" , "+TABLE_COLUMN_CHOIX_2+" , "+TABLE_COLUMN_CHOIX_3+" FROM "+
                TABLE_CHOIX;
        //
        int choix1 = -1;
        int choix2 = -1;
        int choix3 = -1;
        preparedStatement = null;
        ResultSet rs = null;
        String student =null;
        try{

            preparedStatement = connection.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            //
            int index = 0;
            while(index < affecter.size()){
                int theme  = affecter.get(index).theme;
                String enseignant = affecter.get(index).enseignant;
                String tuteur     = affecter.get(index).tuteur;
                //Add to mainAffecter List
                for(int i = 0; i < maxStudents; ++i){
                    if(rs.next()) {
                        student = rs.getString(TABLE_COLUMN_EMAIL_ETUDIANT);
                        choix1 = rs.getInt(TABLE_COLUMN_CHOIX_1);
                        choix2 = rs.getInt(TABLE_COLUMN_CHOIX_2);
                        choix3 = rs.getInt(TABLE_COLUMN_CHOIX_2);
                    }
                    //Add first come first serve politique for affecting themes
                    if(theme == choix1 ){
                        affecterMain.add(new Affectation(enseignant,tuteur,student,theme));
                    }else if(theme == choix2 ){
                        affecterMain.add(new Affectation(enseignant,tuteur,student,theme));
                    }else{
                        affecterMain.add(new Affectation(enseignant,tuteur,student,theme));
                    }
                }
                index++;
            }
            //Everything went well
            rs.close();
            return 1;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }

    //get students
    public int students(){
        int result = -1;
        //Query
        String query = "SELECT COUNT(*) FROM "+TABLE_CHOIX;
        ResultSet rs = null;
        try {
            preparedStatement = null;
            preparedStatement = connection.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            //
            if(rs.next()){
                result = rs.getInt("COUNT(*)");

                rs.close();
                return result;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }

    //get profs
    public int professors() {
        int result = -1;
        //Query
        String query = "SELECT COUNT(*) FROM "+Enseignant.TABLE_ENSEIGNANT;
        ResultSet rs = null;
        try {
            preparedStatement = null;
            preparedStatement = connection.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            //
            if(rs.next()){
                result = rs.getInt("COUNT(*)");
                rs.close();
                return result;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }

    //Process themes to Profs
    public int affectThemesProfs(int t1,int t2,int t3){
        int result = -1;
        //Get profs emails
        String query   = "SELECT "+TABLE_COLUMN_EMAIL+" FROM "+Enseignant.TABLE_ENSEIGNANT;
        String tuteurs = "SELECT "+TABLE_COLUMN_EMAIL+" FROM "+Tuteur.TABLE_TUTEUR;
        String prof    = null;
        String tuteur  = null;
        //
        int indicator = 1;
        preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            //
            ResultSet rs = preparedStatement.executeQuery();
            //
            //Tutuers
            PreparedStatement pt = null;
            ResultSet rsTut      = null;
            pt = connection.prepareStatement(tuteurs);
            rsTut = pt.executeQuery();

            while(rs.next()) {
                //Get each profs name
                prof = rs.getString(TABLE_COLUMN_EMAIL);
                if(rsTut.next()){
                    tuteur = rsTut.getString(TABLE_COLUMN_EMAIL);
                }else{
                    //tutuers are done re-execute
                    rsTut = null;
                    rsTut = pt.executeQuery();
                    tuteur = rsTut.getString(TABLE_COLUMN_EMAIL);
                }
                //Place in Array
                if(indicator == 1) {
                    affecter.add(new Affectation(prof,tuteur, "", t1));
                    indicator++;
                }else if(indicator == 2) {
                    affecter.add(new Affectation(prof, tuteur, "", t2));
                    indicator++;
                }else {
                    affecter.add(new Affectation(prof, tuteur, "", t3));
                    indicator = 1;
                }
            }
            //Everything was okay
            rs.close();
            result = 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //
        return result;
    }

    //
    public int getThemes(int number){
        int result = -1;
        //Query
        String query = "SELECT "+TABLE_COLUMN_ID+" FROM "+TABLE_THEME+" LIMIT 3";
        ResultSet rs = null;
        try {
            preparedStatement = null;
            preparedStatement = connection.prepareStatement(query);
            rs = preparedStatement.executeQuery();
            //
            if(number == 1) {
                if (rs.next()) {
                    result = rs.getInt(TABLE_COLUMN_ID);
                }
            }else if(number == 2){
                rs.next();
                if(rs.next()){
                    result = rs.getInt(TABLE_COLUMN_ID);
                }
            }else{
                rs.last();

                result = rs.getInt(TABLE_COLUMN_ID);

            }
            rs.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return result;
    }

    //All Students States
    public String listStudents(){
        JSONObject response = new JSONObject();
        JSONObject student  = null;
        JSONArray students  = new JSONArray();
        JSONArray phases    = null;
        JSONObject phase    = null;
        //
        String query = "SELECT " + TABLE_COLUMN_EMAIL_ETUDIANT + " FROM " + TABLE_AFFECTATION;

        //
        try{
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            //Get student Names
            //Get Student Info from Student Table
            String queryStudent = "SELECT "+User.COLUMN_NOM+","+User.COLUMN_PRENOM+" FROM "
                    +Etudiant.TABLE_ETUDIANT+" WHERE "+TABLE_COLUMN_EMAIL
                    +" = ? ";

            //Get Etat of that Student
            String queryEtat = "SELECT "+TABLE_COLUMN_NOTE_ENS+","
                    +TABLE_COLUMN_NOTE_TUT +" FROM "
                    +TABLE_ETAT+" WHERE "+TABLE_COLUMN_EMAIL_ETUDIANT+" = ? AND "
                    +TABLE_COLUMN_ID_PHASE +" = ? ";
            //
            PreparedStatement prepEtat = null;
            ResultSet rsEtat = null;
            PreparedStatement prep = null;
            ResultSet rs = null;
            //Loop Through to Get Information
            int index = 0;

            while(resultSet.next()){
                student = new JSONObject();
                phases  = new JSONArray();
                //
                String email = resultSet.getString(TABLE_COLUMN_EMAIL_ETUDIANT);
                student.put(TABLE_COLUMN_EMAIL_ETUDIANT, email);
                //Get name and lastname
                prep = connection.prepareStatement(queryStudent);
                prep.setString(1, email);
                //Execute
                rs = prep.executeQuery();
                if(rs.next()){
                    student.put(Etudiant.TABLE_COLUMN_NOM,rs.getString(Etudiant.TABLE_COLUMN_NOM));
                    student.put(Etudiant.TABLE_COLUMN_PRENOM,rs.getString(Etudiant.TABLE_COLUMN_PRENOM));
                }
                //
                //Get Etat
                for(int i = 1; i <= 4 ; ++i) {
                    prepEtat = connection.prepareStatement(queryEtat);
                    prepEtat.setString(1, email);
                    prepEtat.setInt(2,i);
                    //
                    rsEtat = prepEtat.executeQuery();
                    //
                    if(rsEtat.next()){
                        phase = new JSONObject();
                        phase.put("note_ens",rsEtat.getDouble(TABLE_COLUMN_NOTE_ENS));
                        phase.put("note_tut",rsEtat.getDouble(TABLE_COLUMN_NOTE_TUT));
                        phases.put((i-1),phase);
                    }else{
                        //No other Phases : Stop!
                        break;
                    }
                    rsEtat.close();
                    prepEtat.close();
                }
                //Add phases to Student
                student.put("phases",phases);
                students.put(index, student);
                index += 1;

            }

            rs.close();
            prep.close();


            //
            //Add to response
            //create a response message
            response.put("response code", 1);
            response.put("response message", "Les etudiants ont été sélectionnés avec succès");
            response.put("students", students);
            disconnect();
            //send response back
            return response.toString();


        }catch(SQLException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            response.put("response code", 0);
            response.put("response message", "Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnect();

        return response.toString();
    }

    //Consult Student AgentS
    public String consult(String email){
        JSONObject response = new JSONObject();
        JSONObject student  = new JSONObject();

        String query = "SELECT "+Etudiant.TABLE_COLUMN_NOM+" , "+Etudiant.TABLE_COLUMN_PRENOM+" , "
                    +Etudiant.TABLE_COLUMN_ADDR+" ,"+Etudiant.TABLE_COLUMN_DATE_N+" ,"+Etudiant.TABLE_COLUMN_LIEU+" FROM "+Etudiant.TABLE_ETUDIANT+" WHERE "
                +Etudiant.TABLE_COLUMN_EMAIL+" = ? ";

        preparedStatement = null;
        resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,email);
            resultSet = preparedStatement.executeQuery();
            //
            while(resultSet.next()){
                student.put("lastname",resultSet.getString(Etudiant.TABLE_COLUMN_NOM));
                student.put("firstname",resultSet.getString(Etudiant.TABLE_COLUMN_PRENOM));
                student.put("address",resultSet.getString(Etudiant.TABLE_COLUMN_ADDR));
                student.put("date",resultSet.getDate(Etudiant.TABLE_COLUMN_DATE_N));
                student.put("lieu", resultSet.getString(Etudiant.TABLE_COLUMN_LIEU));
            }
            response.put("response code", 1);
            response.put("response message", "l'etudiant ont été sélectionnés avec succès");
            response.put("student", student);
            disconnect();
            //send response back
            return response.toString();



        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            response.put("response code", 0);
            response.put("response message", "Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //
        disconnect();
        return response.toString();
    }

    //Empty deliberation Table
    public int emptyDeliberation(){
        int result = -1;
        String query = " TRUNCATE "+TABLE_DELIBERATION;

        preparedStatement = null;

        //
        try {
            preparedStatement = connection.prepareStatement(query);
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    //Launch Deliberation
    public String deliberation(){
        JSONObject response = new JSONObject();
        JSONArray  students = null;
        JSONObject studentInfo  = null;
        //Calculate all the students final Grade
        //Check if Phase 4 is done to launch successfully
        //Empty the table if it has info
        int truncate = emptyDeliberation();

        int phaseQuatre = checkState(4);
        try {
            if (phaseQuatre == 2) {
                //Phase 4 has Ended
                //Stage 1: Get Students_Names

                Deliberation student = null;

                String query = " SELECT "+TABLE_COLUMN_EMAIL_ETUDIANT+" FROM "
                        +TABLE_AFFECTATION;
                //
                String queryEtat = " SELECT "+TABLE_COLUMN_NOTE_ENS+" , "
                        +TABLE_COLUMN_NOTE_TUT+" FROM "
                        +TABLE_ETAT+" WHERE "+TABLE_COLUMN_EMAIL_ETUDIANT+" = ? AND "+TABLE_COLUMN_ID_PHASE+" = ? ";
                //
                String queryStudent = "SELECT "+User.COLUMN_NOM+" , "+User.COLUMN_PRENOM
                        +" FROM "+Etudiant.TABLE_ETUDIANT+" WHERE "+User.COLUMN_EMAIL+" = ? ";

                preparedStatement = null;
                resultSet = null;
                PreparedStatement prepStudent = null;
                ResultSet rs = null;
                //
                PreparedStatement prepEtat = null;
                ResultSet rsEtat = null;
                //
                String email = null;
                try {
                    preparedStatement = connection.prepareStatement(query);
                    resultSet = preparedStatement.executeQuery();
                    //

                    while(resultSet.next()){
                        student = new Deliberation();
                        //
                        prepStudent = connection.prepareStatement(queryStudent);
                        email = resultSet.getString(TABLE_COLUMN_EMAIL_ETUDIANT);
                        prepStudent.setString(1,email);
                        rs = prepStudent.executeQuery();
                        //Get Student Info
                        if(rs.next()){
                            student.nom    = rs.getString(User.COLUMN_NOM);
                            student.prenom = rs.getString(User.COLUMN_PRENOM);
                        }
                        //Get Note 1 and 2 for Every Phase ! (1-4)
                        for(int i = 1; i <= 4; ++i){
                            prepEtat = connection.prepareStatement(queryEtat);
                            prepEtat.setString(1,email);
                            prepEtat.setInt(2,i);
                            rsEtat   = prepEtat.executeQuery();
                            //
                            if(rsEtat.next()){
                                double note1 = rsEtat.getDouble(TABLE_COLUMN_NOTE_ENS);
                                double note2 = rsEtat.getDouble(TABLE_COLUMN_NOTE_TUT);
                                //
                                switch(i){
                                    case 1: student.phase1 = ((note1+note2)/2);
                                            break;
                                    case 2: student.phase2 = ((note1+note2)/2);
                                            break;

                                    case 3: student.phase3 = ((note1+note2)/2);
                                            break;
                                    case 4: student.phase4 = ((note1+note2)/2);
                                            break;
                                }
                            }
                            //

                            prepEtat.close();
                            rsEtat.close();
                        }
                        delibList.add(student);
                        //
                        prepStudent.close();
                        rs.close();
                    }
                    //
                    //Insert into Deliberation
                    calculateMoyenne();

                    //
                    String queryFinal = "INSERT INTO "+TABLE_DELIBERATION+" ( "+User.COLUMN_NOM+" , "
                            +User.COLUMN_PRENOM+" , "+COLUMN_PHASE_1+" , "+COLUMN_PHASE_2+" , "+COLUMN_PHASE_3+" , "
                            +COLUMN_PHASE_4+" , "+COLUMN_MOYENNE+" , "+COLUMN_DECISION
                            +") VALUES (?,?,?,?,?,?,?,?) ";
                    //


                    //LOOP THROUGH TO INSERT
                    int result = -1;
                    int index = 0;
                    while(index < delibList.size()){
                        preparedStatement = null;
                        preparedStatement = connection.prepareStatement(queryFinal);
                        preparedStatement.setString(1, delibList.get(index).nom);
                        preparedStatement.setString(2, delibList.get(index).prenom);
                        preparedStatement.setDouble(3, delibList.get(index).phase1);
                        preparedStatement.setDouble(4, delibList.get(index).phase2);
                        preparedStatement.setDouble(5, delibList.get(index).phase3);
                        preparedStatement.setDouble(6, delibList.get(index).phase4);
                        preparedStatement.setDouble(7, delibList.get(index).moyenne);
                        preparedStatement.setInt(8, delibList.get(index).decision);
                        result = preparedStatement.executeUpdate();
                        index++;
                    }
                    if(result == 1){
                        //Send all information for Printing
                        int loc = 0;
                        students = new JSONArray();
                        while(loc < delibList.size()){
                            studentInfo = new JSONObject();
                            int decision = delibList.get(loc).decision;
                            studentInfo.put("nom",delibList.get(loc).nom);
                            studentInfo.put("prenom",delibList.get(loc).prenom);
                            studentInfo.put("phase1",delibList.get(loc).phase1);
                            studentInfo.put("phase2",delibList.get(loc).phase2);
                            studentInfo.put("phase3",delibList.get(loc).phase3);
                            studentInfo.put("phase4",delibList.get(loc).phase4);
                            studentInfo.put("moy"   ,delibList.get(loc).moyenne);
                            if(decision == 1)
                                studentInfo.put("decision","Admis");
                            else
                                studentInfo.put("decision","Ajrne");
                            //
                            students.put(loc,studentInfo);
                            loc++;
                        }

                        //
                        response.put("response code",1);
                        response.put("response message","Deliberation reussit! ");
                        response.put("students",students);

                        disconnect();
                        //send response back
                        return response.toString();
                    }else{
                        response.put("response code",2);
                        response.put("response message","Deliberation a re-contre un erruer ");

                        disconnect();
                        //send response back
                        return response.toString();
                    }
                    //
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //
            } else {
                response.put("response code", 1);
                response.put("response message", " Deliberation non permit, phase 4 n'a pas termine! ");
                disconnect();
                //send response back
                return response.toString();
            }
        }catch(JSONException e){
           e.printStackTrace();
        }
        try {
            response.put("response code", 0);
            response.put("response message", "Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //
        disconnect();
        return response.toString();
    }

    //Deliberation Moyenne
    public void calculateMoyenne() {

        int index = 0;
        while (index < delibList.size()) {
            double p1 = delibList.get(index).phase1;
            double p2 = delibList.get(index).phase2;
            double p3 = delibList.get(index).phase3;
            double p4 = delibList.get(index).phase4;

            double moy = (p1 + p2 + p3 + p4) / 4;
            delibList.get(index).moyenne = moy;

            //Decision
            if (moy >= 10.0)
                delibList.get(index).decision = 1;

            index++;
        }
    }

    public String chooseThemes(String email, int choix1, int choix2, int choix3) {

        JSONObject response = new JSONObject();

        try{
            String query = "INSERT INTO "+TABLE_CHOIX+" ( "+TABLE_COLUMN_EMAIL_ETUDIANT+" , "
                    +TABLE_COLUMN_CHOIX_1+" , "+TABLE_COLUMN_CHOIX_2+" , "+TABLE_COLUMN_CHOIX_3+" ) "+
                    " VALUES (?,?,?,?)";
            String queryUpdate = "UPDATE "+TABLE_CHOIX+" SET "+TABLE_COLUMN_CHOIX_1+" = ? ,"
                    +TABLE_COLUMN_CHOIX_2+" = ? ,"+TABLE_COLUMN_CHOIX_3+" = ? WHERE "
                    +TABLE_COLUMN_EMAIL_ETUDIANT+" = ? ";

            //Check if email already exist in Table
            String queryEmail = " SELECT COUNT(*) FROM "+TABLE_CHOIX+" WHERE "
                    +TABLE_COLUMN_EMAIL_ETUDIANT+" = ?";
            //

            if(valueExists(email,queryEmail)){
                //Update only because he exists
                if(checkAffectation()){
                    response.put("response code",2);
                    response.put("response message","Delai de changer themes a termine !");

                    disconnect();
                    //send response back
                    return response.toString();
                }else{
                    preparedStatement = null;

                    preparedStatement = connection.prepareStatement(queryUpdate);
                    preparedStatement.setInt(1,choix1);
                    preparedStatement.setInt(2,choix2);
                    preparedStatement.setInt(3,choix3);
                    preparedStatement.setString(4,email);
                    if(( preparedStatement.executeUpdate()) == 1){
                        response.put("response code",1);
                        response.put("response message","Votres choix des themes ont éte deposé , Merci ! ");
                        disconnect();
                        //send response back
                        return response.toString();
                    }else{
                        response.put("response code",2);
                        response.put("response message","Themes non choisit! Erreur! ");

                        disconnect();
                        return response.toString();
                    }
                }
            }else{
                //Student has not choosen themes yet ;
                if(checkAffectation()){
                    response.put("response code",2);
                    response.put("response message","Delai de choisir themes a termine !");

                    disconnect();
                    //send response back
                    return response.toString();
                }else{
                    preparedStatement = null;

                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1,email);
                    preparedStatement.setInt(2,choix1);
                    preparedStatement.setInt(3,choix2);
                    preparedStatement.setInt(4,choix3);

                    if(( preparedStatement.executeUpdate()) == 1){
                        response.put("response code",1);
                        response.put("response message","Votres choix des themes ont éte deposé , Merci ! ");
                        disconnect();
                        //send response back
                        return response.toString();
                    }else{
                        response.put("response code",2);
                        response.put("response message","Themes non choisit! Erreur! ");

                        disconnect();
                        return response.toString();
                    }
                }
            }

        } catch(JSONException e){
            e.printStackTrace();
        } catch(SQLException e){
            e.printStackTrace();
        }
        try {
            response.put("response code", 0);
            response.put("response message", "Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnect();

        return response.toString();
    }

    //Check count
    public boolean checkThemes(){

        boolean bool = false;
        String query = "SELECT COUNT(*) FROM "+Database.TABLE_THEME;
        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();//move to first row
            if(resultSet.getInt("COUNT(*)") >= 3){
                bool = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bool;

    }
    //Check Choix
    public boolean checkChoix(){
        boolean bool = false;
        String query = "SELECT COUNT(*) FROM "+Database.TABLE_CHOIX;
        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();//move to first row
            if(resultSet.getInt("COUNT(*)") >= 1){
                bool = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bool;







    }
    //Check Themes
    public boolean checkAffectation(){

        boolean bool = false;
        String query = "SELECT COUNT(*) FROM "+TABLE_AFFECTATION;
        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();//move to first row
            if(resultSet.getInt("COUNT(*)") >= 1){
                bool = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bool;

    }
    //Get themes for Student
    public String fetchThemes() {

        JSONObject response = new JSONObject();
        JSONArray  themes   = null;
        JSONObject theme    = null;
        //

        try{
            //Check if themes exist to list
         if(checkThemes()){
            //Get themes
            String query = "SELECT "+TABLE_COLUMN_ID+" , "+TABLE_COLUMN_TITLE
                    +" , "+TABLE_COLUMN_DESCRIPTION+" FROM "+TABLE_THEME+" LIMIT 3";
            preparedStatement = null;
            resultSet = null;
            preparedStatement =  connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            int i = 0;
            themes = new JSONArray();
            while(resultSet.next()){

                theme = new JSONObject();
                theme.put("id",resultSet.getInt(TABLE_COLUMN_ID));
                theme.put("titre",resultSet.getString(TABLE_COLUMN_TITLE));
                theme.put("description",resultSet.getString(TABLE_COLUMN_DESCRIPTION));
                //
                //Add to Array
                themes.put(i,theme);
                i++;
            }
            //Send Response
             response.put("response code", 1);
             response.put("response message", "List de themes !");
             response.put("themes",themes);
             disconnect();

             return response.toString();
         }else{
             response.put("response code", 0);
             response.put("response message", "Pas de Themes a choisir, Patientez! ");
             disconnect();

             return response.toString();
         }
        } catch(SQLException e){
            e.printStackTrace();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            response.put("response code", 0);
            response.put("response message", "Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        disconnect();

        return response.toString();

    }

    //Get State of Student
    public String consultStudent(String email){
        JSONObject response = new JSONObject();
        JSONObject student  = null;
        JSONArray phases    = null;
        //
        try{
            String queryEtudiant = "SELECT COUNT(*) FROM "+TABLE_ETAT+" WHERE "
                    +TABLE_COLUMN_EMAIL_ETUDIANT+" = ? ";

            String queryEtat = "SELECT "+TABLE_COLUMN_ID+" , "+TABLE_COLUMN_NOTE_ENS+" , "
                    +TABLE_COLUMN_NOTE_TUT+" FROM "
                    +TABLE_ETAT+" WHERE "+TABLE_COLUMN_EMAIL_ETUDIANT+" = ? AND "
                    +TABLE_COLUMN_ID_PHASE+" = ? ";
            String queryProfs = "SELECT "+TABLE_COLUMN_EMAIL_ENSEIGNANT+" , "+TABLE_COLUMN_EMAIL_TUTEUR+" FROM "+TABLE_AFFECTATION+" WHERE "
                    +TABLE_COLUMN_EMAIL_ETUDIANT+" = ? ";

            //Check if student actually exists
            if(valueExists(email,queryEtudiant)){
                //He/She exists..... Get STATE
                System.out.println("reached!!! ");
                phases  = new JSONArray();

                //Loop 4 times for each State
                //Get prof and tutuer
                preparedStatement = null;
                preparedStatement = connection.prepareStatement(queryProfs);
                preparedStatement.setString(1, email);
                resultSet = preparedStatement.executeQuery();

                if(resultSet.next()){
                    response.put("email_enseignant", resultSet.getString(TABLE_COLUMN_EMAIL_ENSEIGNANT));
                    response.put("email_tuteur", resultSet.getString(TABLE_COLUMN_EMAIL_TUTEUR));
                }
                resultSet.close();
                preparedStatement.close();
                //
                int i = 0;
                while(i < 4) {
                    student = new JSONObject();
                    preparedStatement = null;
                    resultSet = null;
                    preparedStatement = connection.prepareStatement(queryEtat);
                    preparedStatement.setString(1, email);
                    preparedStatement.setInt(2,(i+1));
                    //
                    resultSet = preparedStatement.executeQuery();
                    //
                    if(resultSet.next()){
                        student.put("id_etat",resultSet.getInt(TABLE_COLUMN_ID));
                        student.put("note_ens",resultSet.getDouble(TABLE_COLUMN_NOTE_ENS));
                        student.put("note_tut",resultSet.getDouble(TABLE_COLUMN_NOTE_TUT));
                        phases.put(i,student);
                    }else{
                        //Break ; no more information
                        break;
                    }

                    i++;
                    resultSet.close();
                    preparedStatement.close();
                }

                //Everything went okay
                //Send Response
                response.put("response code", 1);
                response.put("response message", " Etat recupére avec success !");
                response.put("phases",phases);
                disconnect();

                return response.toString();
            }else{
                response.put("response code", 0);
                response.put("response message", "Etat non disponible ; Stage (Phases) non commence ");
                disconnect();

                return response.toString();
            }

        } catch(SQLException e){

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //
        try {
            response.put("response code", 2);
            response.put("response message", "Une erreur s'est produite au côté serveur");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        disconnect();
        return response.toString();
    }
//End of class
}
