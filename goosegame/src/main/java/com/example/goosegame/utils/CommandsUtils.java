package com.example.goosegame.utils;

// Class for management of game's commands list, usable from Console
public class CommandsUtils {

	public static final String ADD_PLAYER_COMMAND = "add player ";
	public static final String START_GAME_COMMAND = "start game";
	public static final String MOVE_COMMAND = "move ";
	public static final String RESTART_GAME_COMMAND = "restart game";
	public static final String QUIT_GAME_COMMAND = "quit game";
	public static final String WRONG_COMMAND = "command error";

	// Utils method for commands' splitting
	public static String[] parseCommand(String command) {
		return command.split(" ");
	}

}
