package ru.besok.db.mock;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Store for managing unmarshalled entities
 *
 * Created by Boris Zhguchev on 26/02/2019
 */
public class QueryableStore implements Store {
  private InnerStore innerStore;
  private Random r;


  QueryableStore(InnerStore innerStore) {
	r = new Random();
	this.innerStore = innerStore;
  }

  /**
   * get all entitties by class
   * @param vClass type for entities
   * @param <V> type for object
   * @return List for entities
   */
  @SuppressWarnings("unchecked")
  public <V> List<V> all(Class<V> vClass) {
	return
	  (List<V>) innerStore
		.getEntityStore()
		.byClass(vClass)
		.flatMap(this::byJpaEntity)
		.map(this::collectFromRecord)
		.orElse(new ArrayList<>());
  }

  /**
   * get any of all entities @see {@link QueryableStore#all(Class)}
   * @param vClass - type for entities
   * @param <V> type for object
   * @return any of all
   */
  public <V> Optional<V> any(Class<V> vClass) {
	List<V> list = all(vClass);
	return list.isEmpty() ? Optional.empty() : Optional.of(list.get(r.nextInt(list.size())));
  }

  /**
   * find all entities with needed value from field
   * @param vClass - class of entities
   * @param fieldName -field name - can be chain from fields splitting by dot e.g Order.customer.mail.address
   * @param fieldValue value for comparing
   * @param <V> type
   * @return all by field
   */
  public <V> List<V> allByField(Class<V> vClass, String fieldName, Object fieldValue) {
	return byField(vClass, fieldName, fieldValue).collect(Collectors.toList());
  }

  /**
   * find any of {@link QueryableStore#allByField(Class, String, Object)}
   * @param vClass class of entities
   * @param fieldName field name - can be chain from fields splitting by dot e.g Order.customer.mail.address
   * @param fieldValue value for comparing
   * @param <V> type
   * @return all by field
   */
  public <V> Optional<V> anyByField(Class<V> vClass, String fieldName, Object fieldValue) {
	List<V> collect = byField(vClass, fieldName, fieldValue).collect(Collectors.toList());
	return collect.isEmpty() ? Optional.empty() : Optional.of(collect.get(r.nextInt(collect.size())));
  }

  /**
   * find entity by id
   * @param vClass - class
   * @param id id value
   * @param <V> type
   * @return entity or empty
   */
  public <V> Optional<V> byId(Class<V> vClass, Object id) {
	List<V> list = all(vClass);
	return innerStore.getEntityStore()
	  .byClass(vClass)
	  .map(e -> {
		for (V v : list) {
		  Object idValue = e.getIdValue(v);
		  if (idValue.equals(id)) {
			return v;
		  }
		}
		return null;
	  });
  }

  private <V> Stream<V> byField(Class<V> vClass, String fieldName, Object fielValue) {
	boolean needDeepEq = fieldName.contains(".");
	return
	  all(vClass).stream()
		.filter(el -> needDeepEq ?
		  equalsDeep(fieldName, el, fielValue) :
		  Objects.equals(fielValue, ReflectionUtils.getValue(el, fieldName))
		);
  }

  private boolean equalsDeep(String fieldName, Object value, Object fieldValue) {
	String[] fields = fieldName.split("\\.");
	String field = fields[0];
	Object deepVal = getValueFromField( field, value);
	for (int i = 1; i < fields.length; i++) {
	  deepVal = getValueFromField(fields[i], deepVal);
	  if (Objects.isNull(deepVal)) {
		return false;
	  }
	}
	return Objects.equals(deepVal, fieldValue);
  }

  private Object getValueFromField(String fieldName, Object entity) {
	return ReflectionUtils.getValue(entity,fieldName);


  }

  private List<Object> collectFromRecord(Record r) {
	return r.pairs.stream().map(p -> p.object).collect(Collectors.toList());
  }

  private Optional<Record> byJpaEntity(JpaEntity jpaEntity) {
	return innerStore.getRecordMap().values().stream().filter(r -> r.entity.equals(jpaEntity)).findAny();
  }

}
