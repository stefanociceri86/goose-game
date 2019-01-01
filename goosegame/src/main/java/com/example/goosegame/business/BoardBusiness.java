package com.example.goosegame.business;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.example.goosegame.model.Board;
import com.example.goosegame.model.DiceModeEnum;
import com.example.goosegame.model.GameStateEnum;
import com.example.goosegame.utils.CommandsUtils;
import com.example.goosegame.utils.GameConstants;
import com.example.goosegame.utils.MessagesFactory;

// Business class, used by the Controller as a Singleton Service 
// for the execution of the Goose Game's logics
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BoardBusiness implements IBoardBusiness {

	@Autowired
	MessagesFactory messages;

	@Autowired
	Board boardGame;

	// Method that retrieves the current state of the game
	public GameStateEnum checkStateOfTheGame() {
		return boardGame.getStateOfTheGame();
	}

	// Method for adding players, before game starting
	public String addPlayer(String playerName) throws Exception {
		String[] cmdPlayersList = CommandsUtils.parseCommand(playerName);
		if (cmdPlayersList.length != 3)
			throw new Exception(messages.get("error.addplayer.name"));
		String newPlayerName = cmdPlayersList[2];
		Map<String, Integer> actualPlayers = boardGame.getPlayers();
		if (!actualPlayers.containsKey(newPlayerName)) {
			boardGame.addPlayer(newPlayerName);
			return messages.get("message.current.players", new Object[] { actualPlayers.keySet().toString() });
		} else
			throw new Exception(messages.get("alert.existingPlayer", new Object[] { newPlayerName }));
	}

	// Method that starts game and sets the dice mode (default Automatic)
	public String startGame(String diceMode) throws Exception {
		if (boardGame.getPlayers().size() >= 2) {
			boardGame.setStateOfTheGame(GameStateEnum.GAME_IN_PROGRESS);
			DiceModeEnum gameDiceMode = diceMode.equals("M") ? DiceModeEnum.MANUAL : DiceModeEnum.AUTOMATIC;
			boardGame.setDiceMode(gameDiceMode);
			return messages.get("message.game.started");
		} else
			throw new Exception(messages.get("alert.players.number"));

	}
	
	// Method for moves management
	// Dice Mode AUTOMATIC : Random number between 1 and 6 for each dice
	// Dice Mode MANUAL : Dice numbers of user command
	public String movePlayer(String moveCommand) throws Exception{
		
		Map<String, Integer> gamePlayers = boardGame.getPlayers();
		String playerName = "";
		Integer dice1 = 0;
		Integer dice2 = 0;
		
		String[] cmdPlayerMovement = CommandsUtils.parseCommand(moveCommand);
		
		if(boardGame.getDiceMode().equals(DiceModeEnum.AUTOMATIC)){
			if (cmdPlayerMovement.length != 2)
				throw new Exception(messages.get("error.wrong.command"));
			playerName = cmdPlayerMovement[1];
			dice1 = getRandomDiceNumber();
			dice2 = getRandomDiceNumber();
			
		}
		else if (boardGame.getDiceMode().equals(DiceModeEnum.MANUAL)){
			if (cmdPlayerMovement.length != 4)
				throw new Exception(messages.get("error.wrong.command"));
			playerName = cmdPlayerMovement[1];
			dice1 = Integer.parseInt(cmdPlayerMovement[2].replace(",", ""));
			dice2 = Integer.parseInt(cmdPlayerMovement[3]);
		}
		
		try{
			validateMovement(gamePlayers, playerName, dice1, dice2);
		} catch(Exception e){
			throw new Exception(e);
		}
		
		return manageMovement(gamePlayers, playerName, dice1, dice2);
		
	}

	// Method that restarts the game (players and game state)
	public String restartGame() {
		boardGame.getPlayers().clear();
		boardGame.setStateOfTheGame(GameStateEnum.PLAYERS_SETTINGS);
		return messages.get("message.game.start");
	}

	// Method that stops the game (one player wins or user quits from Console)
	public String quitGame() {
		boardGame.setStateOfTheGame(GameStateEnum.GAME_ENDED);
		return messages.get("message.game.end");
	}
	
	// Method that retrieves current players
	public Map<String,Integer> retrievePlayers(){
		return boardGame.getPlayers();
	}
	
	// Method for movements parameters validation
	private void validateMovement(Map<String, Integer> gamePlayers, String playerName, Integer dice1, Integer dice2) throws Exception{
		if(gamePlayers.get(playerName) == null)
			throw new Exception(messages.get("error.moveplayer.name"));
		if(checkDiceRange(dice1) || checkDiceRange(dice2))
			throw new Exception(messages.get("error.dice.range"));
	}
	
	// Method for validation of the numbers of the dices
	private boolean checkDiceRange(Integer dice){
		if(dice.compareTo(1) >= 0 && dice.compareTo(6) <= 0)
			return false;
		else return true;
	}

	// Method for management of the movements. 
	// It changes on the board the position of the active player
	// If one player reaches winning position, stops the game because there's a winner
	private String manageMovement(Map<String, Integer> gamePlayers, String playerName, Integer dice1, Integer dice2){
		
		Integer actualPosition = gamePlayers.get(playerName);
		String actualPositionStr = getActualPositionStr(actualPosition);
		Integer newPosition = actualPosition + dice1 + dice2;
		String newPositionStr = getNewPositionStr(newPosition);
		
		String returnMessage = messages.get("message.game.movement", new Object[] { 
				playerName,dice1.toString(),dice2.toString(),actualPositionStr,newPositionStr
		});
		
		if(newPosition.compareTo(GameConstants.WINNING_POSITION) > 0){
			newPosition = GameConstants.WINNING_POSITION - (newPosition - GameConstants.WINNING_POSITION);
			returnMessage += messages.get("message.game.bounce", new Object[] { 
					playerName,newPosition
			});	
		}else if(newPosition.equals(GameConstants.WINNING_POSITION)){
			boardGame.setStateOfTheGame(GameStateEnum.GAME_ENDED);
			returnMessage += messages.get("message.game.winner", new Object[] { 
					playerName
			});	
		}
		
		returnMessage = manageSpecialPositions(newPosition, actualPosition, returnMessage, playerName, dice1 + dice2, gamePlayers);
		
		return returnMessage;
	}
	
	//Method for management of special movements
	private String manageSpecialPositions(Integer newPosition, Integer actualPosition, String returnMessage, String playerName, Integer diceSum, Map<String, Integer> gamePlayers){
		
		//Bridge Movement : from position 6 to position 12
		if(newPosition.equals(GameConstants.BRIDGE_POSITION)){
			newPosition = GameConstants.BRIDGE_NEW_POSITION;
			returnMessage += messages.get("message.game.bridge", new Object[] { 
					playerName,newPosition.toString()
			});	
		}
		
		//Goose Movement : if player reaches Goose Position , it moves again (same dice total) 
		while(Arrays.asList(GameConstants.GOOSE_POSITION).contains(newPosition)){
			newPosition += diceSum;
			returnMessage += messages.get("message.game.goose", new Object[] { 
					playerName,newPosition.toString()
			});	
		}
		
		gamePlayers.put(playerName, newPosition);
		
		// Prank Movement : if player1 reaches player2 on the board, 
		// player2 returns back in the starting position of player1
		for (Map.Entry<String, Integer> entry : gamePlayers.entrySet()) {
			String player = entry.getKey();
			Integer position = entry.getValue();
			if(! player.equals(playerName) && position.equals(newPosition)){
				gamePlayers.put(player, actualPosition);
				returnMessage += messages.get("message.game.prunk", new Object[] { 
						newPosition.toString(),player,actualPosition
				});
			}	
	    }
		
		return returnMessage;
		
	}
	
	// Method that constructs "actual position" string for the messages
	private String getActualPositionStr(Integer actualPosition){
		if(actualPosition.compareTo(GameConstants.START_POSITION) == 0)
			return "Start";
		else return actualPosition.toString();
	}
	
	// Method that constructs "new position" string for the messages
	private String getNewPositionStr(Integer newPosition){

		if(newPosition.compareTo(GameConstants.WINNING_POSITION) > 0)
			return GameConstants.WINNING_POSITION.toString();
		else if(newPosition.compareTo(GameConstants.BRIDGE_POSITION) == 0)
			return "Bridge";
		else return newPosition.toString();
	}
	
	// Method that generates random number in range from 1 to 6 for the dice
	private Integer getRandomDiceNumber(){
		int min = 1;
		int max = 6;
	    Random random = new Random();
	    return random.ints(min,(max+1)).findFirst().getAsInt();
	}
	
	
}
