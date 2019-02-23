package ru.besok.db.mock;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
class JpaAnnotationScanner {
  static JpaEntityStore scan(String entityPackage) {
	JpaEntityStore store = new JpaEntityStore();
	if (Objects.isNull(entityPackage) || entityPackage.isEmpty()) {
	  throw new JpaAnnotationScanException("the scan package is null or empty");
	}

	Set<Class<?>> classes = scanPackage(entityPackage);

	for (Class<?> cl : classes) {
	  if (cl.isAnnotationPresent(Entity.class)) {
		JpaEntity jpaEntity = DbUtils.initEntityWithHeader(cl);
		Field[] fields = cl.getDeclaredFields();
		for (Field field : fields) {
		  DbUtils.findIdInField(field)
			.map(jpaEntity::setId)
			.orElseGet(() -> {
			  boolean m2o = DbUtils.processManyToOne(jpaEntity, field);
			  boolean o2m = DbUtils.processOneToMany(jpaEntity, field);
			  boolean o2o = DbUtils.processOneToOne(jpaEntity, field);
			  if(!(m2o || o2m || o2o)){
				DbUtils.processPlain(jpaEntity,field);
			  }
			  return jpaEntity;
			});
		}
		store.add(jpaEntity);
	  }
	}

	return store;
  }


 private static Set<Class<?>> scanPackage(String entityPackage) {
	return new Reflections(entityPackage, new SubTypesScanner(false)).getSubTypesOf(Object.class);
  }
}
