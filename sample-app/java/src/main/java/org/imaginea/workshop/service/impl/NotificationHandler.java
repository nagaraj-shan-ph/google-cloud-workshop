package org.imaginea.workshop.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import java.io.InputStream;
import java.util.Map;
import org.imaginea.workshop.service.ContactsBulkImportService;
import org.imaginea.workshop.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class NotificationHandler {

  private static final Logger logger = LoggerFactory.getLogger(NotificationHandler.class);

  @Value("${application.pubsub.subscription.upload:file-upload-subscription}")
  private String uploadSubscriptionName;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private StorageService storageService;

  @Autowired
  private ContactsBulkImportService importService;

  @Bean
  public PubSubInboundChannelAdapter messageChannelAdapter(@Qualifier("pubsubInputChannel") MessageChannel inputChannel, PubSubOperations pubSubTemplate) {
    PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, uploadSubscriptionName);
    adapter.setOutputChannel(inputChannel);
    adapter.setAckMode(AckMode.MANUAL);
    return adapter;
  }

  @Bean
  public MessageChannel pubsubInputChannel() {
    return new DirectChannel();
  }

  @Bean
  @ServiceActivator(inputChannel = "pubsubInputChannel")
  public MessageHandler messageReceiver() {
    return message -> {
      AckReplyConsumer consumer = (AckReplyConsumer) message.getHeaders().get(GcpPubSubHeaders.ACKNOWLEDGEMENT);
      ;
      try {
        logger.info("Message arrived! Payload: " + message.getPayload());
        Map<String, Object> map = objectMapper.readValue((String) message.getPayload(), new TypeReference<Map<String, Object>>() {
        });
        InputStream inputStream = storageService.download(map.get("name").toString());
        Map metadata = (Map) map.get("metadata");
        importService.importContacts(Long.parseLong((String) metadata.get("tenantId")), Long.parseLong((String) metadata.get("listId")), inputStream);
      } catch (Exception e) {
        logger.error("Error while handling message", e);
      } finally {
        assert consumer != null;
        consumer.ack();
      }
    };
  }

}
