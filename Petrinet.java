import java.io.*;
import java.util.*;
import javafx.util.Pair;

public class Petrinet implements IncidentMatrix {
    private File file;
    private ArrayList<String> P; // Places
    private ArrayList<String> T; // Transitions
    private ArrayList<Integer> M0; // initilaized Marked Matrix
    private Integer[] M; // latest Marked matrix
    private ArrayList<Pair> F; // Function X -> Y
    private int state;

    /* check if matrix D exists then don't calculate it again! */
    private boolean hasD = false;
    private Integer[][] D;

    /* all calculated firing transition matrix (1xm) on running time */
    private ArrayList<Integer[]> Tmatrices;

    public Petrinet(String path) throws IOException {
        if (path.isEmpty()) {
            path = "petrinet.txt";
        }
        this.P = new ArrayList<>();
        this.T = new ArrayList<>();
        this.M0 = new ArrayList<>();
        this.F = new ArrayList<>();
        this.state = 0;
        this.Tmatrices = new ArrayList<>();
        this.file = new File(path);
        FetchData();
    }

    private void FetchData() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.file));
        String str = "";
        int line = 0;
        while ((str = br.readLine()) != null) {
            line++;
            String[] s = str.trim().split("=");
            String left = s[0].trim();
            String right = s[1].trim();
            switch (line) {
            case 1:
                if (left.equals("P")) {
                    right = right.replaceAll("(\\()(.*)(\\))", "$2");
                    String[] ps = right.split(",");
                    for (String i : ps) {
                        this.P.add(i);
                    }
                }
                break;
            case 2:
                if (left.equals("M0")) {
                    right = right.replaceAll("(\\{)(.*)(\\})", "$2");
                    String[] m0s = right.split(",");
                    this.M = new Integer[m0s.length];
                    int idx = 0;
                    for (String i : m0s) {
                        this.M0.add((Integer) Integer.parseInt(i));
                        this.M[idx] = ((Integer) Integer.parseInt(i));
                        idx++;
                    }
                }
                break;
            case 3:
                if (left.equals("T")) {
                    right = right.replaceAll("(\\()(.*)(\\))", "$2");
                    String[] ts = right.split(",");
                    for (String i : ts) {
                        this.T.add(i);
                    }
                }
                break;
            case 4:
                if (left.equals("F")) {
                    right = right.replaceAll("(\\{)(.*)(\\})", "$2");
                    String[] list = right.replaceAll("\\),\\(", ")#(").split("#");
                    for (String i : list) {
                        i = i.replaceAll("(\\()(.*)(\\))", "$2");
                        String[] pair = i.split(",");
                        this.F.add(new Pair<String, String>(pair[0], pair[1]));
                    }
                }
                break;
            }

        }
        br.close();
    }

    public Integer[][] MarkingMatrixToState(int state) {
        Integer[][] marklist = new Integer[state][this.M0.size()];
        marklist[0] = this.M;
        for (int i = 1; i < state; i++) {
            marklist[i] = this.getNextMarkingMatrix();
        }
        return marklist;
    }

    
    /////// Getter/Setter //////
     
    public Integer[][] getD_Minus() {
        return D_Minus(this.P, this.T, this.F);
    }

    public Integer[][] getD_Plus() {
        return D_Plus(this.P, this.T, this.F);
    }

    public Integer[][] getD() {
        return D(this.getD_Minus(), this.getD_Plus());
    }

    public Integer[] getTransitionMatrix() {
        return TransitionMatrix(this.getD_Minus(), this.M);
    }

    public Integer[] getNextMarkingMatrix() {
        this.state++;
        return (this.M = NextMarkingMatrix(this.getD(), this.getTransitionMatrix(), this.M));
    }

    public int getState() {
        return this.state;
    }

    public ArrayList<Integer[]> getTmatrices() {
        return this.Tmatrices;
    }

    ////// Override IncidentMatrix Methods ///////
    @Override
    public Integer[][] D(Integer[][] d_minus, Integer[][] d_plus) {
        if (!this.hasD) {
            this.hasD = true;
            return (this.D = Matrix.subMatricesInteger(d_plus, d_minus));
        } else {
            return this.D;
        }
    }

    @Override
    public Integer[][] D_Minus(ArrayList<String> P, ArrayList<String> T, ArrayList<Pair> F) {
        Integer[][] res = new Integer[T.size()][];
        for (int i = 0; i < T.size(); i++) {
            res[i] = new Integer[P.size()];
            for (int j = 0; j < P.size(); j++) {
                res[i][j] = 0;
            }
        }

        for (int i = 0; i < T.size(); i++) {
            for (Pair el : F) {
                if (el.getValue().equals(T.get(i))) {
                    int idx = P.indexOf(el.getKey());
                    res[i][idx] = 1;
                }
            }
            ;
        }
        return res;
    }

    @Override
    public Integer[][] D_Plus(ArrayList<String> P, ArrayList<String> T, ArrayList<Pair> F) {
        Integer[][] res = new Integer[T.size()][];
        for (int i = 0; i < T.size(); i++) {
            res[i] = new Integer[P.size()];
            for (int j = 0; j < P.size(); j++) {
                res[i][j] = 0;
            }
        }

        for (int i = 0; i < T.size(); i++) {
            for (Pair el : F) {
                if (el.getKey().equals(T.get(i))) {
                    int idx = P.indexOf(el.getValue());
                    res[i][idx] = 1;
                }
            }
            ;
        }
        return res;
    }

    @Override
    public Integer[] TransitionMatrix(Integer[][] d_minus, Integer[] markingMatrix) {
        Integer[] Mtmp = new Integer[markingMatrix.length];
        for (int i = 0; i < markingMatrix.length; i++) {
            if (markingMatrix[i] != 0) {
                Mtmp[i] = 1;
            } else {
                Mtmp[i] = markingMatrix[i];
            }
        }
        Integer[] res = new Integer[this.T.size()];
        for (int i = 0; i < d_minus.length; i++) {
            boolean flag = false;
            for (int j = 0; j < d_minus[i].length; j++) {
                if (d_minus[i][j] == 1) {
                    if (markingMatrix[j] == 0) {
                        flag = false;
                        break;
                    } else {
                        flag = true;
                    }
                }
            }
            if (flag) {
                res[i] = 1;
            } else {
                res[i] = 0;
            }
        }
        this.Tmatrices.add(res);
        return res;
    }

    @Override
    public Integer[] NextMarkingMatrix(Integer[][] d, Integer[] transitionMatrix, Integer[] markingMatrix) {
        Integer[][] res = new Integer[1][markingMatrix.length];
        Integer[][] Mtmp = new Integer[1][markingMatrix.length];
        Integer[][] Ttmp = new Integer[1][transitionMatrix.length];
        for (int i = 0; i < markingMatrix.length; i++) {
            Mtmp[0][i] = markingMatrix[i];
        }
        for (int i = 0; i < transitionMatrix.length; i++) {
            Ttmp[0][i] = transitionMatrix[i];
        }
        Integer[][] mul = Matrix.multiplicar(Ttmp, d);

        res = Matrix.addMatricesInteger(mul, Mtmp);
        return res[0];
    }

    /////// TEST ///////
    /**
     * 
     * @param matrix
     */
    public void __showMatrixInteger_mxn(Integer[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.format("%3d", matrix[i][j]);
            }
            System.out.println("");
        }
    }

    public void __showMatrixInteger_mxn(Integer[][] matrix, int row) {
        for (int i = 0; i < ((matrix.length < row) ? matrix.length : row); i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.format("%3d", matrix[i][j]);
            }
            System.out.println("");
        }
    }

    public void __showMatrixInteger_1xn(Integer[] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.format("%3d", matrix[i]);
        }
        System.out.println("");
    }

    public void __show() {
        String[] list = { "P", "M0", "T", "F", "M" };
        Random r = new Random();
        __show(list[r.nextInt(list.length)]);
    }

    /**
     * 
     * @param s
     */
    public void __show(String s) {
        switch (s) {
        case "P":
            _showP();
            break;
        case "M0":
            _showM0();
            break;
        case "M":
            _showM();
            break;
        case "T":
            _showT();
            break;
        case "F":
            _showF();
            break;
        }
    }

    /////
    private void _showP() {
        for (String i : this.P) {
            System.out.println(i);
        }
    }

    private void _showM0() {
        for (Integer i : this.M0) {
            System.out.println(i);
        }
    }

    private void _showM() {
        for (Integer i : this.M) {
            System.out.println(i);
        }
    }

    private void _showT() {
        for (String i : this.T) {
            System.out.println(i);
        }
    }

    private void _showF() {
        for (Pair i : this.F) {
            System.out.println(i.getKey() + ", " + i.getValue());
        }
    }

}