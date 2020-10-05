package com.guip.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.guip.world.Camera;

public class Menu {

	public static BufferedImage LOGO = Game.spritesheet.getSprite(32, 32, 80, 80);
		
	public String[] options = {"novo jogo", "carregar jogo", "sair", "resumir"};
	
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	
	public boolean up, down, enter;
	public boolean pause = false;
	
	public void tick() {
		if(up) {
			up = false;
			currentOption--;
			if(currentOption < 0) 
				currentOption = maxOption;
		}
		if(down){
			down = false;
			currentOption++;
			if(currentOption > maxOption) 
				currentOption = 0;
		}
		
		if(enter) {
			enter = false;
			if(options[currentOption] == "novo jogo" || options[currentOption] == "resumir") {
				Game.gameState = "NORMAL";
				pause = false;
				}else if(options[currentOption] == "sair") {
					System.exit(1);
				}
		}
	}
	
	
	public void render(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		//g2.setColor(new Color(0,0,0,100));
		g2.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
		g.setColor(Color.red);
		g.setFont(new Font("arial",Font.BOLD,36));
		
		g.drawString("Saca Pina",(Game.WIDTH*Game.SCALE)/2 - 110, (Game.HEIGHT*Game.SCALE)/2-150);
		
		//opções menu
		g.setColor(Color.black);
		g.setFont(new Font("arial",Font.BOLD,24));
		if(pause == false)
			g.drawString("Novo jogo",(Game.WIDTH*Game.SCALE)/2 - 300, (Game.HEIGHT*Game.SCALE)/2-60);
		else
			g.drawString("Resumir",(Game.WIDTH*Game.SCALE)/2 - 300, (Game.HEIGHT*Game.SCALE)/2-60);
		g.drawString("Carregar jogo",(Game.WIDTH*Game.SCALE)/2 - 300, (Game.HEIGHT*Game.SCALE)/2);
		g.drawString("Sair",(Game.WIDTH*Game.SCALE)/2 - 300, (Game.HEIGHT*Game.SCALE)/2+ 60);
		
		
		if(options[currentOption] == "novo jogo") {
			g.drawString(">",(Game.WIDTH*Game.SCALE)/2 - 320, (Game.HEIGHT*Game.SCALE)/2-60);
		}else if (options[currentOption] == "carregar jogo") {
			g.drawString(">",(Game.WIDTH*Game.SCALE)/2 - 320, (Game.HEIGHT*Game.SCALE)/2);
		}else if (options[currentOption] == "sair") {
			g.drawString(">",(Game.WIDTH*Game.SCALE)/2 - 320, (Game.HEIGHT*Game.SCALE)/2+60);
		}
		
		g.drawImage(LOGO, (Game.WIDTH*Game.SCALE)-90, (Game.HEIGHT*Game.SCALE)-90,null);
		
		
	}
}
