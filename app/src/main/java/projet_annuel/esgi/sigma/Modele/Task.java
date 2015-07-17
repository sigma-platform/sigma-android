package projet_annuel.esgi.sigma.Modele;

/**
 * Created by bastien on 30/06/2015.
 */
public class Task {
    public String getNom() {
        return nom;
    }

    public String getDateD() {
        return dateD;
    }

    public String nom;
    public String dateD;

    public String getDateF() {
        return dateF;
    }

    public float getTmp() {
        return tmp;
    }

    public String dateF;
    public float tmp;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String version;

    public Task(String nom,String dateD, String dateF, float tmp, String version){
        this.nom = nom;
        this.dateD = dateD;
        this.dateF = dateF;
        this.tmp=tmp;
        this.version = version;
    }

    public Task(){
        super();
    }
}
