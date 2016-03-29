package finale.year.stage.models;

/**
 * Created by philipchicco on 08/06/15.
 */
public class Deliberation {


    //
    public String nom    = "";
    public String prenom = "";
    public double phase1 = 0.0;
    public double phase2 = 0.0;
    public double phase3 = 0.0;
    public double phase4 = 0.0;
    public double moyenne = 0.0;
    public int    decision = 0;

    public Deliberation(String nom, String prenom, double phase1,
                        double phase2, double phase3, double phase4,
                        double moyenne){
        this.nom = nom; this.prenom = prenom; this.phase1 = phase1;
        this.phase2 = phase2; this.phase3 = phase3; this.phase4 = phase4;
        this.moyenne = moyenne;
    }

    public Deliberation(){

    }
}
