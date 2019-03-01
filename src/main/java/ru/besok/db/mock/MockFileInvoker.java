package ru.besok.db.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static java.util.Collections.*;
import static ru.besok.db.mock.JpaAnnotationScanner.*;
import static ru.besok.db.mock.JpaEntityStore.*;

/**
 * invoker for running stores
 * Created by Boris Zhguchev on 01/03/2019
 */
public class MockFileInvoker {
  private FileMarshaller fileMarshaller;
  private FileUnmarshaller fileUnmarshaller;
  private JpaEntityStore store;
  private StringMapper mapper;

  public MockFileInvoker(String packageForScan) {
	this.store = buildRelations(scan(packageForScan));
	fileMarshaller = new FileMarshaller(this.store);
	fileUnmarshaller = new FileUnmarshaller(this.store);
  }

  /**
   * add customs string mapper @see {@link AbstractStringMapper}
   * @param stringMapper @see StringMapper
   * @return this
   */
  public MockFileInvoker withMapper(StringMapper stringMapper){
    this.mapper = stringMapper;
    this.fileUnmarshaller = new FileUnmarshaller(store,stringMapper);
    return this;
  }

  /**
   * change package for scan jpa annotation
   * @param packageForScan package for scanning
   * @return this
   */
  public MockFileInvoker withPackage(String packageForScan){
    this.store = buildStore(packageForScan);
    this.fileMarshaller = new FileMarshaller(store);
    if(mapper == null) {
	  this.fileUnmarshaller = new FileUnmarshaller(store);
	}else{
      this.fileUnmarshaller = new FileUnmarshaller(store,mapper);
	}
	return this;
  }

  /**
   * marshal object to file
   * @param destination path destination
   * @param object object for marshalling
   * @param <E> object type
   * @return result boolean
   *
   */
  public <E> boolean toFile(Path destination,E object){
    return fileMarshaller.marshal(destination, singletonList(object));
  }

  /**
   * @param destination path destination
   * @param objects colelctions for object for marshalling
   * @param <E> object type
   * @return result boolean
   */
  public <E> boolean toFile(Path destination, Collection<E> objects){
    return fileMarshaller.marshal(destination, objects);
  }

  /**
   * unmarshalling for objects
   *
   * @param source path with sources
   * @return store @see {@link QueryableStore}
   */
  public QueryableStore fromFile(Path source){
    return fileUnmarshaller.unmarshal(source);
  }


  private JpaEntityStore buildStore(String packageForScan){
    return buildRelations(scan(packageForScan));
  }



}
