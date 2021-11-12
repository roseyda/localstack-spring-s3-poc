package com.njgibbon.localstacksprings3poc.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@RestController
@RequestMapping(value = "/", produces = "text/plain")
public class Controller {

	@Autowired
	private AmazonS3 s3;

	@ResponseBody
	@GetMapping("/")
	public String main() {
		return "localstack-spring-s3-poc";
	}

	@ResponseBody
	@GetMapping("/hello")
	public String hello() {
		return "Hello World!";
	}

	@ResponseBody
	@GetMapping("/listBuckets")
	public String listBuckets() {
		return stringifyListBuckets();
	}

	@ResponseBody
	@GetMapping("/listObjects")
	public String listObjects(@RequestParam("bucketName") String bucketName) {
		return stringifyListObjects(bucketName);
	}

	@ResponseBody
	@GetMapping("/readObject")
	public String readObject(@RequestParam("bucketName") String bucketName,
			@RequestParam("objectName") String objectName) {
		return stringifyContentsOfBucketObject(bucketName, objectName);
	}

	public String stringifyContentsOfBucketObject(String bucketName, String objectName) {
		String returnString = "";

		try {
			S3Object o = s3.getObject(new GetObjectRequest(bucketName, objectName));
			returnString = readTextInputStream(o.getObjectContent());
		} catch (AmazonServiceException e) {
			returnString = "oops";
			System.err.println(e.getErrorMessage());
		} catch (FileNotFoundException e) {
			returnString = "oops";
			System.err.println(e.getMessage());
		} catch (IOException e) {
			returnString = "oops";
			System.err.println(e.getMessage());
		}
		return returnString;
	}

	public String stringifyListBuckets() {
		String returnString = "";

		List<Bucket> buckets = s3.listBuckets();
		System.out.println("Your Amazon S3 buckets are:");
		for (Bucket b : buckets) {
			System.out.println("* " + b.getName());
			returnString = returnString + b.getName() + " ";
		}

		return returnString;
	}

	public String stringifyListObjects(String bucketName) {
		String returnString = "";

		ListObjectsV2Result result = s3.listObjectsV2(bucketName);
		List<S3ObjectSummary> objects = result.getObjectSummaries();
		for (S3ObjectSummary os : objects) {
			System.out.println("* " + os.getKey());
			returnString = returnString + os.getKey() + " ";
		}

		return returnString;
	}

	private String readTextInputStream(InputStream input) throws IOException {
		String returnString = "";
		// Read the text input stream one line at a time and display each line.
		// And concat
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			returnString = returnString + line + " ";
		}
		System.out.println();
		return returnString;
	}
}
