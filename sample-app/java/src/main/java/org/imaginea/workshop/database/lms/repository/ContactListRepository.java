package org.imaginea.workshop.database.lms.repository;

import org.imaginea.workshop.database.lms.model.ContactList;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the {@link ContactList} JPA entity. Any custom methods, not already defined in {@link JpaRepository}, are to be defined here.
 */
public interface ContactListRepository extends JpaRepository<ContactList, Long> {

}
