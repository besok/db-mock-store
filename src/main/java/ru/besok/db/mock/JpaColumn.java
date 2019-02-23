package ru.besok.db.mock;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * Created by Boris Zhguchev on 21/02/2019
 */
@Data
@AllArgsConstructor
public class JpaColumn {
  private Field field;
  private String name;
  private boolean nullable;
  private int length=255;
  private int precision=0;
  private int scale=0;
}
