package org.imaginea.workshop.database;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.Session;
import org.imaginea.workshop.annotations.CurrentTenant;
import org.imaginea.workshop.annotations.Tenant;
import org.imaginea.workshop.annotations.WithoutTenant;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MultiTenantAspect {

  @PersistenceContext
  public EntityManager entityManager;

  @Pointcut(value = "@within(org.imaginea.workshop.annotations.CurrentTenant) || @annotation(org.imaginea.workshop.annotations.CurrentTenant)")
  void hasCurrentTenantAnnotation() {
  }

  @Pointcut(value = "@within(org.imaginea.workshop.annotations.Tenant) || @annotation(org.imaginea.workshop.annotations.Tenant)")
  void hasTenantAnnotation() {
  }

  @Pointcut(value = "@within(org.imaginea.workshop.annotations.WithoutTenant) || @annotation(org.imaginea.workshop.annotations.WithoutTenant)")
  void hasWithoutTenantAnnotation() {
  }

  @Pointcut(value = "hasCurrentTenantAnnotation() || hasTenantAnnotation() || hasWithoutTenantAnnotation()")
  void hasMultiTenantAnnotation() {
  }

  @Around("execution(public * *(..)) && hasMultiTenantAnnotation()")
  public Object aroundExecution(ProceedingJoinPoint pjp) throws Throwable {
    final String methodName = pjp.getSignature().getName();
    final MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
    Method method = methodSignature.getMethod();
    if (method.getDeclaringClass().isInterface()) {
      method = pjp.getTarget().getClass().getDeclaredMethod(methodName, method.getParameterTypes());
    }
    Annotation multiTenantAnnotation = getMultiTenantAnnotation(method);
    if (multiTenantAnnotation == null) {
      multiTenantAnnotation = getMultiTenantAnnotation(method.getDeclaringClass());
    }
    if (multiTenantAnnotation != null && !(multiTenantAnnotation instanceof WithoutTenant)) {
      Serializable tenantId = TenantContextHolder.getContext().getTenantId();
      if (multiTenantAnnotation instanceof Tenant) {
        tenantId = Long.parseLong(((Tenant) multiTenantAnnotation).value());
      }
      org.hibernate.Filter filter = entityManager.unwrap(Session.class).enableFilter("tenantFilter");
      filter.setParameter("tenantId", tenantId);
      filter.validate();
    }
    return pjp.proceed();
  }

  private Annotation getMultiTenantAnnotation(AnnotatedElement element) {
    Annotation annotation = element.getAnnotation(CurrentTenant.class);
    if (annotation != null) {
      return annotation;
    }
    annotation = element.getAnnotation(Tenant.class);
    if (annotation != null) {
      return annotation;
    }
    annotation = element.getAnnotation(WithoutTenant.class);
    if (annotation != null) {
      return annotation;
    }
    return null;
  }
}
