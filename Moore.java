import java.util.HashSet;


public class Moore{
	
	/**
	 * Alphabet de l'automate a minimiser
	 */
	static private char[] alphabetIt;
	
	/**
	 * etat de l'automate a minimiser
	 */
	static private EtatMoore[] colonnes;

	/**
	 * Minimisation de l'automate
	 */
	public static Automate minimisation(Automate af){
		boolean premierAppel = true;
		alphabetIt = new char[af.alphabet().size()];
		colonnes = new EtatMoore[af.size()];
		int compteur = 0;
		for(Character c : af.alphabet()) alphabetIt[compteur++] = c.charValue();
		compteur = 0;
		for(Etat e : af) colonnes[compteur++] = new EtatMoore(e, alphabetIt.length);
		
		do{
			if(premierAppel) premierAppel = false;
			else preparationEtape();
			
			for(EtatMoore etape : colonnes){
				for(int j = 0; j < alphabetIt.length; j++){
					etape.transitions[j] = rechercheValeur(etape.etat, alphabetIt[j]);
				}
			}
			bilan();
			print();
		}while(!finMoore());
	
		Automate minimale = new Automate();
		HashSet<Integer> doublee = colonneDouble();
		for(int i = 0; i < colonnes.length; i++){
			EtatMoore etape = colonnes[i];
			if(!doublee.contains(Integer.valueOf(i))){
				minimale.ajouteEtatSeul(new Etat(etape.etat.isInit(), etape.etat.isTerm(), etape.bilan));
			}
		}
		
		for(int k = 0; k < colonnes.length; k++){
			EtatMoore etape = colonnes[k];
			if(!doublee.contains(Integer.valueOf(k))){
				for(int i = 0; i < alphabetIt.length; i++){
					if(etape.transitions[i] != -1){
						Etat etat = minimale.getEtat(etape.bilan);
						etat.ajouteTransition(alphabetIt[i], minimale.getEtat(etape.transitions[i]));
					}
				}
			}
		}
		return minimale;
	}
	
	/**
	 * Recupere les indices de colonnes après un bilan qui sont similaires à un indice inférieur
	 * return la liste des indices similaires à un indice inférieur
	 */
	private static HashSet<Integer> colonneDouble(){
		HashSet<Integer> res = new HashSet<Integer>();
		HashSet<Integer> index = new HashSet<Integer>();
		for(int i = 0; i < colonnes.length; i++){
			if(!index.add(new Integer(colonnes[i].bilan))){
				res.add(new Integer(i));
			}
		}
		return res;
	}
	
	/**
	 * Realise l'etape bilan
	 */
	private static void bilan(){
		int compteur = 1;
		boolean succes = false;
		for(int i = 0; i < colonnes.length; i++){
			for(int j = 0; j < i; j++){
				if(bilanEgale(colonnes[i], colonnes[j])){
					colonnes[i].bilan = colonnes[j].bilan;
					succes = true;
					break;
				}
			}
			if(!succes) colonnes[i].bilan = compteur++;
			succes = false;
		}
	}
	
	/**
	 * Recopie les bilans de chaque etat dans le mot vide pour le prochain tour de l'algorithme de Moore
	 */
	private static void preparationEtape(){
		for(EtatMoore etape : colonnes){
			etape.motVide = etape.bilan;
		}
	}
	
	/**
	 * Test si l'algorithme de Moore est termine
	 */
	private static boolean finMoore(){
		for(EtatMoore etape : colonnes){
			if(etape.motVide != etape.bilan) return false;
		}
		return true;
	}
	
	/**
	 * Test si deux bilan on la meme valeur
    */
	private static boolean bilanEgale(EtatMoore a, EtatMoore b){
		if(a.motVide != b.motVide) return false;
		for(int i = 0; i < a.transitions.length; i++){
			if(a.transitions[i] != b.transitions[i]) return false;
		}
		return true;
	}
	
	/**
	 * Recherche la valeur bilan de la transition
    */
	private static int rechercheValeur(Etat depart, char c){
		EnsEtat succ = depart.succ(c);
		if(succ.isEmpty()) return -1;
		
		Etat suivant = succ.iterator().next();
		EtatMoore etapeDuSuivant = null;
		for(EtatMoore etape : colonnes){
			if(etape.etat == suivant){
				etapeDuSuivant = etape;
				break;
			}
		}
		
		if(etapeDuSuivant == null) return -1;
		
		return etapeDuSuivant.motVide;
	}
	
	/**
	 * Afficher l'etape courant de l'algorithme de Moore
	 */
	private static void print(){
		System.out.print("   ");
		for(EtatMoore etape : colonnes){
			System.out.print(etape.etat.hashCode()+"  ");
		}
		System.out.println();
		
		System.out.print("1  ");
		for(EtatMoore etape : colonnes){
			System.out.print(etape.motVide+"  ");
		}
		System.out.println();
		
		for(int i = 0; i < alphabetIt.length; i++){
			System.out.print(alphabetIt[i]+"  ");
			for(int j = 0; j < colonnes.length; j++){
				if(colonnes[j].transitions[i] == -1) System.out.print(colonnes[j].transitions[i]+" ");
				else System.out.print(colonnes[j].transitions[i]+"  ");
			}
			System.out.println();
		}
		
		System.out.print("=> ");
		for(EtatMoore etape : colonnes){
			System.out.print(etape.bilan+"  ");
		}
		System.out.println("\n");
	}

}

/**
 * Etat de Moore pour un etat de l'automate a minimiser
 */
class EtatMoore{
	
	/**
	 * Etat correspondant dans l'automate
	 */
	public Etat etat;
	
	/**
	 * Bilan de depart de l'etape pour le mot vide
	 */
	public int motVide;
	
	/**
	 * Bilan de fin d'etape
	 */
	public int bilan;
	
	/**
	 * Valeur des transitions pour chaque lettre de l'alphabet de l'automate
	 */
	public int[] transitions;
	
	/**
	 * Constructeur
	 */
	public EtatMoore(Etat etat, int size){
		this.etat = etat;
		this.motVide = (etat.isTerm())? 2 : 1;
		this.bilan = 0;
		this.transitions = new int[size];
	}
}
