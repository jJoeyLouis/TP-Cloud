package org.example;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

//import org.example.CodeProf.s3.S3UploadFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.*;

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
        //String currentDirectory = System.getProperty("user.dir");
        String csvFile = this.nameFichier ;

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
            //createCSV();
            //S3UploadFile();
            uploadCSVToS3("mybucket44447", this.nameFichier);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public byte[] createCSVData() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
    
        writer.println("Store;" + this.store + ";" + this.profitStore);
        writer.println("Product;Quantity;Price;Profit");
        for (Map.Entry<String, InfoProduct> entry : this.products.entrySet()) {
            String nameProduct = entry.getKey();
            InfoProduct product = entry.getValue();
            writer.println(nameProduct + ";" + product.getQuantity() + ";" + product.getPrice() + ";" + product.getProfitTot());
        }
    
        writer.flush();
        return baos.toByteArray();
    }

    public void uploadCSVToS3(String bucketName, String filename) {
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder().region(region).build();

        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketResponse = s3.listBuckets(listBucketsRequest);

        if ((listBucketResponse.hasBuckets()) && (listBucketResponse.buckets().stream().noneMatch(x -> x.name().equals(bucketName)))) {
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder().bucket(bucketName).build();
            s3.createBucket(bucketRequest);
        }

        byte[] csvData = createCSVData();
        PutObjectRequest putOb = PutObjectRequest.builder().bucket(bucketName).key(filename).build();
        s3.putObject(putOb, RequestBody.fromBytes(csvData));
    }

    // public void CSVWriter(){
    //     String currentDirectory = System.getProperty("user.dir");
    //     String csvFile = currentDirectory + "/src/main/resources/worker-data/" + this.nameFichier ;

    //     try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {

    //         writer.println("Store;"+this.store+";"+this.profitStore) ;
    //         writer.println("Product;Quantity;Price;Profit");
    //         for (Map.Entry<String, InfoProduct> entry : this.products.entrySet()) {
    //             String nameProduct = entry.getKey();
    //             InfoProduct product = entry.getValue();
    //             writer.println(nameProduct+";"+product.getQuantity()+";"+product.getPrice()+";"+product.getProfitTot());
    //         }

    //         // System.out.println("Le fichier CSV a été créé avec succès!");

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }

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