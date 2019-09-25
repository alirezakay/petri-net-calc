/**
 * @author Alireza Kavian - kay.alireza@gmail.com
 * Petrinet Incident Matrix
 * 11/14/2018
 */

/*
   [D] = [(D+)] - [(D-)]
   [Next Marking Matrix] = ([Transition Matrix][D]) + [Marking Matrix]
*/

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class CA1 {
    public static void main(String[] args) throws IOException {
        Petrinet pn = new Petrinet("");
        run(pn);
        // __UNIT_TEST__(pn);
    }

    public static void run(Petrinet pn) {
        Scanner in = new Scanner(System.in);
        System.out.print("How many marking matrix derivations do you wanna be calculated??? ");
        int num_calc = in.nextInt();
        System.out.print("How many marking matrix derivations do you wanna be dumped??? ");
        int num_see = in.nextInt();
        System.out.println("************");

        Integer[][] marklist = pn.MarkingMatrixToState(num_calc);
        System.out.println("| First " + num_see + " marking matrices derived from M0 |\n--------------");
        pn.__showMatrixInteger_mxn(marklist, num_see);
        System.out.println("--------------");

        // check safeness
        /* 
         ** It is safe if all the places in all `Markings` don't exceed having upto 1 token
         */
        boolean safe_flag = true;
        for (int i = 0; i < marklist.length; i++) {
            for (int j = 0; j < marklist[i].length; j++) {
                if (marklist[i][j] > 1) {
                    safe_flag = false;
                }
            }
        }
        System.out.println("Is It Safe ? " + (safe_flag ? "YES" : "NO"));
        
        // check liveness
        /* 
         ** It is live if all elements of`Transition `Matrices` in all the particular markings, have upto 1 firings
         */
        boolean live_flag = true;
        ArrayList<Integer[]> tmatrices = pn.getTmatrices();
        for (int i = 0; i < tmatrices.size(); i++) {
            for (int j = 0; j < tmatrices.get(i).length; j++) {
                if (marklist[i][j] == 0) {
                    live_flag = false;
                    break;
                }
            }
            if(!live_flag){
                break;
            }
        }
        System.out.println("Is It Live ? " + (live_flag ? "YES" : "NO"));

        in.close();
    }

    //---------------------------------------------------//
    // TEST
    public static void __UNIT_TEST__(Petrinet pn) {
        pn.__show("M");
        System.out.println("");
        pn.__showMatrixInteger_mxn(pn.getD_Minus());
        System.out.println("");
        pn.__showMatrixInteger_mxn(pn.getD_Plus());
        System.out.println("");
        pn.__showMatrixInteger_mxn(pn.getD());
        System.out.println("");
        pn.__showMatrixInteger_1xn(pn.getTransitionMatrix());
        System.out.println("");
        pn.__showMatrixInteger_1xn(pn.getNextMarkingMatrix());
        pn.__showMatrixInteger_mxn(pn.MarkingMatrixToState(150));
    }
}
