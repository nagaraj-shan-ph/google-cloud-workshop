package org.imaginea.workshop.service.impl;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.imaginea.workshop.service.StorageService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GCSStorageService implements StorageService {

 private static final Logger logger = LoggerFactory.getLogger(GCSStorageService.class);

  @Autowired
  private Storage storage;

  @Value("${application.storage.bucket:sl-clms}")
  private String bucketName;

  @Value("${application.storage.bucket.directory:contact_list/}")
  private String contactsListDir;

  @Override
  public String upload(InputStream stream, String name, String listId) {
    DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS");
    DateTime dt = DateTime.now(DateTimeZone.UTC);
    String dtString = dt.toString(dtf);
    final String fileName = contactsListDir + name + dtString;
    logger.info(fileName);

    Map<String, String> metaData = new HashMap<>();
    metaData.put("fileType", "csv");
    metaData.put("listId", listId);

    BlobInfo blobInfo = storage.create(
        BlobInfo.newBuilder(bucketName, fileName).setAcl(new ArrayList<>(Collections.singletonList(Acl.of(User.ofAllUsers(), Role.READER))))
            .setMetadata(metaData).build(), stream);
    // return the public download link
    blobInfo.toBuilder().build().getBlobId();
    return blobInfo.getMediaLink();
  }

  @Override
  public InputStream download(String fileName) {
    BlobId blobId = BlobId.of(bucketName, fileName);
    ReadChannel reader = storage.get(blobId).reader();
    return Channels.newInputStream(reader);
  }


}
