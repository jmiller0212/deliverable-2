import java.util.*;

public class CoffeeMakerQuestImpl implements CoffeeMakerQuest {

	Player player;
	
	ArrayList<Room> rooms = new ArrayList<Room>();
	private int currRoom = 0;
	private boolean initializedCurrRoom = false;
	private boolean drank = false;	
	
	CoffeeMakerQuestImpl() { }

	/**
	 * Whether the game is over. The game ends when the player drinks the coffee.
	 * 
	 * @return true if successful, false otherwise
	 */
	public boolean isGameOver() {
		if(drank) {
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
			if(r.getAdjective().equalsIgnoreCase(room.getAdjective()) || r.getFurnishing().equalsIgnoreCase(room.getFurnishing())) {
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
		if(!initializedCurrRoom) {
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
		initializedCurrRoom = true;
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
			return moveNorth();
			
		case "S":
			return moveSouth();
				
		case "L":
			return look();
			
		case "I":
			return displayInventory();
				
		case "D":
			String ret = drink();
			return ret;
			
		case "H":
			return displayHelp();
		}
		return "What?\n";
	}
	
	public String moveNorth() {
		if(currRoom >= rooms.size()-1) {
			return doorDoesNotExist();
		}
		currRoom++;
		return "";
	}
	
	public String moveSouth() {
		if(currRoom == 0) {
			return doorDoesNotExist();
		}
		currRoom--;
		return "";
	}
	
	private String doorDoesNotExist() {
		return "A door in that direction does not exist.\n";
	}
	
	public String look() {
		String msg = "";
		Item item = getCurrentRoom().getItem();
		player.addItem(item);
		if(item != Item.NONE) {
			msg += "There might be something here...\n";
			if(item == Item.COFFEE) {
				msg += "You found some caffeinated coffee!\n";
			}
			else if(item == Item.CREAM) {
				msg += "You found some creamy cream!\n";
			}
			else {
				msg += "You found some sweet sugar!\n";
			}
		}
		else {
			msg += "You don't see anything out of the ordinary.\n";
		}
		return msg;
	}
	
	public String displayInventory() {
		String msg = player.getInventoryString();
		return msg;
	}
	
	public String drink() {
		drank = true;
		String msg = player.getInventoryString();
		// win condition
		if (player.checkCoffee() && player.checkCream() && player.checkSugar()) {
			msg += "\nYou drink the beverage and are ready to study!";
			msg += "\nYou win!\n";
		}
		// all lose condition outputs
		else {
			if(player.checkCoffee()) {
				if(!player.checkCream()) {
					msg += "\nWithout cream, you get an ulcer and cannot study.";
				}
				else {
					msg += "\nWithout sugar, the coffee is too bitter. You cannot study.";
				}
			}
			else if(player.checkCream()) {
				if(!player.checkSugar()) {
					msg += "\nYou drink the cream, but without caffeine, you cannot study.";
				}
				else {
					msg += "\nYou drink the sweetened cream, but without caffeine you cannot study.";
				}
			}
			else if(player.checkSugar()) {
				msg += "\nYou eat the sugar, but without caffeine, you cannot study.";
			}
			else {
				msg += "\nYou drink the air, as you have no coffee, sugar, or cream.\nThe air is invigorating, but not invigorating enough. You cannot study.";
			}
			msg += "\nYou lose!\n";
		}
		return msg;
	}

	public String displayHelp() {
		String msg = "";
		msg += "N - Go north\n";
		msg += "S - Go south\n";
		msg += "L - Look and collect any items in the room\n";
		msg += "I - Show inventory of items collected\n";
		msg += "D - Drink coffee made from items in inventory\n";
		return msg;
	}
}
