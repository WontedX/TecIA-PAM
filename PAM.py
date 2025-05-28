from datetime import datetime

# --------- Clases internas ---------


class Group:
    def __init__(self):
        self.items = []
        self.n = 0


class Clusters:
    def __init__(self):
        self.item = 0
        self.cluster = 0

# --------- Algoritmo PAM ---------


class PAM:
    def __init__(self):
        self.d = []  # Matriz de disimilitud
        self.n_objects = 0
        self.k = 0
        self.medoid = []
        self.s_clusters = []
        self.k_clusters = []
        self.d_costo_solucion = 0

    def load_matrix_from_file(self, file_name):
        with open(file_name, 'r') as f:
            data = f.read().split()
            data = [float(x) for x in data]

        # Determinar tamaño de la matriz cuadrada
        n = int(len(data) ** 0.5)
        self.n_objects = n

        self.d = []
        for i in range(n):
            row = data[i * n: (i + 1) * n]
            self.d.append(row)

    def pam(self, n_clusters):
        self.k = n_clusters
        self.medoid = [0] * self.k
        self.k_clusters = [Group() for _ in range(self.k)]
        self.s_clusters = [Clusters() for _ in range(self.n_objects)]

        self.calculate_m1()
        for i in range(1, self.k):
            self.build_init_medoids(i)

        self.step_swap()
        self.calculate_clusters()

    def calculate_m1(self):
        smallest = float('inf')
        m1 = 0
        for i in range(self.n_objects):
            total = sum(self.d[i])
            if total < smallest:
                smallest = total
                m1 = i
        self.medoid[0] = m1

    def build_init_medoids(self, i):
        smallest = float('inf')
        m = 0
        for obj in range(self.n_objects):
            if not self.in_medoids(obj):
                self.medoid[i] = obj
                total = sum(self.d_minimal(j, self.medoid, i)
                            for j in range(self.n_objects) if not self.in_medoids(j))
                if total < smallest:
                    smallest = total
                    m = obj
        self.medoid[i] = m

    def in_medoids(self, obj):
        return obj in self.medoid

    def d_minimal(self, obj, m, n):
        return min(self.d[obj][m[j]] for j in range(n))

    def step_swap(self):
        minimal = self.objective_function(self.medoid)
        i = 0
        while i < self.k:
            centroid = self.medoid[:]
            non_medoids = [j for j in range(
                self.n_objects) if j not in self.medoid]
            ret = False
            for v in non_medoids:
                centroid[i] = v
                total_d = self.objective_function(centroid)
                if total_d < minimal:
                    self.medoid[i] = v
                    minimal = total_d
                    ret = True
            if ret and i != 0:
                self.move_init_pos(i)
                i = 1
            else:
                i += 1
        self.d_costo_solucion = minimal

    def objective_function(self, m):
        return sum(self.d_minimal(i, m, self.k) for i in range(self.n_objects) if i not in m)

    def move_init_pos(self, pos):
        m = self.medoid
        self.medoid = [m[pos]] + m[:pos] + m[pos+1:]

    def calculate_clusters(self):
        non_medoids = [i for i in range(
            self.n_objects) if i not in self.medoid]
        pos = 0
        for i in non_medoids:
            dists = [self.d[i][m] for m in self.medoid]
            g = dists.index(min(dists))
            self.k_clusters[g].n += 1
            self.s_clusters[pos].item = i
            self.s_clusters[pos].cluster = g
            pos += 1
        for i in range(self.k):
            self.s_clusters[pos].item = self.medoid[i]
            self.s_clusters[pos].cluster = i
            pos += 1
        for i in range(self.k):
            self.k_clusters[i].items = self.get_cluster(i)

    def get_cluster(self, cluster_no):
        return [self.medoid[cluster_no]] + [
            s.item for s in self.s_clusters if s.cluster == cluster_no and s.item != self.medoid[cluster_no]
        ]


# --------- Programa principal ---------
if __name__ == "__main__":
    pam = PAM()

    archivo = input(
        "Archivo de disimilitud (sin nombres ni encabezados): ").strip()
    n_clusters = int(input("Número de clusters: "))
    archivo_resultado = input("Archivo de salida: ").strip()

    pam.load_matrix_from_file(archivo)

    inicio = datetime.now().strftime("%H:%M:%S")
    pam.pam(n_clusters)
    fin = datetime.now().strftime("%H:%M:%S")

    print("Hora inicial:", inicio)
    print("Hora final:", fin)
    print("Costo de la solución:", pam.d_costo_solucion)

    with open(archivo_resultado, 'w') as f:
        for i, grupo in enumerate(pam.k_clusters):
            f.write(f"Cluster {i + 1}:\n")
            for idx in grupo.items:
                f.write(f"obj{idx + 1}\n")
            f.write("\n")
    print("Resultado guardado en", archivo_resultado)
