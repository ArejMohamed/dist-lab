
public class Demo {
    public static void main (String[] args)
    {
        int rand = (int) (1+Math.random()*100);
        Thread game = new Thread(new GuessGame(rand));
        System.out.println("Game started");        
        game.start();                     
    }
}
