package vn.giang.upload.s3upload;

import com.amazonaws.services.s3.model.StorageClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:s3-test.properties")
public class ImageUploaderTest {

    private ImageUploader uploader = null;

    @Value("${s3.test.regions}")
    private String regions;

    @Value("${s3.test.bucket}")
    private String bucket;

    private String testFileName = "test.jpg";
    private String testFilePath = null;

    @Before
    public void setup() {
        uploader = ImageUploader.builder()
                .bucket(bucket)
                .region(regions)
                .storageClass(StorageClass.ReducedRedundancy)
                .build();
        testFilePath = getClass().getClassLoader().getResource(testFileName).getPath();
    }

    @Test
    public void testUploaderImageSuccessfully() {
        boolean uploaded = uploader.upload(testFileName, testFilePath);
        assertThat(uploaded).isEqualTo(true);
    }

    @Test
    public void testUploaderImageFailWithNullFileName() throws Exception{
        Throwable thrown = catchThrowable(() -> uploader.upload(null, testFilePath));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testUploaderImageFailWithNullFilePath() throws Exception {
        Throwable thrown = catchThrowable(() -> uploader.upload(testFileName, null));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

}
