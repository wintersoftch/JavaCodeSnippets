package ch.wintersoft.java.aws.s3;

import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

/** Class to interact with AWS S3 buckets */
public class AWSS3Bucket {
  private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3Bucket.class);
  private final S3Client s3Client;

  public AWSS3Bucket(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  /**
   * Check if a bucket with the given name exists
   *
   * @param bucketName the name of the bucket to check
   * @return true if the bucket exists, false otherwise
   */
  public boolean checkBucketExists(String bucketName) {
    boolean exists = false;
    try {
      HeadBucketRequest headBucketRequest = HeadBucketRequest.builder().bucket(bucketName).build();
      HeadBucketResponse result = s3Client.headBucket(headBucketRequest);
      if (result.sdkHttpResponse().statusCode() == 200) {
        LOGGER.info("Bucket {} exists", bucketName);
        exists = true;
      }
    } catch (NoSuchBucketException e) {
      LOGGER.info("Bucket {} does not exist", bucketName);
    }
    return exists;
  }

  /**
   * Check if an object on the given bucket exists
   *
   * @param bucketName the name of the bucket to check
   * @param objectName The name of the object (key) to find
   * @return true if the bucket exists, false otherwise
   */
  public boolean checkObjectExists(String bucketName, String objectName) {
    boolean exists = false;
    try {
      HeadObjectRequest headObjectRequest =
          HeadObjectRequest.builder().bucket(bucketName).key(objectName).build();
      HeadObjectResponse result = s3Client.headObject(headObjectRequest);
      if (result.sdkHttpResponse().statusCode() == 200) {
        LOGGER.info("Object {} exists on bucket {}", objectName, bucketName);
        exists = true;
      }
    } catch (NoSuchKeyException e) {
      LOGGER.info("Object {} does not exist on bucket {}", objectName, bucketName);
    }
    return exists;
  }

  /**
   * Create a new S3 bucket with the given name
   *
   * @param bucketName the name of the bucket to create
   */
  public void createBucket(String bucketName) {
    S3Waiter waiter = s3Client.waiter();
    CreateBucketRequest createBucketRequest =
        CreateBucketRequest.builder().bucket(bucketName).build();
    s3Client.createBucket(createBucketRequest);
    HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder().bucket(bucketName).build();
    WaiterResponse<HeadBucketResponse> waiterResponse =
        waiter.waitUntilBucketExists(bucketRequestWait);
    waiterResponse
        .matched()
        .response()
        .ifPresent(response -> LOGGER.info("Matched response: {}", response));
  }

  /**
   * Upload an object to an S3 bucket
   *
   * @param bucketName The name of the S3 bucket
   * @param fileName The name of the object to put
   */
  public void uploadObject(String bucketName, String fileName) {
    if (!checkBucketExists(bucketName)) {
      LOGGER.warn("Bucket {} does not exist", bucketName);
      createBucket(bucketName);
    }
    LOGGER.info("Uploading file {} to bucket {}", fileName, bucketName);
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucketName).key(fileName).build();
    s3Client.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(fileName)));
  }

  /**
   * Delete an S3 bucket
   *
   * @param bucketName The name of the S3 bucket
   */
  public void deleteBucket(String bucketName) {
    LOGGER.info("Deleting bucket {}", bucketName);
    try {
      s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
    } catch (NoSuchBucketException e) {
      LOGGER.warn("Trying to delete non existing bucket {}", bucketName);
    }
  }

  /**
   * Delete an object from an S3 bucket
   *
   * @param bucketName The name of the S3 bucket
   * @param fileName The name of the object
   */
  public void deleteObject(String bucketName, String fileName) {
    LOGGER.info("Deleting object {} from bucket {}", fileName, bucketName);
    try {
      s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(fileName).build());
    } catch (NoSuchKeyException e) {
      LOGGER.warn("Trying to delete non existing object {} from bucket {}", fileName, bucketName);
    } catch (NoSuchBucketException e) {
      LOGGER.warn("Trying to delete object {} from non existing bucket {}", fileName, bucketName);
    }
  }
}
