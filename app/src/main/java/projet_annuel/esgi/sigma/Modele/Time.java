package projet_annuel.esgi.sigma.Modele;

/**
 * Created by bastien on 16/07/2015.
 */

// The modele who fit with the representation of a Time in our database
public class Time {

    private String lbl;
    private String date;
    private String timeP;
    private String timeE;

    public Time(){
        super();
    }

    public Time(String lbl, String date,String timeP, String timeE){
        this.lbl = lbl;
        this.date = date;
        this.timeE = timeE;
        this.timeP = timeP;
    }
    public String getLbl() {
        return lbl;
    }

    public void setLbl(String lbl) {
        this.lbl = lbl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeP() {
        return timeP;
    }

    public void setTimeP(String timeP) {
        this.timeP = timeP;
    }

    public String getTimeE() {
        return timeE;
    }

    public void setTimeE(String timeE) {
        this.timeE = timeE;
    }


}
