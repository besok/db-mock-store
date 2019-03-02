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
class Order{ 
    @ManyToOne Customer customer
    @ManyToOne Manager manager
    @OneToMany List<OrderBasket> baskets
}
class Customer{
    @OneToOne Address address
}
class Item{
    @OneToMany List<OrderBasket> baskets
}
class OrderBasket{
    @ManyToOne Order order
    @ManyToOne Item item   
}
```
we have a graph :
<img src='https://g.gravizo.com/svg?
 digraph G {
   main -> parse -> execute;
   main -> init;
   main -> cleanup;
   execute -> make_string;
   execute -> printf
   init -> make_string;
   main -> printf;
   execute -> compare;
 }
'/>


### Design

### API

### Use Cases

### Notes 
