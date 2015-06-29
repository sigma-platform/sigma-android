package projet_annuel.esgi.sigma.Modele;

import android.app.Application;

public class SigmaApplication extends Application
{
	private String jsonProjects;

	public String getJsonProjects() {
		return jsonProjects;
	}

	public void setJsonProjects(String jsonProjects) {
		this.jsonProjects = jsonProjects;
	}
}
