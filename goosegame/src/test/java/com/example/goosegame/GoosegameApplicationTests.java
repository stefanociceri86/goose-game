package com.example.goosegame;

import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import com.example.goosegame.business.BoardBusiness;
import com.example.goosegame.model.GameStateEnum;
import com.example.goosegame.utils.CommandsUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplicationConfiguration.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GoosegameApplicationTests {
	
	@Autowired
	BoardBusiness boardBusiness;
	
	@Test
	public void addPlayerTest() throws Exception{
		
		//Check if new player is correctly added to the game
		String addPlayerCommand = "add player Ste";
		String[] cmdPlayersList = CommandsUtils.parseCommand(addPlayerCommand);
		String newPlayerName = cmdPlayersList[2];
		boardBusiness.addPlayer(addPlayerCommand);
		Map<String, Integer> actualPlayers = boardBusiness.retrievePlayers();
		Assert.isTrue(actualPlayers.containsKey(newPlayerName), "PlayerNotAddedToTheGame!");
		
		//Check if same player is correctly not added to the game
		try{
			boardBusiness.addPlayer(addPlayerCommand);
		}catch(Exception e){
			
		}
		actualPlayers = boardBusiness.retrievePlayers();
		Assert.state(actualPlayers.size() == 1, "SamePlayerAddedToTheGame");
		
		//Check if a second player is correctly added to the game
		String addPlayer2Command = "add player Tom";
		String[] cmd2PlayersList = CommandsUtils.parseCommand(addPlayerCommand);
		String newPlayer2Name = cmd2PlayersList[2];
		boardBusiness.addPlayer(addPlayer2Command);
		actualPlayers = boardBusiness.retrievePlayers();
		Assert.isTrue(actualPlayers.containsKey(newPlayer2Name), "PlayerNotAddedToTheGame!");
		
	}
	
	@Test
	public void gameLogicTest() throws Exception{
		
		//Start the game with Manual dice mode
		boardBusiness.startGame("M");
		
		//Check standard movement (Ste moves from Start to 3)
		String command = "move Ste 1, 2";
		boardBusiness.movePlayer(command);
		Map<String, Integer> actualPlayers = boardBusiness.retrievePlayers();
		Assert.isTrue(actualPlayers.get("Ste").equals(3), "Wrong player movement");
		
		//Check Prank case (Tom moves from Start to 3, Ste returns to Start)
		command = "move Tom 1, 2";
		boardBusiness.movePlayer(command);
		Assert.isTrue(actualPlayers.get("Tom").equals(3), "Wrong prank movement");
		Assert.isTrue(actualPlayers.get("Ste").equals(0), "Wrong prank movement");
		
		//Check Bridge case (Ste moves from Start to 6, for the Bridge moves to 12)
		command = "move Ste 3, 3";
		boardBusiness.movePlayer(command);
		Assert.isTrue(actualPlayers.get("Ste").equals(12), "Wrong bridge movement");
		
		//Check Goose case (Tom moves from 3 to 5, for the Goose moves to 7)
		command = "move Tom 1, 1";
		boardBusiness.movePlayer(command);
		Assert.isTrue(actualPlayers.get("Tom").equals(7), "Wrong goose movement");
		
		command = "move Ste 6, 6";
		boardBusiness.movePlayer(command);
		command = "move Ste 6, 6";
		boardBusiness.movePlayer(command);
		command = "move Ste 6, 6";
		boardBusiness.movePlayer(command);
		command = "move Ste 6, 6";
		boardBusiness.movePlayer(command);
		
		System.out.println(actualPlayers.get("Ste"));
		
		//Ste : position 60
		//Check if when position 63 is exceeded, player returns back
		command = "move Ste 2, 3";
		boardBusiness.movePlayer(command);
		Assert.isTrue(actualPlayers.get("Ste").equals(61), "Wrong return back movement");
		
		//Check winner
		command = "move Ste 1, 1";
		boardBusiness.movePlayer(command);
		Assert.isTrue(actualPlayers.get("Ste").equals(63), "Wrong movement");
		Assert.isTrue(actualPlayers.get("Ste").equals(63), "Wrong return back movement");
		Assert.isTrue(boardBusiness.checkStateOfTheGame().equals(GameStateEnum.GAME_ENDED), "Winner Error");
		
	}

}

