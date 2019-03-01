package ru.besok.db.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static ru.besok.db.mock.InnerStore.*;

/**
 * Unmarshaller @see {@link Unmarshaller} from file
 * This does a unmarshalling from file
 *
 * Created by Boris Zhguchev on 26/02/2019
 */
public class FileUnmarshaller implements Unmarshaller<Path, QueryableStore> {
  private Logger logger = Logger.getLogger(FileUnmarshaller.class.getName());
  private InnerStore innerStore;


  public FileUnmarshaller(JpaEntityStore entityStore) {
	this(entityStore, new DummyStringMapper());
  }

  public FileUnmarshaller(JpaEntityStore entityStore, StringMapper stringMapper) {
	if (entityStore == null || stringMapper == null) {
	  throw new UnmarshallerException(" entity store or string mapper must not be empty");
	}
	this.innerStore = new InnerStore(entityStore, stringMapper);
  }

  InnerStore getInnerStore() {
	return innerStore;
  }

  /**
   * @param source for unmarshalling
   * @return store for querying needed entities @see {@link QueryableStore}
   * @throws UnmarshallerException if some goes wrong
   */
  @Override
  public QueryableStore unmarshal(Path source) {
	if (source == null) {
	  throw new IllegalStateDbMockException(" source must not be empty");
	}
	int totalCount = getRecordsFromFile(source);
	logger.info(" got " + totalCount + " records from " + source.toString());

	return new QueryableStore(buildObjectRelations(buildObjects(this.innerStore)));
  }

  private int getRecordsFromFile(Path source) {
	try {
	  int count = 0;
	  if (Files.isDirectory(source)) {
		List<Path> files = Files.list(source).collect(Collectors.toList());
		logger.info(" init directory : " + source.toString());
		for (Path path : files) {
		  String fileName = path.getFileName().toString();
		  List<String> records = Files.readAllLines(path);
		  logger.info(" try to init file : " + fileName);
		  innerStore.initRecord(fileName, records);

		  count++;
		}
	  } else {
		String delimiter = "_@@@_";
		logger.info(" init file  : " + source.toString());
		List<String> lines = Files.readAllLines(source);
		List<String> temp = new ArrayList<>();
		String tempKey = "";
		for (String line : lines) {
		  if (line.contains(delimiter)) {
			String[] key = tempKey.split(delimiter);
			if (key.length > 1) {
			  String schTbl = key[1];
			  logger.info(" try to init entity in file : " + schTbl);
			  innerStore.initRecord(schTbl, temp);
			  count++;
			}
			tempKey = line;
			temp.clear();
		  } else {
			temp.add(line);
		  }
		}
		if (!tempKey.isEmpty() && !temp.isEmpty()) {
		  String[] key = tempKey.split(delimiter);
		  if (key.length > 1) {
			String schTbl = key[1];
			innerStore.initRecord(schTbl, temp);
			logger.info(" try to init entity in file : " + schTbl);
			count++;
		  }
		}

		if (count == 0) {
		  throw new UnmarshallerException(" wrong format for " + source.toString());
		}
	  }
	  return count;
	} catch (IOException e) {
	  throw new UnmarshallerException(" can't process files ", e);
	}
  }


}
