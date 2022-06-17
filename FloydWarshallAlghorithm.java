// Импортируем библиотеки Java содержащие константы ∞ и -∞
// ∞ + ∞ = ∞, ∞ + x = ∞, -∞ + x = -∞ and ∞ + -∞ = NA
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;


public class FloydWarshallAlghorithm {

    // Кол-во вершин
  private int n;
    // Статус решен алгоритм или нет
  private boolean solved;
    // Матрица смежности графа
  private double[][] dp;

  // В качестве аргумента принимается матрица смежности графа
  public FloydWarshallAlghorithm(double[][] matrix) {
    n = matrix.length;
    dp = new double[n][n];

    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          dp[i][j] = matrix[i][j];
        }
    }
  }
  
  // Тело алгоритма
  public double[][] getApspMatrix() {
    solve();
    return dp;
  }

  // Выполняет алгоритм Флойда-Уоршелла
  public void solve() {
    if (solved) return;

    // Тело циклов использующие метод динамического программирования
    // подробности зачем нужна переменная k указано в ПЗ к курсовому проекту
    for (int k = 0; k < n; k++) {
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          if (dp[i][k] + dp[k][j] < dp[i][j]) {
            dp[i][j] = dp[i][k] + dp[k][j];
          }
        }
      }
    }

    // Обнаружение отрицательных циклов
    for (int k = 0; k < n; k++)
      for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
          if (dp[i][k] != POSITIVE_INFINITY && dp[k][j] != POSITIVE_INFINITY && dp[k][k] < 0) {
            dp[i][j] = NEGATIVE_INFINITY;
          }

    solved = true;
  }
  // Создает граф и в качестве аргумента принимается n, что означает количество вершин графа
  public static double[][] createGraph(int n) {
    // Создаем матрицу размером NxN
    double[][] matrix = new double[n][n];
    // Заполняем все значения в матрице +∞
    // у вершин не будет маршрутов к самому себе, поэтому диагональ = 0
    for (int i = 0; i < n; i++) {
      java.util.Arrays.fill(matrix[i], POSITIVE_INFINITY);
      matrix[i][i] = 0;
    }
    // Возвращаем матрицу смежности графа
    return matrix;
}

    public static void main(String[] args) {
    // Строим граф с количеством вершин = 7.
    int n = 7;
    // Создаем матрицу смежности
    double[][] m = createGraph(n);

    // Создаем ребра и назначаем им вес.
    m[0][1] = 2;
    m[0][2] = 5;
    m[0][6] = 10;
    m[1][2] = 2;
    m[1][4] = 11;
    m[2][6] = 2;
    m[6][5] = 11;
    m[4][5] = 1;
    m[5][4] = 2;
    m[3][1] = 6;
    m[5][0] = 2;


    // Объявляем начало алгоритма
    FloydWarshallAlghorithm solver = new FloydWarshallAlghorithm(m);
    // Помещаем в матрицу dist решенную проблему APSP 
    double[][] dist = solver.getApspMatrix();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i==j) continue;
                System.out.printf("This shortest path from node %d to node %d is %.3f\n", i, j, dist[i][j]);
            }
        }
    }
}