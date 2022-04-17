package modele;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GestionFichier {
    private File file;
    private HashMap<String, String> data;

    public GestionFichier(String fileName, String ...dataKeys) {
        this.data = new HashMap<>();
        for (String dataKey : dataKeys) {
            data.put(dataKey, "");
        }
        try {
            this.file = new File(fileName);
            this.file.createNewFile();
        }
        catch (Exception e) {
            System.err.println("Erreur dans le nom du fichier");
        }
    }

    public void saveDataFile(String key, String value) {
        // On modifie les données de notre objet avant de le sauvegarder dans un fichier
        this.data.put(key, value);

        // Dans le try -> Ferme le fichier quand il en sort
        try (BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            for (Map.Entry<String, String> dataVal : this.data.entrySet()) {
                fileWriter.write(dataVal.getKey() + "=" + dataVal.getValue());
                fileWriter.newLine();
            }
        }
        catch (Exception e) {
            System.err.println("Sauvegarde de fichier impossible : Fichier corrompu");
        }
    }

    public HashMap<String, String> getDataFile() {
        // Dans le try -> Ferme le fichier quand il en sort
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            this.data = new HashMap<>();
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] keyValue = line.split("=");
                this.data.put(keyValue[0], keyValue[1]);
            }
        }
        catch (Exception e) {
            System.err.println("Obtention de fichier impossible : Fichier corrompu");
        }
        return this.data;
    }
}
