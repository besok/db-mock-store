package ru.besok.db.tests;

import ru.besok.db.mock.MockFileInvoker;
import ru.besok.db.mock.QueryableStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

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
