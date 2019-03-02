package ru.besok.db.tests;

import ru.besok.db.mock.AbstractStringMapper;
import ru.besok.db.mock.MockFileInvoker;
import ru.besok.db.mock.QueryableStore;
import ru.besok.db.mock.StringMapper;

import java.util.Collection;

import static ru.besok.db.tests.ResourceUtils.resolveIn;

/**
 * Created by Boris Zhguchev on 23/02/2019
 */
public abstract class AbstractJpaFileMock {

  protected MockFileInvoker invoker;

  /**
   * @param pkgForScan place where jpa entities
   */
  public AbstractJpaFileMock(String pkgForScan) {
    invoker=new MockFileInvoker(pkgForScan);
  }

  /**
   * store dir or file in resources
   * @param paths - paths in resources
   * @return QueryableStore
   */
  protected QueryableStore store(String... paths){
    return invoker.fromFile(resolveIn(paths));
  }

  /**
   * change package for scanning
   * @param pkg new package
   */
  protected void withPackage(String pkg){
    invoker.withPackage(pkg);
  }

  /**
   * change stringmapper
   * @param mapper new mapper @see {@link StringMapper} or {@link AbstractStringMapper}
   */
  private void withMapper(StringMapper mapper){
    invoker.withMapper(mapper);
  }

  protected<E> boolean toFile( Collection<E> objects,String... paths){
    return invoker.toFile(resolveIn(paths),objects);
  }
  protected<E> boolean toFile(E object,String... paths){
    return invoker.toFile(resolveIn(paths),object);
  }

  protected MockFileInvoker invoker(String pkgForScan){
    return new MockFileInvoker(pkgForScan);
  }

}
