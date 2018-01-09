//import peip.Server; //import de la classe Server du package peip
import java.io.*;
import java.util.*;
import java.text.* ;

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
		Requete req=getRequete() ;
		if (req!= new Requete("",0))
			repRequete(req) ;
		closeConn();
	}

	//Début de la méthode getRequete
	private Requete getRequete() throws IOException {
		String creply = null;;
		Requete res = new Requete ("",0) ;
		creply = readline();
		String[] listeMots=creply.split(" ") ;

		if ((creply==null)||(creply.equals(""))){
			res=new Requete("", 0) ;
		}
		else if (listeMots[0].equals("GET")) {
			while (!(creply.equals(""))) { 
				creply = readline();
				if(creply==null){
					res=new Requete ("", 0);
				}
				if (listeMots[1].equals("/date")){
					res=new Requete ("/date", 1) ;
				}
				else 
					res=new Requete ("", 0) ;
			
			}
		}
		else if (listeMots[0].equals("POST")){
			while (!(creply.equals(""))) { 
				creply = readline();
				if(creply==null){
					
					res=new Requete ("", 0);
				}
			}
			creply=readline() ;
			res=new Requete("",0);			
		}
		else 
			res=new Requete("",0) ;

		return res ;
	}


	//Début de la méthode repRequete
	private void repRequete(Requete requete) throws IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat ("EE dd MMM yyyy HH:mm:ss") ;
		Date date = new Date() ;

		
		if(requete.getType()==1){

			writeline("HTTP/1.0 200 OK") ;
			writeline("Content-type : text-plain"+"\n") ;

			writeline("Date courante : ") ;
			writeline(dateFormat.format(date)+"\n") ;
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
}
