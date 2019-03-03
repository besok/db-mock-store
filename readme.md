## Database mock store
This is a library for serialization and deserialization pojos to different formats for saving into stable removable store.
This data can be  used in test cases or other jobs.
### Description
If we have a relational database with a bunch relations between tables 
we can hardly use unit tests without mocking database by h2 or other embedded solutions.
This library maintains tools for doing this.
Basic idea is to find base `javax.persistence.*` annotations(`Entity,Table,Column,Id,ManyToOne,OneToOne` and etc) to build entity graph from pojos.
After that , based on that meta information , we can construct flat view of that pojo. It is similar as a csv file uploaded from databases.
Major goal is a possibility to get this entity from csv files exported from database.\
Thus we can construct a graph depending objects by @ManyToOne and @OneToOne annotations(@OneToMany and @ManyToMany are a partial cases for previouses)
For example we have a same structure:
```
@Table(schema=shop,name=order)
class Order{ 
    @ManyToOne Customer customer
    @ManyToOne Manager manager
    @OneToMany List<OrderBasket> baskets
}
@Table(schema=shop,name=customer)
class Customer{
    @OneToOne Address address
}
@Table(schema=shop,name=item)
class Item{
    @OneToOne Code code
    @OneToMany List<OrderBasket> baskets
}
@Table(schema=shop,name=basket)
class OrderBasket{
    @ManyToOne Order order
    @ManyToOne Item item   
}
```
we have a graph :
```
                          | -> Customer -> Address
              |-> Order ->| -> Manager
OrderBasket ->|
              |-> Item -> Code
```

Using this lib we can save OrderBasket to file or directory and get 7 files with depending entities corresponded to tables from database:
- `shop.basket` file
- `shop.order` file
- `shop.customer` file
- `shop.address` file
- `shop.item` file
- `shop.code` file
- `shop.manager` file

Files will contain columns and values separating ';' and wrapped quotes.
```
"id";"comment";"amount";"order_time";"customer_id";"payment_id"
"1";"comment";"10";"2019-01-01T01:01";"1";""
```

### Design
- [JpaAnnotationScanner](src/main/java/ru/besok/db/mock/JpaAnnotationScanner.java) is in charge for saving information from annotations 
- [JpaEntityStore](src/main/java/ru/besok/db/mock/JpaEntityStore.java) is major store for meta information gotten from JpaAnnotationScanner
- [FileMarshaller](src/main/java/ru/besok/db/mock/FileMarshaller.java) is a marshaller to file from entity collection
- [FileUnmarshaller](src/main/java/ru/besok/db/mock/FileUnmarshaller.java) is a unmarshaller from file
- [InnerStore](src/main/java/ru/besok/db/mock/InnerStore.java) is major store for unmarshalling from file
    - *it can built in 2 stages by invoking buildObjects and buildObjectRelations*    
- Jpa* are model classes
- [StringMapper](src/main/java/ru/besok/db/mock/StringMapper.java) is a mapper for transforming string value from file to needed format
    - [AbstractStringMapper](src/main/java/ru/besok/db/mock/AbstractStringMapper.java) is default implementation. It is recommended for inheritance 
- [Record](src/main/java/ru/besok/db/mock/Record.java) is a model for unmarshalling process
- [QueryableStore](src/main/java/ru/besok/db/mock/QueryableStore.java) is a store containing InnerStore and is able to make queries to it.
- [MockFileInvoker](src/main/java/ru/besok/db/mock/MockFileInvoker.java) is a facade for running marshallers and unmarshallers
- [AbstractJpaFileMock](src/main/java/ru/besok/db/tests/AbstractJpaFileMock.java) is default parent class making easier tests
 
### API
you have 2 options for starting work with this library:
- for getting access from tests you need to inherit from [AbstractJpaFileMock](src/main/java/ru/besok/db/tests/AbstractJpaFileMock.java) \
and point a package with pojos(containing @Table,@Entity or etc)
```
public class QueryableStoreTest extends AbstractJpaFileMock {
  public QueryableStoreTest() {
	super("ru.besok.db.mock.data.common");
  }
  @Test
  public void test(){
    List<Item> items = itemRepoFromDb.findAll();
    toFile(items, "item/case"); // save objects to file or directory
    QueryableStore store = store("item","case"); // get store for running objects
    List<Item> itemsFromFile = store.all(Item.class);
  }
}
```
- you can instantiate it with yourself by invoking [MockFileInvoker](src/main/java/ru/besok/db/mock/MockFileInvoker.java)
```
...
List<Item> items = itemRepoFromDb.findAll();
MockFileInvoker invoker = new MockFileInvoker(pkgForScan);
invoker.toFile(items,ResourceUtils.resolveIn("item/case")); // save objects to file or directory
QueryableStore store = invoker.fromFile("item","case"); // get store for running objects
List<Item> itemsFromFile = store.all(Item.class);
...
```
**if source or destination is a directory each entity is in a separated file named schema.table**\
**Otherwise each entity separated by a header _@@@_schema.table**

Files have a csv similar syntax:
- every section has a header from columns wrapped quotes separated ';'
- every line has a values wrapped quotes separated ';'

[QueryableStore](src/main/java/ru/besok/db/mock/QueryableStore.java) let you process followings commands:
- `all(Class<V> vClass)` find all entities from this dir or file by class
- `any(Class<V> vClass)` find random entity from this dir or file by class
- `anyByField | allByField(Class<V> vClass, fieldName, fieldValue)` find all entities from this 
dir or file by class by fieldValue(field can be composite see notes)
- `byId(Class<V> vClass, Object id)` find entity by id


### Use Cases in tests
#### Simple case 
Common case is we can do some programs logic without spring context by mocking components especially Repositories. 
```
public class CommonCaseExampleTest extends AbstractJpaFileMock {
  public CommonCaseExampleTest() {
	super("your.package.with.pojos");
  }
  
  AppComponent component;

  @Before
  public void setUp() throws Exception {
	QueryableStore store = store("test_dir");
	OrderRepo orderRepo = Mockito.mock(OrderRepo.class);
	List<Order> orders = store.all(Order.class); // we can get all orders from file instead of database
	Mockito.when(orderRepo.findAll()).thenReturn(orders); 
	component = new AppComponent(orderRepo);
  }

  @Test
  public void test() {
	int result = component.doSomeLogic();
	Assert.assertEquals(42,result);
  }

  public class AppComponent {
	private OrderRepo orderRepo;

	AppComponent(OrderRepo orderRepo) {
	  this.orderRepo = orderRepo;
	}
	
	public int doSomeLogic(){
	  List<Order> orders = orderRepo.findAll();
	  
	  int res = 42; // some hard logic
	  return res;
	}
  }

}
```
#### Entities from ManyToOne
If you have structure like that:
```
class Customer{	
    @OneToMany List<Order> orders;
  }
class Order{
	@ManyToOne	Customer customer;
	@OneToMany List<OrderBasket> baskets;
  }
class Item{	
    @OneToMany List<OrderBasket> baskets
  }
class OrderBasket{
	@ManyToOne Order order;
	@ManyToOne Item item;
  }
```
and you want to drop to file all OrderBaskets from Customer you can do something like that:
```
List<OrderBasket> baskets = 
    customer.getOrders().stream()
		.map(Order::getBaskets)
		.flatMap(Collection::stream)
		.collect(Collectors.toList());
		
toFile(baskets,"path_for_saving");
```
#### Uncommon mapping
if you have some fields with uncommon format ,for example for date you can get StringMapException. For solving this you can do your own String mapper:
```
"id";"time";"name"
"1";"2019-01-01 00:00";"new_year_party"
```
For solving this you can do your own String mapper by implementing [AbstractStringMapper](src/main/java/ru/besok/db/mock/AbstractStringMapper.java):
```
public class UserStringMapper extends AbstractStringMapper {
  @Override
  public StringFunction<LocalDateTime> localDateTime() {
	return s -> LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
  }
}
```
And mix it to a invoker:
```
public class CommonCaseExampleTest extends AbstractJpaFileMock {
 @Before
  public void setUp() throws Exception {
    withMapper(new UserStringMapper());
  }
}
...
invoker.withMapper(new UserStringMapper());
```
#### Several datasources
if you have several databases with different packages for scan you have to have several invokers or switch one invoker all time:
- you can instantiate several invokers(*note, for invoker you can use ResourceUtils*):
```
public class CommonCaseExampleTest extends AbstractJpaFileMock {


  public CommonCaseExampleTest() {
	super("pkg.for.first.db");
  }

  SpringComponent component;

  @Before
  public void setUp() throws Exception {
	QueryableStore storeForFirstDb = store("test_dir");
	MockFileInvoker invoker = invoker("pkg.for.second.db");
	QueryableStore storeForSecondDb = invoker.fromFile(ResourceUtils.resolveIn("other_dir"));
	}
}
```
- you can switch packages for one invoker:
```
public class CommonCaseExampleTest extends AbstractJpaFileMock {
  public CommonCaseExampleTest() {
	super("pkg.for.first.db");
  }

  SpringComponent component;

  @Before
  public void setUp() throws Exception {
	QueryableStore storeForFirstDb = store("test_dir");
	withPackage("pkg.for.second.db");
	QueryableStore storeForSecondDb = store("other_dir");
	}
}
```
### Notes 
- Source or destination is a directory the lib put each entity into separate file named schema.table \
from @Table annotation or Class Name transforming camelcase to snake case.\
Otherwise the lib put it into a file. The separator is _@@@_schema.table.

- QueryableStore assumed to find entity by composite field.\
For example `Order{customer:Customer{address:Address{street:String}}}`\
And you can do:` store.anyByField(Order.class,"customer.address.street","mystreet5")`

- This lib does not cover all possible cases for java persistent annotation. It plans to do further.

- This lib works correctly with hibernate orm. Others aren't tested.