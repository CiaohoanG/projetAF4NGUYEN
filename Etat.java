
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;


public class Etat {
	
	
	HashMap<Character, EnsEtat> transitions;
	
	
	boolean init;
    boolean term;
	
	/**
	 * Identifiant de l'etat
	 */
	int id;

	/**
	 * Initialise l'etat vide
	 */
	public Etat() {
		this.transitions = new HashMap<Character, EnsEtat>();
	}

	/**
	 * Initialise l'etat selon l'id
    */
	public Etat(int id) {
		this.transitions = new HashMap<Character, EnsEtat>();
		this.id = id;
		this.term = false;
		this.init = false;
	}

	/**
	 * Initialise l'etat selon l'id et initial/acceptant
	 */
	public Etat(boolean init, boolean term, int id) {
		this.transitions = new HashMap<Character, EnsEtat>();
		this.init = init;
		this.term = term;
		this.id = id;
	}

	/**
	 * Recupere tous les successeurs de la transition par la lettre en parametre
	 */
	public EnsEtat succ(char c){
		if(transitions.containsKey(c)){
			return transitions.get(c);
		}
		return new EnsEtat();
	}

	/**
	 * Recupere tous les successeurs de toutes les transitions
     */
	public EnsEtat succ(){
		if(!transitions.isEmpty()){
			EnsEtat tmp = new EnsEtat();
			for(EnsEtat etats: transitions.values()){
				tmp.addAll(etats);
			}
			return tmp;
		}
		return new EnsEtat();
	}

	/**
	 * Ajoute une transition
    */
	public void ajouteTransition(char c, Etat e){
		if(transitions.containsKey(c)){
			EnsEtat tmp = transitions.get(c);
			tmp.add(e);
		}else{
			EnsEtat tmp = new EnsEtat();
			tmp.add(e);
			transitions.put(c, tmp);
		}
	}

	/**
	 * Recupere l'alphabet de l'etat
     */
	public Set<Character> alphabet(){
		return transitions.keySet();
	}

	/**
	 * Getter initial
    */
	public boolean isInit() {
		return init;
	}

	/**
	 * Getter acceptant
    */
	public boolean isTerm() {
		return term;
	}
	
	/**
	 * Setter initial
	 */
	public void setInit(boolean init) {
		this.init = init;
	}

	/**
	 * Setter acceptant
	 */
	public void setTerm(boolean term) {
		this.term = term;
	}

	/**
	 * Getter des transitions
	 */
	public HashMap<Character, EnsEtat> getTransitions(){
		return transitions;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		} else {
			final Etat other = (Etat) obj;
			return (id == other.id);
		}
	}

	@Override
	public String toString(){
		String res = "";
		res += id+"";
		res += (init)? " initial": "";
		res += (term)? " terminal" : "";
		res += "\n";
		
		for(Map.Entry<Character, EnsEtat> entry : transitions.entrySet()){
			res += entry.getKey().charValue();
			for(Etat etat : entry.getValue()){
				res += " "+etat.id;
			}
			res += "\n";
		}
		return res;
	}
	
	/**
	 * Representation alternative d'un etat
    */
	public String affiche(){
		int i = (init)? 1 : 0;
		int t = (term)? 1 : 0;
		String res = "{"+id+" ("+i+","+t+")";

		for(Character a : transitions.keySet()){
			String key = " ["+a+"=>(";
			String etatS = "";
			for(Etat etat : transitions.get(a)){
				if(etatS.length() != 0) etatS += ",";
				etatS += etat.hashCode()+"";
			}
			key += etatS+")]";
			res += key;
		}

		res += " }";
		return res;
	}
}
