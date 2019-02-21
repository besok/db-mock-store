package ru.besok.db.mock.store.meta.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
@Data(staticConstructor = "of")
public class JpaDependency {
  private JpaEntity entity;
  private String column;
  private Map<Property, Boolean> properties;


  JpaDependency property(Property p, boolean flag) {
	properties.put(p, flag);
	return this;
  }


  enum Property {
	OPTIONAL, ALWAYS_NEW, JOIN_PRIMARY_KEYS;
  }
}
