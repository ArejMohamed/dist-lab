
public class Customer {

    String name;
    int age;
    String code;
    double balance;

    public Customer(String name, int age, String code, double balance) {
        this.name = name;
        this.age = age;
        this.code = code;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public double withdraw(double amount)
    {
        if (amount<balance)
        {
            balance-=amount;           
        }
        return balance;
    }
    
    public double deposit(double amount)
    {
        balance+=amount;
        return balance;
    }
}
