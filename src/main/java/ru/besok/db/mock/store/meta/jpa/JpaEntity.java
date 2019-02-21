package ru.besok.db.mock.store.meta.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
@Data
public class JpaEntity {

  private JpaId id;
  private Class<?> entityClass;
  private Header header;

  private List<JpaColumn> columns;
  private Map<Field, JpaDependency> dependencies;

  @AllArgsConstructor
  private static class Header {
	String name;
	String table;
	String schema;
  }

  public void setHeader(String name,String table, String schema){
    header = new Header(name,table,schema);
  }



}
