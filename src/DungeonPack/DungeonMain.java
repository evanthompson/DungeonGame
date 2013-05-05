package DungeonPack;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DungeonMain {
	
	public enum Compass { NORTH, SOUTH, EAST, WEST }
	
	public static ArrayList<Color> cleanUp;
	public static Color dGray, gray, lGray;
	
	public static void main(String[] args) {
		
		final DungeonGame newGame = new DungeonGame();
		cleanUp = new ArrayList<Color>(10);
		
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Dungeon");
		
		makeColors(display);
		
		GridLayout layout;
		GridData data;
		layout = new GridLayout();
		layout.numColumns = 2;
		shell.setBackground(dGray);
		shell.setLayout(layout);
		
		// Floor Initialization
		final Composite floor = new Composite(shell, SWT.NONE);
		floor.setBackground(lGray);
		
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		data.heightHint = 500;
		data.widthHint = 500;
		floor.setLayoutData(data);
		
		// Menu Initialization
		final Composite menu = new Composite(shell, SWT.NONE);
		menu.setBackground(lGray);
		data = new GridData();
		data.horizontalAlignment = SWT.END;
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		data.minimumHeight = 400;
		data.widthHint = 200;
		menu.setLayoutData(data);
		
		// Paint Listener ///////////
		floor.addListener(SWT.Paint, new Listener () {
			public void handleEvent (Event event) {
				ArrayList<GameObject> objects = newGame.getFloor().getObjects();
				int unit = DungeonFloor.UNIT_SIZE;
				for(GameObject obj : objects) {
					String life = "";
					if(obj instanceof Mob) {
						event.gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_RED));
						event.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
						life = ((Mob) obj).getCurrHealth() + " / " + ((Mob) obj).getMaxHealth();
					}
					else if(obj instanceof InanimateObject) {
						event.gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
						life = "*";
					} else {
						event.gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
					}
					event.gc.fillRectangle(obj.getXpos(), obj.getYpos(), unit, unit);
					event.gc.drawText(life, obj.getXpos() + 5, obj.getYpos() + 5);
				}
				
				Hero hero = newGame.getHero();
				event.gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
				event.gc.fillRectangle(hero.getXpos(), hero.getYpos(), unit, unit);
				
				
				int width = unit / 4;
				int x, y;
				
				switch(hero.getDirection()) {
				case NORTH:	x = (int)(hero.getXpos() + (unit / 2) - (width / 2));
							y = hero.getYpos();
							break;
				case SOUTH:	x = (int)(hero.getXpos() + (unit / 2) - (width / 2));
							y = hero.getYpos() + unit - width;
							break;
				case WEST:	x = hero.getXpos();
							y = (int)(hero.getYpos() + (unit / 2) - (width / 2));
							break;
				case EAST:	x = hero.getXpos() + unit - width;
							y = (int)(hero.getYpos() + (unit / 2) - (width / 2));
							break;
				default:	x = y = 0; break;
				}
				
				event.gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
				event.gc.fillOval(x, y, width, width);
			}
		});
		
		menu.addListener(SWT.Paint, new Listener () {
			public void handleEvent (Event event) {
				Hero hero = newGame.getHero();
				event.gc.drawText(hero.getXpos() + "," + hero.getYpos(), 10, 10);
			}
		});
		
		// Key Listener ////////////
		shell.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 97) {
					newGame.getFloor().desiredMove(Compass.WEST, newGame.getHero());
				}
				else if(e.keyCode == 100) {
					newGame.getFloor().desiredMove(Compass.EAST, newGame.getHero());
				}
				else if(e.keyCode == 119) {
					newGame.getFloor().desiredMove(Compass.NORTH, newGame.getHero());
				}
				else if(e.keyCode == 115) {
					newGame.getFloor().desiredMove(Compass.SOUTH, newGame.getHero());
				}
				else if(e.keyCode == 122) { // Z
					System.out.println("attacking");
					newGame.attack();
				}
				else if(e.keyCode == 120) { // X
					System.out.println(e.keyCode);
				}
				else if(e.keyCode == 99) { // C
					System.out.println(e.keyCode);
				}
				
				floor.redraw();
				menu.redraw();
			}
			
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// Dispose Listener
		shell.addDisposeListener(new DisposeListener () {
			public void widgetDisposed(DisposeEvent e) {
				for(Color c : cleanUp) {
					c.dispose();
				}
			}
		});
		
		
		shell.pack();
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		newGame.getThread().killProc();
		display.dispose();
	}
	
	public static void makeColors(Display display) {
		dGray = new Color(display, 30, 30, 30);
		gray = new Color(display, 150, 150, 150);
		lGray = new Color(display, 200, 200, 200);
		
		cleanUp.add(dGray);
		cleanUp.add(gray);
		cleanUp.add(lGray);
	}
}
