package org.imaginea.workshop.service.impl;

import java.util.function.Supplier;
import org.imaginea.workshop.annotations.CurrentTenant;
import org.imaginea.workshop.database.clms.model.ContactList;
import org.imaginea.workshop.database.clms.repository.ContactListRepository;
import org.imaginea.workshop.exception.NotFoundException;
import org.imaginea.workshop.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@CurrentTenant
public class ListServiceImpl implements ListService {

  @Autowired
  private ContactListRepository repository;

  @Override
  public Page<ContactList> getLists(int page, int limit) {
    Pageable pageableRequest = PageRequest.of(page, limit);
    return repository.findAll(pageableRequest);
  }

  @Override
  public ContactList create(ContactList contactList) {
    ContactList list = repository.save(contactList);
    System.out.println(list);
    return list;
  }

  @Override
  public ContactList findById(Long id) {
    return repository.findById(id).orElseThrow((Supplier<RuntimeException>) NotFoundException::new);
  }

  @Override
  public ContactList update(Long listId, ContactList contactList) {
    ContactList fromDb = findById(listId);
    update(contactList, fromDb);
    return repository.save(fromDb);
  }

  @Override
  public void delete(Long id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);
    }
  }

  private void update(ContactList contactList, ContactList fromDb) {
    fromDb.setName(contactList.getName());
  }
}
