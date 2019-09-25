import java.util.*;
import javafx.util.Pair;

public interface IncidentMatrix {

    public Integer[][] D_Minus(ArrayList<String> P, ArrayList<String> T, ArrayList<Pair> F);

    public Integer[][] D_Plus(ArrayList<String> P, ArrayList<String> T, ArrayList<Pair> F);

    public Integer[][] D(Integer[][] d_minus, Integer[][] d_plus);

    public Integer[] TransitionMatrix(Integer[][] d_minus, Integer[] markingMatrix);

    public Integer[] NextMarkingMatrix(Integer[][] d, Integer[] transitionMatrix, Integer[] markingMatrix);

}