/*
 * Author: Cameron Dykstra
 * Email: kramin42@gmail.com
 * 
 * This is a game created for the java4k competition called CodeGolf4k 
 * which is about creating a program that is as small as possible to
 * get the "golf ball" to the hole.
 * 
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

import java.applet.Applet;
import java.awt.BasicStroke;
//import java.awt.Event;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Stack;

public class g extends Applet implements Runnable {

	public void start() {
		new Thread(this).start();
	}

	int mx, my;
	int mDownBox;//positive for toploop, negative for bottom (offset by + or - 1)
	boolean dragging =false;
	boolean mousedown = false;
	
	int x,y,temp;
	
	int numOfCells = 20;
	int cellWidth = 20;
	
	int prgBoxSize = 30;
	int prgBoxSpacing = 40;
	int prgBoxSideClickWidth = 10;
	int numOfPrgBoxes = 25;
	int prgBoxArrayWidth = 5;
	
	int playBtnSpacing = 20;
	int playBtnSize = 40;
	int playMode = 0;//0: paused, 1: single step, 2: play, 3: fast forward
	
	boolean editMode = false;
	
	int selClr = 0;
	int selOp = 0;
	
	//int level[] = new int[numOfCells*numOfCells];
	//int level[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,3,3,1,0,0,0,0,3,3,3,1,0,1,3,3,1,0,0,0,0,0,3,0,0,0,0,0,0,0,3,0,0,0,0,3,0,0,0,1,3,3,2,0,1,3,3,1,0,0,2,3,3,3,3,2,0,0,0,0,0,3,0,0,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,2,3,3,3,1,2,3,3,3,3,3,2,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,3,3,3,3,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,};
	//int level[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,1,3,2,3,3,3,3,3,3,3,3,3,3,2,3,3,1,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,1,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,3,3,2,1,0,0,3,0,0,0,0,0,0,0,0,3,0,1,0,0,1,3,2,3,3,3,2,3,3,3,1,0,0,0,0,3,0,3,0,0,0,0,3,0,0,0,3,0,0,0,0,0,0,1,3,2,3,2,3,3,1,0,1,0,0,0,3,0,0,0,0,0,0,0,0,3,0,3,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,3,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,1,0,1,3,3,3,2,1,0,0,0,0,0,0,0,0,0,3,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,1,3,2,3,3,2,3,3,3,3,3,2,3,3,3,3,1,0,0,0,0,0,3,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,};
	int levels[][] = {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,3,3,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,3,3,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,4,3,2,3,3,3,3,3,3,3,3,3,3,2,3,3,4,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,4,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,4,3,3,2,4,0,0,3,0,0,0,0,0,0,0,0,3,0,4,0,0,4,3,2,3,3,3,2,3,3,3,4,0,0,0,0,3,0,3,0,0,0,0,3,0,0,0,3,0,0,0,0,0,0,4,3,2,3,2,3,3,4,0,4,0,0,0,3,0,0,0,0,0,0,0,0,3,0,3,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,3,0,4,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,4,0,4,3,3,3,2,4,0,0,0,0,0,0,0,0,0,3,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,4,3,2,3,3,2,3,3,3,3,3,2,3,3,3,3,4,0,0,0,0,0,3,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,4,0,0,4,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,},
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,2,3,3,3,3,3,3,3,3,3,2,3,3,3,2,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,3,0,0,0,3,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,3,0,0,0,3,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,3,0,0,0,3,0,0,0,0,2,3,3,3,3,3,3,2,3,3,3,2,0,0,0,3,0,0,0,0,3,0,0,0,0,0,0,3,0,0,0,3,0,0,0,3,0,0,0,0,3,0,0,0,4,0,0,3,0,4,3,2,3,4,0,4,0,0,0,0,3,0,0,0,3,0,0,3,0,0,0,0,0,0,0,0,0,0,0,4,2,3,3,3,2,0,3,2,0,4,3,3,2,3,3,3,4,0,0,0,0,0,0,0,4,0,0,3,0,0,0,0,3,0,0,0,0,0,0,0,0,4,0,0,0,0,0,3,0,0,0,0,3,0,0,0,0,0,0,0,0,2,3,3,3,3,3,2,3,3,3,3,2,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,4,3,2,3,3,3,3,4,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}
	};
	int numsOfButtons[] = {0,0,24,16};
	int pars[] = {2,3,3,10};
	int scores[] = {0,0,0,0};
	int starts[] = {253,289,63,208};
	int ends[] = {189,109,63,208};
	int numOfLevels = 4;
	int currentLevel = 0;
	int pressedButtons = 0;
	boolean endOpen = false;
	
	//int startCell;
	//int endCell;
	int startDirs[][]={{0,-1},{0,-1},{0,-1},{1,0}};
	double pos[]={0,0}, vel[]={0,0};
	int dir[]={0,-1};
	
	int ballSpeed = 1;//5 for fastforward, 1 for normal
	int fastFwrdMult = 5;
	boolean moving = false;
	
	boolean step = false;
//	boolean play = false;
//	boolean fastfwrd = false;
	
	boolean reachedEnd = false;
	boolean outOfBounds = false;
	boolean reset = false;
	boolean createNewGame;
	
	// 0: nop, 1: return, 2: forward, 3: left, 4: right
	int programs[][]  = new int[numOfLevels][numOfPrgBoxes];
	// 0: blank, 1: red, 2: green, 3: blue
	int prgrmclrs[][]= new int[numOfLevels][numOfPrgBoxes];
	Stack<Integer> progStack = new Stack<Integer>();
	
	// index of the block to connect to, "-" sign if connected to bottom, "+" if connected to top, 0 if disconnected
	// requires an offset of 1 to be added or subtracted, depending on sign, to get index of block
	int toploops[][] = new int[numOfLevels][numOfPrgBoxes];
	int botloops[][] = new int[numOfLevels][numOfPrgBoxes];
	int tplpclrs[][] = new int[numOfLevels][numOfPrgBoxes];
	int btlpclrs[][] = new int[numOfLevels][numOfPrgBoxes];
	int bezierOffset = 40;
	int progPos;
	boolean execute;

	public void run() {
		int w = 800, h = 600;
		setSize(w, h); // For AppletViewer, remove later.

		// Set up the graphics stuff, double-buffering.
		BufferedImage screen = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) screen.getGraphics();
		Graphics2D appletGraphics = (Graphics2D) getGraphics();
		
		AffineTransform identity = new AffineTransform();
		AffineTransform tempTrans = new AffineTransform();
		
		BasicStroke thinStroke = new BasicStroke(1);
		BasicStroke medStroke = new BasicStroke(2);
		
		int xpoints[] = {-5,0,5};
		int ypoints[] = {0,-10,0};
		Polygon triangle = new Polygon(xpoints, ypoints, 3);
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		BufferedImage cursor = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) cursor.getGraphics();
		g.setColor(Color.cyan);
		g.drawLine(0, 6, 12, 6);
		g.drawLine(6, 0, 6, 12);
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursor , new Point(6,6), ""));
		
		enableEvents(MouseEvent.MOUSE_EVENT_MASK | MouseEvent.MOUSE_MOTION_EVENT_MASK | KeyEvent.KEY_EVENT_MASK);
		
		Random rand = new Random();

		//ball vars
		double dest[] = {0,0};
		int currentCell = 0;
		
		// other vars
		
		FontMetrics fm;
		Rectangle2D rect;
		
		
		// constants
		int bRad = 6;

		// fonts
		Font normalFont = new Font("SansSerif", Font.PLAIN, 16);
		//Font largeFont = new Font("SansSerif", Font.PLAIN, 60);
		//Font mediumFont = new Font("SansSerif", Font.PLAIN, 30);
		//Font hugeFont = new Font("SansSerif", Font.PLAIN, 120);
		
		g2d.setFont(normalFont);
		
		//Colours
		Color clrBG = Color.black;
		Color clrLines = Color.darkGray;
		Color clrDimRed = new Color(0x80FF0000,true);
		Color clrDimGreen = new Color(0x8000FF00,true);
		Color clrDimBlue = new Color(0x800000FF,true);
		Color clrSel = Color.yellow;
		Color clrText = Color.white;
		Color clrBall = Color.white;
		
		Color clrLevel[] = {Color.gray, new Color(96,0,0), new Color(0,96,0), new Color(0,0,128)};

		// Some variables to use for the fps.
		int tick = 0, fps = 0, acc = 0;
		long lastTime = System.nanoTime();
		
		int frameNum = 0;
		int stepDelay = 0;
		
		//build level p=path h=hidden
		// 0: blank, 1: pred, 2: pgreen, 3: pblue, 4: hpred, 5: hpgreen, 6: hpblue
		//level[9*numOfCells+9]=2;
		
//		for(int i = 1; i<numOfCells-1; i++){
//			for (int j = 1; j<numOfCells-1; j++){
//				if (i==0 && j==0) continue;
//				level[j*numOfCells+i]=1+((i+j)%3);
//			}
//		}
//		startCell = 14*numOfCells+10;
//		endCell   = 0*numOfCells+0;
		
		createNewGame = true;
		
		

		// Game loop.
		while (true) {
			long now = System.nanoTime();
			acc += now - lastTime;
			tick++;
			if (acc >= 1000000000L) {
				acc -= 1000000000L;
				fps = tick;
				tick = 0;
			}
			//
			//game update
			//
			
			if (createNewGame || reset){ //reset game
				if (reachedEnd){
					//if (++currentLevel>=numOfLevels) currentLevel=0;
					scores[currentLevel]=0;
					for (int i=0;i<numOfPrgBoxes;i++){
						if (programs[currentLevel][i]!=0 || toploops[currentLevel][i]!=0 || botloops[currentLevel][i]!=0) scores[currentLevel]++;
					}
				}
				
				//startDir[0]=0; startDir[1]=-1; //0: up, 1: right, 2: down, 3: left
				vel[0]=0;
				vel[1]=0;
				moving=false;
				step=false;
				pos[0] = (starts[currentLevel]%numOfCells)*cellWidth + cellWidth/2;
				pos[1] = (starts[currentLevel]/numOfCells)*cellWidth + cellWidth/2;
				dir[0] = startDirs[currentLevel][0]; dir[1] = startDirs[currentLevel][1];
				currentCell = starts[currentLevel];
				progPos = 0;
				progStack.clear();
				reachedEnd = false;
				outOfBounds = false;
				reset = false;
				createNewGame=false;
				execute=false;
				playMode=0;
				pressedButtons = 0;
				endOpen = false;
				for (int i=0; i<numOfCells*numOfCells;i++){
					if (levels[currentLevel][i]>6) levels[currentLevel][i]-=3;
				}
			}
			
			//update whether the end is open
			endOpen = (pressedButtons == numsOfButtons[currentLevel]);
			
			frameNum++;
			//if paused reset frameNum
			switch (playMode) {
			case 0:
				frameNum = 0;
				break;
			case 1:
				stepDelay = 4;
				break;
			case 2:
				stepDelay = 30;
				break;
			case 3:
				stepDelay = 2;
				break;
			}
			
			//update the ball
			if (step){
				moving=true;
				vel[0] = ballSpeed*dir[0];
				vel[1] = ballSpeed*dir[1];
				if (playMode==3){//0: paused, 1: single step, 2: play, 3: fast forward
					vel[0] = ballSpeed*fastFwrdMult*dir[0];
					vel[1] = ballSpeed*fastFwrdMult*dir[1];
				}
				dest[0] = pos[0]+dir[0]*cellWidth;
				dest[1] = pos[1]+dir[1]*cellWidth;
				currentCell+=dir[0]+numOfCells*dir[1];
				step = false;
			}
			if (moving){
				pos[0]+=vel[0];
				pos[1]+=vel[1];
				// find if it has reached the next cell
				if (pos[0]==dest[0] && pos[1]==dest[1]){// "==" works as long as speed is an integer division of cellWidth
					moving=false;
					vel[0]=0;
					vel[1]=0;
					frameNum=0;
					//pos[0]=dest[0];
					//pos[1]=dest[1];

					
					//check if it has reached the end
					if (endOpen) reachedEnd = currentCell==ends[currentLevel];
					outOfBounds = (levels[currentLevel][currentCell]==0/* || levels[currentLevel][currentCell]>3*/);
					reset = reachedEnd || outOfBounds;
					
					//check if it is on a button
					if (levels[currentLevel][currentCell]>3 && levels[currentLevel][currentCell]<=6){
						levels[currentLevel][currentCell]+=3;
						pressedButtons++;
					}
					
//					if (currentCell==endCell){
//						reachedEnd=true;
//					} else if (level[currentCell]==0 || level[currentCell]>3){
//						outOfBounds=true;
//					}
				}
			}
			
			// go to next instruction
			if (playMode>0 && !moving && frameNum%stepDelay==0) {
				//System.out.println("running: " + program[progPos] + " at " + progPos);
				// 0: blank, 1: red, 2: green, 3: blue
				//System.out.println("program position: "+progPos);
				if (prgrmclrs[currentLevel][progPos] == 0 || prgrmclrs[currentLevel][progPos] == ((levels[currentLevel][currentCell]-1)%3+1)) {
					// 0: nop, 1: return, 2: forward, 3: left, 4: right
					temp = dir[0];
					if (programs[currentLevel][progPos] == 2)
						step = true;
					if (programs[currentLevel][progPos] == 3) {
						dir[0] = dir[1];
						dir[1] = -temp;
					}
					if (programs[currentLevel][progPos] == 4) {
						dir[0] = -dir[1];
						dir[1] = temp;
					}
					if (programs[currentLevel][progPos] == 1){
						if (progStack.empty()){
							reset=true;
						} else {
							progPos=progStack.pop()+1;
							if (progPos>=numOfPrgBoxes) progPos=0;
							//System.out.println("returned to "+progPos);
						}
						temp=-2;
					}
				}
				
				// toploops gives the index of the block to connect to, "-" sign if connected to bottom, "+" if connected to top, 0 if disconnected
				// requires an offset of 1 to be added or subtracted, depending on sign, to get index of block
				if (temp!=-2){//did not just return
					temp=-1;
					//check for coloured branches first
					if (tplpclrs[currentLevel][progPos] == ((levels[currentLevel][currentCell]-1)%3+1) && toploops[currentLevel][progPos]!=0){
						temp=progPos;
						progPos = Math.abs(toploops[currentLevel][progPos])-1;
					} else if (btlpclrs[currentLevel][progPos] == ((levels[currentLevel][currentCell]-1)%3+1) && botloops[currentLevel][progPos]!=0){
						temp=progPos;
						progPos = Math.abs(botloops[currentLevel][progPos])-1;
					} else if (tplpclrs[currentLevel][progPos]==0 && toploops[currentLevel][progPos]!=0){
						temp=progPos;
						progPos = Math.abs(toploops[currentLevel][progPos])-1;
					} else if (btlpclrs[currentLevel][progPos]==0 && botloops[currentLevel][progPos]!=0){
						temp=progPos;
						progPos = Math.abs(botloops[currentLevel][progPos])-1;
					}
					if (temp!=-1) {
						progStack.push(temp);
						//System.out.println("branched to: "+progPos+", added "+progStack.peek()+" to stack.");
					}
					if (temp==-1 && ++progPos >= numOfPrgBoxes) progPos = 0;//increment and loop back to 0
				}
				playMode = playMode==1 ? 0 : playMode;//0: paused, 1: single step, 2: play, 3: fast forward
			}
			

			lastTime = now;
			
			//
			// Render
			//

			// draw the background
			// g2d.drawImage(background, 0, 0, w-1, h-1, this);
			g2d.setColor(clrBG);
			g2d.fillRect(0, 0, w, h);
			
			
			g2d.setColor(clrText);
			g2d.setFont(normalFont);
			g2d.drawString("FPS " + String.valueOf(fps), 10, h-10);
			
			//draw the ball area
			//draw the grid
			g2d.setColor(clrLines);
			for (int i=0;i<=numOfCells;i++){
				g2d.drawLine(0, i*cellWidth, numOfCells*cellWidth, i*cellWidth);
				g2d.drawLine(i*cellWidth, 0, i*cellWidth, numOfCells*cellWidth);
			}
			//draw the coloured squares
			for (int i=0; i<numOfCells*numOfCells;i++){
				if (levels[currentLevel][i]>0){
					g2d.setColor(clrLevel[((levels[currentLevel][i]-1)%3+1)]);
					g2d.fillRect((i%numOfCells)*cellWidth+1, (i/numOfCells)*cellWidth+1, cellWidth-1, cellWidth-1);
					if (levels[currentLevel][i]>6){//draw the pressed buttons
						g2d.setColor(clrSel);
						g2d.fillOval((i%numOfCells)*cellWidth+cellWidth/2-bRad, (i/numOfCells)*cellWidth+cellWidth/2-bRad, 2*bRad, 2*bRad);
					}
					else if (levels[currentLevel][i]>3){//draw the unpressed buttons
						g2d.setColor(clrLines);
						g2d.fillOval((i%numOfCells)*cellWidth+cellWidth/2-bRad, (i/numOfCells)*cellWidth+cellWidth/2-bRad, 2*bRad, 2*bRad);
					}
				}
			}
			//draw the hole
			g2d.translate((ends[currentLevel]%numOfCells)*cellWidth, (ends[currentLevel]/numOfCells)*cellWidth);
			g2d.setColor(clrLines);
			if (levels[currentLevel][ends[currentLevel]] != 0) g2d.setColor(clrLevel[((levels[currentLevel][ends[currentLevel]]-1)%3+1)]);
			g2d.fillRect(1, 1, cellWidth-1, cellWidth-1);
			g2d.setColor(clrBG);
			g2d.fillOval((cellWidth/2 - bRad), (cellWidth/2 - bRad), bRad*2+1, bRad*2+1);
			//draw bars if locked
			g2d.setColor(clrLines);
			//if (levels[currentLevel][ends[currentLevel]] != 0) g2d.setColor(clrLevel[((levels[currentLevel][ends[currentLevel]]-1)%3+1)]);
			if (!endOpen){
				g2d.drawLine(0, bRad+1, cellWidth, bRad+1);
				g2d.drawLine(0, cellWidth-bRad-1, cellWidth, cellWidth-bRad-1);
				g2d.drawLine(bRad+1, 0, bRad+1, cellWidth);
				g2d.drawLine(cellWidth-bRad-1, 0, cellWidth-bRad-1, cellWidth);
			}
			g2d.setTransform(identity);
			
			//draw the ball
			g2d.setColor(clrBall);
			g2d.drawOval((int)(pos[0]-bRad), (int)(pos[1]-bRad), bRad*2, bRad*2);
			g2d.drawLine((int)(pos[0]+dir[1]*bRad), (int)(pos[1]-dir[0]*bRad), (int)(pos[0]+dir[0]*cellWidth/2), (int)(pos[1]+dir[1]*cellWidth/2));
			g2d.drawLine((int)(pos[0]-dir[1]*bRad), (int)(pos[1]+dir[0]*bRad), (int)(pos[0]+dir[0]*cellWidth/2), (int)(pos[1]+dir[1]*cellWidth/2));
			
			//draw the program
			for (int i=0; i<numOfPrgBoxes; i++){
				x=((i%prgBoxArrayWidth)+1)*prgBoxSpacing+(i%prgBoxArrayWidth)*prgBoxSize+numOfCells*cellWidth;
				y=((i/prgBoxArrayWidth)+1)*prgBoxSpacing+(i/prgBoxArrayWidth)*prgBoxSize;
				g2d.translate(x, y);
				if (prgrmclrs[currentLevel][i]>0){// 0: blank, 1: red, 2: green, 3: blue
					g2d.setColor(clrLevel[prgrmclrs[currentLevel][i]]);
					g2d.fillRect(0, 0, prgBoxSize, prgBoxSize);
				}
				if (progPos == i) g2d.setColor(clrSel);
				else g2d.setColor(clrLines);
				g2d.drawRect(0, 0, prgBoxSize, prgBoxSize);
				//if (mx>x && mx<x+prgBoxSize && my>y-prgBoxSideClickWidth && my<y+prgBoxSize+prgBoxSideClickWidth) g2d.drawRect(0, -prgBoxSideClickWidth, prgBoxSize, prgBoxSize+2*prgBoxSideClickWidth);
				if (mx>x && mx<x+prgBoxSize && my>y-prgBoxSideClickWidth && my<y)
					g2d.drawRect(0, -prgBoxSideClickWidth, prgBoxSize, prgBoxSideClickWidth);
				if (mx>x && mx<x+prgBoxSize && my>y+prgBoxSize && my<y+prgBoxSize+prgBoxSideClickWidth)
					g2d.drawRect(0, prgBoxSize, prgBoxSize, prgBoxSideClickWidth);
				tempTrans = g2d.getTransform();
				g2d.translate(prgBoxSize/2, prgBoxSize/2);
				// 0: nop, 1: return, 2: forward, 3: left, 4: right
				if (programs[currentLevel][i]==3){
					g2d.rotate(-1.570796327);// -pi/2
				} else if (programs[currentLevel][i]==4){
					g2d.rotate(1.570796327); //  pi/2
				}
				if (programs[currentLevel][i]>1){
					g2d.fill(triangle);
				}
				if (programs[currentLevel][i]==1){
					g2d.drawString("R", -4, 4);
				}
				
				g2d.setTransform(identity);
			}
			
			//draw beziers
			for (int i=0; i<numOfPrgBoxes; i++){
				g2d.translate(((i%prgBoxArrayWidth)+1)*prgBoxSpacing+(i%prgBoxArrayWidth)*prgBoxSize+numOfCells*cellWidth+prgBoxSize/2, ((i/prgBoxArrayWidth)+1)*prgBoxSpacing+(i/prgBoxArrayWidth)*prgBoxSize);
				int ti;
				int tx;
				int ty;
				int side=1;
				g2d.setStroke(medStroke);
				if (toploops[currentLevel][i]!=0){
					g2d.setColor(clrLevel[tplpclrs[currentLevel][i]]);
					g2d.scale(1, 0.5);
					g2d.fill(triangle);
					g2d.scale(1, 2);
					ti=(Math.abs(toploops[currentLevel][i])-1);
					tx=(ti%prgBoxArrayWidth-i%prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					ty=(ti/prgBoxArrayWidth-i/prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					if (toploops[currentLevel][i]<0){
						side=-1;
						ty+=prgBoxSize+2;
					}
					temp = bezierOffset*(1+Math.abs(ti/prgBoxArrayWidth-i/prgBoxArrayWidth));
					g2d.draw(new CubicCurve2D.Double(0, -2, 0, -temp, tx, ty-side*temp, tx, ty-1));
				}
				g2d.translate(0, prgBoxSize+1);
				side=-1;
				if (botloops[currentLevel][i]!=0){
					g2d.setColor(clrLevel[btlpclrs[currentLevel][i]]);
					g2d.scale(1, -0.5);
					g2d.fill(triangle);
					g2d.scale(1, -2);
					ti=(Math.abs(botloops[currentLevel][i])-1);
					tx=(ti%prgBoxArrayWidth-i%prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					ty=(ti/prgBoxArrayWidth-i/prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					if (botloops[currentLevel][i]>0){
						side=1;
						ty-=prgBoxSize+2;
					}
					temp = bezierOffset*(1+Math.abs(ti/prgBoxArrayWidth-i/prgBoxArrayWidth));
					g2d.draw(new CubicCurve2D.Double(0, 1, 0, temp, tx, ty-side*temp, tx, ty));
				}
				g2d.setStroke(thinStroke);
				g2d.setTransform(identity);
			}
			
			//draw bezier to mouse when dragging
			if (dragging){
				g2d.setStroke(medStroke);
				g2d.setColor(clrLevel[selClr]);
				int i = Math.abs(mDownBox)-1;
				int side = 1;
				if (mDownBox<0) side = -1;
				x = ((i%prgBoxArrayWidth)+1)*prgBoxSpacing+(i%prgBoxArrayWidth)*prgBoxSize+numOfCells*cellWidth+prgBoxSize/2;
				y = ((i/prgBoxArrayWidth)+1)*prgBoxSpacing+(i/prgBoxArrayWidth)*prgBoxSize + (1-side)*prgBoxSize/2;
				//temp = bezierOffset*(1+2*Math.abs(my-y)/(prgBoxSize+prgBoxSpacing));
				g2d.draw(new CubicCurve2D.Double(x, y, x, y-side*bezierOffset*2, mx, my, mx, my));
				g2d.setTransform(identity);
				g2d.setStroke(thinStroke);
			}
			
			//draw the play control buttons
			for (int i=0; i<5; i++){
				g2d.setColor(clrLines);
				if (playMode+1==i) g2d.setColor(clrSel);
				g2d.translate((i+1)*(playBtnSpacing+playBtnSize), numOfCells*cellWidth+playBtnSpacing);
				g2d.drawRect(0, 0, playBtnSize, playBtnSize);
				g2d.translate(playBtnSize/2, playBtnSize/2);
				g2d.setStroke(medStroke);
				switch (i){
				case 0://back to start
					g2d.drawLine(-10, -5, -10, 4);
					g2d.rotate(-1.570796327);
					g2d.fill(triangle);
					g2d.translate(0, 9);
					g2d.fill(triangle);
					break;
				case 1://pause
					g2d.drawLine(-5, -5, -5, 4);
					g2d.drawLine(5, -5, 5, 4);
					break;
				case 2://single step
					g2d.translate(-5, 0);
					g2d.drawLine(10, -5, 10, 4);
					g2d.rotate(1.570796327);
					g2d.fill(triangle);
					break;
				case 3://play
					g2d.translate(-5, 0);
					g2d.rotate(1.570796327);
					g2d.fill(triangle);
					break;
				case 4://fast forward
					g2d.rotate(1.570796327);
					g2d.fill(triangle);
					g2d.translate(0, 9);
					g2d.fill(triangle);
					break;
				}
				g2d.setStroke(thinStroke);
				g2d.setTransform(identity);
			}
			
			//draw the score and par labels
			g2d.setColor(clrText);
			g2d.translate(playBtnSpacing+playBtnSize, numOfCells*cellWidth+playBtnSpacing+2*playBtnSize);
			//g2d.drawString("Level:", 0, -10); g2d.drawString(Integer.toString(currentLevel), playBtnSpacing+playBtnSize, -10);
			g2d.drawString("Score:", 0, 0);  g2d.drawString(Integer.toString(scores[currentLevel]), playBtnSpacing+playBtnSize, 0);
			g2d.drawString("Par:", 0, 20);    g2d.drawString(Integer.toString(pars[currentLevel]), playBtnSpacing+playBtnSize, 20);
			
			//draw the next level and previous level buttons (g2d is still translated from before)
			g2d.setColor(clrLines);
			g2d.drawRect(0, playBtnSize, playBtnSpacing+2*playBtnSize, playBtnSpacing);
			g2d.setColor(clrText);
			g2d.drawString("Previous",20, playBtnSize+16);
			g2d.drawString(Integer.toString(currentLevel), 2*playBtnSpacing+2*playBtnSize + playBtnSize/2 - 4, playBtnSize+16);//draw the level label
			g2d.translate(3*(playBtnSpacing+playBtnSize),0);
			g2d.setColor(clrLines);
			g2d.drawRect(0, playBtnSize, playBtnSpacing+2*playBtnSize, playBtnSpacing);
			g2d.setColor(clrText);
			g2d.drawString("Next",34, playBtnSize+16);
			
			g2d.setTransform(identity);
			
			//draw the colour palette
			g2d.translate(prgBoxSpacing+numOfCells*cellWidth, numOfCells*cellWidth+playBtnSpacing);
			for (int i=0;i<4;i++){
				g2d.setColor(clrLevel[i]);
				g2d.fillRect((i%2)*cellWidth, (i/2)*cellWidth, cellWidth, cellWidth);
				g2d.setColor(clrLines);
				g2d.drawRect((i%2)*cellWidth, (i/2)*cellWidth, cellWidth, cellWidth);
			}
			g2d.setColor(clrSel);
			g2d.drawRect((selClr%2)*cellWidth, (selClr/2)*cellWidth, cellWidth, cellWidth);
			g2d.setTransform(identity);
			
			//draw the operation selection buttons
			for (int i=0;i<5;i++){// 0: nop, 1: return, 2: forward, 3: left, 4: right
				x=2*prgBoxSpacing+i*(prgBoxSpacing-prgBoxSize)/2+(i+1)*prgBoxSize+numOfCells*cellWidth;
				y=numOfCells*cellWidth+playBtnSpacing + (cellWidth*2 - prgBoxSize)/2;
				g2d.translate(x, y);
				g2d.setColor(clrLines);
				if (i==selOp) g2d.setColor(clrSel);
				g2d.drawRect(0, 0, prgBoxSize, prgBoxSize);
				g2d.translate(prgBoxSize/2, prgBoxSize/2);
				if (i==3){
					g2d.rotate(-1.570796327);// -pi/2
				} else if (i==4){
					g2d.rotate(1.570796327); //  pi/2
				}
				if (i>1){
					g2d.fill(triangle);
				}
				if (i==1){
					g2d.drawString("R", -4, 4);
				}
				g2d.setTransform(identity);
			}
			
			appletGraphics.drawImage(screen, 0, 0, null);

			do {
				Thread.yield();
			} while (System.nanoTime() - lastTime < 16000000L);

			if (!isActive()) {
				return;
			}
		}
	}
	
	public void processKeyEvent(KeyEvent e) {
		switch (e.getKeyCode()){
		case KeyEvent.VK_S:
			String s = "{";
			for (int i=0; i<numOfCells*numOfCells;i++){
				s=s+levels[currentLevel][i]+",";
			}
			s=s+"}";
			System.out.println(s);
			System.out.println("start: "+starts[currentLevel]);
			System.out.println("end: "+ends[currentLevel]);
			break;
		case KeyEvent.VK_B:
			if (editMode){
				for (int i=0; i<numOfCells*numOfCells;i++){
					x = (i%numOfCells)*cellWidth;
					y = (i/numOfCells)*cellWidth;
					if (mx>x && mx<x+cellWidth && my>y && my<y+cellWidth){
						if (selClr!=0) levels[currentLevel][i] = selClr+3;
					}
				}
			}
			break;
//		case Event.UP:
//			if (!moving) step = true;
//			break;
//		case Event.LEFT:
//			if (!moving){
//				int temp=dir[0];
//				dir[0]=dir[1];
//				dir[1]=-temp;
//			}
//			break;
//		case Event.RIGHT:
//			if (!moving){
//				int temp=dir[0];
//				dir[0]=-dir[1];
//				dir[1]=temp;
//			}
//			break;
		}
	}
	
	public void processMouseEvent(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_PRESSED){
			if (e.getButton() == MouseEvent.BUTTON1) mousedown=true;
			mx = e.getX();
			my = e.getY();
			// in editmode check for clicks in the grid
			if (editMode){
				for (int i=0; i<numOfCells*numOfCells;i++){
					x = (i%numOfCells)*cellWidth;
					y = (i/numOfCells)*cellWidth;
					if (mx>x && mx<x+cellWidth && my>y && my<y+cellWidth){
						if (e.getButton() == MouseEvent.BUTTON1){
							levels[currentLevel][i] = selClr;
						}
						if (e.getButton() == MouseEvent.BUTTON2){//start
							starts[currentLevel]=i;
						}
						if (e.getButton() == MouseEvent.BUTTON3){//end
							ends[currentLevel]=i;
						}
					}
				}
			}
			
			//System.out.println("mouse click at x="+e.x+", y="+e.y);
			//check for clicks on the play control buttons
			for (int i=0; i<5; i++){
				//g2d.translate((i+1)*(playBtnSpacing+playBtnSize), numOfCells*cellWidth+playBtnSpacing);
				//g2d.drawRect(0, 0, playBtnSize, playBtnSize);
				x = (i+1)*(playBtnSpacing+playBtnSize);
				y = numOfCells*cellWidth+playBtnSpacing;
				//System.out.println("checking for button at x="+x+", y="+y);
				if (mx>x && mx<x+playBtnSize && my>y && my<y+playBtnSize){
					//System.out.println("playmode set to "+(i-1));
					playMode = i-1;
				}
				if (playMode==-1){reset = true; playMode=0;}
			}
			
			//check for clicks in the previous and next level buttons
			x = playBtnSpacing+playBtnSize;
			y = numOfCells*cellWidth+playBtnSpacing+3*playBtnSize;
			if (mx>x && mx<x+playBtnSpacing+2*playBtnSize && my>y && my<y+playBtnSpacing){//previous
				if (--currentLevel<0) currentLevel=numOfLevels-1;
				reset=true;
			}
			x+=3*(playBtnSpacing+playBtnSize);
			if (mx>x && mx<x+playBtnSpacing+2*playBtnSize && my>y && my<y+playBtnSpacing){//next
				if (++currentLevel>=numOfLevels) currentLevel=0;
				reset=true;
			}
			
			//g2d.translate(prgBoxSpacing+numOfCells*cellWidth, numOfCells*cellWidth+playBtnSpacing);
			for (int i=0;i<4;i++){
				x = prgBoxSpacing+numOfCells*cellWidth + (i%2)*cellWidth;
				y = numOfCells*cellWidth+playBtnSpacing + (i/2)*cellWidth;
				if (mx>x && mx<x+cellWidth && my>y && my<y+cellWidth ){
					selClr=i;
				}
//				g2d.setColor(clrLevel[i]);
//				g2d.fillRect((i%2)*cellWidth, (i/2)*cellWidth, cellWidth, cellWidth);
//				g2d.setColor(clrLines);
//				g2d.drawRect((i%2)*cellWidth, (i/2)*cellWidth, cellWidth, cellWidth);
			}
			
			//check the operation selection buttons
			for (int i=0;i<5;i++){// 0: nop, 1: return, 2: forward, 3: left, 4: right
				x=2*prgBoxSpacing+i*(prgBoxSpacing-prgBoxSize)/2+(i+1)*prgBoxSize+numOfCells*cellWidth;
				y=numOfCells*cellWidth+playBtnSpacing + (cellWidth*2 - prgBoxSize)/2;
				if (mx>x && mx<x+prgBoxSize && my>y && my<y+prgBoxSize){
					selOp=i;
				}
			}
			
			//check for clicks in the program boxes
			for (int i=0; i<numOfPrgBoxes; i++){
				x=((i%prgBoxArrayWidth)+1)*prgBoxSpacing+(i%prgBoxArrayWidth)*prgBoxSize+numOfCells*cellWidth;
				y=((i/prgBoxArrayWidth)+1)*prgBoxSpacing+(i/prgBoxArrayWidth)*prgBoxSize;
				if (mx>x && mx<x+prgBoxSize && my>y && my<y+prgBoxSize){
					if (e.getButton() == MouseEvent.BUTTON1){
						if (selOp!=0) programs[currentLevel][i]=selOp;
						prgrmclrs[currentLevel][i]=selClr;
					} else {
						programs[currentLevel][i]=0;
						prgrmclrs[currentLevel][i]=0;
					}
				}
			}
			
			for (int i=0; i<numOfPrgBoxes; i++){
				x=((i%prgBoxArrayWidth)+1)*prgBoxSpacing+(i%prgBoxArrayWidth)*prgBoxSize+numOfCells*cellWidth;
				y=((i/prgBoxArrayWidth)+1)*prgBoxSpacing+(i/prgBoxArrayWidth)*prgBoxSize;
				if (mx>x && mx<x+prgBoxSize && my>y-prgBoxSideClickWidth && my<y){
					if (e.getButton() == MouseEvent.BUTTON1){
						mDownBox=i+1;
						dragging=true;
					} else {
						toploops[currentLevel][i]=0;
					}
				}
				if (mx>x && mx<x+prgBoxSize && my>y+prgBoxSize && my<y+prgBoxSize+prgBoxSideClickWidth){
					if (e.getButton() == MouseEvent.BUTTON1){
						mDownBox=-i-1;
						dragging=true;
					} else {
						botloops[currentLevel][i]=0;
					}
				}
			}
		}
		if (e.getID() == MouseEvent.MOUSE_RELEASED){
			mx = e.getX();
			my = e.getY();
			if (e.getButton() == MouseEvent.BUTTON1) mousedown=false;
			if (e.getButton() == MouseEvent.BUTTON1 && dragging){
				for (int i=0; i<numOfPrgBoxes; i++){
					x=((i%prgBoxArrayWidth)+1)*prgBoxSpacing+(i%prgBoxArrayWidth)*prgBoxSize+numOfCells*cellWidth;
					y=((i/prgBoxArrayWidth)+1)*prgBoxSpacing+(i/prgBoxArrayWidth)*prgBoxSize;
					int sign = 0;
					if (mx>x && mx<x+prgBoxSize && my>y-prgBoxSideClickWidth && my<y){
						sign=1;
					}
					if (mx>x && mx<x+prgBoxSize && my>y+prgBoxSize && my<y+prgBoxSize+prgBoxSideClickWidth){
						sign=-1;
					}
					if (sign!=0){
						temp=Math.abs(mDownBox)-1;//the index of the box
						if (mDownBox>0){
							tplpclrs[currentLevel][temp]=selClr;
							if (sign*(i+1)==mDownBox) continue;//don't create connections to the same side of the same program box
							toploops[currentLevel][temp]=sign*(i+1);
						}
						if (mDownBox<0){
							btlpclrs[currentLevel][temp]=selClr;
							if (sign*(i+1)==mDownBox) continue;//don't create connections to the same side of the same program box
							botloops[currentLevel][temp]=sign*(i+1);
						}
					}
				}
				dragging =false;
			}
		}
	}
	
	public void processMouseMotionEvent(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		// in editmode check for clicks in the grid
		if (editMode && mousedown){
			for (int i=0; i<numOfCells*numOfCells;i++){
				x = (i%numOfCells)*cellWidth;
				y = (i/numOfCells)*cellWidth;
				if (mx>x && mx<x+cellWidth && my>y && my<y+cellWidth){
					levels[currentLevel][i] = selClr;
				}
			}
		}
	}
}