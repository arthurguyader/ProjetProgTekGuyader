package controleur;
import java.util.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import model.*;
import java.util.regex.*;

public class EcouteurJTree implements TreeSelectionListener {

	private Utilisateur lUtilisateur;

	public EcouteurJTree(Utilisateur lUtilisateur){
		this.lUtilisateur=lUtilisateur;
	}


     public void valueChanged(TreeSelectionEvent event) {
	   try{
	     	String res = event.getNewLeadSelectionPath().toString();
	     	String [] splitArray = null;

	     	String carac1 = Pattern.quote("[");
	     	String carac2 = Pattern.quote("]");
	     	String res2 = (res.split(carac1))[1];
	     	String path = (res2.split(carac2))[0];
	     	splitArray = path.split(",");

	     	for(int i=0; i < splitArray.length;i++){
	     		if(i>=1){
					lUtilisateur.setSelection(lUtilisateur.getPositionBase(splitArray[1].trim()));
	     		}

	     		if (i==2){
	     			lUtilisateur.setTable(splitArray[2].trim());
	     		}
	     		else{
	     			lUtilisateur.setTable("");
	     		}
	     	}
	     }
	     catch(NullPointerException npe){
	     	lUtilisateur.setSelection(-1);
	     	lUtilisateur.setTable("");
	     }

		System.out.println("Base : "+lUtilisateur.getSelection());
     	System.out.println("Table : "+lUtilisateur.getTable());
	 }
 }