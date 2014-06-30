import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.File;

/**
 * Structure d'un Automate
 */
public class Automate extends EnsEtat {

	/**
	 * Les etats initiaux
	 */
    private EnsEtat initiaux;

	/**
	 * Automate vide
	 */
    public Automate() {
        super();
        initiaux = new EnsEtat();
    }
    
    /**
	 * Automate auquel on ajoute tous les etats accessibles depuis l'etat en parametre
	 */
    public Automate(Etat etat){
		this();
		this.ajouteEtatRecursif(etat);
	}
	
    
    /**
	 * Automate depuis la lecture d'un fichier
	 */
    public Automate(String fichier){
    	this();
    	this.readFile(fichier);
    }
    
	/**
	 * Ajoute l'etat a l'automate
	 */
	public boolean ajouteEtatSeul(Etat e){
		if(!this.add(e)) return false;
		if(e.isInit()) initiaux.add(e);
		return true;
	}

	/**
	 * Ajouter l'etat et ses etats accessibles a l'automate
	 */
	public boolean ajouteEtatRecursif(Etat e){
		if(ajouteEtatSeul(e)){
			EnsEtat succ = e.succ();
			if(succ != null){
				for(Etat etat : e.succ()) ajouteEtatRecursif(etat);
			}
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Tester si l'automate est deterministe
	 */
	public boolean estDeterministe(){
		if(this.isEmpty()) return true;

		if(this.initiaux.size() > 1) return false;
		
		for(Etat e : this){
			for(Character a : e.getTransitions().keySet()){
				if(e.succ(a).size() > 1){
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	/**
	 *Determiniser l'automate
	 */
	public Automate determinise(){
		if(this.estDeterministe()) return this;
		Automate det = new Automate();
		HashMap<EnsEtat, Etat> map = new HashMap<EnsEtat, Etat>();
		Stack<EnsEtat> pile = new Stack<EnsEtat>();
		Set<Character> alphabet = this.alphabet();
		ArrayList<EnsEtat> listEnsEtat = new ArrayList<EnsEtat>();
		int etatCompteur = 0;

		Etat initEtat = new Etat(true, false, etatCompteur++);
		det.ajouteEtatSeul(initEtat);
		EnsEtat init = new EnsEtat();
		for(Etat etat : initiaux) init.add(etat);
		map.put(init, initEtat);
		pile.push(init);
		listEnsEtat.add(init);

		while(!pile.empty()){
			EnsEtat ensPop = pile.pop();
			//Recupere etat lie a l'ensemble d'etat pris dans la pile
			Etat etatLie = map.get(ensPop);
			//Parcours de l'alphabet de l'automate
			for(Character a : alphabet){
				EnsEtat etatsLieSuccA = new EnsEtat();
				//Parcours de l'ensemble d'etats pris dans la pile
				for(Etat etatPop : ensPop){
					//Recupere les etats succeceurs a la lettre courante
					EnsEtat etatsSucc = etatPop.succ(a.charValue());
					if(etatsSucc != null){
						//Ajoute les etats successeurs a un ensemble des successeurs de la lettre courante
						for(Etat tmp : etatsSucc) etatsLieSuccA.add(tmp);
					}
				}
				
				//Si on a trouver des successeurs pour la lettre courante
				if(!etatsLieSuccA.isEmpty()){
					//Cherche si on a pas deja creer un ensemble d'etat correspondant
					// aux successeurs de la lettre courante
					boolean existeDeja = false;
					EnsEtat refMemeEnsEtat = null;
					for(EnsEtat listEtat : listEnsEtat){
						if(listEtat.egale(etatsLieSuccA)){
							refMemeEnsEtat = listEtat;
							break;
						}
					}
					
					//Si l'ensemble des successeurs de la lettre courante existe pas deja
					if(refMemeEnsEtat == null){
						Etat nouvelEtat = new Etat(false, etatsLieSuccA.contientTerminal(), etatCompteur++);
						det.ajouteEtatSeul(nouvelEtat);
						map.put(etatsLieSuccA, nouvelEtat);
						pile.push(etatsLieSuccA);
						listEnsEtat.add(etatsLieSuccA);
						etatLie.ajouteTransition(a.charValue(), nouvelEtat);
					}else{
						etatLie.ajouteTransition(a.charValue(), map.get(refMemeEnsEtat));
					}
				}
			}
		}
		det.setMapDeterminise(map);
		return det;
	}
	
	/**
	 * rend une copie de l'automate
	 */
	public Automate copy(){
		Automate cp = new Automate();
		for(Etat e : this){
			cp.ajouteEtatSeul(new Etat(e.isInit(), e.isTerm(), e.hashCode()));
		}
		for(Etat e : cp){
			Etat lie = this.getEtat(e.hashCode());
			for(Map.Entry<Character, EnsEtat> entre : lie.transitions.entrySet()){
				for(Etat succ : entre.getValue()){
					e.ajouteTransition(entre.getKey().charValue(), succ);
				}
			}
		}
		return cp;
	}

	
			
	/**
	 * Test si le mot est accepte par l'automate
	 */
	public boolean accepte(String s){
		return initiaux.accepte(s, 0);
	}
	
	/**
	 * Recupere l'automate minimiser avec l'algorithme de Moore
	 */
	public Automate minimisation(){
		if(this.estDeterministe()) return Moore.minimisation(this);
		else return Moore.minimisation(this.determinise());
	}
	
		
	/**
	 * Creer l'automate depuis un fichier
	 */
	public void readFile(String fichier){
		try{
    		Scanner sc = new Scanner(new File(fichier));
    		String ligne = sc.nextLine();
    		String[] ligneSplit = ligne.split(" ");
    		int etats = Integer.parseInt(ligneSplit[0]);
    		for(int  i = 0; i < etats; i++){
    			this.add(new Etat(false, false, i));
    		}
    		
    		Etat etatTmp = null;
    		while(sc.hasNextLine()){
    			ligne = sc.nextLine();
    			
    			if(ligne != null && ligne.length() > 0){
    				ligneSplit = ligne.split(" ");
    				etatTmp = getEtat(Integer.parseInt(ligneSplit[0]));
    				etatTmp.setInit(ligneSplit.length > 1 && ligneSplit[1].equals("initial"));
    				etatTmp.setTerm(ligneSplit.length > 1 && ligneSplit[1].equals("terminal") || ligneSplit.length > 2 && ligneSplit[2].equals("terminal"));
    				if(etatTmp.isInit()) initiaux.add(etatTmp);
    				
    				do{
    					ligne = sc.nextLine();
    					if(ligne == null || ligne.length() == 0) break;
    					ligneSplit = ligne.split(" ");
    					char transition = ligneSplit[0].charAt(0);
    					for(int i = 1; i < ligneSplit.length; i++){
    						etatTmp.ajouteTransition(transition, getEtat(Integer.parseInt(ligneSplit[i])));
    					}
    				}while(sc.hasNextLine());
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

	@Override
	public String toString(){
		return super.toString();
	}
	
	/**
	 * Representation alternative d'un automate
	 */
	public String affiche(){
		return super.affiche();
	}

	/**
	 * Getter des initiaux
	 */
    public EnsEtat getInitiaux() {
        return initiaux;
    }
    /*
     *Comparer deux automates
     */
    public boolean estEgale(Automate test){
		if(this.size() != test.size()){
			System.out.println("Ne sont pas égaux : tailles differentes");
			return false;
		}
		
		if(!this.alphabet().equals(test.alphabet())){
			System.out.println("Ne sont pas égaux : alphabets differents");
			return false;
		}
		
		HashMap<Etat, Etat> map = new HashMap<Etat, Etat>();
		ArrayList<Etat> liste = new ArrayList<Etat>();
		Stack<Etat> pile = new Stack<Etat>();
		Set<Character> alphabet = this.alphabet();
		
		if(this.initiaux.size() != 1 || test.initiaux.size() != 1){
			System.out.println("Ne sont pas égaux : il y a pas ou plus d'un état initial");
			return false;
		}
		
		pile.push(this.initiaux.iterator().next());
		map.put(this.initiaux.iterator().next(), test.initiaux.iterator().next());
		
		while(!pile.isEmpty()){
			Etat courant = pile.pop();
			Etat lie = map.get(courant);
			for(Character c : alphabet){
				EnsEtat ensSucc = courant.succ(c);
				EnsEtat ensLieSucc = lie.succ(c);
				if(ensSucc.size() > 0 && ensLieSucc.size() > 0){
					if(ensSucc.size() != 1 || ensLieSucc.size() != 1){
						System.out.println("Ne sont pas égaux : non déterminisé");
						return false;
					}
					Etat courantSucc = ensSucc.iterator().next();
					if(!liste.contains(courantSucc)){
						Etat lieSucc = ensLieSucc.iterator().next();
						liste.add(courantSucc);
						map.put(courantSucc, lieSucc);
						pile.push(courantSucc);
					}
				}
			}
		}
		return true;
	}

}
