package com.njgibbon.localstacksprings3poc.app;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class Config {
	
	@Autowired
	private Environment environment;
	
	private final static String LOCAKSTACK_PROFIL = "localstack";
	
	// TODO: set as properties
	private final static String S3_SERVICE_ENDPOINT = "http://localhost:4566/";
	private final static String S3_SERVICE_REGION= "eu-west-1";
	private final static String NON_PROXY_HOST = "localhost|127.0.0.1|10.0.2.15";

	@Bean
	public AmazonS3 getAmazonS3() {
		AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(S3_SERVICE_ENDPOINT, S3_SERVICE_REGION))
				.withPathStyleAccessEnabled(true);
		
		if (Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> (LOCAKSTACK_PROFIL.equalsIgnoreCase(profile)))) {
			builder.withClientConfiguration(new ClientConfiguration()
					.withNonProxyHosts(NON_PROXY_HOST));
		}
		
		return builder.build();
	}
}
