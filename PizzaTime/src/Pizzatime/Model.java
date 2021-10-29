package Pizzatime;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;


public class Model extends JPanel implements ActionListener {
	
	private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

	private Image ii;
	private Dimension d;
	private boolean inGame = false;
	private boolean dying = false;
	
	private boolean titleScreen = false;
	
	private final int BLOCK_SIZE = 24; //describes how big the blocks are in the game.
	private final int N_BLOCKS = 15; //15*15 grid, so 225 places where car can be.
	private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; //MAX SIZE OF SCREEN
	private final int MAX_ENEMIES = 3; //max amount of enemy allowed in the game.
	private final int CAR_SPEED = 6;
	
	private int N_ENEMIES = 1; //AMOUNT OF STARTING ENEMIES
	private int lives, score; //counter for life and score.
	private int[] dx,dy;
	private int[] enemy_x,enemy_y,enemydx,enemydy, enemySpeed;
	
	private int hero_dx, hero_dy,view_dx,view_dy;
	private int car_x,car_y,cardx,cardy;
	
	private final int validSpeed[] = {1,2,3};//allowable speed in the game
	private final int maxSpeed = 3;
	private int currentSpeed = 3;
	
	private short[] screenData;
	private Timer timer;
	
	private Color mazeColor;
	
	
	private Image life,enemy, died,intro;
	private Image car,downcar,leftcar,rightcar;
	
	public final short level1[] =
			{
					
		19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        21, 0, 0, 0, 17, 0, 0, 0, 0, 0, 0, 16, 0, 0, 20,
        21, 0, 0, 0, 17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
        21, 0, 0, 0, 17, 0, 0, 24, 0, 0, 0, 0, 0, 0, 20,
        17, 18, 18, 18, 0, 0, 20, 0, 17, 0, 0, 0, 0, 0, 20,
        17, 0, 0, 0, 0, 0, 20, 0, 17, 0, 0, 0, 0, 24, 20,
        25, 0, 0, 0, 24, 24, 28, 0, 25, 24, 24, 0, 20, 0, 21,
        1, 17, 0, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
        1, 17, 0, 0, 18, 18, 22, 0, 19, 18, 18, 0, 20, 0, 21,
        1, 17, 0, 0, 0, 0, 20, 0, 17, 0, 0, 0, 20, 0, 21,
        1, 17, 0, 0, 0, 0, 20, 0, 17, 0, 0, 0, 20, 0, 21,
        1, 17, 0, 0, 0, 0, 0, 18, 0, 0, 0, 0, 20, 0, 21,
        1, 17, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 21,
        1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 18, 20,
        9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
        
        
			};
	
	
	public Model()
	{
		addKeyListener(new TAdapter());
	
		this.loadImage(); //imageloader
		this.initBoard();
		
		
		setFocusable(true);
		this.startGame();
	}
	


	public void loadImage() {
		

		intro = new ImageIcon("src/images/introscreen.jpg").getImage();
        enemy = new ImageIcon("src/images/enemy.png").getImage();
        car = new ImageIcon("src/images/car.png").getImage();
        downcar = new ImageIcon("src/images/downcar.png").getImage();
        leftcar = new ImageIcon("src/images/leftcar.png").getImage();
        rightcar = new ImageIcon("src/images/rightcar.png").getImage();
 
    
	}
	
	 private void initBoard() {

	        screenData = new short[N_BLOCKS * N_BLOCKS];
	        d = new Dimension(400,400);
	        screenData = new short[N_BLOCKS * N_BLOCKS];
	        mazeColor = new Color(5, 100, 5);
	        d = new Dimension(400, 400);
	        enemy_x = new int[MAX_ENEMIES];
	        enemydx = new int[MAX_ENEMIES];
	        enemy_y = new int[MAX_ENEMIES];
	        enemydy = new int[MAX_ENEMIES];
	        enemySpeed = new int[MAX_ENEMIES];
	        dx = new int[4];
	        dy = new int[4];

	        timer = new Timer(40, this); //game is redrawn every n milliseconds every frame. n is bigger = game is slower.
	        timer.start(); //start timer.
	    }
	 
	 class TAdapter extends KeyAdapter{
		 public void keyPressed(KeyEvent e) {
			 int key = e.getKeyCode(); //we are taking  adapter for up/down/right/left
		   if (inGame) {
               if (key == KeyEvent.VK_LEFT) {
                   hero_dx = -1;
                   hero_dy = 0;
               } else if (key == KeyEvent.VK_RIGHT) {
                   hero_dx = 1;
                   hero_dy = 0;
               } else if (key == KeyEvent.VK_UP) {
                   hero_dx = 0;
                   hero_dy = -1;
               } else if (key == KeyEvent.VK_DOWN) {
                   hero_dx = 0;
                   hero_dy = 1;
               } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                   inGame = false;
               } else if (key == KeyEvent.VK_PAUSE) {
                   if (timer.isRunning()) {
                       timer.stop();
                   } else {
                       timer.start();
                   }
               }
           } else {
               if (key == 's' || key == 'S') {
                   inGame = true;
                   startGame(); //start the game if button is started.
               }
           }
       }
	 }
	 
	/* game start method*/	
		private void startGame() {
			//values are initialized;
			lives = 3;
			score = 0;
			initLevel(); //initialize level
			N_ENEMIES = 1;
			currentSpeed = 1;
			
		}
		
		private void initLevel() {
			int i;
			for(i=0;i<(N_BLOCKS*N_BLOCKS);i++)
			{
				screenData[i] = level1[i];
			}
		}
		
		 private void continueLevel() {

		        short i;
		        int dx = 1;
		        int random;

		        for (i = 0; i < N_ENEMIES; i++) {

		            enemy_y[i] = 4 * BLOCK_SIZE;
		            enemy_x[i] = 4 * BLOCK_SIZE;
		            enemydy[i] = 0;
		            enemydx[i] = dx;
		            dx = -dx;
		            random = (int) (Math.random() * (currentSpeed + 1));

		            if (random > currentSpeed) {
		                random = currentSpeed;
		            }

		            enemySpeed[i] = validSpeed[random];
		        }

		        car_x = 7 * BLOCK_SIZE;
		        car_y = 11 * BLOCK_SIZE;
		        hero_dx = 0;
		        hero_dy = 0;
		        cardx = 0;
		        cardy = 0;
		        view_dx = -1;
		        view_dy = 0;
		        dying = false;
		    }



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		repaint();
	}
	
	   @Override
	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);

	        doDrawing(g);
	    }
	   
	   private void doDrawing(Graphics g) {

	        Graphics2D g2d = (Graphics2D) g;

	        

	        drawMaze(g2d);
	        drawScore(g2d);
	        //doAnim();

	        if (inGame) {
	            playGame(g2d);
	        } else {
	            showIntroScreen(g);
	        }

	        g2d.drawImage(ii, 5, 5, this);
	        Toolkit.getDefaultToolkit().sync();
	        g2d.dispose();
	    }
	   
	   private void playGame(Graphics2D g2d) {

	        if (dying) {

	            death();

	        } else {

	            moveCar();
	            drawCar(g2d);
	            moveEnemies(g2d);
	            checkMap();
	        }
	        
	        
	    }
	   /*
	    * map 
	    * -1 = left
	    * 1 = right
	    * -2 = up
	    * 2 = down
	    * 
	    */
	   private void drawCar(Graphics2D g2d) {

	        if (view_dx == -1) {
	        	drawCarOrentation(-1,g2d);
	        } else if (view_dx == 1) {
	        	drawCarOrentation(1,g2d);
	        } else if (view_dy == -1) {
	        	drawCarOrentation(-2,g2d);
	        } else {
	        	drawCarOrentation(2,g2d);
	        }
	    }
	   
	   private void drawCarOrentation(int orentation,Graphics2D g2d)
	   {
		   switch (orentation) {
           case -1:
               g2d.drawImage(leftcar, car_x + 1, car_y + 1, this);
               break;
           case 1:
               g2d.drawImage(rightcar, car_x + 1, car_y + 1, this);
               break;
           case -2:
               g2d.drawImage(car, car_x + 1, car_y + 1, this);
               break;
           case 2:
               g2d.drawImage(downcar, car_x + 1, car_y + 1, this);
               break;
       }
	   }
	   
	   private void checkMap() {

	        short i = 0;
	        boolean finished = true;

	        while (i < N_BLOCKS * N_BLOCKS && finished) {

	            if ((screenData[i] & 48) != 0) {
	                finished = false;
	            }

	            i++;
	        }
	        
	        if (finished) {

	            score += 50;

	            if (N_ENEMIES < MAX_ENEMIES) {
	                N_ENEMIES++;
	            }

	            if (currentSpeed < maxSpeed) {
	                currentSpeed++;
	            }

	            initLevel();
	        }
	   }
	   
	   private void death() {

	        lives--;

	        if (lives == 0) {
	            inGame = false;
	        }

	        continueLevel();
	    }
	   
	   private void showIntroScreen(Graphics g) {

		   g.setColor(Color.BLACK);
	        g.fillRect(0,0,600,600);
	        g.drawImage(intro,0,0,Color.BLACK,null);

	    }

	   
	   private void drawMaze(Graphics2D g2d) {

	        short i = 0;
	        int x, y;

	        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
	            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

	                g2d.setColor(mazeColor);
	                g2d.setStroke(new BasicStroke(2));

	                if ((screenData[i] & 1) != 0) {
	                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
	                }

	                if ((screenData[i] & 2) != 0) {
	                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
	                }

	                if ((screenData[i] & 4) != 0) {
	                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
	                            y + BLOCK_SIZE - 1);
	                }

	                if ((screenData[i] & 8) != 0) {
	                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
	                            y + BLOCK_SIZE - 1);
	                }

	                if ((screenData[i] & 16) != 0) {
	                    g2d.setColor(Color.GREEN);
	                    g2d.fillRect(x + 11, y + 11, 2, 2);
	                }

	                i++;
	            }
	        }
	    }
	   
	   private void drawScore(Graphics2D g) {

	        int i;
	        String s;

	        g.setFont(smallFont);
	        g.setColor(new Color(96, 128, 255));
	        s = "Score: " + score;
	        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

	        for (i = 0; i < 3; i++) {
	            g.drawImage(died, i * 28 + 8, SCREEN_SIZE + 1, this);
	        }
	    }
	
	   private void moveEnemies(Graphics2D g2d) {

	        short i;
	        int pos;
	        int count;

	        for (i = 0; i < N_ENEMIES; i++) {
	            if (enemy_x[i] % BLOCK_SIZE == 0 && enemy_y[i] % BLOCK_SIZE == 0) {
	                pos = enemy_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (enemy_y[i] / BLOCK_SIZE);

	                count = 0;

	                if ((screenData[pos] & 1) == 0 && enemydx[i] != 1) {
	                    dx[count] = -1;
	                    dy[count] = 0;
	                    count++;
	                }

	                if ((screenData[pos] & 2) == 0 && enemydy[i] != 1) {
	                    dx[count] = 0;
	                    dy[count] = -1;
	                    count++;
	                }

	                if ((screenData[pos] & 4) == 0 && enemydx[i] != -1) {
	                    dx[count] = 1;
	                    dy[count] = 0;
	                    count++;
	                }

	                if ((screenData[pos] & 8) == 0 && enemydy[i] != -1) {
	                    dx[count] = 0;
	                    dy[count] = 1;
	                    count++;
	                }

	                if (count == 0) {

	                    if ((screenData[pos] & 15) == 15) {
	                        enemydx[i] = 0;
	                        enemydy[i] = 0;
	                    } else {
	                        enemydx[i] = -enemydx[i];
	                        enemydy[i] = -enemydy[i];
	                    }

	                } else {

	                    count = (int) (Math.random() * count);

	                    if (count > 3) {
	                        count = 3;
	                    }

	                    enemydx[i] = dx[count];
	                    enemydy[i] = dy[count];
	                }

	            }

	            enemy_x[i] = enemy_x[i] + (enemydx[i] * enemySpeed[i]);
	            enemy_y[i] = enemy_y[i] + (enemydy[i] * enemySpeed[i]);
	            drawenemy(g2d, enemy_x[i] + 1, enemy_y[i] + 1);

	            if (car_x > (enemy_x[i] - 12) && car_x < (enemy_x[i] + 12)
	                    && car_y > (enemy_y[i] - 12) && car_y < (enemy_y[i] + 12)
	                    && inGame) {

	                dying = true;
	            }
	        }
	    }

	    private void drawenemy(Graphics2D g2d, int x, int y) {

	        g2d.drawImage(enemy, x, y, this);
	    }
	    
	    private void moveCar() {

	        int pos;
	        short ch;

	        if (hero_dx == -cardx && hero_dy == -cardy) {
	            cardx = hero_dx;
	            cardy = hero_dy;
	            view_dx = cardx;
	            view_dy = cardy;
	        }

	        if (car_x % BLOCK_SIZE == 0 && car_y % BLOCK_SIZE == 0) {
	            pos = car_x / BLOCK_SIZE + N_BLOCKS * (int) (car_y / BLOCK_SIZE);
	            ch = screenData[pos];

	            if ((ch & 16) != 0) {
	                screenData[pos] = (short) (ch & 15);
	                score++;
	            }

	            if (hero_dx != 0 || hero_dy != 0) {
	                if (!((hero_dx == -1 && hero_dy == 0 && (ch & 1) != 0)
	                        || (hero_dx == 1 && hero_dy == 0 && (ch & 4) != 0)
	                        || (hero_dx == 0 && hero_dy == -1 && (ch & 2) != 0)
	                        || (hero_dx == 0 && hero_dy == 1 && (ch & 8) != 0))) {
	                    cardx = hero_dx;
	                    cardy = hero_dy;
	                    view_dx = cardx;
	                    view_dy = cardy;
	                }
	            }

	            // Check for standstill
	            if ((cardx == -1 && cardy == 0 && (ch & 1) != 0)
	                    || (cardx == 1 && cardy == 0 && (ch & 4) != 0)
	                    || (cardx == 0 && cardy == -1 && (ch & 2) != 0)
	                    || (cardx == 0 && cardy == 1 && (ch & 8) != 0)) {
	                cardx = 0;
	                cardy = 0;
	            }
	        }
	        car_x = car_x + CAR_SPEED * cardx;
	        car_y = car_y + CAR_SPEED * cardy;
	    }

	


}
