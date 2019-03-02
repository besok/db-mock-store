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
- [JpaAnnotationScanner](src\main\java\ru\besok\db\mock\JpaAnnotationScanner.java) is in charge for saving information from annotations 
- [JpaEntityStore](src\main\java\ru\besok\db\mock\JpaEntityStore.java) is major store for meta information gotten from JpaAnnotationScanner
- [FileMarshaller](src\main\java\ru\besok\db\mock\FileMarshaller.java) is a marshaller to file from entity collection
- [FileUnmarshaller](src\main\java\ru\besok\db\mock\FileUnmarshaller.java) is a unmarshaller from file
- [InnerStore](src\main\java\ru\besok\db\mock\InnerStore.java) is major store for unmarshalling from file
    - *it can built in 2 stages by invoking buildObjects and buildObjectRelations*    
- Jpa* are model classes
- [StringMapper](src\main\java\ru\besok\db\mock\StringMapper.java) is a mapper for transforming string value from file to needed format
    - [AbstractStringMapper](src\main\java\ru\besok\db\mock\AbstractStringMapper.java) is default implementation. It is recommended for inheritance 
- [Record](src\main\java\ru\besok\db\mock\Record.java) is a model for unmarshalling process
- [QueryableStore](src\main\java\ru\besok\db\mock\QueryableStore.java) is a store containing InnerStore and is able to make queries to it.
- [MockFileInvoker](src\main\java\ru\besok\db\mock\MockFileInvoker.java) is a facade for running marshallers and unmarshallers
- [AbstractJpaFileMock](src\main\java\ru\besok\db\tests\AbstractJpaFileMock.java) is default parent class making easier tests
 
### API

### Use Cases
#### Common case
#### Entities from ManyToOne
#### Uncommon mapping
#### Several datasources

### Notes 
- Source or destination is a directory the lib put each entity into separate file named schema.table \
from @Table annotation or Class Name transforming camelcase to snake case. \
Otherwise the lib put it into a file. The separator is _@@@_schema.table.

- QueryableStore assumed to find entity by composite field.\
For example `Order{customer:Customer{address:Address{street:String}}}`\
And you can do:` store.anyByField(Order.class,"customer.address.street","mystreet5")`