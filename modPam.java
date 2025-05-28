public class modPam {
    public float[][] d; // Matriz de disimilitud
    public int nObjects; // Número de objetos
    public int k; // Número de clusters
    public int[] medoid; // Centroides
    public Clusters[] sClusters; // Estructura de clusters
    public Group[] kClusters; // Grupos de clusters
    public float dCostoSolucion; // Costo de la solución

    // Estructura para almacenar clusters
    public static class Clusters {
        int item; // Objeto
        int cluster; // Cluster al que pertenece
    }

    // Estructura para almacenar grupos
    public static class Group {
        int[] items; // Elementos del cluster
        int n; // Número de elementos

        public Group() {
            n = 0; // Inicializar el contador de elementos
            items = new int[0]; // Inicializar el arreglo de elementos
        }
    }

    // Algoritmo PAM
    public void pam(int nclusters) {
        k = nclusters;
        kClusters = new Group[k];
        sClusters = new Clusters[nObjects];

        // Inicializar los grupos de clusters
        for (int i = 0; i < k; i++) {
            kClusters[i] = new Group();
        }

        // Inicializar la estructura de clusters
        for (int i = 0; i < nObjects; i++) {
            sClusters[i] = new Clusters();
        }

        medoid = new int[k];

        calculateM1(); // Calcular el primer centroide
        for (int i = 1; i < k; i++) {
            buildInitMedoids(i); // Calcular el resto de los centroides
        }

        stepSwap(); // Intercambio de centroides
        calculateClusters(); // Calcular los clusters
    }

    // Calcula el primer centroide
    public void calculateM1() {
        int m1 = 0;
        float smallest = Float.MAX_VALUE;
        for (int i = 0; i < nObjects; i++) {
            float sum = 0;
            for (int j = 0; j < nObjects; j++) {
                sum += d[i][j];
            }
            if (sum < smallest) {
                smallest = sum;
                m1 = i;
            }
        }
        medoid[0] = m1;
    }

    // Construye los centroides iniciales
    public void buildInitMedoids(int i) {
        float smallest = Float.MAX_VALUE;
        int m = 0;
        for (int obj = 0; obj < nObjects; obj++) {
            if (!inMedoids(obj, medoid)) {
                medoid[i] = obj;
                float sum = 0;
                for (int j = 0; j < nObjects; j++) {
                    if (!inMedoids(j, medoid)) {
                        sum += dMinimal(j, medoid, i);
                    }
                }
                if (sum < smallest) {
                    m = obj;
                    smallest = sum;
                }
            }
        }
        medoid[i] = m;
    }

    // Verifica si un objeto es un centroide
    public boolean inMedoids(int obj, int[] m) {
        for (int i = 0; i < k; i++) {
            if (obj == m[i]) {
                return true;
            }
        }
        return false;
    }

    // Devuelve la disimilitud mínima de un objeto a los centroides
    public float dMinimal(int obj, int[] m, int n) {
        float minimal = d[obj][m[0]];
        for (int i = 1; i < n; i++) {
            if (d[obj][m[i]] < minimal) {
                minimal = d[obj][m[i]];
            }
        }
        return minimal;
    }

    // Intercambio de centroides
    public void stepSwap() {
        int[] centroid = new int[k];
        int[] v = new int[nObjects - k];
        float minimal = objectiveFunction(medoid);
        int i = 0;
        while (i < k) {
            System.arraycopy(medoid, 0, centroid, 0, k);
            setNoMedoids(v);
            int n = nObjects - k;
            boolean ret = false;
            for (int j = 0; j < n; j++) {
                centroid[i] = v[j];
                float totald = objectiveFunction(centroid);
                if (totald < minimal) {
                    medoid[i] = v[j];
                    minimal = totald;
                    ret = true;
                }
            }
            if (ret && i != 0) {
                moveInitPos(medoid, i);
                i = 1;
            } else {
                i++;
            }
        }
        dCostoSolucion = minimal;
    }

    // Función objetivo
    public float objectiveFunction(int[] m) {
        float sum = 0;
        for (int i = 0; i < nObjects; i++) {
            if (!inMedoids(i, m)) {
                sum += dMinimal(i, m, k);
            }
        }
        return sum;
    }

    // Copia los objetos que no son centroides
    public void setNoMedoids(int[] v) {
        int n = 0;
        for (int i = 0; i < nObjects; i++) {
            if (!inMedoids(i, medoid)) {
                v[n] = i;
                n++;
            }
        }
    }

    // Mueve un centroide a la posición inicial
    public void moveInitPos(int[] m, int pos) {
        int[] aux = new int[k];
        int j = 1;
        for (int i = 0; i < k; i++) {
            if (i == pos) {
                aux[0] = m[i];
            } else {
                aux[j] = m[i];
                j++;
            }
        }
        System.arraycopy(aux, 0, m, 0, k);
    }

    // Calcula los clusters
    public void calculateClusters() {
        int[] v = new int[nObjects - k];
        setNoMedoids(v);
        int pos = 0;
        for (int i = 0; i < nObjects - k; i++) {
            float min = d[v[i]][medoid[0]];
            int g = 0;
            for (int j = 1; j < k; j++) {
                if (d[v[i]][medoid[j]] < min) {
                    min = d[v[i]][medoid[j]];
                    g = j;
                }
            }
            kClusters[g].n++;
            sClusters[pos].cluster = g;
            sClusters[pos].item = v[i];
            pos++;
        }
        for (int i = 0; i < k; i++) {
            sClusters[pos].cluster = i;
            sClusters[pos].item = medoid[i];
            pos++;
        }
        for (int i = 0; i < k; i++) {
            kClusters[i].items = new int[kClusters[i].n + 1];
            getCluster(i, kClusters[i].items, kClusters[i].n);
        }
    }

    // Obtiene los elementos de un cluster
    public void getCluster(int noCluster, int[] items, int noItems) {
        items[0] = medoid[noCluster];
        int pos = 1;
        for (int i = 0; i < nObjects - k; i++) {
            if (sClusters[i].cluster == noCluster) {
                items[pos] = sClusters[i].item;
                pos++;
            }
        }
        noItems = pos - 1;
    }
}