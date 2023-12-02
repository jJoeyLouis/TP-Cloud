package org.example;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Worker {
    private String nameFichier ;
    private double profitStore = 0.0;
    private String store ;
    private Map<String, InfoProduct> products = new HashMap<>() ;
    public Worker(String namefichier){
        this.nameFichier = namefichier ;
        CSVReader() ;
    }

    public void CSVReader(){
        String currentDirectory = System.getProperty("user.dir");
        String csvFile = currentDirectory + "/src/main/resources/sales-data/" + this.nameFichier ;

        // Define separators of rows, columns
        String line = "";
        String cvsSplitBy = ";";

        Map<String, InfoProduct> products = new HashMap<>();
        Integer compt = 0 ;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy); // Créer un tableau (de la ligne) de type String

                // Header not interesting
                if(!data[3].equals("Quantity")) {
                    if(compt == 0){
                        this.store = data[1] ;
                        compt = 1 ;
                    }
                    // Increment profit for this store
                    this.profitStore += Math.round((Double.parseDouble(data[3]) * Double.parseDouble(data[6]))* 100.0) / 100.0 ;

                    // Create, or add (if already exits) the <Map> for this product
                    InfoProduct product ;
                    if (!this.products.containsKey(data[2])) {
                        product = new InfoProduct(Integer.parseInt(data[3]), Double.parseDouble(data[7]), Double.parseDouble(data[6])) ;
                    } else {
                        product = this.products.get(data[2]) ;
                        product.add(Integer.parseInt(data[3]), Double.parseDouble(data[7]), Double.parseDouble(data[6])) ;
                    }
                    this.products.put(data[2],product) ; // Si clef n'existe pas, créer, sinon remplace
                }
            }
            CSVWriter();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void CSVWriter(){
        String currentDirectory = System.getProperty("user.dir");
        String csvFile = currentDirectory + "/src/main/resources/worker-data/" + this.nameFichier ;

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {

            writer.println("Store;"+this.store+";"+this.profitStore) ;
            writer.println("Product;Quantity;Price;Profit");
            for (Map.Entry<String, InfoProduct> entry : this.products.entrySet()) {
                String nameProduct = entry.getKey();
                InfoProduct product = entry.getValue();
                writer.println(nameProduct+";"+product.getQuantity()+";"+product.getPrice()+";"+product.getProfitTot());
            }

            // System.out.println("Le fichier CSV a été créé avec succès!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getProfitStore() {
        return profitStore;
    }
    public void setProfitStore(double profitStore) {
        this.profitStore = profitStore;
    }
    public Map<String, InfoProduct> getProducts() {
        return products;
    }
    public void setProducts(Map<String, InfoProduct> products) {
        this.products = products;
    }
}