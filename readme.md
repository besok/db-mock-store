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