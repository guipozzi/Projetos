package com.guip.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.guip.entities.BulletShoot;
import com.guip.entities.Enemy;
import com.guip.entities.Entity;
import com.guip.entities.Player;
import com.guip.graficos.Spritesheet;
import com.guip.graficos.UI;
import com.guip.world.World;

public class Game extends Canvas implements Runnable,KeyListener,MouseListener{

	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	//320
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	
	private int CUR_LEVEL = 1,MAX_LEVEL = 2;
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static Spritesheet spritesheet;
	public static List<BulletShoot> bullets;
	
	public static World world;
	
	public static Player player;
	
	public static Random rand;
	
	public UI ui;
	
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	public Menu menu;
	
	
	

	public Game() {
		Sound.musicBackground.play();
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		//inicializando objetos;
		ui = new UI();
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		
		
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
		
		menu = new Menu();
		
	}
	
	public void initFrame() {
		frame = new JFrame("game #1");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String args[]) {
		Game game = new Game();
		game.start();
		
	}
	
	public void tick() {
		if(gameState == "NORMAL") {
			this.restartGame = false;
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			
			e.tick();
		}
		
		for(int i = 0; i < bullets.size(); i++) {
		bullets.get(i).tick();
		}
		
		if(enemies.size() == 0) {
			//System.out.println("proxima fase");
			CUR_LEVEL++;
			if(CUR_LEVEL > MAX_LEVEL) {
				CUR_LEVEL = 1;
			}
			String newWorld = "level"+CUR_LEVEL+".png";
			World.restartGame(newWorld);
		}
		}else if(gameState == "GAME_OVER") {
			this.framesGameOver++;
			if(this.framesGameOver == 30) {
				this.framesGameOver = 0;
				if(this.showMessageGameOver)
					this.showMessageGameOver = false;
				else
					this.showMessageGameOver = true;
			}
			
			if(restartGame) {
				this.restartGame = false;
				this.gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}
		}else if(gameState == "MENU") {
			
			menu.tick();
		}
		
		if(player.run == true) {
			player.speed ++;
			if(player.speed >= player.maxSpd) {
				player.speed = player.maxSpd;
			}
		}else {
			player.speed = player.minSpd;
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0,WIDTH,HEIGHT);
		world.render(g);
		//Graphics2D g2 = (Graphics2D) g;
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
			}
		ui.render(g);
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE,HEIGHT*SCALE,null);
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.white);
		g.drawString("Munição total: " + player.ammo, 540, 30);
		if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setFont(new Font("arial",Font.BOLD,80));
			g.setColor(Color.white);
			g.drawString("GAME OVER!",(WIDTH*SCALE)/2 - 240, (HEIGHT*SCALE)/2 + 15);
			g.setFont(new Font("arial",Font.BOLD,20));
			
			if(showMessageGameOver) 
				g.drawString(">Pressione Enter para Reiniciar",(WIDTH*SCALE)/2 - 150, (HEIGHT*SCALE)/2 + 50);
		}else if (gameState == "MENU") {
			menu.render(g);
		}
		
		
		bs.show();
	}
	
	public void run() {
			long lastTime = System.nanoTime();
			double amountOfTicks = 60.0;
			double ns = 1000000000 / amountOfTicks;
			double delta = 0;
			int frames = 0;
			double timer = System.currentTimeMillis();
			requestFocus();
			while(isRunning) {
				long now = System.nanoTime();
				delta+= (now - lastTime) / ns;
				lastTime = now;
				if(delta >= 1) {
					tick();
					render();
					frames++;
					delta--;
				}
				
				if(System.currentTimeMillis() - timer >= 1000) {
					System.out.println("fps: "+ frames);
					frames = 0;
					timer+=1000;
				}
			}
		stop();
	}

	@Override
	public void keyPressed(KeyEvent e) {
	
		
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || 
				e.getKeyCode()== KeyEvent.VK_D) {
			player.right = true;
			//System.out.println("direita");
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || 
				e.getKeyCode()== KeyEvent.VK_A) {
			player.left = true;
			//System.out.println("esquerda");
		}
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
			player.run = true;
			
		}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || 
				e.getKeyCode()== KeyEvent.VK_W) {
			player.up = true;
			
			if(gameState == "MENU") {
				menu.up = true;
			}
			
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || 
				e.getKeyCode()== KeyEvent.VK_S) {
			player.down = true;
			
			if(gameState == "MENU") {
				menu.down = true;
			}
		}
		
		if(e.getKeyCode()== KeyEvent.VK_O) {
			player.shoot = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			restartGame = true;
			if(gameState == "MENU") {
				menu.enter = true;
			}				
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			menu.pause = true;
		}
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_Z) {
			player.jump = true;
		}
		
			
		if(e.getKeyCode() == KeyEvent.VK_RIGHT || 
				e.getKeyCode()== KeyEvent.VK_D) {
			player.right = false;
			//System.out.println("direita");
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT || 
				e.getKeyCode()== KeyEvent.VK_A) {
			player.left = false;
			//System.out.println("esquerda");
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
			player.run = false;
			}
		
		if(e.getKeyCode() == KeyEvent.VK_UP || 
				e.getKeyCode()== KeyEvent.VK_W) {
			player.up = false;
			//System.out.println("cima");
		}else if(e.getKeyCode() == KeyEvent.VK_DOWN || 
				e.getKeyCode()== KeyEvent.VK_S) {
			player.down = false;
			//System.out.println("baixo");
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX()/3);
		player.my = (e.getY()/3);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
