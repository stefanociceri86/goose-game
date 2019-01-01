package com.example.goosegame.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.goosegame.utils.GameConstants;

// Class that represents actual board's situation
@Component
public class Board {

	// Map of players, with their name and their position
	Map<String, Integer> players = new HashMap<>();

	// Game state
	GameStateEnum stateOfTheGame = GameStateEnum.PLAYERS_SETTINGS;

	// Dice Mode
	DiceModeEnum diceMode = DiceModeEnum.AUTOMATIC;

	// Method that retrieve actual players on the board
	public Map<String, Integer> getPlayers() {
		return players;
	}

	// Method for adding players to the board
	public void addPlayer(String playerName) {
		players.put(playerName, GameConstants.START_POSITION);
	}

	// Method that retrieve the state of the game
	public GameStateEnum getStateOfTheGame() {
		return stateOfTheGame;
	}

	// Method that changes the state of the game
	public void setStateOfTheGame(GameStateEnum stateOfTheGame) {
		this.stateOfTheGame = stateOfTheGame;
	}

	// Method that retrieve the dice mode of the game
	public DiceModeEnum getDiceMode() {
		return diceMode;
	}

	// Method that changes the dice mode of the game
	public void setDiceMode(DiceModeEnum diceMode) {
		this.diceMode = diceMode;
	}

}
