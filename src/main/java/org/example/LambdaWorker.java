package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

public class LambdaWorker implements RequestHandler<S3Event, String>{
	public String handleRequest(S3Event event, Context context) {
		S3EventNotificationRecord record = event.getRecords().get(0);
		String bucketName = record.getS3().getBucket().getName();
		String fileKey = record.getS3().getObject().getUrlDecodedKey();
	
		//String currentDirectory = System.getProperty("user.dir");
        //String csvFile = this.nameFichier ;

        // Define separators of rows, columns
		//String nameFichier ;
		double profitStore = 0.0;
		String store ;
		Map<String, InfoProduct> products = new HashMap<>() ;
        String line = "";
        String cvsSplitBy = ";";
        Integer compt = 0 ;

		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
		try (final S3Object s3Object = s3Client.getObject(bucketName, fileKey);
			final InputStreamReader streamReader = new InputStreamReader(s3Object.getObjectContent(),
				StandardCharsets.UTF_8);
			final BufferedReader reader = new BufferedReader(streamReader)) {
	

				while ((line = reader.readLine()) != null) {
					String[] data = line.split(cvsSplitBy); // Créer un tableau (de la ligne) de type String
	
					// Header not interesting
					if(!data[3].equals("Quantity")) {
						if(compt == 0){
							store = data[1] ;
							compt = 1 ;
						}
						// Increment profit for this store
						profitStore += Math.round((Double.parseDouble(data[3]) * Double.parseDouble(data[6]))* 100.0) / 100.0 ;
	
						// Create, or add (if already exits) the <Map> for this product
						InfoProduct product ;
						if (!products.containsKey(data[2])) {
							product = new InfoProduct(Integer.parseInt(data[3]), Double.parseDouble(data[7]), Double.parseDouble(data[6])) ;
						} else {
							product = products.get(data[2]) ;
							product.add(Integer.parseInt(data[3]), Double.parseDouble(data[7]), Double.parseDouble(data[6])) ;
						}
						products.put(data[2],product) ; // Si clef n'existe pas, créer, sinon remplace
					}
				}
				//createCSV();
				//S3UploadFile();
				String[] keyParts = fileKey.split("/");
    			String fileName = keyParts[keyParts.length - 1];
				uploadCSVToS3("mybucket44447", fileName);


	  }
	  catch (final IOException e) {
		System.out.println("IOException: " + e.getMessage());
	  }
	  System.out.println("Finished... processing file");
    return "Ok";
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
}
