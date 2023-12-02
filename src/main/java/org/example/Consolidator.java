package org.example;

import java.io.*;
import java.util.*;

public class Consolidator {
    private String date ;
    private Map<String, Double> stores = new HashMap<>() ;
    private Map<String, InfoProduct> products = new HashMap<>();
    public Consolidator(String date){
        this.date = date ;
        ReadFile();
    }

    public void ReadFile(){
        String currentDirectory = System.getProperty("user.dir");
        String dossier = currentDirectory + "/src/main/resources/worker-data/";
        File repertoire = new File(dossier);

        // Vérification si le chemin correspond à un répertoire existant
        if (repertoire.isDirectory()) {
            File[] fichiers = repertoire.listFiles();
            if (fichiers != null) {
                for (File fichier : fichiers) {
                    if (fichier.isFile() && fichier.getName().endsWith(".csv") && fichier.getName().startsWith(this.date)) {
                        // Define CSV separator (rows, columns)
                        String line = "";
                        String cvsSplitBy = ";";

                        try (BufferedReader br = new BufferedReader(new FileReader(currentDirectory + "/src/main/resources/worker-data/" + fichier.getName()))) {
                            while ((line = br.readLine()) != null) {
                                String[] data = line.split(cvsSplitBy);

                                // Create ["store1" => 873478.89 $] ...
                                if(data[0].equals("Store")){
                                    this.stores.put(data[1], Math.round(Double.parseDouble(data[2]) * 100.0) / 100.0) ;
                                } else if (data[0].equals("Product")) {
                                } else {
                                // Create ["nameProduct"=> [Quantity, Price, Profit]]
                                    InfoProduct product ;
                                    if (!this.products.containsKey(data[2])) {
                                        product = new InfoProduct(Integer.parseInt(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3])) ;
                                    } else {
                                        product = this.products.get(data[2]) ;
                                        product.add(Integer.parseInt(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3])) ;
                                    }
                                    this.products.put(data[0],product) ;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        WriteStores() ;
        WriteProducts() ;
    }

    public void WriteStores(){
        String currentDirectory = System.getProperty("user.dir");
        String fileName = currentDirectory + "/src/main/resources/result/" + "store.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            SortStores(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void WriteProducts(){
        String currentDirectory = System.getProperty("user.dir");
        String fileName = currentDirectory + "/src/main/resources/result/" + "products.csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, InfoProduct> entry : this.products.entrySet()) {
                String nameProduct = entry.getKey();
                InfoProduct product = entry.getValue();
                writer.println(nameProduct+";"+product.getQuantity()+";"+product.getPrice()+";"+product.getProfitTot());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SortStores(PrintWriter writer){
        /* Pas optimal niveau mémoire, car je dois créer
            - une liste (contenant les valeurs triées)
            - un HashMap (contenant les stores par ordre décroissant de bénéfice
         */
        List<Map.Entry<String, Double>> list = new ArrayList<>(this.stores.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            writer.println(entry.getKey() + ";" + entry.getValue());
        }
    }
}
