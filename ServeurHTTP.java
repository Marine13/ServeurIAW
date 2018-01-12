//import peip.Server; //import de la classe Server du package peip
import java.io.*;
import java.util.*;
import java.text.* ;
import java.nio.file.*;

public class ServeurHTTP extends Server {
	public ServeurHTTP(int port, boolean verbose) throws IOException {
		super(port, verbose) ;
	}

	public static void main(String[] args) {
		ServeurHTTP myserver = null;
		try {
			myserver = new ServeurHTTP(1234, true);
		} 
		catch (IOException e) {
			System.out.println("Problem while creating server!");
			System.exit(-1); // code erreur <> 0 pour signaler qu'il y a un pbm
		}
		try {
			while (true) {
				myserver.dialogue();
			}
		}
		catch (IOException e) {
			System.out.println("Problem while talking to the client!");
		} 
		finally {
			System.out.println("Killing the server");
			myserver.close();
		}
	}
// methode de dialogue correspondant à l'écho par le serveur d'une (seule) chaine lue cad reçue (envoyée) du client
	private void dialogue () throws IOException {		
		acceptConn();
		try {
			Requete req=getRequete() ;
			if (req!= new Requete(""))
				repRequete(req) ;
			closeConn();
		}
		catch (NullPointerException e) {
			closeConn() ;
		}
	}

	//Début de la méthode getRequete
	private Requete getRequete() throws IOException {

		String creply = null;
		Requete res = new Requete ("") ;
		creply = readline();
		String[] listeMots=creply.split(" ") ;
		String chemin;
		String parametre ;

		if ((creply==null)||(creply.equals(""))){
			res=new Requete("") ;
		}
		else if (listeMots[0].equals("GET")) {
			while (!(creply.equals(""))) { 
				creply = readline();
				if(creply==null){
					res=new Requete ("");
				}
				else {
					chemin = "."+listeMots[1] ;
					res=new Requete (reformChemin(chemin)) ;
				}
			
			}
		}
		else if (listeMots[0].equals("POST")){
			while (!(creply.equals(""))) { 
				creply = readline();
				if(creply==null){
					
					res=new Requete ("");
				}
			}
			creply=readline() ;
			res=new Requete("");			
		}
		else 
			res=new Requete("") ;

		return res ;
	}


	//Début de la méthode repRequete
	private void repRequete(Requete requete) throws IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat ("EE dd MMM yyyy HH:mm:ss") ;
		Date date = new Date() ;
		String nom=requete.getNom() ;

		if (requete.getType()>0){
			String mimetype=requete.getMimetype() ;

			writeline("HTTP/1.0 200 OK") ;
			if (requete.getType()!=5) 
			// Lorsque la requete demande l'execution d'un script shell (demande de type 5), le content-type y est déjà compris
			// il ne faut donc pas le réécrire. 
				writeline("Content-type :"+mimetype+"\n") ;


			// Type 1 lorsque URL est /date
			if(requete.getType()==1){
				writeline("Date courante : ") ;
				writeline(dateFormat.format(date)+"\n") ;
			}

			else if (requete.getType()==5) {
				try {
					String[] commande = new String[1] ;
					String paramFin ;
					commande[0]=requete.getChemin() ;
					System.out.println(commande[0]);
					if (requete.getParametre()!="") {
						String[] param=requete.getParametre().split("&") ;
						paramFin=" "+param[0].split("=")[1]+" "+param[1].split("=")[1] ;
					}
					else 
						paramFin="" ;

					Process myp = Runtime.getRuntime().exec(commande[0]+paramFin);
					System.out.println(commande[0]+paramFin);

					myp.waitFor(); 
					// attention, cette attente peut bloquer si la commande externe rencontre un problème
					String line = null;
					BufferedReader in = new BufferedReader(new InputStreamReader(myp.getInputStream()));
					while((line = in.readLine()) != null) {
						writeline(line);
					}
				} 
				catch(Exception e) {
					writeline("error");
				}
			}

			else if (requete.getType()==2){
				writeline("<!doctype html>");
				writeline("<html lang='fr'>") ;
				writeline("<head>") ;
				writeline("		<meta charset=utf-8>") ;
				writeline("		<title>"+nom+" </title>") ;
				writeline("		<link rel='stylesheet' href='style.css'>") ;
				writeline("</head>") ;
				writeline("<body>") ;
				writeline("<h1> Le Répertoire "+String.valueOf(requete)+" contient : </h1>") ;

				File repertoire = new File(requete.getChemin());
				File[] sousRep = repertoire.listFiles();

				writeline("<ul>") ;
				for(File s:sousRep){
					writeline("<li> <a href=\"http://localhost:1234/."+ s.getPath()+"\">"+s.getName()+"</a> </li>");
					System.out.println(s.getPath());
				}
				writeline("</ul>") ;	
				writeline("</body>") ;
				writeline("</html>") ; 			
			}

			else if (requete.getType()==3){
				ecrireFichier(requete.getChemin()) ;
			}

			else if (requete.getType()==4){
				writeline("<!DOCTYPE html>");
				writeline("<html lang=\"fr_FR\">") ;
  				writeline("<head>") ;
    			writeline("<meta charset=\"utf-8\">") ;
    			writeline("<meta http-equiv=\"refresh\" content=\"0; URL=http://localhost:1234/"+reformChemin(requete.getChemin())+"/Index.html\">") ;
				writeline("</head>") ;
				writeline("</html>");
			}
		}
			
		else if (requete.getType()==0) {
			System.out.println("Ok type 0");

			writeline("HTTP/1.0 404 Not Found") ;
			writeline ("Content-type: text/html \n") ;

			writeline("<!DOCTYPE html>");
			writeline("<html lang=\"fr_FR\">") ;
			writeline("<head>") ;
			writeline("<meta charset=\"utf-8\">") ;
			writeline("<meta http-equiv=\"refresh\" content=\"0; URL=http://localhost:1234/Erreur404/erreur404.html\">") ;
			writeline("</head>") ;
			writeline("</html>");

			/*writeline("<!DOCTYPE html>") ;
			writeline("<html lang=\"fr_FR\">") ;
  			writeline("<head>") ;
    		writeline("<meta charset=\"utf-8\">") ;
   			writeline("<title>Erreur 404</title>") ;
    		writeline("<link rel=\"stylesheet\" type=\"text/css\" href=\"Erreur404/erreur404.css\">") ; 
  			writeline("</head>") ;
			writeline("<body>") ;
			writeline("<h1>Erreur 404</h1>") ;
			writeline("<div class=image>") ;
			writeline("<img src=\"Erreur404/Oups.png\" alt=\"image de Oups\">") ;
			writeline("</div>") ;
			writeline("<div class=texte>") ;
			writeline("<p> Le fichier demandé n'existe pas </p>") ; 
			writeline("</div>") ;
			writeline("</body>") ;
			writeline("</html>") ;*/
		}


	}

	private void ecrireFichier(String chemin) throws IOException {
		try {
			int br ;
			byte[] b = new byte[1];
			File f ;
			f=new File(chemin) ;
			FileInputStream fis ;
			fis = new FileInputStream(f) ;
			br=fis.read(b) ;
			while (br>=0) {
				write(b) ;
				br=fis.read(b) ;
			}
		}
		catch (Exception e) {
			return ;
		}
		
	}

	private static String reformChemin(String chemin) {
		String[] partiesChemin = chemin.split(" ");
		String res=partiesChemin[0] ;
		for (int i=1 ; i<partiesChemin.length ; i++) {
			res=res+"%20"+partiesChemin[i] ;
		}
		res=res.replaceAll("é","%C3%A9") ;
		res=res.replaceAll("è", "%C3%A8") ;
		res=res.replaceAll("ç", "%C3%A7") ;
		return res ;
	}

}