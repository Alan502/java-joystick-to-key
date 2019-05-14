import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.filechooser.FileFilter;

import com.centralnexus.input.Joystick;
import com.centralnexus.input.JoystickListener;

import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.Graphics;


public class ConfigurationPanel extends JPanel {
	
	private Joystick joyStick;
	private JScrollBar scrollBar;
	
	private JButton[] buttonsButtons;
	private AxisPanel axisPanel;
	
	private JPanel xyAxesPanel;
	
	private SimpleAxisPanel rAxis;
	private SimpleAxisPanel uAxis;
	private SimpleAxisPanel vAxis;
	private SimpleAxisPanel zAxis;
	
	private Properties properties;
	private int currentProfile;
	
	private static final int[] buttonConstants = {Joystick.BUTTON1,Joystick.BUTTON2,Joystick.BUTTON3,Joystick.BUTTON4,Joystick.BUTTON5,Joystick.BUTTON6,Joystick.BUTTON7,Joystick.BUTTON8,Joystick.BUTTON9,Joystick.BUTTON10,Joystick.BUTTON11,Joystick.BUTTON12,Joystick.BUTTON13,Joystick.BUTTON14,Joystick.BUTTON15,Joystick.BUTTON16,Joystick.BUTTON17,Joystick.BUTTON18,Joystick.BUTTON19,Joystick.BUTTON20,Joystick.BUTTON21,Joystick.BUTTON22,Joystick.BUTTON23,Joystick.BUTTON24,Joystick.BUTTON25,Joystick.BUTTON26,Joystick.BUTTON27,Joystick.BUTTON28,Joystick.BUTTON29,Joystick.BUTTON30,Joystick.BUTTON31,Joystick.BUTTON32};


	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	
	public ConfigurationPanel(int joyID) throws IOException {
		
		//check that the joyID is a valid ID
		if(joyID<Joystick.getNumDevices()){
			joyStick = Joystick.createInstance(joyID);
		}else{
			return;
		}
		
		properties = new Properties();
		
		//if it has POV capabilities, alert the user of support
		if(joyStick.getCapability(Joystick.HAS_POV4DIR) | joyStick.getCapability(Joystick.HAS_POV) | joyStick.getCapability(Joystick.HAS_POVCONT)){
			JOptionPane.showMessageDialog(this, "Sorry! But this program does not yet support the POV of your joystick. E-mail me if you want this feature at: alanrmorales@gmail.com");
		}
		
		setLayout(new BorderLayout(10, 10));
		
		JPanel buttonsPanel = new JPanel();
		add(buttonsPanel, BorderLayout.WEST);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
		//add the button actions, when a button is clicked a new Configuration Window is created
		buttonsButtons = new JButton[joyStick.getNumButtons()];
		for(int i=0;i<buttonsButtons.length;i++){
			final int j = i+1;
			final ConfigurationPanel panel = this;
			
			buttonsButtons[i] = new JButton("Button "+(i+1));
			buttonsButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					new ConfigurationWindow(panel, "BUTTON"+j);			
				}
			});
			buttonsPanel.add(buttonsButtons[i]);
		}
		
		//add a Joystick listener that will change the colors of the buttons when the button on the Joystick is pressed, so that the user knows which button is which
		joyStick.addJoystickListener(new JoystickListener() {
			public void joystickButtonChanged(Joystick arg0) {
				for(int i=0;i<buttonsButtons.length;i++){
					if(joyStick.isButtonDown(buttonConstants[i])){
						buttonsButtons[i].setBackground(Color.yellow);
					}else{
						buttonsButtons[i].setBackground(Color.white);
					}
				}
			}
			
			public void joystickAxisChanged(Joystick arg0) {
				
				if(axisPanel!=null)
					axisPanel.setCoordinates((int)((joyStick.getX()+1)*50), (int)((joyStick.getY()+1)*50));//update x and y axes
				
				if(xyAxesPanel!=null)
					xyAxesPanel.repaint();
				
				if(rAxis!=null)
					rAxis.setAxisValue(joyStick.getR());
	
				if(zAxis!=null)
					zAxis.setAxisValue(joyStick.getZ());
				
				if(vAxis!=null)
					vAxis.setAxisValue(joyStick.getV());
				
				if(uAxis!=null)
					uAxis.setAxisValue(joyStick.getU());
					
			}
		});
		
		xyAxesPanel = new JPanel();
		add(xyAxesPanel, BorderLayout.CENTER);
		xyAxesPanel.setLayout(new BoxLayout(xyAxesPanel, BoxLayout.Y_AXIS));
		
		JLabel joystickDeadzoneLabel = new JLabel("Joystick Deadzone");
		joystickDeadzoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		xyAxesPanel.add(joystickDeadzoneLabel);
		
		//make the scrollbar to set the deadzone of the joystick
		scrollBar = new JScrollBar();
		scrollBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				if(joyStick!=null)
					joyStick.setDeadZone(scrollBar.getValue()/100);
			}
		});
		
		scrollBar.setOrientation(JScrollBar.HORIZONTAL);
		xyAxesPanel.add(scrollBar);
		
		JLabel joystickNameLabel = new JLabel("Joystick Name");
		joystickNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		joystickNameLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		add(joystickNameLabel, BorderLayout.NORTH);
		
		//set the name of the joystick in the label
		joystickNameLabel.setText(joyStick.toString());
		
		//add y and x axes
		axisPanel = new AxisPanel();
		xyAxesPanel.add(axisPanel);
		
		JPanel otherAxesPanel = new JPanel();
		add(otherAxesPanel, BorderLayout.EAST);
		otherAxesPanel.setLayout(new BoxLayout(otherAxesPanel, BoxLayout.Y_AXIS));
		//make as many Simple Axis panels needed to every joystick axis
		if(joyStick.getCapability(Joystick.HAS_R)){
			rAxis = new SimpleAxisPanel("R");
			otherAxesPanel.add(rAxis);
		}
		if(joyStick.getCapability(Joystick.HAS_Z)){
			zAxis = new SimpleAxisPanel("Z");
			otherAxesPanel.add(zAxis);
		}
		if(joyStick.getCapability(Joystick.HAS_U)){
			uAxis = new SimpleAxisPanel("U");
			otherAxesPanel.add(uAxis);
		}
		if(joyStick.getCapability(Joystick.HAS_V)){
			vAxis = new SimpleAxisPanel("V");
			otherAxesPanel.add(vAxis);
		}

	}
	
	public void save() throws IOException{
		//let the user choose the file, use the joystick's name as a default name for the file
		JFileChooser chooser = new JFileChooser(new File(".").getAbsolutePath());
		chooser.setSelectedFile(new File(joyStick.toString().trim().substring(0, 10)+".jjk"));
		//see only .jjk files
		chooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return ".jjk files";
			}
			
			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".jjk");
			}
		});
				
		if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)//check the user did select a file
			return;
		
		File file = chooser.getSelectedFile();
		FileOutputStream output = new FileOutputStream(file);
		//store the properties object
		properties.store(output, "Saved settings for joystick");
		
		output.close();
	}
	
	public void load() throws IOException{
		//let the user choose the file, use the joystick's name as a default name for the file
		JFileChooser chooser = new JFileChooser(new File(".").getAbsolutePath());
		chooser.setSelectedFile(new File(joyStick.toString().substring(0, 7)+".jjk"));
		//see only jjk files
		chooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return ".jjk files";
			}
			
			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".jjk");
			}
		});
		
		if(chooser.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION)//check the user did select a file
			return;
		
		File file = chooser.getSelectedFile();
		FileInputStream input = new FileInputStream(file);
		//load the properties object
		properties.load(input);
		
		input.close();
		
	}
	public boolean reset(){
		//ask the user if he wants to save the configuration, if he answers cancel (2) return false, if he answers yes then prompt him to save and return true, if he answers no then return true
		switch(JOptionPane.showConfirmDialog(this, "Save current configuration?")){
		case 2:
			return false;
		case 0:
			try {
				save();
			} catch (IOException e) {
				System.out.println("Error saving when reseting.");
				System.exit(1);
			}
		case 1:
			return true;
		default:
			return false;
			
		}
	}
	public void setProfileNum(int curProfile){
		if(curProfile<10 & curProfile>=0)
			currentProfile = curProfile;
	}
	
	public void setKey(String name, String key){
		properties.setProperty(currentProfile+"."+name, key);
	}
	
	public Properties getProperties(){
		return properties;
	}
	
	public Joystick getJoystick(){
		return joyStick;
	}
	
	public String getKey(String name){
		return properties.getProperty(currentProfile+"."+name);
	}
	
	
	
	class SimpleAxisPanel extends JPanel {

		private JButton negativeButton;
		private JButton positiveButton;
		/**
		 * Create the panel.
		 */
		public SimpleAxisPanel(String axisPosition) {
			//make a negative and positive button for each axis
			negativeButton = new JButton(axisPosition+" axis -");
			negativeButton.setBackground(Color.white);
			
			final String axis = axisPosition;
			final ConfigurationPanel panel = ConfigurationPanel.this;
			
			negativeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new ConfigurationWindow(panel , axis+"-");
				}
			});
			add(negativeButton);
			
			positiveButton = new JButton(axisPosition+" axis +");
			positiveButton.setBackground(Color.white);
			positiveButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					new ConfigurationWindow(panel , axis+"+");
				}
			});
			add(positiveButton);

		}
		public void setAxisValue(float axisValue){
			//change the button colors when the axis is moved
			if(axisValue>0)
				positiveButton.setBackground(Color.cyan);
			else
				positiveButton.setBackground(Color.white);
				
			if(axisValue<0)
				negativeButton.setBackground(Color.cyan);
			else
				negativeButton.setBackground(Color.white);
		}
		

	}
	
	class AxisPanel extends JPanel {

		/**
		 * Create the panel.
		 */
		
		private int x;
		private int y;
		
		private JButton upButton;
		private JButton leftButton;
		private JButton rightButton;
		private JButton downButton;
		
		private JoyPanel joyPanel;
		
		public AxisPanel() {
			
			setLayout(new BorderLayout(0, 0));
			
			x = 50;
			y = 50;
			//set the buttons for up, down, left and right in the XY joystick
			upButton = new JButton("Up");
			upButton.setBackground(Color.white);
			upButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					new ConfigurationWindow(ConfigurationPanel.this, "Y-");
				}
			});
			add(upButton, BorderLayout.NORTH);
			
			leftButton = new JButton("Left");
			leftButton.setBackground(Color.white);
			leftButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					new ConfigurationWindow(ConfigurationPanel.this, "X-");
					
				}
			});
			add(leftButton, BorderLayout.WEST);
			
			rightButton = new JButton("Right");
			rightButton.setBackground(Color.white);
			rightButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					new ConfigurationWindow(ConfigurationPanel.this, "X+");

					
				}
			});
			add(rightButton, BorderLayout.EAST);
			
			downButton = new JButton("Down");
			downButton.setBackground(Color.white);
			downButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					new ConfigurationWindow(ConfigurationPanel.this, "Y+");
					
				}
			});
			add(downButton, BorderLayout.SOUTH);
			
			joyPanel = new JoyPanel();
			joyPanel.setForeground(Color.RED);
			joyPanel.setBackground(Color.WHITE);
			add(joyPanel, BorderLayout.CENTER);
			
		}
		//set the coordinates of the red button, set the color of the button and repaint the panel so the red dot appears where it should
		public void setCoordinates(int xCoor, int yCoor){
			if(x<=100&x>=0){
				x = xCoor;
				if(x>51)
					rightButton.setBackground(Color.cyan);
				else
					rightButton.setBackground(Color.white);
				
				if(x<49)
					leftButton.setBackground(Color.cyan);
				else
					leftButton.setBackground(Color.white);
			}
			if(y<=100&y>=0){
				y = yCoor;
				if(y>51)
					downButton.setBackground(Color.cyan);
				else
					downButton.setBackground(Color.white);
				
				if(y<49)
					upButton.setBackground(Color.cyan);
				else
					upButton.setBackground(Color.white);
			}
			joyPanel.repaint();
		}
		
		class JoyPanel extends JPanel{
			
			public JoyPanel(){
				setBackground(Color.white);
			}
			
			public void paintComponent(Graphics g){
				g.setColor(Color.red);
				g.fillOval(this.getWidth()/100*x,this.getHeight()/100*y, 4, 4);//fill oval with respect to the panel's height and width
			}
		}

	}
}