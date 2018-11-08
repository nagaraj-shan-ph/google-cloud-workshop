package org.imaginea.workshop.web.controller.list;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import java.io.InputStream;
import java.time.Instant;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.imaginea.workshop.database.clms.model.Contact;
import org.imaginea.workshop.service.ContactService;
import org.imaginea.workshop.service.ListService;
import org.imaginea.workshop.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${application.basePath}/lists/{listId}/contacts", produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags = "ListContacts", description = "List Contacts")
public class ContactController {

  @Autowired
  private ContactService contactService;

  @Autowired
  private ListService listService;

  @Autowired
  private StorageService storageService;

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Page<Contact>> getLists(@PathVariable("listId") Long listId, @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "limit", defaultValue = "25") int limit) {
    return ResponseEntity.ok(contactService.getContacts(listId, page, limit));
  }

  @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Contact> create(@PathVariable("listId") Long listId, @RequestBody Contact contact) {
    return ResponseEntity.ok(contactService.create(listId, contact));
  }


  @GetMapping(path = {"/{id}"}, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Contact> findOne(@PathVariable("listId") Long listId, @PathVariable("id") Long id) {
    return ResponseEntity.ok(contactService.findByListIdAndId(listId, id));
  }

  @PutMapping(path = {"/{id}"}, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Contact> update(@PathVariable("listId") Long listId, @PathVariable("id") Long id, @RequestBody Contact contact) {
    return ResponseEntity.ok(contactService.update(listId, id, contact));
  }

  @DeleteMapping(path = {"/{id}"}, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> delete(@PathVariable("listId") Long listId, @PathVariable("id") Long id) {
    contactService.delete(listId, id);
    return ResponseEntity.noContent().build();
  }

  @ApiImplicitParams({@ApiImplicitParam(name = "file", value = "Contacts Csv File", dataType = "file", paramType = "formData")})
  @PostMapping(path = "/bulkCreate", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {"multipart/form-data"})
  ResponseEntity<?> upload(@PathVariable("listId") Long listId, HttpServletRequest request) {
    listService.findById(listId);
    long start = Instant.now().toEpochMilli();
    if (!ServletFileUpload.isMultipartContent(request)) {
      return ResponseEntity.badRequest().build();
    }
    // Create a new file upload handler
    ServletFileUpload upload = new ServletFileUpload();
    // Parse the request
    try {
      FileItemIterator iter = upload.getItemIterator(request);
      while (iter.hasNext()) {
        FileItemStream item = iter.next();
        String name = item.getFieldName();
        InputStream stream = item.openStream();
        if (!item.isFormField()) {
          System.out.println("File field " + name + " with file name " + item.getName() + " detected.");
          String uploadUrl = storageService.upload(stream, item.getName(), listId.toString());
          System.out.println(uploadUrl);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Upload failed", e);
    }
    System.out.println("Total time taken to upload and process data:" + ((Instant.now().toEpochMilli() - start)) + "Millis");
    return ResponseEntity.ok().build();
  }
}
