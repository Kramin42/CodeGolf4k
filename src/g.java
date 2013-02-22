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
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
//import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.util.Stack;

public class g extends Applet implements Runnable {

	public void start() {
		new Thread(this).start();
	}
	//static final int w = 800, h = 600;

	int mx, my;
	int mDownBox;//positive for toploop, negative for bottom (offset by + or - 1)
	boolean dragging =false;
	boolean mousedown = false;
	
	static final int numOfCells = 20;
	static final int cellWidth = 20;
	
	static final int prgBoxSize = 30;
	static final int prgBoxSpacing = 40;
	static final int prgBoxSideClickWidth = 10;
	static final int numOfPrgBoxes = 25;
	static final int prgBoxArrayWidth = 5;
	
	static final int playBtnSpacing = 20;
	static final int playBtnSize = 40;
	
	int playMode = 0;//0: paused, 1: single step, 2: play, 3: fast forward
	
	boolean editMode=true;
	
	int selClr = 0;
	int selOp = 0;
	
	static final int numOfLevels = 10;
	
	int levels[][] = {{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}
	,{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,3,3,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,3,3,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}
	,{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,4,3,2,3,3,3,3,3,3,3,3,3,3,2,3,3,4,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,4,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,4,3,3,2,4,0,0,3,0,0,0,0,0,0,0,0,3,0,4,0,0,4,3,2,3,3,3,2,3,3,3,4,0,0,0,0,3,0,3,0,0,0,0,3,0,0,0,3,0,0,0,0,0,0,4,3,2,3,2,3,3,4,0,4,0,0,0,3,0,0,0,0,0,0,0,0,3,0,3,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,3,0,4,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,4,0,4,3,3,3,2,4,0,0,0,0,0,0,0,0,0,3,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,4,3,2,3,3,2,3,3,3,3,3,2,3,3,3,3,4,0,0,0,0,0,3,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,4,0,0,4,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}
	,{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,2,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,2,2,3,2,2,3,3,3,0,0,0,0,0,2,0,0,0,0,0,0,0,0,3,0,0,0,0,2,0,0,0,0,0,2,0,0,0,0,0,0,0,0,3,0,0,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,3,0,0,0,0,0,3,2,2,3,3,3,3,0,0,3,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,2,0,0,2,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,3,0,0,3,3,3,3,2,3,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}
	,{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}
	,{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,0,0,0,0,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,2,0,0,0,2,3,3,3,3,3,3,3,3,2,2,2,2,2,2,2,2,0,0,0,2,3,2,2,2,2,2,2,2,2,2,2,2,2,2,0,0,0,0,0,2,3,2,2,0,0,2,2,2,3,3,3,3,3,3,2,0,0,0,0,2,3,3,2,2,2,2,3,3,3,2,2,2,2,3,2,0,0,0,0,0,2,3,3,3,3,3,3,2,2,2,2,2,2,3,2,0,0,0,0,0,0,2,2,2,2,2,2,0,0,2,6,3,3,3,2,0,0,0,0,0,0,0,0,0,2,2,2,0,0,2,2,2,2,3,2,0,0,0,0,0,0,0,0,2,3,3,3,2,0,0,0,0,2,3,2,0,0,0,0,0,0,0,0,2,3,2,3,3,2,2,2,2,3,3,2,0,0,0,0,0,0,0,0,2,3,2,2,3,3,3,3,3,3,2,0,0,0,0,0,0,0,0,0,2,3,2,2,2,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0,2,3,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,3,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,3,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}
	,{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,2,3,3,3,3,3,3,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,3,0,0,0,3,3,3,2,3,3,3,0,0,0,3,0,0,0,0,0,3,0,0,0,3,0,0,0,0,0,3,0,0,0,3,0,0,0,0,0,3,0,0,0,3,0,0,0,0,0,2,0,0,0,2,0,0,0,0,0,3,0,0,0,3,0,0,0,3,0,3,0,0,0,3,0,0,0,0,0,2,0,0,0,2,0,0,0,3,2,3,0,0,0,3,0,0,0,0,0,3,0,0,0,3,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,3,0,0,0,3,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,3,0,0,0,3,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,3,0,0,0,3,3,3,3,3,2,3,3,3,3,3,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}
	,{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,3,3,3,3,2,0,0,0,0,0,2,3,3,3,3,3,2,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,3,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,3,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,2,3,3,2,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,3,3,3,3,3,3,3,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}
	,{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,2,3,3,3,3,3,3,3,3,3,2,3,3,3,2,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,3,0,0,0,3,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,3,0,0,0,3,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,3,0,0,0,3,0,0,0,0,2,3,3,3,3,3,3,2,3,3,3,2,0,0,0,3,0,0,0,0,3,0,0,0,0,0,0,3,0,0,0,3,0,0,0,3,0,0,0,0,3,0,0,0,4,0,0,3,0,4,3,2,3,4,0,4,0,0,0,0,3,0,0,0,3,0,0,3,0,0,0,0,0,0,0,0,0,0,0,4,2,3,3,3,2,0,3,2,0,4,3,3,2,3,3,3,4,0,0,0,0,0,0,0,4,0,0,3,0,0,0,0,3,0,0,0,0,0,0,0,0,4,0,0,0,0,0,3,0,0,0,0,3,0,0,0,0,0,0,0,0,2,3,3,3,3,3,2,3,3,3,3,2,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,4,3,2,3,3,3,3,4,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}
	,{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,2,1,1,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,3,0,3,2,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,3,0,2,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,3,0,2,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,1,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,3,3,3,3,1,2,2,3,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}
	};
	//int levels[][] = new int[numOfLevels][numOfCells*numOfCells];
	//static final int levelData[][] = {{190,-3,-3,-3,-2,19,-3,19,-3,19,-3},{129,-3,19,-3,19,-2,-3,-3,-1,19,-3,19,-3,16,-2,-3,-3,-1,16,-3,19,-3,19,-3},{23,-4,10,-4,8,-3,10,-3,6,-4,-3,-2,-3,-3,-3,-3,-3,-3,-3,-3,-3,-3,-2,-3,-3,-4,5,-3,10,-3,8,-3,6,-4,3,-3,8,-3,3,-4,-3,-3,-2,-4,2,-3,8,-3,1,-4,2,-4,-3,-2,-3,-3,-3,-2,-3,-3,-3,-4,4,-3,1,-3,4,-3,3,-3,6,-4,-3,-2,-3,-2,-3,-3,-4,1,-4,3,-3,8,-3,1,-3,8,-4,8,-3,1,-4,6,-4,10,-3,8,-3,10,-3,2,-4,1,-4,-3,-3,-3,-2,-4,9,-3,2,-3,5,-3,8,-4,-3,-2,-3,-3,-2,-3,-3,-3,-3,-3,-2,-3,-3,-3,-3,-4,5,-3,2,-3,5,-3,10,-4,2,-4,5,-3,19,-4},{86,-3,-2,-3,-3,-3,-3,-3,13,-3,5,-2,13,-3,5,-3,19,-3,10,-3,-3,-3,-3,-3,-3,-3,-2,-2,-3,-2,-2,-3,-3,-3,5,-2,8,-3,4,-2,5,-2,8,-3,4,-3,5,-3,8,-3,4,-3,5,-3,-2,-2,-3,-3,-3,-3,2,-3,4,-3,11,-2,2,-2,4,-3,11,-3,2,-3,-3,-3,-3,-2,-3,11,-3,19,-3,19,-3},{49,-3,19,-3,19,-3,19,-3,19,-3,19,-3,19,-3,19,-2,19,-3,19,-3,19,-3,19,-3,19,-3,19,-3,19,-3},{29,-2,-2,-2,-2,-2,-2,-2,-2,-2,4,-2,-2,-2,-2,-2,-2,-2,-3,-3,-3,-3,-3,-3,-3,-3,-2,3,-2,-3,-3,-3,-3,-3,-3,-3,-3,-2,-2,-2,-2,-2,-2,-2,-2,3,-2,-3,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,5,-2,-3,-2,-2,2,-2,-2,-2,-3,-3,-3,-3,-3,-3,-2,4,-2,-3,-3,-2,-2,-2,-2,-3,-3,-3,-2,-2,-2,-2,-3,-2,5,-2,-3,-3,-3,-3,-3,-3,-2,-2,-2,-2,-2,-2,-3,-2,6,-2,-2,-2,-2,-2,-2,2,-2,-6,-3,-3,-3,-2,9,-2,-2,-2,2,-2,-2,-2,-2,-3,-2,8,-2,-3,-3,-3,-2,4,-2,-3,-2,8,-2,-3,-2,-3,-3,-2,-2,-2,-2,-3,-3,-2,8,-2,-3,-2,-2,-3,-3,-3,-3,-3,-3,-2,9,-2,-3,-2,-2,-2,-2,-2,-2,-2,-2,10,-2,-3,-2,-2,16,-2,-3,-2,-2,16,-2,-3,-2,17,-2,-2,-2},{41,-3,-3,-3,-3,-3,-3,-3,-2,-3,-3,-3,-3,-3,-3,-3,5,-3,13,-3,5,-3,13,-3,5,-3,13,-3,5,-3,3,-3,-3,-3,-2,-3,-3,-3,3,-3,5,-3,3,-3,5,-3,3,-3,5,-3,3,-3,5,-2,3,-2,5,-3,3,-3,3,-3,1,-3,3,-3,5,-2,3,-2,3,-3,-2,-3,3,-3,5,-3,3,-3,9,-3,5,-3,3,-3,9,-3,5,-3,3,-3,9,-3,5,-3,3,-3,-3,-3,-3,-3,-2,-3,-3,-3,-3,-3,5,-3,19,-3,19,-3,19,-3},{55,-2,19,-3,19,-3,19,-3,14,-2,-3,-3,-3,-3,-2,5,-2,-3,-3,-3,-3,-3,-2,2,-3,10,-3,5,-3,2,-3,10,-3,5,-3,2,-3,10,-3,5,-2,-3,-3,-2,10,-3,19,-3,19,-2,-3,-3,-3,-3,-3,-3,-3,-2,19,-3,19,-3,19,-3,19,-3,19,-2},{23,-4,13,-4,5,-2,-3,-3,-3,-3,-3,-3,-3,-3,-3,-2,-3,-3,-3,-2,5,-3,9,-3,3,-3,5,-4,9,-3,3,-3,4,-4,10,-3,3,-3,4,-2,-3,-3,-3,-3,-3,-3,-2,-3,-3,-3,-2,3,-3,4,-3,6,-3,3,-3,3,-3,4,-3,3,-4,2,-3,1,-4,-3,-2,-3,-4,1,-4,4,-3,3,-3,2,-3,11,-4,-2,-3,-3,-3,-2,1,-3,-2,1,-4,-3,-3,-2,-3,-3,-3,-4,7,-4,2,-3,4,-3,8,-4,5,-3,4,-3,8,-2,-3,-3,-3,-3,-3,-2,-3,-3,-3,-3,-2,8,-3,10,-3,8,-3,10,-3,8,-3,10,-3,6,-4,-3,-2,-3,-3,-3,-3,-4,5,-3,19,-4},{20,-3,-3,2,-3,1,-3,13,-3,1,-3,3,-3,-3,3,-3,2,-3,-3,1,-3,1,-3,-3,-3,2,-3,1,-3,1,-3,1,-3,1,-3,1,-3,1,-3,1,-3,1,-3,1,-3,1,-3,1,-3,1,-3,1,-3,-3,-3,1,-3,-3,2,-3,1,-3,-3,2,-3,1,-3,1,-3,1,-3,1,-3,1,-3,1,-3,1,-3,23,-3,-2,-1,-1,-3,-3,-3,-3,-3,-3,10,-3,8,-1,10,-3,1,-3,-2,5,-1,10,-3,1,-2,6,-2,10,-3,1,-2,6,-3,10,-3,1,-1,6,-3,10,-3,1,-3,-3,-3,-3,-1,-2,-2,-3,10,-1,19,-1,19,-1,19,-3}};
	static final int starts[] = {253
	,289
	,63
	,349
	,329
	,326
	,361
	,369
	,208
	,342
	};
	static final int ends[] = {189
	,109
	,63
	,126
	,50
	,56
	,189
	,55
	,208
	,185
	};
	//static final int numsOfButtons[] = {0,0,24,16};
	//static final int pars[] = {2,3,3,10};
	//int scores[] = {0,0,0,0};
	//static final int starts[] = {253,289,63,208};
	//static final int ends[] = {189,109,63,208};
	int currentLevel = 0;
	//int pressedButtons = 0;
	//boolean endOpen = false;
	
	//int startCell;
	//int endCell;
	//static final int startDirs[][]={{0,-1},{0,-1},{0,-1},{1,0}};
//	double pos[]={0,0}, vel[]={0,0};
//	int dir[]={0,-1};
	
	static final int ballSpeed = 1;//5 for fastforward, 1 for normal
	static final int fastFwrdMult = 5;
	static final int bRad = 6;
	//boolean moving;
	
	//boolean step;
//	boolean play = false;
//	boolean fastfwrd = false;
	
//	boolean reachedEnd;
//	boolean outOfBounds;
	boolean reset;
//	boolean createNewGame;
	
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
	static final int bezierOffset = 40;
	//int progPos;
	//boolean execute;
	
//	//Colours
//	static final Color clrBG = Color.black;
//	static final Color clrLines = Color.darkGray;
//	//Color clrDimRed = new Color(0x80FF0000,true);
//	//Color clrDimGreen = new Color(0x8000FF00,true);
//	//Color clrDimBlue = new Color(0x800000FF,true);
//	static final Color clrSel = Color.yellow;
//	static final Color clrText = Color.white;
//	static final Color clrBall = Color.white;
//	
//	static final Color clrLevel[] = {Color.gray, new Color(96,0,0), new Color(0,96,0), new Color(0,0,128)};

	public void run() {
		int w = 800, h = 600;
		int x,y,temp=0;
		setSize(w, h); // For AppletViewer, remove later.
		
		//extract levels from levelData
//		for (int i=0; i<numOfLevels; i++){
//			x=0;
//			System.out.println("level: "+i);
//			for (int j=0; j<levelData[i].length;j++){
//				if (levelData[i][j]>0){
//					System.out.println("x and levelData[i][j]:");
//					System.out.println(x);
//					System.out.println(levelData[i][j]);
//					for (y=x; y<x+levelData[i][j];y++){//put zeroes in
//						levels[i][y]=0;
//					}
//					x=y;
//				} else {
//					System.out.println("putting data at: "+x);
//					levels[i][x]=-levelData[i][j];
//					x++;
//				}
//			}
//		}

		// Set up the graphics stuff, double-buffering.
		BufferedImage screen = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) screen.getGraphics();
		Graphics2D appletGraphics = (Graphics2D) getGraphics();
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		BufferedImage cursor = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) cursor.getGraphics();
		g.setColor(Color.cyan);
		g.drawLine(0, 6, 12, 6);
		g.drawLine(6, 0, 6, 12);
		setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursor , new Point(6,6), null));
		
		//AffineTransform tempTrans = new AffineTransform();
		
		enableEvents(MouseEvent.MOUSE_EVENT_MASK | MouseEvent.MOUSE_MOTION_EVENT_MASK/* | KeyEvent.KEY_EVENT_MASK*/);
		
		//Random rand = new Random();

		//ball vars
		double dest[] = {0,0};
		int currentCell = 0;
		
		// other vars
		int scores[] = new int[numOfLevels];
		int side;
		
		//FontMetrics fm;
		//Rectangle2D rect;
		
		AffineTransform identity = new AffineTransform();
		
		BasicStroke thinStroke = new BasicStroke(1);
		BasicStroke medStroke = new BasicStroke(2);
		
		int xpoints[] = {-5,0,5};
		int ypoints[] = {0,-10,0};
		Polygon triangle = new Polygon(xpoints, ypoints, 3);
		
		//Font normalFont = new Font("SansSerif", Font.PLAIN, 16);
		
		
		// constants
		int pars[] = {2
				,3
				,3
				,5
				,7
				,4
				,6
				,7
				,10
				,18
				};
		int numsOfButtons[] = {0
				,0
				,24
				,0
				,0
				,1
				,0
				,0
				,16
				,0
				};
		int startDirs[][]={{0,-1}
				,{0,-1}
				,{0,-1}
				,{0,-1}
				,{0,-1}
				,{0,-1}
				,{0,-1}
				,{0,-1}
				,{1,0}
				,{0,-1}};
		
		// fonts
		//Font normalFont = new Font("SansSerif", Font.PLAIN, 16);
		//Font largeFont = new Font("SansSerif", Font.PLAIN, 60);
		//Font mediumFont = new Font("SansSerif", Font.PLAIN, 30);
		//Font hugeFont = new Font("SansSerif", Font.PLAIN, 120);
		
		g2d.setFont(new Font(null, Font.PLAIN, 16));
		
		//Colours
		Color clrBG = Color.black;
		Color clrLines = Color.darkGray;
		//Color clrDimRed = new Color(0x80FF0000,true);
		//Color clrDimGreen = new Color(0x8000FF00,true);
		//Color clrDimBlue = new Color(0x800000FF,true);
		Color clrSel = Color.yellow;
		Color clrText = Color.white;
		Color clrBall = Color.white;
		
		Color clrLevel[] = {Color.gray, new Color(96,0,0), new Color(0,96,0), new Color(0,0,128)};

		// Some variables to use for the fps.
		int /*tick = 0, fps = 0,*/ acc = 0;
		long lastTime = System.nanoTime();
		
		int frameNum = 0;
		int stepDelay = 0;
		int pressedButtons = 0;
		boolean endOpen = false;
		int pos[]=new int[2], vel[]=new int[2];
		int dir[] = new int[2];
		boolean moving=false;
		boolean step=false;
		
		boolean reachedEnd=false;
		boolean outOfBounds;
		boolean createNewGame;
		
		int progPos=0;
		//boolean execute;
		
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
			//tick++;
			if (acc >= 1000000000L) {
				acc -= 1000000000L;
				//fps = tick;
				//tick = 0;
			}
			//
			//game update
			//
			
			if (createNewGame || reset){ //reset game
				if (reachedEnd){
					//if (++currentLevel>=numOfLevels) currentLevel=0;
					scores[currentLevel]=0;
					for (x=0;x<numOfPrgBoxes;x++){
						if (programs[currentLevel][x]!=0 || toploops[currentLevel][x]!=0 || botloops[currentLevel][x]!=0) scores[currentLevel]++;
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
				//execute=false;
				playMode=0;
				pressedButtons = 0;
				endOpen = false;
				for (x=0; x<numOfCells*numOfCells;x++){
					if (levels[currentLevel][x]>6) levels[currentLevel][x]-=3;
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
			
			
			//g2d.setColor(clrText);
			//g2d.setFont(normalFont);
			//g2d.drawString("FPS " + String.valueOf(fps), 10, h-10);
			
			//draw the ball area
			//draw the grid
			g2d.setColor(clrLines);
			for (x=0;x<=numOfCells;x++){
				g2d.drawLine(0, x*cellWidth, numOfCells*cellWidth, x*cellWidth);
				g2d.drawLine(x*cellWidth, 0, x*cellWidth, numOfCells*cellWidth);
			}
			//draw the coloured squares
			for (x=0; x<numOfCells*numOfCells;x++){
				if (levels[currentLevel][x]>0){
					g2d.setColor(clrLevel[((levels[currentLevel][x]-1)%3+1)]);
					g2d.fillRect((x%numOfCells)*cellWidth+1, (x/numOfCells)*cellWidth+1, cellWidth-1, cellWidth-1);
					if (levels[currentLevel][x]>6){//draw the pressed buttons
						g2d.setColor(clrSel);
						g2d.fillOval((x%numOfCells)*cellWidth+cellWidth/2-bRad, (x/numOfCells)*cellWidth+cellWidth/2-bRad, bRad*2+1, bRad*2+1);
					}
					else if (levels[currentLevel][x]>3){//draw the unpressed buttons
						g2d.setColor(clrLines);
						g2d.fillOval((x%numOfCells)*cellWidth+cellWidth/2-bRad, (x/numOfCells)*cellWidth+cellWidth/2-bRad, bRad*2+1, bRad*2+1);
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
			for (temp=0; temp<numOfPrgBoxes; temp++){
				x=((temp%prgBoxArrayWidth)+1)*prgBoxSpacing+(temp%prgBoxArrayWidth)*prgBoxSize+numOfCells*cellWidth;
				y=((temp/prgBoxArrayWidth)+1)*prgBoxSpacing+(temp/prgBoxArrayWidth)*prgBoxSize;
				g2d.translate(x, y);
//				if (prgrmclrs[currentLevel][temp]>0){// 0: blank, 1: red, 2: green, 3: blue
//					g2d.setColor(clrLevel[prgrmclrs[currentLevel][temp]]);
//					g2d.fillRect(0, 0, prgBoxSize, prgBoxSize);
//				}
				if (progPos == temp) g2d.setColor(clrSel);
				else g2d.setColor(clrLines);
				g2d.drawRect(0, 0, prgBoxSize, prgBoxSize);
				//if (mx>x && mx<x+prgBoxSize && my>y-prgBoxSideClickWidth && my<y+prgBoxSize+prgBoxSideClickWidth) g2d.drawRect(0, -prgBoxSideClickWidth, prgBoxSize, prgBoxSize+2*prgBoxSideClickWidth);
				if (mx>x && mx<x+prgBoxSize && my>y-prgBoxSideClickWidth && my<y)
					g2d.drawRect(0, -prgBoxSideClickWidth, prgBoxSize, prgBoxSideClickWidth);
				if (mx>x && mx<x+prgBoxSize && my>y+prgBoxSize && my<y+prgBoxSize+prgBoxSideClickWidth)
					g2d.drawRect(0, prgBoxSize, prgBoxSize, prgBoxSideClickWidth);
				//tempTrans = g2d.getTransform();
				g2d.translate(prgBoxSize/2, prgBoxSize/2);
				g2d.setColor(clrLevel[prgrmclrs[currentLevel][temp]]);
				// 0: nop, 1: return, 2: forward, 3: left, 4: right
				//System.out.println(temp);
				if (programs[currentLevel][temp]==3){
					g2d.fillRect(5, 0, 2, 7);
					g2d.rotate(-1.570796327);// -pi/2
				} else if (programs[currentLevel][temp]==4){
					g2d.fillRect(-7, 0, 2, 7);
					g2d.rotate(1.570796327); //  pi/2
				}
				if (programs[currentLevel][temp]>1){
					g2d.fill(triangle);
					g2d.fillRect(-1, 0, 2, 7);
				}
				if (programs[currentLevel][temp]==1){
					g2d.drawString("R", -5, 5);
				}
				
				g2d.setTransform(identity);
			}
			
			//draw beziers
			for (x=0; x<numOfPrgBoxes; x++){
				g2d.translate(((x%prgBoxArrayWidth)+1)*prgBoxSpacing+(x%prgBoxArrayWidth)*prgBoxSize+numOfCells*cellWidth+prgBoxSize/2, ((x/prgBoxArrayWidth)+1)*prgBoxSpacing+(x/prgBoxArrayWidth)*prgBoxSize);
				int ti;
				int tx;
				int ty;
				side=1;
				g2d.setStroke(medStroke);
				if (toploops[currentLevel][x]!=0){
					g2d.setColor(clrLevel[tplpclrs[currentLevel][x]]);
					g2d.scale(1, 0.5);
					g2d.fill(triangle);
					g2d.scale(1, 2);
					ti=(Math.abs(toploops[currentLevel][x])-1);
					tx=(ti%prgBoxArrayWidth-x%prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					ty=(ti/prgBoxArrayWidth-x/prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					if (toploops[currentLevel][x]<0){
						side=-1;
						ty+=prgBoxSize+2;
					}
					temp = bezierOffset*(1+Math.abs(ti/prgBoxArrayWidth-x/prgBoxArrayWidth));
					g2d.draw(new CubicCurve2D.Double(0, -2, 0, -temp, tx, ty-side*temp, tx, ty-1));
				}
				g2d.translate(0, prgBoxSize+1);
				side=-1;
				if (botloops[currentLevel][x]!=0){
					g2d.setColor(clrLevel[btlpclrs[currentLevel][x]]);
					g2d.scale(1, -0.5);
					g2d.fill(triangle);
					g2d.scale(1, -2);
					ti=(Math.abs(botloops[currentLevel][x])-1);
					tx=(ti%prgBoxArrayWidth-x%prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					ty=(ti/prgBoxArrayWidth-x/prgBoxArrayWidth)*(prgBoxSpacing+prgBoxSize);
					if (botloops[currentLevel][x]>0){
						side=1;
						ty-=prgBoxSize+2;
					}
					temp = bezierOffset*(1+Math.abs(ti/prgBoxArrayWidth-x/prgBoxArrayWidth));
					g2d.draw(new CubicCurve2D.Double(0, 1, 0, temp, tx, ty-side*temp, tx, ty));
				}
				g2d.setStroke(thinStroke);
				g2d.setTransform(identity);
			}
			
			//draw bezier to mouse when dragging
			if (dragging){
				g2d.setStroke(medStroke);
				g2d.setColor(clrLevel[selClr]);
				temp = Math.abs(mDownBox)-1;
				side = 1;
				if (mDownBox<0) side = -1;
				x = ((temp%prgBoxArrayWidth)+1)*prgBoxSpacing+(temp%prgBoxArrayWidth)*prgBoxSize+numOfCells*cellWidth+prgBoxSize/2;
				y = ((temp/prgBoxArrayWidth)+1)*prgBoxSpacing+(temp/prgBoxArrayWidth)*prgBoxSize + (1-side)*prgBoxSize/2;
				//temp = bezierOffset*(1+2*Math.abs(my-y)/(prgBoxSize+prgBoxSpacing));
				g2d.draw(new CubicCurve2D.Double(x, y, x, y-side*bezierOffset*2, mx, my, mx, my));
				g2d.setTransform(identity);
				g2d.setStroke(thinStroke);
			}
			
			//draw the play control buttons
			for (x=0; x<5; x++){
				g2d.setColor(clrLines);
				if (playMode+1==x) g2d.setColor(clrSel);
				g2d.translate((x+1)*(playBtnSpacing+playBtnSize), numOfCells*cellWidth+playBtnSpacing);
				g2d.drawRect(0, 0, playBtnSize, playBtnSize);
				g2d.translate(playBtnSize/2, playBtnSize/2);
				g2d.setStroke(medStroke);
				//switch (x){
				if (x==0){//back to start
					g2d.drawLine(-10, -5, -10, 4);
					g2d.rotate(-1.570796327);
					g2d.fill(triangle);
					g2d.translate(0, 9);
					g2d.fill(triangle);
				}
				if (x==1){//pause
					g2d.drawLine(-5, -5, -5, 4);
					g2d.drawLine(5, -5, 5, 4);
				}
				if (x==2){//single step
					g2d.translate(-5, 0);
					g2d.drawLine(10, -5, 10, 4);
					g2d.rotate(1.570796327);
					g2d.fill(triangle);
				}
				if (x==3){//play
					g2d.translate(-5, 0);
					g2d.rotate(1.570796327);
					g2d.fill(triangle);
				}
				if (x==4){//fast forward
					g2d.rotate(1.570796327);
					g2d.fill(triangle);
					g2d.translate(0, 9);
					g2d.fill(triangle);
				}
				//}
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
			for (x=0;x<4;x++){
				g2d.setColor(clrLevel[x]);
				g2d.fillRect((x%2)*cellWidth, (x/2)*cellWidth, cellWidth, cellWidth);
				g2d.setColor(clrLines);
				g2d.drawRect((x%2)*cellWidth, (x/2)*cellWidth, cellWidth, cellWidth);
			}
			g2d.setColor(clrSel);
			g2d.drawRect((selClr%2)*cellWidth, (selClr/2)*cellWidth, cellWidth, cellWidth);
			g2d.setTransform(identity);
			
			//draw the operation selection buttons
			for (temp=0;temp<5;temp++){// 0: nop, 1: return, 2: forward, 3: left, 4: right
				x=2*prgBoxSpacing+temp*(prgBoxSpacing-prgBoxSize)/2+(temp+1)*prgBoxSize+numOfCells*cellWidth;
				y=numOfCells*cellWidth+playBtnSpacing + (cellWidth*2 - prgBoxSize)/2;
				g2d.translate(x, y);
				g2d.setColor(clrLines);
				if (temp==selOp) g2d.setColor(clrSel);
				g2d.drawRect(0, 0, prgBoxSize, prgBoxSize);
				g2d.translate(prgBoxSize/2, prgBoxSize/2);
				if (temp==3){
					g2d.fillRect(5, 0, 2, 7);
					g2d.rotate(-1.570796327);// -pi/2
				}
				if (temp==4){
					g2d.fillRect(-7, 0, 2, 7);
					g2d.rotate(1.570796327); //  pi/2
				}
				if (temp>1){
					g2d.fill(triangle);
					g2d.fillRect(-1, 0, 2, 7);
				}
				if (temp==1){
					g2d.drawString("R", -5, 5);
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
	
//	public void processKeyEvent(KeyEvent e) {
//		int x,y,temp;
//		switch (e.getKeyCode()){
//		case KeyEvent.VK_S:
//			String s = "{";
//			for (x=0; x<numOfCells*numOfCells;x++){
//				s=s+levels[currentLevel][x]+",";
//			}
//			s=s+"}";
//			System.out.println(s);
//			System.out.println("start: "+starts[currentLevel]);
//			System.out.println("end: "+ends[currentLevel]);
//			break;
//		case KeyEvent.VK_B:
//			if (editMode){
//				for (temp=0; temp<numOfCells*numOfCells;temp++){
//					x = (temp%numOfCells)*cellWidth;
//					y = (temp/numOfCells)*cellWidth;
//					if (mx>x && mx<x+cellWidth && my>y && my<y+cellWidth){
//						if (selClr!=0) levels[currentLevel][temp] = selClr+3;
//					}
//				}
//			}
//			break;
////		case Event.UP:
////			if (!moving) step = true;
////			break;
////		case Event.LEFT:
////			if (!moving){
////				int temp=dir[0];
////				dir[0]=dir[1];
////				dir[1]=-temp;
////			}
////			break;
////		case Event.RIGHT:
////			if (!moving){
////				int temp=dir[0];
////				dir[0]=-dir[1];
////				dir[1]=temp;
////			}
////			break;
//		}
//	}
	
	public void processMouseEvent(MouseEvent e) {
		int x,y,temp;
		mx = e.getX();
		my = e.getY();
		if (e.getID() == MouseEvent.MOUSE_PRESSED){
			if (e.getButton() == MouseEvent.BUTTON1) mousedown=true;
			// in editmode check for clicks in the grid
//			if (editMode){
//				for (temp=0; temp<numOfCells*numOfCells;temp++){
//					x = (temp%numOfCells)*cellWidth;
//					y = (temp/numOfCells)*cellWidth;
//					if (mx>x && mx<x+cellWidth && my>y && my<y+cellWidth){
//						if (e.getButton() == MouseEvent.BUTTON1){
//							levels[currentLevel][temp] = selClr;
//						}
//						if (e.getButton() == MouseEvent.BUTTON2){//start
//							starts[currentLevel]=temp;
//						}
//						if (e.getButton() == MouseEvent.BUTTON3){//end
//							ends[currentLevel]=temp;
//						}
//					}
//				}
//			}
			
			//System.out.println("mouse click at x="+e.x+", y="+e.y);
			//check for clicks on the play control buttons
			for (temp=0; temp<5; temp++){
				//g2d.translate((i+1)*(playBtnSpacing+playBtnSize), numOfCells*cellWidth+playBtnSpacing);
				//g2d.drawRect(0, 0, playBtnSize, playBtnSize);
				x = (temp+1)*(playBtnSpacing+playBtnSize);
				y = numOfCells*cellWidth+playBtnSpacing;
				//System.out.println("checking for button at x="+x+", y="+y);
				if (mx>x && mx<x+playBtnSize && my>y && my<y+playBtnSize){
					//System.out.println("playmode set to "+(i-1));
					playMode = temp-1;
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
			for (temp=0;temp<4;temp++){
				x = prgBoxSpacing+numOfCells*cellWidth + (temp%2)*cellWidth;
				y = numOfCells*cellWidth+playBtnSpacing + (temp/2)*cellWidth;
				if (mx>x && mx<x+cellWidth && my>y && my<y+cellWidth ){
					selClr=temp;
				}
//				g2d.setColor(clrLevel[i]);
//				g2d.fillRect((i%2)*cellWidth, (i/2)*cellWidth, cellWidth, cellWidth);
//				g2d.setColor(clrLines);
//				g2d.drawRect((i%2)*cellWidth, (i/2)*cellWidth, cellWidth, cellWidth);
			}
			
			//check the operation selection buttons
			for (temp=0;temp<5;temp++){// 0: nop, 1: return, 2: forward, 3: left, 4: right
				x=2*prgBoxSpacing+temp*(prgBoxSpacing-prgBoxSize)/2+(temp+1)*prgBoxSize+numOfCells*cellWidth;
				y=numOfCells*cellWidth+playBtnSpacing + (cellWidth*2 - prgBoxSize)/2;
				if (mx>x && mx<x+prgBoxSize && my>y && my<y+prgBoxSize){
					selOp=temp;
				}
			}
			
			//check for clicks in the program boxes
			for (temp=0; temp<numOfPrgBoxes; temp++){
				x=((temp%prgBoxArrayWidth)+1)*prgBoxSpacing+(temp%prgBoxArrayWidth)*prgBoxSize+numOfCells*cellWidth;
				y=((temp/prgBoxArrayWidth)+1)*prgBoxSpacing+(temp/prgBoxArrayWidth)*prgBoxSize;
				if (mx>x && mx<x+prgBoxSize && my>y && my<y+prgBoxSize){
					if (e.getButton() == MouseEvent.BUTTON1){
						if (selOp!=0) programs[currentLevel][temp]=selOp;
						prgrmclrs[currentLevel][temp]=selClr;
					} else {
						programs[currentLevel][temp]=0;
						prgrmclrs[currentLevel][temp]=0;
					}
				}
			}
			
			for (temp=0; temp<numOfPrgBoxes; temp++){
				x=((temp%prgBoxArrayWidth)+1)*prgBoxSpacing+(temp%prgBoxArrayWidth)*prgBoxSize+numOfCells*cellWidth;
				y=((temp/prgBoxArrayWidth)+1)*prgBoxSpacing+(temp/prgBoxArrayWidth)*prgBoxSize;
				if (mx>x && mx<x+prgBoxSize && my>y-prgBoxSideClickWidth && my<y){
					if (e.getButton() == MouseEvent.BUTTON1){
						mDownBox=temp+1;
						dragging=true;
					} else {
						toploops[currentLevel][temp]=0;
					}
				}
				if (mx>x && mx<x+prgBoxSize && my>y+prgBoxSize && my<y+prgBoxSize+prgBoxSideClickWidth){
					if (e.getButton() == MouseEvent.BUTTON1){
						mDownBox=-temp-1;
						dragging=true;
					} else {
						botloops[currentLevel][temp]=0;
					}
				}
			}
		}
		if (e.getID() == MouseEvent.MOUSE_RELEASED){
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
//		int x,y,temp;
//		if (editMode && mousedown){
//			for (temp=0; temp<numOfCells*numOfCells;temp++){
//				x = (temp%numOfCells)*cellWidth;
//				y = (temp/numOfCells)*cellWidth;
//				if (mx>x && mx<x+cellWidth && my>y && my<y+cellWidth){
//					levels[currentLevel][temp] = selClr;
//				}
//			}
//		}
	}
}