package com.example.goosegame.business;

import java.util.Map;

import com.example.goosegame.model.GameStateEnum;

// Interface of business class
public interface IBoardBusiness {
	public GameStateEnum checkStateOfTheGame();
	public String addPlayer(String playerName) throws Exception;
	public String startGame(String diceMode) throws Exception;
	public String movePlayer(String moveCommand) throws Exception;
	public String restartGame();
	public String quitGame();
	public Map<String,Integer> retrievePlayers();
}
