package com.example.goosegame.controller;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.example.goosegame.business.IBoardBusiness;
import com.example.goosegame.model.GameStateEnum;
import com.example.goosegame.utils.CommandsUtils;
import com.example.goosegame.utils.MessagesFactory;

//Controller class, for management of I/O with the Console and invocation of business logic
@Controller
public class GameController {
 
	@Autowired
	MessagesFactory messages;

	@Autowired
	IBoardBusiness boardBusiness;

	// Manages I/O with Console and execution of the commands (invoking business class for execution of the game logic)
	public void startGame() {

		printMessage(messages.get("message.game.start"));

		Scanner scanner = new Scanner(System.in);
	
		//Game logic and command execution continues until game has a winner (or user quits the game)
		while (!boardBusiness.checkStateOfTheGame().equals(GameStateEnum.GAME_ENDED)) {
			try{	
				printMessage(messages.get("message.enter.command"));
				String command = scanner.nextLine();
				switch(checkCommandType(command)){
					// Invokes business method for adding players
					case CommandsUtils.ADD_PLAYER_COMMAND : 
						printMessage(boardBusiness.addPlayer(command));
						break;
					// Invokes business method that starts the game and sets dice mode (Automatic or Manual)
					case CommandsUtils.START_GAME_COMMAND : 
						String diceMode = diceModeSelection(scanner);
						printMessage(boardBusiness.startGame(diceMode));				
						break;
					// Invokes business method that restarts the game (re-initialization of players and positions)
					case CommandsUtils.RESTART_GAME_COMMAND : 
						printMessage(boardBusiness.restartGame());
						break;
					// Invoke business method that stops the game
					case CommandsUtils.QUIT_GAME_COMMAND : 
						printMessage(boardBusiness.quitGame());
						break;
					// Invoke business method for management of the players' movements on the board
					case CommandsUtils.MOVE_COMMAND : 
						printMessage(boardBusiness.movePlayer(command));
						break;
					// Message of Wrong Command (from syntactic or logical point of view, according to the state of the game)
					default :
						printMessage(messages.get("error.wrong.command"));
				}
			}catch(Exception e){
				printMessage(e.getMessage());
			}

		}
				
		scanner.close();
	}
	
	// Method for determination of dice mode (Automatic or Manual).
	// If input command received from Console is wrong, dice mode is setted by default on Automatic
	private String diceModeSelection(Scanner scanner){
		printMessage(messages.get("message.game.dicemode"));
		String diceMode = scanner.nextLine();
		if(! (diceMode.toUpperCase().equals("M") || diceMode.toUpperCase().equals("A"))){
			diceMode = "A";
			printMessage(messages.get("alert.game.diceMode"));
		}
		return diceMode.toUpperCase();
	}

	// Method for command validation
	private String checkCommandType(String command) {
		if (command.startsWith(CommandsUtils.ADD_PLAYER_COMMAND)
				&& boardBusiness.checkStateOfTheGame().equals(GameStateEnum.PLAYERS_SETTINGS))
			return CommandsUtils.ADD_PLAYER_COMMAND;
		if (command.equals(CommandsUtils.START_GAME_COMMAND))
			return CommandsUtils.START_GAME_COMMAND;
		else if(command.startsWith(CommandsUtils.MOVE_COMMAND)
				&& boardBusiness.checkStateOfTheGame().equals(GameStateEnum.GAME_IN_PROGRESS))
			return CommandsUtils.MOVE_COMMAND;
		if (command.equals(CommandsUtils.RESTART_GAME_COMMAND))
			return CommandsUtils.RESTART_GAME_COMMAND;
		if (command.equals(CommandsUtils.QUIT_GAME_COMMAND))
			return CommandsUtils.QUIT_GAME_COMMAND;
		else
			return CommandsUtils.WRONG_COMMAND;
	}

	// Method for centralization of the printing of output messages
	private void printMessage(String message) {
		System.out.println(message);
	}

}
