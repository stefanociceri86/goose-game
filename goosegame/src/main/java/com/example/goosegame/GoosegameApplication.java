package com.example.goosegame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.goosegame.controller.GameController;

@SpringBootApplication
public class GoosegameApplication implements CommandLineRunner {
	
	@Autowired
	GameController gameController;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GoosegameApplication.class);
        app.run(args);
	}
	
	@Override
    public void run(String... args) throws Exception {
        gameController.startGame();
    }

}

