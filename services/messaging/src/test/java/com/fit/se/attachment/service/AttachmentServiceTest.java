package com.fit.se.attachment.service;

import com.fit.se.attachment.dto.AttachmentUploadResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AttachmentServiceTest {

    @Test
    void upload_putsFileToS3AndReturnsRenderableMetadata() {
        S3Client s3Client = mock(S3Client.class);
        AttachmentService attachmentService = new AttachmentService(s3Client);
        ReflectionTestUtils.setField(attachmentService, "bucketName", "dialo-media");
        ReflectionTestUtils.setField(attachmentService, "keyPrefix", "messaging/attachments");
        ReflectionTestUtils.setField(attachmentService, "publicBaseUrl", "https://cdn.example.com");
        ReflectionTestUtils.setField(attachmentService, "awsRegion", "ap-southeast-1");

        MultipartFile file = new MockMultipartFile(
                "files",
                "hello.txt",
                "text/plain",
                "hello world".getBytes()
        );

        AttachmentUploadResponse response = attachmentService.upload(java.util.List.of(file));

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().getFirst().getFileName()).isEqualTo("hello.txt");
        assertThat(response.getItems().getFirst().getContentType()).isEqualTo("text/plain");
        assertThat(response.getItems().getFirst().getUrl()).startsWith("https://cdn.example.com/messaging/attachments/");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
