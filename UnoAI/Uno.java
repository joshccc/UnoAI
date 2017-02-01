import java.util.ArrayList;
import java.util.Scanner;

public class Uno
{
    public static void main(String[] args)
    {
        ArrayList<Unocard> playerdeck = new ArrayList<Unocard>();
        ArrayList<Unocard> compdeck = new ArrayList<Unocard>();
        int win; // 0 - no result; 1 - win; -1 - loss. 
        Scanner input;
        Unocard topCard; // card on top of the "pile"
        int choiceIndex; // Index of chosen card for both player and computer
        String currentColor; // Mainly used for wild cards

        gameLoop:
        while (true)
        {
            playerdeck.clear();
            compdeck.clear();
            win = 0;
            topCard = new Unocard();
            currentColor = topCard.color;

            System.out.println("\nWelcome to Uno! Initialising decks...");
            draw(7, playerdeck);
            draw(7, compdeck);

            /*****************Turns*****************/
            for (boolean playersTurn = true; win == 0; playersTurn ^= true)
            {
                choiceIndex = 0;
                System.out.println("\nThe top card is: " + topCard.getFace());

                if (playersTurn) /*****Player's turn******/
                {
                    // Displaying user's deck
                    System.out.println("Your turn! Your choices:");
                    for (int i = 0; i < playerdeck.size(); i++)
                    {
                        System.out.print(String.valueOf(i + 1) + ". " + 
                        ((Unocard) playerdeck.get(i) ).getFace() + "\n");
                    }
                    System.out.println(String.valueOf(playerdeck.size() + 1 ) + ". " + "Draw card" + "\n" + 
                                       String.valueOf(playerdeck.size() + 2) + ". " + "Quit");
                    // Repeats every time the user doesn't input a number
                    do
                    {
                        System.out.print("\nPlaease input the number of your choice: ");
                        input = new Scanner(System.in);
                    } while (!input.hasNextInt() );
                    // The choices were incremented to make them seem more natural (i.e not starting with 0)
                    choiceIndex = input.nextInt() - 1;

                    // Taking action
                    if (choiceIndex == playerdeck.size() )
                        draw(1, playerdeck);
                    else if (choiceIndex == playerdeck.size() + 1)
                        break gameLoop;
                    else if ( ((Unocard) playerdeck.get(choiceIndex)).canPlace(topCard, currentColor) )
                    {
                        topCard = (Unocard) playerdeck.get(choiceIndex);
                        playerdeck.remove(choiceIndex);
                        currentColor = topCard.color;
                        // Producing the action of special cards                        
                        if (topCard.value >= 10)
                        {
                            playersTurn = false; // Skipping turn

                            switch (topCard.value)
                            {
                                case 12: // Draw 2
                                System.out.println("Drawing 2 cards...");
                                draw(2,compdeck);
                                break;

                                case 13: case 14: // Wild cards                         
                                do // Repeats every time the user doesn't input a valid color
                                {
                                    System.out.print("\nEnter the color you want: ");
                                    input = new Scanner(System.in);
                                } while (!input.hasNext("R..|r..|G....|g....|B...|b...|Y.....|y.....") ); //Something I learned recently
                                if (input.hasNext("R..|r..") )
                                    currentColor = "Red";
                                else if (input.hasNext("G....|g....") )
                                    currentColor = "Green";
                                else if (input.hasNext("B...|b...") )
                                    currentColor = "Blue";
                                else if (input.hasNext("Y.....|y.....") )
                                    currentColor = "Yellow";

                                System.out.println("You chose " + currentColor);
                                if (topCard.value == 14) // Wild draw 4
                                {
                                    System.out.println("Drawing 4 cards...");
                                    draw(4,compdeck);
                                }
                                break;
                            }
                        }
                    } else System.out.println("Invalid choice. Turn skipped.");


                } else /************ computer's turn **************/
                {
                    System.out.println("My turn! I have " + String.valueOf(compdeck.size() ) 
                                        + " cards left!" + ((compdeck.size() == 1) ? "...Uno!":"") );
                    // Finding a card to place
                    for (choiceIndex = 0; choiceIndex < compdeck.size(); choiceIndex++)
                    {
                        if ( ((Unocard) compdeck.get(choiceIndex)).canPlace(topCard, currentColor) ) // Searching for playable cards
                            break; 
                    }

                    if (choiceIndex == compdeck.size() )
                    {
                         System.out.println("I've got nothing! Drawing cards...");
                         draw(1,compdeck);
                    } else 
                    {
                         topCard = (Unocard) compdeck.get(choiceIndex);
                         compdeck.remove(choiceIndex);
                         currentColor = topCard.color;
                         System.out.println("I choose " + topCard.getFace() + " !");

                         // Must do as part of each turn because topCard can stay the same through a round
                         if (topCard.value >= 10)
                         {
                             playersTurn = true; // Skipping turn

                             switch (topCard.value)
                             {
                                 case 12: // Draw 2
                                 System.out.println("Drawing 2 cards for you...");
                                 draw(2,playerdeck);
                                 break;

                                 case 13: case 14: // Wild cards                         
                                 do // Picking a random color that's not none
                                 {
                                     currentColor = new Unocard().color;
                                 } while (currentColor == "none");

                                 System.out.println("New color is " + currentColor);
                                 if (topCard.value == 14) // Wild draw 4
                                 {
                                     System.out.println("Drawing 4 cards for you...");
                                     draw(4,playerdeck);
                                 }
                                 break;
                             }
                         }
                    }

                    // If decks are empty
                    if (playerdeck.size() == 0)
                        win = 1;
                    else if (compdeck.size() == 0)
                        win = -1;
                }

            } // turns loop end

            /*************Results**************/
            if (win == 1)
                System.out.println("You win :)");
            else 
                System.out.println("You lose :(");

            System.out.print("\nPlay again? ");
            input = new Scanner(System.in);

            if (input.next().toLowerCase().contains("n") )
                break;
        } // game loop end

        System.out.println("Bye bye");
    }
    // For drawing cards
    public static void draw(int cards, ArrayList<Unocard> deck)
    {
        for (int i = 0; i < cards; i++)
            deck.add(new Unocard() );
    }
}