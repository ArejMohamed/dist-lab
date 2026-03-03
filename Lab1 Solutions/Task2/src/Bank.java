
import java.util.ArrayList;


public class Bank {
    
    ArrayList<Customer> customers;
   
    public Bank() {
        customers = new ArrayList<Customer>();
        customers.add(new Customer("Ahmed", 30,"A30",3000.0));
        customers.add(new Customer("Ali", 25,"A25",4000.50));
        customers.add(new Customer("Mona", 21,"M21",500000.0));
    }
    
    public Customer login (String code)
    {
        for (Customer c:customers)
            if (c.code.equals(code))
                return c;
        return null;    
    }
    
    public void addCustomer(Customer c)
    {
        customers.add(c);
    }
       
    
}
