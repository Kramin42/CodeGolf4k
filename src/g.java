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
	
	int selClr = 0;
	int selOp = 0;
	
	//int level[] = new int[numOfCells*numOfCells];
	int level[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,3,3,1,0,0,0,0,3,3,3,1,0,1,3,3,1,0,0,0,0,0,3,0,0,0,0,0,0,0,3,0,0,0,0,3,0,0,0,1,3,3,2,0,1,3,3,1,0,0,2,3,3,3,3,2,0,0,0,0,0,3,0,0,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,2,3,3,3,0,2,3,3,3,3,3,2,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,3,3,3,3,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,};	
	
	int startCell;
	int endCell;
	int startDir[]={0,-1};
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
	int program[]  = new int[numOfPrgBoxes];
	// 0: blank, 1: red, 2: green, 3: blue
	int prgrmclrs[]= new int[numOfPrgBoxes];
	Stack<Integer> progStack = new Stack<Integer>();
	
	// index of the block to connect to, "-" sign if connected to bottom, "+" if connected to top, 0 if disconnected
	// requires an offset of 1 to be added or subtracted, depending on sign, to get index of block
	int toploops[] = new int[numOfPrgBoxes];
	int botloops[] = new int[numOfPrgBoxes];
	int tplpclrs[] = new int[numOfPrgBoxes];
	int btlpclrs[] = new int[numOfPrgBoxes];
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
		Font normalFont = new Font("Ariel", Font.PLAIN, 12);
		Font largeFont = new Font("Ariel", Font.PLAIN, 60);
		Font mediumFont = new Font("Ariel", Font.PLAIN, 30);
		Font hugeFont = new Font("Ariel", Font.PLAIN, 120);
		
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
		startCell = 348;
		endCell = 189;
		startDir[0]=0; startDir[1]=-1; //0: up, 1: right, 2: down, 3: left
		
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
				vel[0]=0;
				vel[1]=0;
				moving=false;
				step=false;
				pos[0] = (startCell%numOfCells)*cellWidth + cellWidth/2;
				pos[1] = (startCell/numOfCells)*cellWidth + cellWidth/2;
				dir[0] = startDir[0]; dir[1] = startDir[1];
				currentCell = startCell;
				progPos = 0;
				progStack.clear();
				reachedEnd = false;
				outOfBounds = false;
				reset = false;
				createNewGame=false;
				execute=false;
				playMode=0;
			}
			
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
					reachedEnd = currentCell==endCell;
					outOfBounds = (level[currentCell]==0 || level[currentCell]>3);
					reset = reachedEnd || outOfBounds;
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
				if (prgrmclrs[progPos] == 0 || prgrmclrs[progPos] == level[currentCell]) {
					// 0: nop, 1: return, 2: forward, 3: left, 4: right
					temp = dir[0];
					if (program[progPos] == 2)
						step = true;
					if (program[progPos] == 3) {
						dir[0] = dir[1];
						dir[1] = -temp;
					}
					if (program[progPos] == 4) {
						dir[0] = -dir[1];
						dir[1] = temp;
					}
					if (program[progPos] == 1){
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
					if (tplpclrs[progPos] == level[currentCell] && toploops[progPos]!=0){
						temp=progPos;
						progPos = Math.abs(toploops[progPos])-1;
					} else if (btlpclrs[progPos] == level[currentCell] && botloops[progPos]!=0){
						temp=progPos;
						progPos = Math.abs(botloops[progPos])-1;
					} else if (tplpclrs[progPos]==0 && toploops[progPos]!=0){
						temp=progPos;
						progPos = Math.abs(toploops[progPos])-1;
					} else if (btlpclrs[progPos]==0 && botloops[progPos]!=0){
						temp=progPos;
						progPos = Math.abs(botloops[progPos])-1;
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
			//draw the hole
			g2d.setColor(clrLines);
			g2d.fillRect((endCell%numOfCells)*cellWidth+1, (endCell/numOfCells)*cellWidth+1, cellWidth-1, cellWidth-1);
			g2d.setColor(clrBG);
			g2d.fillOval((endCell%numOfCells)*cellWidth+(cellWidth/2 - bRad), (endCell/numOfCells)*cellWidth+(cellWidth/2 - bRad), bRad*2+1, bRad*2+1);
			//draw the coloured squares
			for (int i=0; i<numOfCells*numOfCells;i++){
				if (level[i]>0){
					g2d.setColor(clrLevel[level[i]]);
					g2d.fillRect((i%numOfCells)*cellWidth+1, (i/numOfCells)*cellWidth+1, cellWidth-1, cellWidth-1);
				}
			}
			
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
				if (prgrmclrs[i]>0){// 0: blank, 1: red, 2: green, 3: blue
					g2d.setColor(clrLevel[prgrmclrs[i]]);
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
				if (program[i]==3){
					g2d.rotate(-1.570796327);// -pi/2
				} else if (program[i]==4){
					g2d.rotate(1.570796327); //  pi/2
				}
				if (program[i]>1){
					g2d.fill(triangle);
				}
				if (program[i]==1){
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
				if (toploops[i]!=0){
					g2d.setColor(clrLevel[tplpclrs[i]]);
					g2d.scale(1, 0.5);
					g2d.fill(triangle);
					g2d.scale(1, 2);
					ti=(Math.abs(toploops[i])-1);
					tx=(ti%prgBoxArrayWidth-i%prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					ty=(ti/prgBoxArrayWidth-i/prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					if (toploops[i]<0){
						side=-1;
						ty+=prgBoxSize+2;
					}
					temp = bezierOffset*(1+Math.abs(ti/prgBoxArrayWidth-i/prgBoxArrayWidth));
					g2d.draw(new CubicCurve2D.Double(0, -2, 0, -temp, tx, ty-side*temp, tx, ty-1));
				}
				g2d.translate(0, prgBoxSize+1);
				side=-1;
				if (botloops[i]!=0){
					g2d.setColor(clrLevel[btlpclrs[i]]);
					g2d.scale(1, -0.5);
					g2d.fill(triangle);
					g2d.scale(1, -2);
					ti=(Math.abs(botloops[i])-1);
					tx=(ti%prgBoxArrayWidth-i%prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					ty=(ti/prgBoxArrayWidth-i/prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					if (botloops[i]>0){
						side=1;
						ty-=prgBoxSize+2;
					}
					temp = bezierOffset*(1+Math.abs(ti/prgBoxArrayWidth-i/prgBoxArrayWidth));
					g2d.draw(new CubicCurve2D.Double(0, 1, 0, temp, tx, ty-side*temp, tx, ty));
				}
				g2d.setStroke(thinStroke);
				g2d.setTransform(identity);
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
			mx = e.getX();
			my = e.getY();
			//System.out.println("mouse click at x="+e.x+", y="+e.y);
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
						if (selOp!=0) program[i]=selOp;
						prgrmclrs[i]=selClr;
					} else {
						program[i]=0;
						prgrmclrs[i]=0;
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
						toploops[i]=0;
					}
				}
				if (mx>x && mx<x+prgBoxSize && my>y+prgBoxSize && my<y+prgBoxSize+prgBoxSideClickWidth){
					if (e.getButton() == MouseEvent.BUTTON1){
						mDownBox=-i-1;
						dragging=true;
					} else {
						botloops[i]=0;
					}
				}
			}
		}
		if (e.getID() == MouseEvent.MOUSE_RELEASED){
			mx = e.getX();
			my = e.getY();
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
							tplpclrs[temp]=selClr;
							if (sign*(i+1)==mDownBox) continue;//don't create connections to the same side of the same program box
							toploops[temp]=sign*(i+1);
						}
						if (mDownBox<0){
							btlpclrs[temp]=selClr;
							if (sign*(i+1)==mDownBox) continue;//don't create connections to the same side of the same program box
							botloops[temp]=sign*(i+1);
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
	}
}