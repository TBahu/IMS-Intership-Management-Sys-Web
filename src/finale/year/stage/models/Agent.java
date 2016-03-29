package finale.year.stage.models;

import finale.year.stage.database.Database;

/**
 * Created by newton on 4/29/15.
 */
public class Agent extends User {

    public static final String TABLE_AGENT = "agent";

    //Mentionner un note
    public static String scolarite(String emailEtudiant){
        Database databasehandle = Database.connect();
        return databasehandle.consult(emailEtudiant);
    }
}
