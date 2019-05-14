import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.centralnexus.input.Joystick;
import com.centralnexus.input.JoystickListener;


public class Engine {
	
	private static final int[] buttonConstants = {Joystick.BUTTON1,Joystick.BUTTON2,Joystick.BUTTON3,Joystick.BUTTON4,Joystick.BUTTON5,Joystick.BUTTON6,Joystick.BUTTON7,Joystick.BUTTON8,Joystick.BUTTON9,Joystick.BUTTON10,Joystick.BUTTON11,Joystick.BUTTON12,Joystick.BUTTON13,Joystick.BUTTON14,Joystick.BUTTON15,Joystick.BUTTON16,Joystick.BUTTON17,Joystick.BUTTON18,Joystick.BUTTON19,Joystick.BUTTON20,Joystick.BUTTON21,Joystick.BUTTON22,Joystick.BUTTON23,Joystick.BUTTON24,Joystick.BUTTON25,Joystick.BUTTON26,Joystick.BUTTON27,Joystick.BUTTON28,Joystick.BUTTON29,Joystick.BUTTON30,Joystick.BUTTON31,Joystick.BUTTON32};
	private String currentProfile;
	private Robot robot;

	private boolean changeProfilePressed;
	private String lastProfile;
	private String changeProfileButton;
	
	private Joystick joyStick;
	private Properties properties;
	
	public Engine(Properties prop, Joystick joy) throws AWTException, IOException{
		currentProfile = "0";
		robot = new Robot();
		
		joyStick = joy;
		properties = prop;
		
		changeProfilePressed = false;
		lastProfile = currentProfile;
		changeProfileButton = "";
		
		joyStick.addJoystickListener(new JoystickListener() {
			
			@Override
			public void joystickButtonChanged(Joystick arg0) {
				
				for(int i=0;i<joyStick.getNumButtons();i++){
					receiveJoystickEvent("BUTTON"+(i+1), joyStick.isButtonDown(buttonConstants[i]));
				}
				
			}
			
			@Override
			public void joystickAxisChanged(Joystick arg0) {
				
				//check X axes
				if(joyStick.getX()>0.1){
					receiveJoystickEvent("X+", true);
				}else{
					receiveJoystickEvent("X+", false);
				}
				
				if(joyStick.getX()<-0.1){
					receiveJoystickEvent("X-", true);
				}else{
					receiveJoystickEvent("X-", false);
				}
				
				//check Y axes
				if(joyStick.getY()>0.1){
					receiveJoystickEvent("Y+", true);
				}else{
					receiveJoystickEvent("Y+", false);
				}
				
				if(joyStick.getY()<-0.1){
					receiveJoystickEvent("Y-", true);
				}else{
					receiveJoystickEvent("Y-", false);
				}
				
				//check the rest of the axes
				if(joyStick.getCapability(Joystick.HAS_R)){
					
					if(joyStick.getR()>0.1){
						receiveJoystickEvent("R+", true);
					}else{
						receiveJoystickEvent("R+", false);
					}
					
					if(joyStick.getR()<-0.1){
						receiveJoystickEvent("R-", true);
					}else{
						receiveJoystickEvent("R-", false);
					}
					
				}
				
				if(joyStick.getCapability(Joystick.HAS_Z)){
					
					if(joyStick.getZ()>0.1){
						receiveJoystickEvent("Z+", true);
					}else{
						receiveJoystickEvent("Z+", false);
					}
					
					if(joyStick.getZ()<-0.1){
						receiveJoystickEvent("Z-", true);
					}else{
						receiveJoystickEvent("Z-", false);
					}
					
				}
				
				if(joyStick.getCapability(Joystick.HAS_V)){
					
					if(joyStick.getV()>0.1){
						receiveJoystickEvent("V+", true);
					}else{
						receiveJoystickEvent("V+", false);
					}
					
					if(joyStick.getV()<-0.1){
						receiveJoystickEvent("V-", true);
					}else{
						receiveJoystickEvent("V-", false);
					}
					
				}
				
				if(joyStick.getCapability(Joystick.HAS_U)){
					
					if(joyStick.getU()>0.1){
						receiveJoystickEvent("U+", true);
					}else{
						receiveJoystickEvent("U+", false);
					}
					
					if(joyStick.getU()<-0.1){
						receiveJoystickEvent("U-", true);
					}else{
						receiveJoystickEvent("U-", false);
					}
					
				}
				
			}
		});
		setTrayIcon();
		
	}
	
	private void setTrayIcon(){
		TrayIcon trayIcon = null;
	     if (SystemTray.isSupported()) {
	         
	    	 SystemTray tray = SystemTray.getSystemTray();
	         PopupMenu popup = new PopupMenu();
	         
	         MenuItem exitItem = new MenuItem("Exit");
	         exitItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					System.exit(0);
				}
			});
	         popup.add(exitItem);
	         Image img = null;
	         try{
	        	 img = (Image)ImageIO.read(new FileInputStream(new File("tray.png")));
	         }catch (Exception e) {
	        	 JOptionPane.showMessageDialog(null, "Couldn't create the tray icon. To kill the application kill Java via task manager.");
	        	 return; 
	         }
	         try {
				trayIcon = new TrayIcon( img , "Java JoyToKey", popup);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "Couldn't create the tray icon. To kill the application kill Java via task manager.");
				return;
			}
	         try {
	             tray.add(trayIcon);
	         } catch (AWTException e) {
	        	 JOptionPane.showMessageDialog(null, "Couldn't create the tray icon. To kill the application kill Java via task manager.");
	        	 return;
	         }

	     } else {
	         JOptionPane.showMessageDialog(null, "Your system does not support docking the program to Tray. Please kill java when you are done using the application.");
	     }
	}
	
	public void receiveJoystickEvent(String name, boolean isPressed){
		String key = properties.getProperty(currentProfile+"."+name);//get the property of this button
		if(isPressed){//if this button is pressed
			if(key!=null && key.startsWith("PROFILE") && !changeProfilePressed){//if this button is supposed to change to a profile, and the user is NOT pressing another button that changes profiles, then: (we check that the user is not pressing another button of profiles because otherwise it would cause a conflict)
				String profileChangeDetails[] = key.split(":");//read the profile data
				changeProfilePressed = Boolean.parseBoolean(profileChangeDetails[2]);//check if the user ticked tha change back option in the ConfigurationWindow
				if(changeProfilePressed){
					lastProfile = currentProfile;
					changeProfileButton = name;//save last profile and the name of the profile to be used when the user releases the button
				}
				currentProfile = profileChangeDetails[1];
			}else if(key!=null){
				robot.keyPress(Integer.parseInt(key));//if this button is just a normal keypress, then do the keypress
			}
			
		}else if(name.equals(changeProfileButton) && changeProfilePressed){//if the button is not pressed, then checked if this button is supposed to have been changing a profile, and the button is now released
			currentProfile = lastProfile;
			changeProfilePressed = false;
		}else if(key!=null){// just release the normal key		
			try{
				robot.keyRelease(Integer.parseInt(key));
			}catch (NumberFormatException e) {
				System.out.println("Couldn't parse key event "+key);
			}
		}
		
	}
	
}
