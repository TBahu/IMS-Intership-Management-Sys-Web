package finale.year.stage.models;

import finale.year.stage.database.Database;
import org.json.JSONObject;

/**
 * Created by newton on 4/29/15.
 */
public class User {

    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_PRENOM = "prenom";
    public static final String COLUMN_SEXE = "sexe";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_ADRESSE = "adresse";
    public static final String COLUMN_TEL = "tel";


    public static String register(String id, String email, String password){
        Database databaseHandle = Database.connect();
        return databaseHandle.register(id,email,password);
    }

    public static String login(String email, String password){
        Database databaseHandle = Database.connect();
        return databaseHandle.login(email,password);
    }

    public static String recoverPassword(String email){
        Database databaseHandle = Database.connect();
        return databaseHandle.recoverPassword(email);
    }


    public static String changePassword(String email,String code, String password){
        Database databaseHandle = Database.connect();
        return databaseHandle.changePassword(email,code,password);
    }

    public static String listThemes(){
        Database databaseHandle = Database.connect();
        return databaseHandle.listThemes();
    }
    public static String deleteTheme(int id){
        Database databaseHandle = Database.connect();
        return databaseHandle.deleteTheme(id);
    }
    public static String addTheme(String theme){
        Database databaseHandle = Database.connect();
        return databaseHandle.addTheme(theme);
    }

    public static String modifyTheme(String theme){
        Database databaseHandle = Database.connect();
        return databaseHandle.modifyTheme(theme);
    }

    public static String listReclamation(){
        Database databaseHandle = Database.connect();
        return databaseHandle.listReclamation();
    }

    public static String transferReclamation(int id,int state){
        Database databaseHandle = Database.connect();
        return databaseHandle.transferReclamation(id,state);
    }
    //
    //Get Students Etat
    public static String fetchEtat(String emailUser,int type){
        Database databaseHandle = Database.connect();
        return databaseHandle.fetchEtat(emailUser,type);
    }



}
