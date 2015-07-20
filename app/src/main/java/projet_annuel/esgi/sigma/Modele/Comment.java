package projet_annuel.esgi.sigma.Modele;

import android.util.Log;

/**
 * Created by bastien on 17/07/2015.
 */
public class Comment {

    private String description;
    private String date;
    private String name;

    public Comment(String desc,String date,String name){
        this.description = desc;
        this.date=date;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
