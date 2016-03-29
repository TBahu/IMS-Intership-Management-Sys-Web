package finale.year.stage.models;

import finale.year.stage.database.Database;

/**
 * Created by newton on 4/29/15.
 */
public class Etudiant  extends User {

    //Table Specifics
    public static final String TABLE_ETUDIANT      = "etudiant";
    public static final String TABLE_COLUMN_NOM    = "nom";
    public static final String TABLE_COLUMN_PRENOM = "prenom";
    public static final String TABLE_COLUMN_SEXE   = "sexe";
    public static final String TABLE_COLUMN_EMAIL  = "email";
    public static final String TABLE_COLUMN_ADDR   = "adresse";
    public static final String TABLE_COLUMN_TEL    = "tel";
    public static final String TABLE_COLUMN_DATE_N = "date_naissance";
    public static final String TABLE_COLUMN_LIEU   = "lieu_naissance";
    public static final String TABLE_COLUMN_VILLE  = "ville";
    public static final String TABLE_COLUMN_NAT    = "nationalite";
    public static final String TABLE_COLUMN_FORM   = "formation";
    public static final String TABLE_COLUMN_SPEC   = "specialite";

    public static String submitReclamation(int idEtat, String description, String emailRecepteur,String senderEmail){
        Database databaseHandle = Database.connect();
        return  databaseHandle.submitReclamation(idEtat,description,emailRecepteur,senderEmail);
    }

    public static String choisirThemes(String email,int choix1,int choix2,int choix3){
        Database databaseHandle = Database.connect();
        return  databaseHandle.chooseThemes(email, choix1, choix2, choix3);
    }

    public static String list(){
        Database databaseHandle = Database.connect();
        return  databaseHandle.fetchThemes();
    }

    public static String consult(String email){
        Database databaseHandle = Database.connect();
        return  databaseHandle.consultStudent(email);
    }
}
