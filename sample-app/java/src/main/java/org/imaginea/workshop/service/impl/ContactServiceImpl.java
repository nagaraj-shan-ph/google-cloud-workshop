package org.imaginea.workshop.service.impl;

import java.util.function.Supplier;
import org.imaginea.workshop.database.clms.model.Contact;
import org.imaginea.workshop.database.clms.model.ContactList;
import org.imaginea.workshop.database.clms.repository.ContactListRepository;
import org.imaginea.workshop.database.clms.repository.ContactRepository;
import org.imaginea.workshop.exception.ResourceNotFoundException;
import org.imaginea.workshop.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ContactServiceImpl implements ContactService {

  @Autowired
  private ContactRepository repository;

  @Autowired
  private ContactListRepository listRepository;

  @Override
  public Page<Contact> getContacts(Long listId, int page, int limit) {
    Pageable pageableRequest = PageRequest.of(page, limit);
    return repository.findAllByContactListId(listId, pageableRequest);
  }

  @Override
  @Transactional
  public Contact create(Long listId, Contact contact) {
    ContactList contactList = listRepository.findById(listId).orElseThrow(
        (Supplier<RuntimeException>) () -> new ResourceNotFoundException("List not found for Id " + listId));
    contact.setContactList(contactList);
    repository.save(contact);
    return contact;
  }

  @Override
  public Contact findByListIdAndId(Long listId, Long id) {
    return repository.findByIdAndContactListId(id, listId).orElseThrow(
        (Supplier<RuntimeException>) () -> new ResourceNotFoundException("Contact not found for Id " + id + "In the list " + listId));
  }

  @Override
  @Transactional
  public Contact update(Long listId, Long id, Contact contact) {
    Contact fromDb = findByListIdAndId(listId, id);
    update(contact, fromDb);
    return repository.save(fromDb);
  }

  @Override
  @Transactional
  public void delete(Long listId, Long id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);
    }
  }

  private void update(Contact contact, Contact fromDb) {
    fromDb.setCompany(contact.getCompany());
    fromDb.setAddress1(contact.getAddress1());
    fromDb.setAddress2(contact.getAddress2());
    fromDb.setCity(contact.getCity());
    fromDb.setEmail(contact.getEmail());
    fromDb.setFirstName(contact.getFirstName());
    fromDb.setLastName(contact.getLastName());
    fromDb.setState(contact.getState());
    fromDb.setMiddleName(contact.getMiddleName());
    fromDb.setZip(contact.getZip());
    fromDb.setCountry(contact.getCountry());
    fromDb.setMobile(contact.getMobile());
  }
}
