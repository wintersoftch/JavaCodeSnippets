package ch.wintersoft.java.aws.s3;

import java.util.Objects;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AWSS3BucketTest {

  private static final String BUCKET_NAME = "my-bucket";
  private final String objectName =
      Objects.requireNonNull(getClass().getClassLoader().getResource("myfile.txt")).getFile();
  private LocalStackContainer localStackContainer;
  private AWSS3Bucket awss3Bucket;

  @BeforeAll
  void startContainer() {

    // Creating and starting a LocalStack container
    localStackContainer =
        new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.10"));

    localStackContainer.start();
    // initializing the S3 client to connect to the LocalStack S3 service
    S3Client s3Client =
        S3Client.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
            .region(Region.EU_CENTRAL_1)
            .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
            .endpointOverride(localStackContainer.getEndpoint())
            .build();
    awss3Bucket = new AWSS3Bucket(s3Client);
  }

  @BeforeEach
  @AfterEach
  void cleanupBucket() {
    awss3Bucket.deleteObject(BUCKET_NAME, objectName);
    awss3Bucket.deleteBucket(BUCKET_NAME);
  }

  @AfterAll
  void afterAll() {
    if (localStackContainer != null) {
      localStackContainer.stop();
    }
  }

  @Test
  void createBucket_resultsInExistingBucket() {
    awss3Bucket.createBucket(BUCKET_NAME);
    Assertions.assertTrue(awss3Bucket.checkBucketExists(BUCKET_NAME));
  }

  @Test
  void deletingABucket_resultsInNotExistingBucket() {
    awss3Bucket.createBucket(BUCKET_NAME);
    Assertions.assertTrue(awss3Bucket.checkBucketExists(BUCKET_NAME));
    awss3Bucket.deleteBucket(BUCKET_NAME);
    Assertions.assertFalse(awss3Bucket.checkBucketExists(BUCKET_NAME));
  }

  @Test
  void creatingObjectOnNonExistingBucket_createsBucketAndObject() {
    Assertions.assertFalse(awss3Bucket.checkObjectExists(BUCKET_NAME, objectName));
    Assertions.assertFalse(awss3Bucket.checkBucketExists(BUCKET_NAME));
    awss3Bucket.uploadObject(BUCKET_NAME, objectName);
    Assertions.assertTrue(awss3Bucket.checkObjectExists(BUCKET_NAME, objectName));
    Assertions.assertTrue(awss3Bucket.checkBucketExists(BUCKET_NAME));
  }

  @Test
  void creatingAnObject_resultsInExistingObject() {
    awss3Bucket.createBucket(BUCKET_NAME);
    Assertions.assertFalse(awss3Bucket.checkObjectExists(BUCKET_NAME, objectName));
    awss3Bucket.uploadObject(BUCKET_NAME, objectName);
    Assertions.assertTrue(awss3Bucket.checkObjectExists(BUCKET_NAME, objectName));
  }

  @Test
  void deletingANonExistingObject_resultsInNonExistingObject() {
    awss3Bucket.createBucket(BUCKET_NAME);
    Assertions.assertTrue(awss3Bucket.checkBucketExists(BUCKET_NAME));
    Assertions.assertFalse(awss3Bucket.checkObjectExists(BUCKET_NAME, objectName));
    awss3Bucket.deleteObject(BUCKET_NAME, objectName);
    Assertions.assertFalse(awss3Bucket.checkObjectExists(BUCKET_NAME, objectName));
  }

  @Test
  void deletingAnObject_resultsInNonExistingObject() {
    awss3Bucket.createBucket(BUCKET_NAME);
    Assertions.assertFalse(awss3Bucket.checkObjectExists(BUCKET_NAME, objectName));
    awss3Bucket.uploadObject(BUCKET_NAME, objectName);
    Assertions.assertTrue(awss3Bucket.checkObjectExists(BUCKET_NAME, objectName));
    awss3Bucket.deleteObject(BUCKET_NAME, objectName);
    Assertions.assertFalse(awss3Bucket.checkObjectExists(BUCKET_NAME, objectName));
  }
}
