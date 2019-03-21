package ru.besok.db.mock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ru.besok.db.mock.data.common.*;
import ru.besok.db.tests.AbstractJpaFileMock;

import java.util.List;

/**
 * Created by Boris Zhguchev on 03/03/2019
 */
public class CommonCaseExampleTest extends AbstractJpaFileMock {


    public CommonCaseExampleTest() {
        super("ru.besok.db.mock.data.common");
    }

    SpringComponent component;

    @Before
    public void setUp() throws Exception {
        QueryableStore store = store("test_dir");
        OrderRepo orderRepo = Mockito.mock(OrderRepo.class);
        List<Order> orders = store.all(Order.class); // we can get all orders from file instead of database
        Mockito.when(orderRepo.findAll()).thenReturn(orders);
        component = new SpringComponent(orderRepo);
    }

    @Test
    public void test() {
        int result = component.doSomeLogic();
        Assert.assertEquals(42, result);
    }

    public class SpringComponent {
        private OrderRepo orderRepo;

        SpringComponent(OrderRepo orderRepo) {
            this.orderRepo = orderRepo;
        }

        public int doSomeLogic() {
            List<Order> orders = orderRepo.findAll();
            int res = 42; // some hard logic
            return res;
        }
    }

    @Test
    public void testGetter(){
        Client client = new Client();
        client.setAge(1L);
        client.setId(12);
        client.setName("Name");
        toFile(client,"getter");
    }

}
