import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class modImpExp {

    public float[][] d; // Matriz de disimilitud
    public String[] nameObjects; // Nombres de los objetos
    public int nObjects; // Número de objetos
    public int nVariables; // Número de variables

    // Importa la matriz de costos desde un archivo
    public void importMatrixCost(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            nObjects = getNRows(fileName) - 1;
            nVariables = nObjects;
            nameObjects = new String[nObjects];
            d = new float[nObjects][nObjects];

            String line;
            br.readLine(); // Saltar la primera línea (encabezado)
            for (int i = 0; i < nObjects; i++) {
                line = br.readLine();
                if (line == null || line.trim().isEmpty()) {
                    throw new IOException("Línea vacía en el archivo en la posición " + (i + 1));
                }
                String[] parts = line.split("\\s+");
                nameObjects[i] = parts[0];
                for (int j = 0; j < nObjects; j++) {
                    if (parts.length <= j + 1 || parts[j + 1].trim().isEmpty()) {
                        throw new IOException(
                                "Valor faltante en la matriz en la fila " + (i + 1) + ", columna " + (j + 1));
                    }
                    d[i][j] = Float.parseFloat(parts[j + 1]);
                }
            }
        }
    }

    // Obtiene el número de filas en un archivo
    public int getNRows(String fileName) throws IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while (br.readLine() != null) {
                count++;
            }
        }
        return count;
    }
}