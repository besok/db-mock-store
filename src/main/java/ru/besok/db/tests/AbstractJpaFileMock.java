package ru.besok.db.tests;

import ru.besok.db.mock.MockFileInvoker;
import ru.besok.db.mock.QueryableStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

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
    return invoker.fromFile(inResource(paths));
  }



  public static String inToString(String... paths){
	try {
	  return Files.readAllLines(inResource(paths)).stream().collect(Collectors.joining());
	} catch (IOException e) {
	  e.printStackTrace();
	}
	return null;
  }

  public static Path inResource(String... paths){
	Path resource = getResource();
	for (String path : paths) {
	  resource = resource.resolve(path);
	}
	return resource;
  }

  public static Path getResource(){
	return Paths.get("src","test","resources");
  }
}
