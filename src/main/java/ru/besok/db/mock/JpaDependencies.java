package ru.besok.db.mock;

import java.lang.reflect.Field;
import java.util.*;

/**
 * class maintains @see {@link JpaDependency} list
 *
 * Created by Boris Zhguchev on 23/02/2019
 */
public class JpaDependencies {
  private Set<JpaDependency> dependencies;


  public JpaDependencies() {
	this.dependencies = new HashSet<>();
  }


  public Set<JpaDependency> getDependencies() {
	return dependencies;
  }

  public JpaDependency put(JpaDependency dependency) {
	dependencies.add(dependency);
	return dependency;
  }

  public Optional<JpaDependency> get(Field field) {
	return dependencies.stream().filter(d -> d.getField().equals(field)).findAny();
  }

  public Optional<JpaDependency> get(String column){
    return dependencies.stream().filter(d -> d.getColumn().equals(column)).findAny();
  }

  public boolean replace(JpaDependency dependency) {
	get(dependency.getField())
	  .ifPresent(jpaDependency -> dependencies.remove(jpaDependency));

	return dependencies.add(dependency);
  }

}
