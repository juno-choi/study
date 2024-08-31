package hello.springmvc;

public class Test {

    @org.junit.jupiter.api.Test
    public void Test(){

    }

}
class Bank{
    private String bankName;
    static float investing = 0.1f;

    public Bank(String bankName){
        this.bankName = bankName;
    }

    public void printBank(){
        System.out.println("은행 : "+ bankName);
        System.out.println("금리 : "+ investing);
    }
}
class BankMain{
    public static void main(String[] args){
        Bank bank = new Bank("카카오뱅크");
        Bank.investing = 0.2f;
        bank.printBank();
    }
}

class StaticTest2{
    int c;
    static int count;
    public StaticTest2(){
        c++;
        count++;
    }
}

class StaticTest3{
    public static void main(String[] args){
        StaticTest2 s1 = new StaticTest2();
        System.out.println("s1의 c = "+s1.c+", s1의 count = "+s1.count);

        StaticTest2 s2 = new StaticTest2();
        System.out.println("s2의 c = "+s2.c+", s2의 count = "+s2.count);


        StaticTest2 s3 = new StaticTest2();
        System.out.println("s3의 c = "+s3.c+", s3의 count = "+s3.count);
    }
}

class StaticTest4{

    int a = 100;
    static int b = 200;
    static{
        b=5000;
        c=10000;
    }
    public static void main(String[] args){
        System.out.println("c = "+c);
    }
    static int c;
}