package finale.year.stage.models;

/**
 * Created by philipchicco on 06/06/15.
 */
public class Affectation {

    public String enseignant = "";
    public String tuteur     = "";
    public String student    = "";
    public int theme         = -1;

    //Structure for Affectation
    public Affectation(String enseignant,String tuteur,String student,int theme){
        this.enseignant = enseignant;
        this.student = student;
        this.theme = theme;
        this.tuteur = tuteur;
    }

}
