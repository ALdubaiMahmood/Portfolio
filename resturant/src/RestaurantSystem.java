import java.util.Scanner;

public class RestaurantSystem {

    //  MENU DATA 
    static String[] foodItems = {"Burger", "Fried Rice", "Chicken Chop", "Pizza"}; //  food items
    static double[] foodPrices = {8.50, 7.00, 12.00, 15.00}; //  prices for food

    static String[] drinkItems = {"Iced Tea", "Lemon Juice", "Coffee"}; //  drink items
    static double[] drinkPrices = {3.00, 4.00, 5.50}; //  prices for drinks

    static double subtotal = 0; // To store the running total of the order

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in); // Scanner object for user input

        printHeader(); // Print restaurant header

        boolean ordering = true; // Flag for the ordering loop
        while (ordering) {

            displayMenu(); // Show menu to the customer

            // Prompt user for item numbers and quantities
            System.out.print("\nEnter item numbers and quantities (e.g., 1x2,5x1): ");
            String line = input.nextLine();

            // Process input; if invalid, loop again
            if (!processInput(line)) {
                System.out.println("⚠ Invalid input format! Use number x quantity, separated by commas.\n");
                continue;
            }

            // Ask if user wants to continue ordering
            System.out.print("Do you want to order more items? (Y/N): ");
            String ans = input.nextLine();
            ordering = ans.equalsIgnoreCase("Y"); // Continue loop if user inputs 'Y'
        }

        printReceipt(); // Print the final receipt
        input.close(); // Close Scanner
    }
    // Display restaurant header
    public static void printHeader() {
        System.out.println("====================================================");
        System.out.println("                  META RESTAURANT                 ");
        System.out.println("                Welcome to Our System             ");
        System.out.println("====================================================");
    }
    // Display the menu with food and drink 
    public static void displayMenu() {
        System.out.println("\n-------------------------- MENU --------------------------");

        System.out.println("\n FOODS:");
        for (int i = 0; i < foodItems.length; i++) {
            System.out.printf("%d. %-20s RM %.2f%n", (i + 1), foodItems[i], foodPrices[i]); // Print food item and price
        }

        System.out.println("\n DRINKS:");
        for (int i = 0; i < drinkItems.length; i++) {
            System.out.printf("%d. %-20s RM %.2f%n", (i + 5), drinkItems[i], drinkPrices[i]); // Print drink item and price
        }

        System.out.println("-----------------------------------------------------------");
    }
    // Process user input and update subtotal
    public static boolean processInput(String line) {
        String[] orders = line.split(","); // Split multiple orders by comma
        for (String order : orders) {
            order = order.trim(); // Remove whitespace
            // Validate input format (number x quantity)
            if (!order.matches("\\d+x\\d+")) {
                return false; // Invalid format
            }
            String[] parts = order.split("x"); // Split item number and quantity
            int itemNum = Integer.parseInt(parts[0]);
            int qty = Integer.parseInt(parts[1]);
            // Validate item choice and quantity
            if (!isValidChoice(itemNum) || qty <= 0) {
                return false; // Invalid item number or quantity
            }
            double price = getPrice(itemNum); // Get price of select item
            String itemName = getItemName(itemNum); // Get name of select item

            double itemTotal = price * qty; // Calculate total for  item
            subtotal += itemTotal; // Add to subtotal

            System.out.println("✔ " + qty + " x " + itemName + " added. (RM " + String.format("%.2f", itemTotal) + ")");
        }
        System.out.println("-----------------------------------------------");
        return true; // Input processed successfully
    }
    // Check if item number is valid
    public static boolean isValidChoice(int choice) {
        return choice >= 1 && choice <= 7; // 1-4 for food, 5-7 for drinks
    }
    // Get price of item based on item number
    public static double getPrice(int choice) {
        if (choice >= 1 && choice <= 4) {
            return foodPrices[choice - 1];
        } else {
            return drinkPrices[choice - 5];
        }
    }
    // Get item name based on item number
    public static String getItemName(int choice) {
        if (choice >= 1 && choice <= 4) {
            return foodItems[choice - 1];
        } else {
            return drinkItems[choice - 5];
        }
    }
    // Print the final receipt
    public static void printReceipt() {
        double tax = subtotal * 0.06; // Calculate 6% service tax
        double total = subtotal + tax; // Calculate total amount

        System.out.println("\n\n=================== RECEIPT ================");

        System.out.printf("Subtotal:             RM %.2f%n", subtotal);
        System.out.printf("Service Tax (6%%):        RM %.2f%n", tax);
        System.out.printf("TOTAL AMOUNT:            RM %.2f%n", total);

        System.out.println("================================================");
        System.out.println("    Thank you for ordering from META Restaurant!");
        System.out.println("================================================");
    }
}
