
import java.util.Scanner;

  
public class GuessGame implements Runnable {

   private int number;
   
   public GuessGame(int number) {
      this.number = number;
   }
   
    @Override
    public void run() {
      int counter = 0;
      int guess = 0;
      Scanner input = new Scanner (System.in);
      do {
         System.out.println("Enter a number between 10 and 100");
         guess = input.nextInt();
         counter++;
         if (number>guess)
              System.out.println("Enter higher number.");
         else if (number<guess)
              System.out.println("Enter lower number.");                        
      } while(guess != number);
      System.out.println("** Correct!" + counter + "guesses.**");
    }
   
}
