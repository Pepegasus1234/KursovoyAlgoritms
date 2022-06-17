import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DijkstrasAlgorithmDHeap {

  // Класс Edge представляет собой соединенное ребро, 
  // в котором to - конечная вершина куда направляет ребро, cost - вес ребра
  public static class Edge {
    int to;
    double cost;

    public Edge(int to, double cost) {
      this.to = to;
      this.cost = cost;
    }
  }
  public static void main(String[] args) {
    new DijkstrasAlgorithmDHeap(5);
    addEdge(0, 1, 5);
    addEdge(0, 2, 7);
    addEdge(0, 3, 2);
    
    addEdge(1, 3, 5);
    addEdge(1, 2, 1);

    addEdge(2, 1, 4);
    addEdge(2, 4, 2);
    
    addEdge(3, 4, 2);
    addEdge(3, 1, 9);

    addEdge(4, 2, 1);

    int size = getGraph().size();

    for (int i=0;i<size;i++) {
    Iterator <Edge> iterator2 = getGraph().get(i).iterator();
    Iterator <Edge> iterator1 = getGraph().get(i).iterator();
    
    System.out.print("Node "+i+" is connected");
      while (iterator2.hasNext() && iterator1.hasNext()) {
        System.out.print(", to node " + iterator1.next().to +", with a cost of: "+iterator2.next().cost);    
      }
      System.out.println("");
    }

    double[] result = dijkstra(0);
    System.out.println("\nThe shortest paths are distributed like this:");
    System.out.println(Arrays.toString(result));
    for (int i=0;i<dist.length;i++) {
      System.out.printf("To get to the node %d it costs %.0f\n", i, result[i]);
    }
  }
  
  private static int n;

  static int edgeCount;
  static double[] dist;
  static Integer[] prev;
  static List<List<Edge>> graph;

  // Аргумент n задает количество вершин, которые будут в нашем графе
  public DijkstrasAlgorithmDHeap(int n) {
    this.n = n;
    createEmptyGraph();
  }

  // Как только было заданно количество вершин, этот метод создает матрицу смежности, которая и будет строить граф
  private void createEmptyGraph() {
    graph = new ArrayList<>(n);
    for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
  }

  // Метод позволяющий добавлять ребра в граф ! ВСЕГДА НЕОБХОДИМО ЗАДАВАТЬ cost > 0 !
  public static void addEdge(int from, int to, int cost) {
    edgeCount++;
    graph.get(from).add(new Edge(to, cost));
  }

  // Метод позволяющий вернуть созданный граф
  public static List<List<Edge>> getGraph() {
    return graph;
  }
  
  // Метод вызывающий сам алгоритм нахождения пути
  public static double[] dijkstra(int start) {

    int degree = edgeCount / n;
    // Создаем индексированную очередь с приоритетом для хранения непосещенных вершин графа
    // Аргумент degree указывает на то сколько детей может быть у каждой вершины в двоичной куче, на то она и двоичная =)
    MinIndexedDHeap<Double> ipq = new MinIndexedDHeap<>(degree, n);
    // Задаем стартовую вершину графа start = 0, с расстоянием равным также 0
    ipq.insert(start, 0.0);

    // Создаем массив хранящий минимальное расстояние до каждого из узлов
    dist = new double[n];
    // До инициализации этот массив заполняется значением +∞
    Arrays.fill(dist, Double.POSITIVE_INFINITY);
    // Инициализируем первый элемент массива и задаем ему расстояние 0
    dist[start] = 0.0;

    // Данные массивы и переменные позволяют построить кратчайшие маршруты если они нам понадобятся
    boolean[] visited = new boolean[n];
    prev = new Integer[n];

    // Пока наша очередь не пуста
    while (!ipq.isEmpty()) {
      // Получим ID вершины с минимальным расстоянием из очереди  
      int nodeId = ipq.peekMinKeyIndex();
      // Отмечаем ее как посещенную
      visited[nodeId] = true;
      // Получаем минимальное расстояние до этой ноды
      double minValue = ipq.pollMinValue();

      // Если уже найдено лучшее расстояние до этой ноды то
      // игнорируем блок кода ниже и возвращаемся в старт цикла while
      if (minValue > dist[nodeId]) continue;

      // Получим все ребра исходящие из ноды X 
      for (Edge edge : graph.get(nodeId)) {

        // Если нода куда ведет это ребро уже посещенная, 
        // игнорируем блок кода ниже и возвращаемся в старт цикла for
        if (visited[edge.to]) continue;

        // Получаем обновленное расстояние идущее из текущей ноды в ноду куда ведет это ребро
        // делается это суммированием уже известного расстояния до текущей ноды и веса ребра 
        double newDist = dist[nodeId] + edge.cost;

        // Если обновленное растояние до ноды куда ведет это ребро меньше чем
        // уже известное, выполняем код условия ниже, если же больше пропускаем этот код
        if (newDist < dist[edge.to]) {
          prev[edge.to] = nodeId;
          // Добавляем в массив минимальных расстояний обновленное расстояние до ноды
          dist[edge.to] = newDist;
          // Если наша очередь с приоритетом не содержит запись о ноде куда вело это ребро, 
          // добавляем информацию о том что можно проложить путь до нее с таким то расстоянием
          if (!ipq.contains(edge.to)) ipq.insert(edge.to, newDist);
          // Если же нет то обновляем информацию о новом оптимальном расстоянии
          else ipq.decrease(edge.to, newDist);
        }
      }
    }
    // Если же в очереди с приоритетом не оказалось записей, возвращаем массив dist
    return dist;
  }
  
  private static class MinIndexedDHeap<T extends Comparable<T>> {

    // Текущее количество элементов в двоичной куче.
    private int sz;

    // Максимальное количество элементов в двоичной куче.
    private final int N;

    // Количество детей которое может иметь каждая вершина в куче.
    private final int D;

    // Массив для того чтобы видеть детей и родителей каждой ноды.
    private final int[] child, parent;

    // Позиционная мапа позволяющее отслеживать индексы вершин ki и куда этот индекс ведет исходя из очереди
    public final int[] pm;

    // Инверсионная мапа хранит индексы ключей ki в диапазоне [0, sz] только в обратном порядке чем pm
    public final int[] im;

    // Значения связанные с ключами. Этот массив проиндексирован ki
    public final Object[] values;

    // Инициализирует двоичную кучу с максимальным размером maxSize
    public MinIndexedDHeap(int degree, int maxSize) {
      if (maxSize <= 0) throw new IllegalArgumentException("maxSize <= 0");

      D = max(2, degree);
      N = max(D + 1, maxSize);

      im = new int[N];
      pm = new int[N];
      child = new int[N];
      parent = new int[N];
      values = new Object[N];

      for (int i = 0; i < N; i++) {
        parent[i] = (i - 1) / D;
        child[i] = i * D + 1;
        pm[i] = im[i] = -1;
      }
    }

    public int size() {
      return sz;
    }

    public boolean isEmpty() {
      return sz == 0;
    }

    public boolean contains(int ki) {
      keyInBoundsOrThrow(ki);
      return pm[ki] != -1;
    }

    public int peekMinKeyIndex() {
      isNotEmptyOrThrow();
      return im[0];
    }

    public int pollMinKeyIndex() {
      int minki = peekMinKeyIndex();
      delete(minki);
      return minki;
    }

    @SuppressWarnings("unchecked")
    public T peekMinValue() {
      isNotEmptyOrThrow();
      return (T) values[im[0]];
    }

    public T pollMinValue() {
      T minValue = peekMinValue();
      delete(peekMinKeyIndex());
      return minValue;
    }

    public void insert(int ki, T value) {
      if (contains(ki)) throw new IllegalArgumentException("index already exists; received: " + ki);
      valueNotNullOrThrow(value);
      pm[ki] = sz;
      im[sz] = ki;
      values[ki] = value;
      swim(sz++);
    }

    @SuppressWarnings("unchecked")
    public T valueOf(int ki) {
      keyExistsOrThrow(ki);
      return (T) values[ki];
    }

    @SuppressWarnings("unchecked")
    public T delete(int ki) {
      keyExistsOrThrow(ki);
      final int i = pm[ki];
      swap(i, --sz);
      sink(i);
      swim(i);
      T value = (T) values[ki];
      values[ki] = null;
      pm[ki] = -1;
      im[sz] = -1;
      return value;
    }

    @SuppressWarnings("unchecked")
    public T update(int ki, T value) {
      keyExistsAndValueNotNullOrThrow(ki, value);
      final int i = pm[ki];
      T oldValue = (T) values[ki];
      values[ki] = value;
      sink(i);
      swim(i);
      return oldValue;
    }

    public void decrease(int ki, T value) {
      keyExistsAndValueNotNullOrThrow(ki, value);
      if (less(value, values[ki])) {
        values[ki] = value;
        swim(pm[ki]);
      }
    }

    public void increase(int ki, T value) {
      keyExistsAndValueNotNullOrThrow(ki, value);
      if (less(values[ki], value)) {
        values[ki] = value;
        sink(pm[ki]);
      }
    }

    private void sink(int i) {
      for (int j = minChild(i); j != -1; ) {
        swap(i, j);
        i = j;
        j = minChild(i);
      }
    }

    private void swim(int i) {
      while (less(i, parent[i])) {
        swap(i, parent[i]);
        i = parent[i];
      }
    }

    private int minChild(int i) {
      int index = -1, from = child[i], to = min(sz, from + D);
      for (int j = from; j < to; j++) if (less(j, i)) index = i = j;
      return index;
    }

    private void swap(int i, int j) {
      pm[im[j]] = i;
      pm[im[i]] = j;
      int tmp = im[i];
      im[i] = im[j];
      im[j] = tmp;
    }

    @SuppressWarnings("unchecked")
    private boolean less(int i, int j) {
      return ((Comparable<? super T>) values[im[i]]).compareTo((T) values[im[j]]) < 0;
    }

    @SuppressWarnings("unchecked")
    private boolean less(Object obj1, Object obj2) {
      return ((Comparable<? super T>) obj1).compareTo((T) obj2) < 0;
    }

    @Override
    public String toString() {
      List<Integer> lst = new ArrayList<>(sz);
      for (int i = 0; i < sz; i++) lst.add(im[i]);
      return lst.toString();
    }

    private void isNotEmptyOrThrow() {
      if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
    }

    private void keyExistsAndValueNotNullOrThrow(int ki, Object value) {
      keyExistsOrThrow(ki);
      valueNotNullOrThrow(value);
    }

    private void keyExistsOrThrow(int ki) {
      if (!contains(ki)) throw new NoSuchElementException("Index does not exist; received: " + ki);
    }

    private void valueNotNullOrThrow(Object value) {
      if (value == null) throw new IllegalArgumentException("value cannot be null");
    }

    private void keyInBoundsOrThrow(int ki) {
      if (ki < 0 || ki >= N)
        throw new IllegalArgumentException("Key index out of bounds; received: " + ki);
    }
  }

}