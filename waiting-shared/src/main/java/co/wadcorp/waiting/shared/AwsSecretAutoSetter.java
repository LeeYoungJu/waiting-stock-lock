package co.wadcorp.waiting.shared;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 로컬 환경에서 ~/.aws/credentials 파일을 읽어서 AWS_SECRET_CREDENTIAL을 좀 더 편하게 세팅해주는 클래스.
 */
@Slf4j
@Component
public class AwsSecretAutoSetter {

    private static final AtomicBoolean isSet = new AtomicBoolean(false);

    @PostConstruct
    public boolean setAwsCredentialProperty() {
        if (isSet.get()) {
            return true;
        }

        final String springActiveProfile = System.getenv("spring.profiles.active");
        if (springActiveProfile != null && !StringUtils.contains(springActiveProfile, "default")) {
            log.info("spring.profiles.active is not for local. skip aws credential setting.");
            return false;
        }


        String envAccessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        String envSecretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        if (StringUtils.isNotBlank(envAccessKeyId) && StringUtils.isNotBlank(envSecretAccessKey)) {
            log.info("AWS_ACCESS_KEY_ID or AWS_SECRET_ACCESS_KEY is already set. Skip auto setting.");
            return true;
        }

        String profile = System.getenv("AWS_PROFILE");
        if (StringUtils.isBlank(profile)) {
            profile = "wad-dev";
        }

        if (StringUtils.isBlank(profile)) {
            log.info("AWS_PROFILE is not determined. Skip auto setting.");
            return false;
        }

        INIConfiguration ini = new INIConfiguration();
        final File awsCredentialFile = new File(System.getProperty("user.home") + "/.aws/credentials");
        if (!awsCredentialFile.exists()) {
            log.warn("~/.aws/credentials is not exists. Skip auto setting.");
            return false;
        }
        try (FileReader reader = new FileReader(awsCredentialFile)) {
            ini.read(reader);
        } catch (Exception e) {
            log.warn("Failed to read ~/.aws/credentials", e);
            return false;
        }

        SubnodeConfiguration section = ini.getSection(profile);
        Object objAwsAccessKeyId = section.getProperty("aws_access_key_id");
        Object objAwsSecretAccessKey = section.getProperty("aws_secret_access_key");
        if (objAwsAccessKeyId instanceof String && objAwsSecretAccessKey instanceof String) {
            System.setProperty("aws.accessKeyId", (String) objAwsAccessKeyId);
            System.setProperty("aws.secretAccessKey", (String) objAwsSecretAccessKey);
            log.info("aws.accessKeyId and aws.secretAccessKey is set from ~/.aws/credentials");

            isSet.set(true);
            return true;
        }
        return false;
    }

}
