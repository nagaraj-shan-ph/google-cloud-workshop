package org.imaginea.workshop.service.impl;

import static de.bytefish.pgbulkinsert.util.PostgreSqlUtils.getPGConnection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.bytefish.pgbulkinsert.PgBulkInsert;
import de.bytefish.pgbulkinsert.configuration.Configuration;
import de.bytefish.pgbulkinsert.mapping.AbstractMapping;
import java.io.InputStream;
import java.sql.Connection;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.sql.DataSource;
import org.imaginea.workshop.database.TenantContextHolder;
import org.imaginea.workshop.database.admin.model.Tenant;
import org.imaginea.workshop.database.admin.repository.TenantRepository;
import org.imaginea.workshop.database.lms.model.Contact;
import org.imaginea.workshop.service.ContactsBulkImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactsBulkImportServiceImpl implements ContactsBulkImportService {

  public static final String TABLE_NAME = "contacts";

  public static final int BUFFER_SIZE = 5 * 1024000;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private TenantRepository repository;

  private PgBulkInsert<Contact> bulkInsert;

  @Override
  public void importContacts(Long tenantId, Long listId, InputStream inputStream) throws Exception {
    System.out.println("Inside import contacts for list ID" + listId);
    setTenant(tenantId);
    String schemaName = TenantContextHolder.getContext().getTenant().getSchemaName();
    bulkInsert = new PgBulkInsert<>(new Configuration(BUFFER_SIZE), new ContactMapping(listId));
    long start = Instant.now().toEpochMilli();
    AtomicInteger counter = new AtomicInteger(0);
    CsvSchema schema = CsvSchema.emptySchema().withHeader().withAnyPropertyName("");
    MappingIterator<Contact> iterator =
        new CsvMapper().enable(JsonGenerator.Feature.IGNORE_UNKNOWN).readerFor(Contact.class).with(schema).readValues(inputStream);
    Iterable<Contact> iterable = () -> iterator;
    Stream<Contact> stream = StreamSupport.stream(iterable.spliterator(), true).peek(contact -> {
      if (counter.getAndIncrement() % 10000 == 0) {
        System.out.println("Values added - " + counter.get());
      }
    });
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      bulkInsert.saveAll(getPGConnection(connection), stream);
    } finally {
      assert connection != null;
      connection.close();
    }
    long end = Instant.now().toEpochMilli();
    System.out.println("Time Taken :" + ((end - start)) + " Millis");
  }


  private void setTenant(Long tenantId) {
    Tenant tenant = repository.findById(tenantId).orElseThrow(() -> new RuntimeException("tenants not found"));
    TenantContextHolder.setTenant(tenant, true);
  }

  private void doNothing() {
  }

  private class ContactMapping extends AbstractMapping<Contact> {

    protected ContactMapping(Long listId) {
      super("", TABLE_NAME);
      mapText("address1", Contact::getAddress1);
      mapText("address2", Contact::getAddress2);
      mapText("city", Contact::getCity);
      mapText("company", Contact::getCompany);
      mapText("country", Contact::getCountry);
      mapTimeStamp("created_at", contact -> LocalDateTime.now());
      mapText("created_by", Contact::getCreatedBy);
      mapText("email", Contact::getEmail);
      mapText("first_name", Contact::getFirstName);
      mapText("last_name", Contact::getLastName);
      mapText("middle_name", Contact::getMiddleName);
      mapText("mobile", Contact::getMobile);
      mapText("state", Contact::getState);
      mapTimeStamp("updated_at", contact -> LocalDateTime.now());
      mapText("updated_by", Contact::getUpdatedBy);
      mapLong("version", contact -> 0L);
      mapText("zip", Contact::getZip);
      mapLong("list_id", contact -> listId);
    }
  }
}