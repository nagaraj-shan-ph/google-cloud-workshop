package org.imaginea.workshop.service;

import org.imaginea.workshop.database.clms.model.Contact;
import org.springframework.data.domain.Page;

public interface ContactService {

  void delete(Long listId, Long id);

  Contact findByListIdAndId(Long listId, Long id);

  Contact update(Long listId, Long id, Contact contact);

  Contact create(Long listId, Contact contact);

  Page<Contact> getContacts(Long listId, int page, int limit);

}
