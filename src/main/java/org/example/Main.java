package org.example;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        /* To automate the task of call Worker (class)
        * I got your current path,
        * I ask you to put CSV file in </src/main/resources/sales-data> directory
        */
        String currentDirectory = System.getProperty("user.dir");
        String dossier = currentDirectory + "/src/main/resources/sales-data/";
        File repertoire = new File(dossier);

        if (repertoire.isDirectory()) {
            File[] fichiers = repertoire.listFiles();
            if (fichiers != null) {
                for (File fichier : fichiers) {
                    if (fichier.isFile() && fichier.getName().endsWith(".csv")) {
                        // I call Worker (class)
                        Worker worker = new Worker(fichier.getName()) ;
                    }
                }
                /* I call Consolidator (class) after create all .csv file with Worker
                * You put the date in the constructor
                * Results are in /src/main/resources/result/
                 */
                Consolidator consolidator = new Consolidator("01-10-2022") ;
                System.out.println("Everything is good");
                System.out.println("Results are in '/src/main/resources/results/' directory");
            }
        } else {
            System.out.println("The path isn't a directory.");
        }

    }
}