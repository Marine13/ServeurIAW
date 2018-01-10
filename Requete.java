import java.io.* ;

public class Requete {
	private String chemin ;
	private int type ;
	private File fichier ; 
	private String mimetype="" ;
	private String nom ;

	public Requete (String chemin){
		this.chemin=chemin ;
		if (chemin.equals("/date")) {
			type=1 ;
			mimetype="text/plain" ;
			nom="Date courante" ;
		}
		else {
			fichier=new File (chemin) ;
			String[] nomCourant = chemin.split("/") ;
			if (nomCourant.length!=0)
				nom=nomCourant[nomCourant.length-1] ;
		 	if (fichier.exists()) {
				if (fichier.isFile()) {
					type=3 ;
					try{
						String[] extension = chemin.split(".") ;
						switch(extension[1]) {
							case "jpg" : mimetype="image/jpg" ; break ;
							case "png" : mimetype="image/png" ; break ;
							case "mp4" : mimetype="video/mp4" ; break ;
							case "html" :
							case "htm" : mimetype="text/html" ; break ;
							case "css" : mimetype="text/css" ; break ;
							case "pdf" : mimetype="application/pdf" ; break ;
						}
					}
					catch(Exception e) {
						mimetype="text/plain" ;
					}
				}

				else if (fichier.isDirectory()) {
					File repertoire = new File(chemin);
					File[] sousRep = repertoire.listFiles();
					type=2 ;
					mimetype="text/html" ;
					for (File s : sousRep){
						if (s.getName().equals("Index.html")){
							type=4 ;
						}

					}
					
				}
			}
			else 
				type=0 ;
		} 
	}

	public String toString(){
		return chemin ;
	}

	public int getType(){
		return type;
	}

	public String getChemin(){
		return chemin ;
	}

	public String getMimetype(){
		return mimetype ;
	}

	public String getNom() {
		return nom ;
	}


}