package finale.year.stage.models;

import finale.year.stage.database.Database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by newton on 4/29/15.
 */
public class Enseignant extends Tuteur {

    public static final String TABLE_ENSEIGNANT = "enseignant";
    public static final String TABLE_COLUMN_NOM    = "nom";
    public static final String TABLE_COLUMN_PRENOM = "prenom";
    public static final String TABLE_COLUMN_SEXE   = "sexe";
    public static final String TABLE_COLUMN_EMAIL  = "email";
    public static final String TABLE_COLUMN_ADDR   = "adresse";
    public static final String TABLE_COLUMN_TEL    = "tel";

    public static String consulterReclamations(String email){
        Database databaseHandle = Database.connect();
        return  databaseHandle.consulterReclamations(email);
    }

    //Created by Philip Chicco

    public static String modifierProfile(String oldEmail,String password,String firstname,
                                         String lastname,String address,String sex,String tel,int type){
        Database databaseHandle = Database.connect();
        return  databaseHandle.modifierProfile(oldEmail,password,firstname,lastname,address,sex,tel,type);
    }

    public static String fetchStudents(String emailUser,int type){
        Database databasehandle = Database.connect();
        return databasehandle.fetchStudents(emailUser, type);
    }

    //Mentionner un note
    public static String mentionnerNote(String emailEtudiant,double note,int type){
        Database databasehandle = Database.connect();
        return databasehandle.mentionnerNote(emailEtudiant,note,type);
    }
}
