package dbdr.global.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory;
import io.awspring.cloud.sqs.listener.acknowledgement.AcknowledgementOrdering;
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
public class SqsConfig {

	@Value("${cloud.aws.credentials.access-key}")
	private String AWS_ACCESS_KEY;

	@Value("${cloud.aws.credentials.secret-key}")
	private String AWS_SECRET_KEY;

	@Value("${cloud.aws.region.static}")
	private String AWS_REGION;

	// 클라이언트 설정: region과 자격증명
	@Bean
	public SqsAsyncClient sqsAsyncClient() {
		return SqsAsyncClient.builder()
			.credentialsProvider(() -> new AwsCredentials() {
				@Override
				public String accessKeyId() {
					return AWS_ACCESS_KEY;
				}

				@Override
				public String secretAccessKey() {
					return AWS_SECRET_KEY;
				}
			})
			.region(Region.of(AWS_REGION))
			.build();
	}

	// Listener Factory 설정 (Listener 쪽)
	@Bean
	SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(SqsAsyncClient sqsAsyncClient) {
		return SqsMessageListenerContainerFactory
			.builder()
			.configure(options -> options
				.acknowledgementMode(AcknowledgementMode.ALWAYS)
				.acknowledgementInterval(Duration.ofSeconds(3))
				.acknowledgementThreshold(5)
				.acknowledgementOrdering(AcknowledgementOrdering.ORDERED)
			)
			.sqsAsyncClient(sqsAsyncClient)
			.build();
	}

	// 메시지 발송을 위한 SQS 템플릿 설정 (Sender 쪽)
	@Bean
	public SqsTemplate sqsTemplate() {
		return SqsTemplate.newTemplate(sqsAsyncClient());
	}
}
