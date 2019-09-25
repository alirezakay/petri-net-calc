
/**
 * Matrix
 */
import java.util.*;

public class Matrix {
    public static Integer[][] multiplicar(Integer[][] A, Integer[][] B) {
        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }
        Integer[][] product = new Integer[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                product[i][j] = 0;
            }
        }
        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    product[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return product;
    }

    public static Integer[][] subMatricesInteger(Integer[][] A, Integer[][] B) {
        Integer[][] sub = new Integer[A.length][];
        for (int i = 0; i < A.length; i++) {
            sub[i] = new Integer[A[i].length];
            for (int j = 0; j < A[i].length; j++) {
                sub[i][j] = A[i][j] - B[i][j];
            }
        }
        return sub;
    }

    public static Integer[][] addMatricesInteger(Integer[][] A, Integer[][] B) {
        Integer[][] sum = new Integer[A.length][];
        for (int i = 0; i < A.length; i++) {
            sum[i] = new Integer[A[i].length];
            for (int j = 0; j < A[i].length; j++) {
                sum[i][j] = A[i][j] + B[i][j];
            }
        }
        return sum;
    }
}