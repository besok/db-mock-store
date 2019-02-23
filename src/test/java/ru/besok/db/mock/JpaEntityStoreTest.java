package ru.besok.db.mock;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static ru.besok.db.mock.JpaAnnotationScanner.*;

/**
 * Created by Boris Zhguchev on 23/02/2019
 */
public class JpaEntityStoreTest {
  @Test
  public void buildRelationsTest() {
	JpaEntityStore store = JpaEntityStore.buildRelations(scan("ru.besok.db.mock.store.data.common"));
	List<JpaEntity> entities = store.getEntities();
	for (JpaEntity entity : entities) {
	  for (JpaDependency dependency : entity.dependencySet()) {
		Assert.assertNotNull(dependency.getEntity());
	  }
	}
  }
}