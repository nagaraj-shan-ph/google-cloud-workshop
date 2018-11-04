package org.imaginea.workshop.service.impl;

import org.imaginea.workshop.database.admin.model.Tenant;
import org.imaginea.workshop.database.admin.repository.TenantRepository;
import org.imaginea.workshop.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TenantServiceImpl implements TenantService {

  @Autowired
  private TenantRepository tenantRepository;

  @Override
  public Tenant findByTenantName(String name) {
    return tenantRepository.findByName(name).orElse(null);
  }
}
