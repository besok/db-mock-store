package ru.besok.db.tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Created by Boris Zhguchev on 02/03/2019
 */
public class ResourceUtils {
  public static String toString(String... paths){
	try {
	  return Files.readAllLines(resolveIn(paths)).stream().collect(Collectors.joining());
	} catch (IOException e) {
	  e.printStackTrace();
	}
	return null;
  }

  public static Path resolveIn(String... paths){
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
