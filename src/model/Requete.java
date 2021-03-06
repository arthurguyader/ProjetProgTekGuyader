package model;
import utilitaire.*;
import java.util.*;
import java.sql.*;


/**
 * Cette classe prend en paramètre un objet Connexion et créée un objet Statement avec lequel on peut effectuer des commandes SQL sur la base de données.
 * Les méthodes de la classe prennent une chaîne de caractère qui représente une commande SQL. Ces méthodes ont des usages différents :
 * <P> manuel() permet d'exécuter n'importe quelle commande SQL
 * <P> 
 * Le constructore ne fait qu'initialiser le paramètre Statement, que les méthodes pourront modifier et exécuter.
 */	
public class Requete {

	/**
	 * L'objet Statement à modifier et exécuter pour exécuter les requêtes SQL.
	 */
	private Statement state;
	
	/**
	 * Le nom de la table sur laquelle exécuter la requête
	 */
	private String table;

	/**
	 * Le nom de la base de données sur laquelle exécuter la requete
	 */
	private String base;
	
	/**
	 * Constructeur de la classe. Prend en paramètre un objet Connection en paramètre et créé un objet Statement sur cette connexion. Stocke le Statement dans son attribut state.
	 * @param connexion la connexion sur laquelle créer un Statement
	 * @param table le nome de la table sur laquelle exécuter la requête
	 * @param base la base contenant la table sur laquelle exécuter la requête.
	 * @throws SQLException si la connexion est invalide, ou qu'une autre erreur SQL survient
	 */
	public Requete(Connection connexion,String base,String table) throws SQLException, Exception{
		this.table = table;
		this.base=base;
		this.state = connexion.createStatement();
		if(!base.equals(""))
		manuel("use "+base);
		
	}

	/** 
	 * Intègre à l'attribut state la commande SQL passée en paramètre, puis exécute cette requête. Retourne le nombre de tuples modifiés par la requête
	 * Cette méthode est utilisée pour créer ou supprimer des tables, vues ou triggers, mais aussi modifier, supprimer ou ajouter des tuples à une table existante.
	 * @param requete la requete à exécuter
	 * @return le nombre de ligne insérées et/ou modifiées et/ou supprimées
	 * @throws SQLException si la requete est incorrecte, que state n'est pas initialisé ou si une autre erreur SQL survient
	 * @throws Exception si la connexion ne peut pas être fermée ou si une autre erreur survient
	 */
	public int creerOuModifier(String requete) throws SQLException, Exception{			
		
		int ret = 0;
		
		try {
			
			ret = state.executeUpdate(requete);
		}
		catch(SQLException sqle) {
			sqle.printStackTrace();
			throw sqle;
		}
		return ret;
	}
	
	/**
	 * Intègre à l'attribut state la commande SQL donnée en paramètre, puis exécute cette requête.
	 * Cette commande est générale, elle peut être utilisée pour créer, supprimer ou modifier des éléments, mais aussi pour faire des requêtes et en récupérer le résultat.
	 * @param requete la requete à exécuter
	 * @return Un tableau d'objet (ret[0] = boolean ResultSet ou pas - ret[1] = ResultSet - ret[2] = Nombre De ligne modifier)
	 * @throws SQLException si la requete est incorrecte, que state n'est pas initialisé ou si une autre erreur SQL survient
	 * @throws Exception si la connexion ne peut pas être fermée ou si une autre erreur survient
	 */
	public Object[] manuel(String requete) throws SQLException, Exception{
		Object[] ret = new Object[3];
		ret[2] = 0;
		
		try {
			ret[0] = state.execute(requete);
			if ((boolean)ret[0]) {
				 ret[1] = state.getResultSet();
			}
			else {
				ret[2] = state.getUpdateCount();
			}
		}
		catch(SQLException sqle) {
			sqle.printStackTrace();
			throw sqle;
		}
		return ret;
	}	
	
	/** 
	  * Supprime la table dont le nom est donné en paramètre si celle-ci existe, puis créé une chaîne de caractère contenant une requête permettant de créer la table
	  * avec le nom et les attributs passés en paramètre, puis exécute cette requête.
	  * @param nomTable le nom de la table à ajouter
	  * @param listeAttribut la liste des attributs de la table 
	  * @return le nombre de ligne insérées et/ou modifiées et/ou supprimées
	  * @throws SQLException si l'ajout de la table est impossible à cause d'une erreur SQL
	  * @throws Exception si l'ajout de la table est impossible à cause d'une autre erreur
	  */
	 public int ajouterTable(String nomTable, ArrayList<Attribut> listeAttribut) throws SQLException,Exception {			
		int ret = 0;
		boolean premiereClePrimaire = true;
	 	
		manuel("DROP TABLE IF EXISTS "+nomTable+";");
		String requete = "CREATE TABLE "+nomTable+"(\n";
		
		for(Attribut monAtt : listeAttribut){
			requete = requete + monAtt.getNomVariable() +" "+monAtt.getType();
			if(monAtt.getValeur()>-1) requete =requete +"("+monAtt.getValeur()+")";
			if(!monAtt.getContrainte().equals("")){
				requete = requete + monAtt.getContrainte() +",\n";
			}
			else{
				requete = requete + ",\n";
			}
		}

		for(Attribut monAtt : listeAttribut){
			if(monAtt.getEstClePrimaire() && premiereClePrimaire){
				requete = requete +"PRIMARY KEY ("+monAtt.getNomVariable();
				premiereClePrimaire = false;
			}
			else if(monAtt.getEstClePrimaire() && !premiereClePrimaire){
				requete = requete +","+monAtt.getNomVariable();
			}
		}
		if (!premiereClePrimaire) {
			requete = requete + "),";	
		}
		

		requete = ModifierString.supprimerAvecPlace(requete,requete.length()-1,1);

		requete = requete + "\n);";


		for (Attribut monAtt : listeAttribut) {
			if (monAtt.getACleEtrangere()) {
				requete = requete + "\n\nALTER TABLE "+nomTable+" ADD FOREIGN KEY ("+monAtt.getNomVariable()+") REFERENCES ";
				requete = requete + monAtt.getReferenceTableEtrangere()+"("+monAtt.getReferenceAttributEtranger()+");";
			}
		}
		
		System.out.println(requete);
		ret = (int)(manuel(requete))[2];
		return ret;
	 }

	

	
	/**
	 * Intègre à l'attribut state la commande SQL permettant de supprimer une nouvelle table à la base de données à laquelle l'utilisateur, puis exécute cette requête.
	 * @param table la table à enlever
	 * @return le nombre de ligne insérées et/ou modifiées et/ou supprimées
	 * @throws SQLException si une erreur SQL empêche le retrait du tuple
	 * @throws Exception si une autre erreur empêche le retrait du tuple
	 */
	public int enleverTable(String table) throws Exception, SQLException {
		
		int ret=-1;
		
		ret = (int)manuel("DROP TABLE "+table+";")[2];
		
		return ret;
	}
	
	/**
	  * Intègre à l'attribut state la commande SQL permettant de supprimer un tuple à la table précisée par l'utilisateur, puis exécute cette requête.
	  * @param val la valeur de la clé primaire du tuple à retirer
	  * @param nomPrim le nom de l'attribut qui est clé primaire pour le tuple à retirer
	  * @return le nombre de ligne insérées et/ou modifiées et/ou supprimées
	  * @throws SQLException si une erreur SQL empêche le retrait du tuple
	 ** @throws Exception si une autre erreur empêche le retrait du tuple
	  */
	public int enleverTuple(String val,String nomPrim) throws SQLException,Exception {
		int ret;

		try {
        	int nb = Integer.parseInt(val);
        	ret = (int)manuel("DELETE FROM "+table+" WHERE "+nomPrim+"="+nb+";")[2];
	    } catch (NumberFormatException nfe) {
	        ret = (int)manuel("DELETE FROM "+table+" WHERE "+nomPrim+"='"+val+"';")[2];
	    }

		
		return ret;
	}
	
	/**
	  * Intègre à l'attribut state la commande SQL permettant de supprimer un trigger de la base de données à laquelle l'utilisateur, puis exécute cette requête.
	  * @param trigger Le trigger qui doit être supprimé
	  * @return le nombre de ligne insérées et/ou modifiées et/ou supprimées
	  * @throws SQLException si une erreur SQL empêche la méthode de fonctionner. Renvoie l'erreur à la méthode appelante.
	  * @throws Exception si une autre erreur empêche la méthode de fonctionner. Renvoie l'erreur à la méthode appelante.
	  */
	public int enleverTrigger(String trigger) throws SQLException,Exception{
		int ret=-1;
		ret = (int)manuel("DROP TRIGGER "+trigger+";")[2];
		return ret;
	}
	
	/**
	 * Intègre à l'attribut state la commande SQL permettant de supprimer une vue à la base de données à laquelle l'utilisateur est connecté, puis exécute cette requête.4
	 * @param vue La vue qui doit être supprimée
	 * @return le nombre de ligne insérées et/ou modifiées et/ou supprimées
	 * @throws SQLException si une erreur SQL empêche la méthode de fonctionner. Renvoie l'erreur à la méthode appelante.
	 * @throws Exception si une autre erreur empêche la méthode de fonctionner. Renvoie l'erreur à la méthode appelante.
	 */
	public int enleverVue(String vue) throws SQLException,Exception{
		int ret = -1;
		ret = (int)manuel("DROP VIEW "+vue+";")[2];
		return ret;
	}


	/**
	 * Permet de traiter un resultSet que ce soit une méthode Select ou non
	 * @param rs Le ResultSet qui doit être traité
	 * @param nEstPasUnSelect False si la requete est un Select - True sinon
	 * @return ret[0] = Les valeurs - ret[1] = lesColonnes
	 * @throws SQLException si une erreur SQL empêche la méthode de fonctionner. Renvoie l'erreur à la méthode appelante.
	 * @throws Exception si une autre erreur empêche la méthode de fonctionner. Renvoie l'erreur à la méthode appelante.
	 */

	public static Object[] retournerResultSet(ResultSet rs,boolean nEstPasUnSelect) throws SQLException,Exception{
		ArrayList<String> lesNomdeColonnes = new ArrayList<String>();
		ArrayList<String> lesValeurs = new ArrayList<String>();
		Object [] ret = new Object[2];

		String ligne="";
		try{
		
			
			ResultSetMetaData rsmd = rs.getMetaData();
		   	int columnsNumber = rsmd.getColumnCount();
		   	while (rs.next()) {
		       for (int i = 1; i <= columnsNumber; i++) {

		           	String columnValue = rs.getString(i);
		           	
		           	
		           	if(nEstPasUnSelect){
			           	if(i==1){
			           		String columnName = rs.getString(1);
			           		lesNomdeColonnes.add(columnName);
			           	}
			        }


			        lesValeurs.add(columnValue);
		       }
			}

			for (int i = 1; i <= columnsNumber; i++){
				if (!nEstPasUnSelect) {
			           		
		           			String columnName = rsmd.getColumnName(i);
		           			lesNomdeColonnes.add(columnName);
			           		
			    }
			}

		ret[0]=lesValeurs;

	
		ret[1]=lesNomdeColonnes;	
		

		}
		catch(SQLException se){
			throw se;
		}
		catch(Exception e){
			throw e;
		}

		

		return ret;
	}


}