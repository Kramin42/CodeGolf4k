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
import java.awt.AlphaComposite;
import java.awt.Event;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class g extends Applet implements Runnable {

	public void start() {
		new Thread(this).start();
	}

	//int mx = 0, my = 0;
	
	int numOfCells = 20;
	int cellWidth = 20;
	
	int prgBoxSize = 30;
	int prgBoxSpacing = 50;
	
	int level[] = new int[numOfCells*numOfCells];
	int startCell;
	int endCell;
	int startDir[]={0,-1};
	double pos[]={0,0}, vel[]={0,0};
	int dir[]={0,-1};
	
	int ballSpeed = 1;//5 for fastforward, 1 for normal
	boolean moving = false;
	
	boolean step = false;
	boolean play = false;
	boolean fastfwrd = false;
	
	boolean reachedEnd = false;
	boolean outOfBounds = false;
	boolean createNewGame;
	
	// 0: nop, 1: return, 2: forward, 3: left, 4: right
	int program[]  = {2,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0};
	// 0: blank, 1: red, 2: green, 3: blue
	int prgrmclrs[]= {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0};
	// index of the block to connect to, "-" sign if connected to bottom, "+" if connected to top, 0 if disconnected
	// requires an offset of 1 to be added or subtracted, depending on sign, to get index of block
	int toploops[] = {0,0,14,0,0,0,0,0,0,0,0,0,0,1,0,0};
	int botloops[] = {0,0,16,0,0,0,0,0,0,0,0,0,0,1,0,0};
	int tplpclrs[] = {0,0,3,0,0,0,0,0,0,0,0,0,0,1,0,0};
	int btlpclrs[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0};
	int bezierOffset = 25;
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
		
		int xpoints[] = {-5,0,5};
		int ypoints[] = {0,-10,0};
		Polygon triangle = new Polygon(xpoints, ypoints, 3);
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		Random rand = new Random();

		//ball vars
		double dest[] = {0,0};
		int currentCell = 0;
		
		// other vars
		int temp;
		
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
		
		//build level p=path h=hidden
		// 0: blank, 1: pred, 2: pgreen, 3: pblue, 4: hpred, 5: hpgreen, 6: hpblue
		//level[9*numOfCells+9]=2;
		
		for(int i = 1; i<numOfCells-1; i++){
			for (int j = 1; j<numOfCells-1; j++){
				if (i==9 && j==9) continue;
				level[j*numOfCells+i]=1+((i+j)%3);
			}
		}
		startCell = 14*numOfCells+10;
		endCell   = 9*numOfCells+9;
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
			
			if (createNewGame || reachedEnd || outOfBounds){ //reset game
				pos[0] = (startCell%numOfCells)*cellWidth + cellWidth/2;
				pos[1] = (startCell/numOfCells)*cellWidth + cellWidth/2;
				dir[0] = startDir[0]; dir[1] = startDir[1];
				currentCell = startCell;
				progPos = 0;
				reachedEnd = false;
				outOfBounds = false;
				createNewGame=false;
				execute=false;
			}
			
			//update the ball
			if (step){
				moving=true;
				vel[0] = ballSpeed*dir[0];
				vel[1] = ballSpeed*dir[1];
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
					//pos[0]=dest[0];
					//pos[1]=dest[1];

					
					//check if it has reached the end
					reachedEnd = currentCell==endCell;
					outOfBounds = (level[currentCell]==0 || level[currentCell]>3);
//					if (currentCell==endCell){
//						reachedEnd=true;
//					} else if (level[currentCell]==0 || level[currentCell]>3){
//						outOfBounds=true;
//					}
				}
			}
			
			// go to next instruction
			if (execute && !moving /*&& tick%5==0*/) {
				//System.out.println("running: " + program[progPos] + " at " + progPos);
				// 0: blank, 1: red, 2: green, 3: blue
				if (prgrmclrs[progPos] == 0 || prgrmclrs[progPos] == level[currentCell]) {
					// 0: nop, 1: return, 2: forward, 3: left, 4: right
					temp = dir[0];
					if (program[progPos] == 2)
						step = true;
					else if (program[progPos] == 3) {
						dir[0] = dir[1];
						dir[1] = -temp;
					} else if (program[progPos] == 4) {
						dir[0] = -dir[1];
						dir[1] = temp;
					}
				}
				
				// toploops gives the index of the block to connect to, "-" sign if connected to bottom, "+" if connected to top, 0 if disconnected
				// requires an offset of 1 to be added or subtracted, depending on sign, to get index of block
				//TODO: add to stack when branching
				temp=0;
				if (toploops[progPos]!=0 || botloops[progPos]!=0){
					if (tplpclrs[progPos]==0){
						progPos = Math.abs(toploops[progPos])-1;
						temp=1;
					} else if (btlpclrs[progPos]==0){
						progPos = Math.abs(botloops[progPos])-1;
						temp=1;
					} else if (tplpclrs[progPos] == level[currentCell]){
						progPos = Math.abs(toploops[progPos])-1;
						temp=1;
					} else if (btlpclrs[progPos] == level[currentCell]){
						progPos = Math.abs(botloops[progPos])-1;
						temp=1;
					} 
				}
				if (temp==0 && ++progPos >= 16) progPos = 0;//increment and loop back to 0
				execute = play || fastfwrd;
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
			for (int i=0; i<16; i++){
				g2d.translate(((i%4)+1)*prgBoxSpacing+(i%4)*prgBoxSize+numOfCells*cellWidth, ((i/4)+1)*prgBoxSpacing+(i/4)*prgBoxSize);
				if (prgrmclrs[i]>0){// 0: blank, 1: red, 2: green, 3: blue
					g2d.setColor(clrLevel[prgrmclrs[i]]);
					g2d.fillRect(0, 0, prgBoxSize, prgBoxSize);
				}
				if (progPos == i) g2d.setColor(clrSel);
				else g2d.setColor(clrLines);
				g2d.drawRect(0, 0, prgBoxSize, prgBoxSize);
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
				g2d.setTransform(tempTrans);
				// draw beziers
				g2d.translate(prgBoxSize/2, 0);
				int ti;
				int tx;
				int ty;
				if (toploops[i]!=0){
					g2d.setColor(clrLevel[tplpclrs[i]]);
					g2d.scale(1, 0.5);
					g2d.fill(triangle);
					g2d.scale(1, 2);
					ti=(Math.abs(toploops[i])-1);
					tx=(ti%4-i%4)*(prgBoxSpacing+prgBoxSize);
					ty=(ti/4-i/4)*(prgBoxSpacing+prgBoxSize);
					temp = bezierOffset*(1+Math.abs(ti/4-i/4));
					g2d.draw(new CubicCurve2D.Double(0, -1, 0, -temp, tx, ty-temp, tx, ty-1));
				}
				g2d.translate(0, prgBoxSize+1);
				if (botloops[i]!=0){
					g2d.setColor(clrLevel[btlpclrs[i]]);
					g2d.scale(1, -0.5);
					g2d.fill(triangle);
					g2d.scale(1, -2);
					ti=(Math.abs(botloops[i])-1);
					tx=(ti%4-i%4)*(prgBoxSpacing+prgBoxSize);
					ty=(ti/4-i/4)*(prgBoxSpacing+prgBoxSize);
					temp = bezierOffset*(1+Math.abs(ti/4-i/4));
					g2d.draw(new CubicCurve2D.Double(0, 0, 0, temp, tx, ty+temp, tx, ty));
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

	public boolean handleEvent(Event e) {
		if (e.id == Event.KEY_ACTION){
			switch (e.key){
			case Event.UP:
				if (!moving) step = true;
				break;
			case Event.LEFT:
				if (!moving){
					int temp=dir[0];
					dir[0]=dir[1];
					dir[1]=-temp;
				}
				break;
			case Event.RIGHT:
				if (!moving){
					int temp=dir[0];
					dir[0]=-dir[1];
					dir[1]=temp;
				}
				break;
			}
		}
		if (e.id == Event.KEY_RELEASE){
			switch (e.key){
			case ' ':
				execute = !execute;
				break;
			}
		}
		if (e.id == Event.MOUSE_DOWN){
			
		}
		if (e.id == Event.MOUSE_UP){
			
		}
		return false;
	}
}