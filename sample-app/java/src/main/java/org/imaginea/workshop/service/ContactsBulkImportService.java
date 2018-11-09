package org.imaginea.workshop.service;

import java.io.InputStream;

public interface ContactsBulkImportService {

  void importContacts(Long listId, InputStream inputStream) throws Exception;
}
