package org.imaginea.workshop.service;

import java.io.InputStream;

public interface StorageService {

  String upload(InputStream stream, String name, String listId);

  InputStream download(String file);
}
