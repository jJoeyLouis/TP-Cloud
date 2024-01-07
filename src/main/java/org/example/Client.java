package fr.emse.s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/*
 * Pour VS CODE :
 * - Run et Debug / ouvrir launch.json/ ajouter : 
 * "args": ["21112023", "C:/Users/noset/Documents/MajeureInfo/TP-Cloud/src/main/resources/sales-data/", "01-10-2022-store1.csv"]
 * lancer Client.java
 * 
 * Problème : je n'arrive pas à récupérer mes security credentials
 */
public class Client {

  public static void main(String[] args) {
    Region region = Region.US_EAST_1;

    /*if (args.length < 3) {
      System.out.println(
          "Missing the Bucket Name, File Path, or File Name arguments");
      System.exit(1);
    }*/

    // String bucketName = args[0];
    // String path = args[1];
    // String filename = args[2];

    String bucketName = "897347856983209";
    // String path = "C:\Users\noset\Documents\MajeureInfo\aws-cloud\src\main\resources";
    String path = "C:/Users/noset/Documents/MajeureInfo/aws-cloud/src/main/resources/" ;
    String filename = "01-10-2022-store3.csv";

    S3Client s3 = S3Client.builder().region(region).build();

    ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder()
        .build();
    ListBucketsResponse listBucketResponse = s3.listBuckets(listBucketsRequest);

    if ((listBucketResponse.hasBuckets()) && (listBucketResponse.buckets()
        .stream().noneMatch(x -> x.name().equals(bucketName)))) {

      CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
          .bucket(bucketName).build();

      s3.createBucket(bucketRequest);
    }

    PutObjectRequest putOb = PutObjectRequest.builder().bucket(bucketName)
        .key(filename).build();
    s3.putObject(putOb,
        RequestBody.fromBytes(getObjectFile(path + File.separator + filename)));
  }


  private static byte[] getObjectFile(String filePath) {

    FileInputStream fileInputStream = null;
    byte[] bytesArray = null;

    try {
      File file = new File(filePath);
      bytesArray = new byte[(int) file.length()];
      fileInputStream = new FileInputStream(file);
      fileInputStream.read(bytesArray);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (fileInputStream != null) {
        try {
          fileInputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return bytesArray;
  }
}
