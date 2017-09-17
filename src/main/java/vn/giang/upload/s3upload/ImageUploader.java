package vn.giang.upload.s3upload;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.StringUtils;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Giang on 17-Sep-17.
 */
@Builder
@Data
public class ImageUploader {
    private String bucket;
    private String region;
    private StorageClass storageClass;

    public boolean upload(@NotNull String fileName, @NotNull String filePath) {
        if(StringUtils.isNullOrEmpty(fileName) || StringUtils.isNullOrEmpty(filePath))
            throw new IllegalArgumentException("file name and file path must not be null");

        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(AmazonS3ClientBuilder.standard()
                        .withCredentials(new ProfileCredentialsProvider())
                        .withRegion(region)
                        .build())
                .build();
        // TransferManager processes all transfers asynchronously,
        // so this call will return immediately.

        Upload upload = null;
        Map<String, String> userMetadata = Collections.unmodifiableMap(
                Stream.of(
                        new AbstractMap.SimpleEntry<>("storage-class", storageClass.toString()))
                        .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setUserMetadata(userMetadata);
        try {
            upload = tm.upload(
                    bucket, fileName, new FileInputStream(new File(filePath)), metadata);
            // Or you can block and wait for the upload to finish
            upload.waitForCompletion();
            return true;
        } catch (AmazonClientException amazonClientException) {
            amazonClientException.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            return false;
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}
