package ru.besok.db.mock;

import org.junit.Test;
import ru.besok.db.mock.data.common.City;
import ru.besok.db.mock.data.common.OrdinalType;
import ru.besok.db.mock.data.common.Type;
import ru.besok.db.tests.AbstractJpaFileMock;

import java.util.List;

public class EnumTest extends AbstractJpaFileMock {
    public EnumTest() {
        super("ru.besok.db.mock.data.common");
    }

    @Test
    public void serializTest() {

        City city = new City();
        city.setEnumType(Type.First);
        city.setOrdinalType(OrdinalType.First);
        city.setName("name");
        city.setCode(1);
        city.setId(2);
        toFile(city,"city_enum");

        QueryableStore store = store("city_enum");
        List<City> cityList = store.all(City.class);
    }
}
