import java.util.*;

public class CoffeeMakerQuestImpl implements CoffeeMakerQuest {

	Player player;
	
	ArrayList<Room> rooms = new ArrayList<Room>();
	private int currRoom = -1;
	private boolean drank = false;
	
	
	CoffeeMakerQuestImpl() { }

	/**
	 * Whether the game is over. The game ends when the player drinks the coffee.
	 * 
	 * @return true if successful, false otherwise
	 */
	public boolean isGameOver() {
		if(fullInventory() && drank) {
			return true;
		}
		return false;
	}

	/**
	 * Set the player to p.
	 * 
	 * @param p the player
	 */
	public void setPlayer(Player p) {
		player = p;
	}
	
	/**
	 * Add the first room in the game. If room is null or if this not the first room
	 * (there are pre-exiting rooms), the room is not added and false is returned.
	 * 
	 * @param room the room to add
	 * @return true if successful, false otherwise
	 */
	public boolean addFirstRoom(Room room) {
		if(room == null) {
			return false;
		}
		
		if(rooms.size() == 0) {
			rooms.add(room);
			currRoom = 0;
			return true;
		}
		return false;
	}

	/**
	 * Attach room to the northern-most room. 
	 * If either room, northDoor, or southDoor are null, the room is not added.
	 * If there are no pre-exiting rooms, the room is not added.
	 * If room is not a unique room (a pre-exiting room has the same adjective or furnishing), the room is not added.
	 * If all these tests pass, the room is added.
	 * Also, the north door of the northern-most room is labeled northDoor
	 * and the south door of the added room is labeled southDoor.
	 * Of course, the north door of the new room is still null because there is
	 * no room to the north of the new room.
	 * 
	 * @param room      the room to add
	 * @param northDoor string to label the north door of the current northern-most room
	 * @param southDoor string to label the south door of the newly added room
	 * @return true if successful, false otherwise
	 */
	public boolean addRoomAtNorth(Room room, String northDoor, String southDoor) {
		if(room == null || northDoor == null || southDoor == null) {
			return false;
		}
		
		if(rooms.size() == 0) {
			return false;
		}
		
		for(Room r : rooms) {
			if(r.getAdjective().equalsIgnoreCase(room.getAdjective())) {
				return false;
			}
			if(r.getFurnishing().equalsIgnoreCase(room.getFurnishing())) {
				return false;
			}
		}
		rooms.get(rooms.size()-1).setNorthDoor(northDoor);
		room.setSouthDoor(southDoor);
		rooms.add(room);
		return true;
	}

	/**
	 * Returns the room the player is currently in. If location of player has not
	 * yet been initialized with setCurrentRoom, returns null.
	 * 
	 * @return room player is in, or null if not yet initialized
	 */ 
	public Room getCurrentRoom() {
		if(currRoom < 0) {
			return null;
		}
		return rooms.get(currRoom);
	}
	
	/**
	 * Set the current location of the player. If room does not exist in the game,
	 * then the location of the player does not change and false is returned.
	 * 
	 * @param room the room to set as the player location
	 * @return true if successful, false otherwise
	 */
	public boolean setCurrentRoom(Room room) {
		if(room == null) {
			return false;
		}
		
		for(int i = 0; i < rooms.size(); i++) {
			Room temp = rooms.get(i);
			if(temp.equals(room)) {
				currRoom = i;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the instructions string command prompt. It returns the following prompt:
	 * " INSTRUCTIONS (N,S,L,I,D,H) > ".
	 * 
	 * @return command prompt string
	 */
	public String getInstructionsString() {
		return " INSTRUCTIONS (N,S,L,I,D,H) > ";
	}
	
	/**
	 * Processes the user command given in String cmd and returns the response
	 * string. For the list of commands, please see the Coffee Maker Quest
	 * requirements documentation (note that commands can be both upper-case and
	 * lower-case). For the response strings, observe the response strings printed
	 * by coffeemaker.jar. The "N" and "S" commands potentially change the location
	 * of the player. The "L" command potentially adds an item to the player
	 * inventory. The "D" command drinks the coffee and ends the game. Make
     * sure you use Player.getInventoryString() whenever you need to display
     * the inventory.
	 * 
	 * @param cmd the user command
	 * @return response string for the command
	 */
	public String processCommand(String cmd) {
		switch(cmd.toUpperCase()) {
		case "N":
			moveNorth();
			break;
			
		case "S":
			moveSouth();
			break;
				
		case "L":
			look();
			break;
			
		case "I":
			displayInventory();
			break;
				
		case "D":
			drink();
			break;
			
		case "H":
			displayHelp();
			break;
				
		}
		return "";
	}
	
	public void moveNorth() {
		if(currRoom >= rooms.size()-1) {
			doorDoesNotExist();
			return;
		}
		currRoom++;
		String roomDesc = getCurrentRoom().getDescription();
		System.out.println(roomDesc);
		
	}
	
	public void moveSouth() {
		if(currRoom == 0) {
			doorDoesNotExist();
			return;
		}
		currRoom--;
		String desc = getCurrentRoom().getDescription();
		System.out.println(desc);
	}
	
	private void doorDoesNotExist() {
		System.out.println("A door in that direction does not exist.\n");
	}
	
	public void look() {
		Item item = getCurrentRoom().getItem();
		player.addItem(item);
		if(item == Item.COFFEE) {
			System.out.println("There might be something here...\nYou have found a delicious cup of coffee.");
		}
		else if(item == Item.CREAM) {
			System.out.print("There might be something here...\nYou found some creamy cream!\n");
		}
		else if(item == Item.SUGAR) {
			System.out.println("You have found some sugar cubes.");
		}
		else {
			System.out.println("You have found nothing!");
		}
		
	}
	
	public void displayInventory() {
		String msg = player.getInventoryString();
		System.out.println(msg);
	}
	
	public void drink() {
		drank = true;
		if (player.checkCoffee() && player.checkCream() && player.checkSugar()) // player has all 3
		{
			System.out.println("You have a cup of delicious coffee.\nYou have some fresh cream.\nYou have some tasty sugar.\n\nYou drink the beverage and are ready to study!\nYou win!\n");
		}
		else if (!player.checkCoffee() && !player.checkCream() && !player.checkSugar()) // player has none
		{
			System.out.println("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n\nYou drink the air, as you have no coffee, sugar, or cream.\nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n");
		}
		else if (!player.checkCoffee() && !player.checkCream() && player.checkSugar()) // player only has sugar
		{
			System.out.println( "YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYou have some tasty sugar.\n\nYou drink the air, as you have no coffee, or cream.\nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n");
		}
		else if (!player.checkCoffee() && player.checkCream() && !player.checkSugar()) // player only has cream
		{
			System.out.println("YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYOU HAVE NO SUGAR!\n\nYou drink the air, as you have no coffee, or sugar.\nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n");
		}
		else if (player.checkCoffee() && !player.checkCream() && !player.checkSugar()) // player only has coffee
		{
			System.out.println("You have a cup of delicious coffee.\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n\nYou drink the air, as you have no sugar, or cream.\nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n");
		}
		else if (player.checkCoffee() && player.checkCream() && !player.checkSugar()) // player has coffee and cream
		{
			System.out.println("You have a cup of delicious coffee.\nYou have some fresh cream.\nYOU HAVE NO SUGAR!\n\nYou drink the air, as you have no sugar.\nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n");
		}
		else if (player.checkCoffee() && !player.checkCream() && player.checkSugar()) // player has coffee and sugar
		{
			System.out.println("You have a cup of delicious coffee.\nYOU HAVE NO CREAM!\nYou have some tasty sugar.\n\nYou drink the air, as you have no cream.\nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n");
		}
		else if (!player.checkCoffee() && player.checkCream() && player.checkSugar()) // player has cream and sugar
		{
			System.out.println("YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYou have some tasty sugar.\n\nYou drink the air, as you have no coffee. \nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n");
		}
	}

	public void displayHelp() {
		String msg = "";
		msg += "N - Moves the player north if there is an available room.\n";
		msg += "S - Moves the player south if there is an available room.\n";
		msg += "L - Look for items in your current room.\n";
		msg += "I - Access your inventory of items currently possessed.\n";
		msg += "D - Drink the mixture to decide the fate of your quest...\n";
		msg += "H - Help\n";
		System.out.print(msg);
	}
	
	public boolean fullInventory() {
		if(player.checkCoffee() && player.checkCream() && player.checkSugar()) {
			return true;
		} else {
			return false;
		}
	}
}
