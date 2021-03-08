import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.*;
import org.mockito.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CoffeeMakerQuestTest {

	CoffeeMakerQuest cmq;
	Player player;
	Room room1;	// Small room
	Room room2;	// Funny room
	Room room3;	// Refinanced room
	Room room4;	// Dumb room
	Room room5;	// Bloodthirsty room
	Room room6;	// Rough room
	
	@Before
	public void setup() {
		// 0. Turn on bug injection for Player and Room.
		Config.setBuggyPlayer(true);
		Config.setBuggyRoom(true);
		
		// 1. Create the Coffee Maker Quest object and assign to cmq.
		cmq = CoffeeMakerQuest.createInstance();

		// TODO: 2. Create a mock Player and assign to player and call cmq.setPlayer(player). 
		// Player should not have any items (no coffee, no cream, no sugar)
		player = mock(Player.class);
		when(player.checkCoffee()).thenReturn(false);
		when(player.checkCream()).thenReturn(false);
		when(player.checkSugar()).thenReturn(false);
		doNothing().when(player).addItem(Item.COFFEE);
		doNothing().when(player).addItem(Item.CREAM);
		doNothing().when(player).addItem(Item.SUGAR);
		doNothing().when(player).addItem(Item.NONE);

		cmq.setPlayer(player);
		
		// TODO: 3. Create mock Rooms and assign to room1, room2, ..., room6.
		// Mimic the furnishings / adjectives / items of the rooms in the original Coffee Maker Quest.
		room1 = mock(Room.class);
		when(room1.getFurnishing()).thenReturn("Quaint sofa");
		when(room1.getAdjective()).thenReturn("Small");
		when(room1.getItem()).thenReturn(Item.CREAM);
		room2 = mock(Room.class);
		when(room2.getFurnishing()).thenReturn("Sad record player");
		when(room2.getAdjective()).thenReturn("Funny");
		when(room2.getItem()).thenReturn(Item.NONE);
		room3 = mock(Room.class);
		when(room3.getFurnishing()).thenReturn("Tight pizza");
		when(room3.getAdjective()).thenReturn("Refinanced");
		when(room3.getItem()).thenReturn(Item.COFFEE);
		room4 = mock(Room.class);
		when(room4.getFurnishing()).thenReturn("Flat energy drink");
		when(room4.getAdjective()).thenReturn("Dumb");
		when(room4.getItem()).thenReturn(Item.NONE);
		room5 = mock(Room.class);
		when(room5.getFurnishing()).thenReturn("Beautiful bag of money");
		when(room5.getAdjective()).thenReturn("Bloodthirsty");
		when(room5.getItem()).thenReturn(Item.NONE);
		room6 = mock(Room.class);
		when(room6.getFurnishing()).thenReturn("Perfect air hockey table");
		when(room6.getAdjective()).thenReturn("Rough");
		when(room6.getItem()).thenReturn(Item.SUGAR);

		// TODO: 4. Add the rooms created above to mimic the layout of the original Coffee Maker Quest.
		cmq.addFirstRoom(room1);
		cmq.addRoomAtNorth(room2, "Refinanced room", "Small room");
		cmq.addRoomAtNorth(room3, "Dumb room", "Funny room");
		cmq.addRoomAtNorth(room4, "Bloodthirsty room", "Refinanced room");
		cmq.addRoomAtNorth(room5, "Rough room", "Dumb room");
		// wrap around
		cmq.addRoomAtNorth(room6, "Small room", "Bloodthirsty room");
	}

	@After
	public void tearDown() { }
	
	/**
	 * Test case for String getInstructionsString().
	 * Preconditions: None.
	 * Execution steps: Call cmq.getInstructionsString().
	 * Postconditions: Return value is " INSTRUCTIONS (N,S,L,I,D,H) > ".
	 */
	@Test
	public void testGetInstructionsString() {
		String ret = cmq.getInstructionsString();
		assertEquals(ret," INSTRUCTIONS (N,S,L,I,D,H) > ");
	}
	
	/**
	 * Test case for boolean addFirstRoom(Room room).
	 * Preconditions: room1 ~ room6 have been added to cmq.
	 *                Create a mock room and assign to myRoom.
	 * Execution steps: Call cmq.addFirstRoom(myRoom).
	 * Postconditions: Return value is false.
	 */
	@Test
	public void testAddFirstRoom() {
		Room myRoom = mock(Room.class);
		assertFalse(cmq.addFirstRoom(myRoom));
	}
	
	/**
	 * Test case for boolean addRoomAtNorth(Room room, String northDoor, String southDoor).
	 * Preconditions: room1 ~ room6 have been added to cmq.
	 *                Create a mock "Fake" room with "Fake bed" furnishing with no item, and assign to myRoom.
	 * Execution steps: Call cmq.addRoomAtNorth(myRoom, "North", "South").
	 * Postconditions: Return value is true.
	 *                 room6.setNorthDoor("North") is called.
	 *                 myRoom.setSouthDoor("South") is called.
	 */
	@Test
	public void testAddRoomAtNorthUnique() {
		Room myRoom = mock(Room.class);
		when(myRoom.getFurnishing()).thenReturn("Fake bed");
		when(myRoom.getItem()).thenReturn(null);
		doNothing().when(room6).setNorthDoor("North");
		doNothing().when(myRoom).setSouthDoor("South");
		assertTrue(cmq.addRoomAtNorth(myRoom, "North", "South"));
		verify(room6, Mockito.atLeastOnce()).setNorthDoor("North");
		verify(myRoom, Mockito.atLeastOnce()).setSouthDoor("South");
	}
	
	/**
	 * Test case for boolean addRoomAtNorth(Room room, String northDoor, String southDoor).
	 * Preconditions: room1 ~ room6 have been added to cmq.
	 *                Create a mock "Fake" room with "Flat energy drink" furnishing with no item, and assign to myRoom.
	 * Execution steps: Call cmq.addRoomAtNorth(myRoom, "North", "South").
	 * Postconditions: Return value is false.
	 *                 room6.setNorthDoor("North") is not called.
	 *                 myRoom.setSouthDoor("South") is not called.
	 */
	@Test
	public void testAddRoomAtNorthDuplicate() {
		Room myRoom = mock(Room.class);
		when(myRoom.getFurnishing()).thenReturn("Flat energy drink");
		when(myRoom.getItem()).thenReturn(null);
		assertFalse(cmq.addRoomAtNorth(myRoom, "North", "South"));
		verify(room6, Mockito.never()).setNorthDoor("North");
		verify(myRoom, Mockito.never()).setSouthDoor("South");
	}
	
	/**
	 * Test case for Room getCurrentRoom().
	 * Preconditions: room1 ~ room6 have been added to cmq.
	 *                cmq.setCurrentRoom(Room) has not yet been called.
	 * Execution steps: Call cmq.getCurrentRoom().
	 * Postconditions: Return value is null.
	 */
	@Test
	public void testGetCurrentRoom() {
		Room ret = cmq.getCurrentRoom();
		System.out.println("testGetCurrentRoom ret: "+ret);
		assertNull(ret);
	}
	
	/**
	 * Test case for void setCurrentRoom(Room room) and Room getCurrentRoom().
	 * Preconditions: room1 ~ room6 have been added to cmq.
	 *                cmq.setCurrentRoom(Room room) has not yet been called.
	 * Execution steps: Call cmq.setCurrentRoom(room3).
	 *                  Call cmq.getCurrentRoom().
	 * Postconditions: Return value of cmq.setCurrentRoom(room3) is true. 
	 *                 Return value of cmq.getCurrentRoom() is room3.
	 */
	@Test
	public void testSetCurrentRoom() {
		boolean ret1 = cmq.setCurrentRoom(room3);
		Room ret2 = cmq.getCurrentRoom();
		assertTrue(ret1);
		System.out.println("testSetCurrentRoom: "+ret1);
		System.out.println("testSetCurrentRoom: "+ret2);
		assertEquals(ret2, room3);
	}
	
	/**
	 * Test case for String processCommand("I").
	 * Preconditions: Player does not have any items.
	 * Execution steps: Call cmq.processCommand("I").
	 * Postconditions: Return value is "YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n".
	 */
	@Test
	public void testProcessCommandI() {
		when(player.getInventoryString()).thenReturn("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n");
		cmq.setPlayer(player);
		String ret = cmq.processCommand("I");
		System.out.println("testProcessCommandI ret: "+ret);
		assertEquals(cmq.processCommand("I"), "YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n");
	}
	
	/**
	 * Test case for String processCommand("l").
	 * Preconditions: room1 ~ room6 have been added to cmq.
	 *                cmq.setCurrentRoom(room1) has been called.
	 * Execution steps: Call cmq.processCommand("l").
	 * Postconditions: Return value is "There might be something here...\nYou found some creamy cream!\n".
	 *                 player.addItem(Item.CREAM) is called.
	 */
	@Test
	public void testProcessCommandLCream() {
		String ret = cmq.processCommand("l");
		assertEquals(ret, "There might be something here...\nYou found some creamy cream!\n");
		verify(player, Mockito.atLeastOnce()).addItem(Item.CREAM);
	}
	
	/**
	 * Test case for String processCommand("n").
	 * Preconditions: room1 ~ room6 have been added to cmq.
	 *                cmq.setCurrentRoom(room4) has been called.
	 * Execution steps: Call cmq.processCommand("n").
	 *                  Call cmq.getCurrentRoom().
	 * Postconditions: Return value of cmq.processCommand("n") is "".
	 *                 Return value of cmq.getCurrentRoom() is room5.
	 */
	@Test
	public void testProcessCommandN() {
		cmq.setCurrentRoom(room4);
		String ret1 = cmq.processCommand("n");
		Room ret2 = cmq.getCurrentRoom();
		assertEquals("", ret1);
		assertEquals(room5, ret2);
	}
	
	/**
	 * Test case for String processCommand("s").
	 * Preconditions: room1 ~ room6 have been added to cmq.
	 *                cmq.setCurrentRoom(room1) has been called.
	 * Execution steps: Call cmq.processCommand("s").
	 *                  Call cmq.getCurrentRoom().
	 * Postconditions: Return value of cmq.processCommand("s") is "A door in that direction does not exist.\n".
	 *                 Return value of cmq.getCurrentRoom() is room1.
	 */
	@Test
	public void testProcessCommandS() {
		cmq.setCurrentRoom(room1);
		String ret1 = cmq.processCommand("s");
		Room ret2 = cmq.getCurrentRoom();
		assertEquals(ret1, "A door in that direction does not exist.\n");
		assertEquals(ret2, room1);
	}
	
	/**
	 * Test case for String processCommand("D").
	 * Preconditions: Player has no items.
	 * Execution steps: Call cmq.processCommand("D").
	 *                  Call cmq.isGameOver().
	 * Postconditions: Return value of cmq.processCommand("D") is "YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n\nYou drink the air, as you have no coffee, sugar, or cream.\nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n".
	 *                 Return value of cmq.isGameOver() is true.
	 */
	@Test
	public void testProcessCommandDLose() {
		when(player.getInventoryString()).thenReturn("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n");
		String ret1 = cmq.processCommand("D");
		boolean ret2 = cmq.isGameOver();
		//"YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n"
		//\nYou drink the air, as you have no coffee, sugar, or cream.\nThe air is invigorating, but not invigorating enough.  You cannot study."
		//\nYou lose!
		assertEquals("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n\nYou drink the air, as you have no coffee, sugar, or cream.\nThe air is invigorating, but not invigorating enough.  You cannot study.\nYou lose!", ret1);
		assertTrue(ret2);
	}
	
	/**
	 * Test case for String processCommand("D").
	 * Preconditions: Player has all 3 items (coffee, cream, sugar).
	 * Execution steps: Call cmq.processCommand("D").
	 *                  Call cmq.isGameOver().
	 * Postconditions: Return value of cmq.processCommand("D") is "You have a cup of delicious coffee.\nYou have some fresh cream.\nYou have some tasty sugar.\n\nYou drink the beverage and are ready to study!\nYou win!\n".
	 *                 Return value of cmq.isGameOver() is true.
	 */
	@Test
	public void testProcessCommandDWin() {
		when(player.checkCoffee()).thenReturn(true);
		when(player.checkCream()).thenReturn(true);
		when(player.checkSugar()).thenReturn(true);
		when(player.getInventoryString()).thenReturn("You have a cup of delicious coffee.\nYou have some fresh cream.\nYou have some tasty sugar.\n");
		String ret1 = cmq.processCommand("D");
		boolean ret2 = cmq.isGameOver();
		assertEquals("You have a cup of delicious coffee.\nYou have some fresh cream.\nYou have some tasty sugar.\n\nYou drink the beverage and are ready to study!\nYou win!\n", ret1);
		assertTrue(ret2);
	}
	
	// TODO: Put in more unit tests of your own making to improve coverage!
	
	 /* Test case for boolean addFirstRoom(Room room).
	 * Preconditions: room1 ~ room6 have been added to cmq.
	 *                Create a mock room and assign to myRoom.
	 * Execution steps: Call cmq.addFirstRoom(null).
	 * Postconditions: Return value is false.
	 */
	@Test
	public void testAddFirstRoomNull() {
		assertFalse(cmq.addFirstRoom(null));
	}
	
	@Test
	public void testAddRoomAtNorthAllNull() {
		assertFalse(cmq.addRoomAtNorth(null, null, null));
		verify(room6, Mockito.never()).setNorthDoor(null);
	}

	/**
	 * Test case for displayHelp()
	 * Preconditions: none
	 * Execution steps: Call cmq.processCommand("H")
	 * Postconditions: Return value of cmq.processCommand("H") is 
	 * 	msg += "N - Go north\n";
		msg += "S - Go south\n";
		msg += "L - Look and collect any items in the room\n";
		msg += "I - Show inventory of items collected\n";
		msg += "D - Drink coffee made from items in inventory\n";
		msg += "H - Help\n";													
	 */
	@Test
	public void testDisplayHelp() {
		String test = cmq.processCommand("H");
		assertEquals("N - Go north\nS - Go south\nL - Look and collect any items in the room\nI - Show inventory of items collected\nD - Drink coffee made from items in inventory\n", test);
	}
	
	@Test
	public void checkPlayerOnlySugar() {
		when(player.checkCoffee()).thenReturn(false);
		when(player.checkCream()).thenReturn(false);
		when(player.checkSugar()).thenReturn(true);
		when(player.getInventoryString()).thenReturn("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYou have some tasty sugar.\n");
		assertEquals("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYou have some tasty sugar.\n\nYou eat the sugar, but without caffeine, you cannot study.\nYou lose!\n", cmq.processCommand("D"));
	}
	
	@Test
	public void checkPlayerOnlyCoffee() {
		when(player.checkCoffee()).thenReturn(true);
		when(player.checkCream()).thenReturn(false);
		when(player.checkSugar()).thenReturn(false);
		when(player.getInventoryString()).thenReturn("You have a cup of delicious coffee.\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n");
		assertEquals("You have a cup of delicious coffee.\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n\nWithout cream, you get an ulcer and cannot study.\nYou lose!\n", cmq.processCommand("D"));
	}
	
	@Test
	public void checkPlayerOnlyCream() {
		when(player.checkCoffee()).thenReturn(false);
		when(player.checkCream()).thenReturn(true);
		when(player.checkSugar()).thenReturn(false);
		when(player.getInventoryString()).thenReturn("YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYOU HAVE NO SUGAR!\n");
		assertEquals("YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYOU HAVE NO SUGAR!\n\nYou drink the cream, but without caffeine you cannot study.\nYou lose!\n", cmq.processCommand("D"));
	}
	
	@Test
	public void checkPlayerOnlyCoffeeAndCream() {
		when(player.checkCoffee()).thenReturn(true);
		when(player.checkCream()).thenReturn(true);
		when(player.checkSugar()).thenReturn(false);
		when(player.getInventoryString()).thenReturn("You have a cup of delicious coffee.\nYou have some fresh cream.\nYOU HAVE NO SUGAR!\n");
		assertEquals("You have a cup of delicious coffee.\nYou have some fresh cream.\nYOU HAVE NO SUGAR!\n\nWithout sugar, the coffee is too bitter. You cannot study.\nYou lose!\n", cmq.processCommand("D"));
	}
	
	@Test
	public void checkPlayerOnlyCreamAndSugar() {
		when(player.checkCoffee()).thenReturn(false);
		when(player.checkCream()).thenReturn(true);
		when(player.checkSugar()).thenReturn(true);
		when(player.getInventoryString()).thenReturn("YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYou have some tasty sugar.\n");
		assertEquals("YOU HAVE NO COFFEE!\nYou have some fresh cream.\nYou have some tasty sugar.\n\nYou drink the sweetened cream, but without caffeine, you cannot study.\nYou lose!\n", cmq.processCommand("D"));
	}
	@Test
	public void checkPlayerNoItems() {
		when(player.checkCoffee()).thenReturn(false);
		when(player.checkCream()).thenReturn(false);
		when(player.checkSugar()).thenReturn(false);
		when(player.getInventoryString()).thenReturn("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n");
		assertEquals("YOU HAVE NO COFFEE!\nYOU HAVE NO CREAM!\nYOU HAVE NO SUGAR!\n\nYou drink the air, as you have no coffee, sugar, or cream.\nThe air is invigorating, but not invigorating enough. You cannot study.\nYou lose!\n", cmq.processCommand("D"));
	}
	
	/**
	 * Test case for private doorDoesNotExist()
	 * Precondtions: none
	 * Executionsteps: returnValue = doorDoesNotExist()
	 * Postconditions: return value should equal 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	@Test
	public void testPrivate() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Method doorDoesNotExistMethod = CoffeeMakerQuestImpl.class.getDeclaredMethod("doorDoesNotExist");
		doorDoesNotExistMethod.setAccessible(true);
		String ret = (String) doorDoesNotExistMethod.invoke(cmq);
//		String ret = (String) doorDoesNotExistMethod.invoke(cmq, "doorDoesNotExist");
		assertEquals("A door in that direction does not exist!\n", ret);
	}
}
