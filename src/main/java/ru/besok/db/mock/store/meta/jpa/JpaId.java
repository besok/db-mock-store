package ru.besok.db.mock.store.meta.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
@Data(staticConstructor = "of")
public class JpaId {
  private Field field;
  private String column;
  private boolean generated;
}
