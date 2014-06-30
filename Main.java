import java.util.Scanner;
import java.io.*;


public class Main{
    public static void main(String[]args){
        Automate a  = new Automate(args[0]);
		System.out.println("Det ? "+a.estDeterministe());
		Automate det = a.determinise();
		System.out.println("--Determiniser--\n"+det);
		System.out.println("--MOORE--\n"+det.minimisation());
        if (args[1] != null){
            Automate b = new Automate(args[1]);
            Automate det2 = b.determinise();
            System.out.println("Det2 ? "+b.estDeterministe());
            System.out.println("--Determiniser2--\n"+det2);
            System.out.println("--MOORE2--\n"+det2.minimisation());
            if(det.minimisation().estEgale(det2.minimisation())){
                System.out.println("Les deux automates sont equivalents");
            }else{
                System.out.println("Les deux automates ne sont pas equivalents");
            }
        }
    }
        
}