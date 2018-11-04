package org.imaginea.workshop.service;

import org.imaginea.workshop.database.admin.model.Tenant;

public interface TenantService {

  Tenant findByTenantName(String name);
}
