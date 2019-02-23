package ru.besok.db.mock;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

import static ru.besok.db.mock.JpaDependency.Property.JOIN_PRIMARY_KEYS;

/**
 * Created by Boris Zhguchev on 23/02/2019
 */
public class JpaEntityStore {

  private Logger logger = Logger.getLogger(JpaEntityStore.class.getName());


  private List<JpaEntity> entities;

  public JpaEntityStore() {
	entities = new ArrayList<>();
  }




  public void add(JpaEntity entity) {
	entities.add(entity);
	logger.info("add antity to store " + entity.fullHeader());
  }

  public Optional<JpaEntity> byClass(Class<?> entityClass) {
	return entities.stream().filter(e -> Objects.equals(entityClass, e.getEntityClass())).findAny();
  }

  public Optional<JpaEntity> bySchemaTable(Class<?> entityClass) {
	return entities.stream().filter(e -> Objects.equals(entityClass, e.getEntityClass())).findAny();
  }

  public Optional<JpaEntity> byField(Field f) {
	return ReflectionUtils.typeFromField(f).flatMap(this::byClass);
  }

  public List<JpaEntity> getEntities() {
	return entities;
  }

  static JpaEntityStore buildRelations(JpaEntityStore store) {
	List<JpaEntity> entities = store.getEntities();
	for (JpaEntity entity : entities) {
	  Set<JpaDependency> depSet = new HashSet<>();
	  for (JpaDependency dependency : entity.dependencySet()) {
		Field field = dependency.getField();
		store
		  .byField(field)
		  .ifPresent(depEntity -> {
			JpaDependency.Property j = JOIN_PRIMARY_KEYS;
			JpaDependency completedJpaEntity = new JpaDependency(
			  dependency.getField(), depEntity,
			  dependency.getParent(),
			  dependency.getColumn(),
			  dependency.getType(),
			  dependency.getMappedBy(),
			  dependency.getProperties())
			  .property(j, !dependency.property(j));
			depSet.add(completedJpaEntity);
		  });
	  }
	  entity.replaceDependency(depSet);
	}

	return store;
  }
}
