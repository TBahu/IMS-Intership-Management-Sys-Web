package finale.year.stage.models;

import finale.year.stage.database.Database;

/**
 * Created by newton on 4/29/15.
 */
public class Responsable  extends User {

    public static final String TABLE_RESPONSABLE = "responsable";

    public static String launchPhase(int phase,java.sql.Date start,java.sql.Date end){
        Database databasehandle = Database.connect();
        return databasehandle.launchPhase(phase,start,end);
    }

    public static String launchAffectation(){
        Database databasehandle = Database.connect();
        return databasehandle.launchGrouping();
    }

    public static String listStudents(){
        Database databasehandle = Database.connect();
        return databasehandle.listStudents();
    }

    public static String deliberation(){
        Database databasehandle = Database.connect();
        return databasehandle.deliberation();
    }

    public static String endPhase(int phase){
        Database databasehandle = Database.connect();

        return databasehandle.endPhaseNow(phase);
    }

    public static String consult(){
        Database databasehandle = Database.connect();
        return databasehandle.fetchEtat("",4);
    }

}

