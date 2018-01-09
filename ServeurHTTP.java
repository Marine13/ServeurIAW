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
		System.out.println(creply);
		String[] listeMots=creply.split(" ") ;
		String chemin = "" ;

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
					chemin = listeMots[1] ; 
					res=new Requete (chemin) ;
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
			writeline("Content-type :"+mimetype+"\n") ;

			if (mimetype.equals("text/html")) {
				writeline("<!doctype html>");
				writeline("<html lang='fr'>") ;
				writeline("<head>") ;
				writeline("		<meta charset=utf-8>") ;
				writeline("		<title>"+nom+" </title>") ;
				writeline("		<link rel='stylesheet' href='style.css'>") ;
				writeline("</head>") ;
				writeline("<body>") ;
			}

			if(requete.getType()==1){
				writeline("Date courante : ") ;
				writeline(dateFormat.format(date)+"\n") ;
			}

			else if (requete.getType()==2){
				writeline("<h1> Le Répertoire "+String.valueOf(requete)+" contient : </h1>") ;
				File repertoire = new File(requete.getChemin());
				File[] sousRep = repertoire.listFiles();
				writeline("<ul>") ;
				for(File s:sousRep){
					writeline("<li> <a href=\"http://localhost:1234"+ s.getPath()+"\">"+s.getName()+"</a> </li>");
				}
				writeline("</ul>") ;				
			}

			else if (requete.getType()==3){
				ecrireFichier(requete.getChemin()) ;
			}

			if (mimetype.equals("text/html")){
				writeline("</body>") ;
				writeline("</html>") ; 
			}
		}
			
		else {
			writeline("HTTP/1.0 404 Not Found") ;
			writeline ("Content-type: text/html \n") ;

			

			writeline("<!doctype html>");
			writeline("<html lang='fr'>") ;
			writeline("<head>") ;
			writeline("		<meta charset=utf-8>") ;
			writeline("		<title> Erreur 404 </title>") ;
			writeline("		<link rel='stylesheet' href='style.css'>") ;
			writeline("</head>") ;
			writeline("<body>") ;
			writeline("		<p> Erreur 404 </p>") ;
			writeline("</body>");
			writeline("</html>") ;
		}


	}

	private static void ecrireFichier(String chemin) throws IOException {
		Path path = Paths.get(chemin);
		byte[] br = Files.readAllBytes(path) ;
	}

	/*private static String reformChemin(String chemin) {
		String[] partiesChemin = chemin.split(" ");
		String res=partiesChemin[0] ;
		for (int i=1 ; i<partiesChemin.length-1 ; i++) {
			res=res+"\\ "+partiesChemin[i] ;
		}
		return res ;
	} */
}
