package org.imaginea.workshop;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleCloudConfig {

  @Bean
  public Storage cloudStorage() throws IOException {
    return StorageOptions.newBuilder().setCredentials(GoogleCredentials.getApplicationDefault()).build().getService();
  }

}
