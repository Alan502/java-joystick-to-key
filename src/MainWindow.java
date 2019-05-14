import java.awt.AWTException;
import java.awt.EventQueue;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import com.centralnexus.input.Joystick;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Toolkit;


public class MainWindow {

	private JFrame frmJjoytokey;
	private ConfigurationPanel configPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmJjoytokey.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public MainWindow() throws IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		
		if(Joystick.getNumDevices()<1){
			JOptionPane.showMessageDialog(null, "No joysticks were found!");
		}
		
		frmJjoytokey = new JFrame();
		frmJjoytokey.setIconImage(Toolkit.getDefaultToolkit().getImage("tray.png"));
		BorderLayout borderLayout = (BorderLayout) frmJjoytokey.getContentPane().getLayout();
		borderLayout.setVgap(10);
		borderLayout.setHgap(10);
		frmJjoytokey.setTitle("JJoyToKey");
		frmJjoytokey.setSize(600,600);
		frmJjoytokey.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmJjoytokey.setJMenuBar(menuBar);
		
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		JMenuItem configLoad = new JMenuItem("Load Configuration...");
		configLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					configPanel.load();
				} catch (IOException e) {
					System.out.println("Error using configPanel's save method.");
					System.exit(1);
				}
			}
		});
		menuFile.add(configLoad);
		
		JMenuItem configSave = new JMenuItem("Save Configuration...");
		configSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					configPanel.save();
				} catch (IOException e) {
					System.out.println("Error using configPanel's load method.");
					System.exit(1);
				}
			}
		});
		menuFile.add(configSave);
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		menuFile.add(exit);
		
		JMenu menuJoystick = new JMenu("Joystick");
		menuBar.add(menuJoystick);
		
		JMenu menuProfile = new JMenu("Profiles");
		menuBar.add(menuProfile);
		
		JRadioButtonMenuItem[] profiles = new JRadioButtonMenuItem[10];
		ButtonGroup profilesGroup = new ButtonGroup();
		
		for(int i=0;i<10;i++){
			profiles[i] = new JRadioButtonMenuItem("Profile #"+(i+1));
			final int j = i;
			profiles[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					configPanel.setProfileNum(j);
				}
			});
			profilesGroup.add(profiles[i]);
			menuProfile.add(profiles[i]);
			
			if(i==0)
				profiles[0].setSelected(true);
		}
		
		JMenuItem engineStart = new JMenuItem("Start Mapping!");
		engineStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Engine eng = new Engine(configPanel.getProperties(), configPanel.getJoystick());
					frmJjoytokey.dispose();
				} catch (AWTException e) {
					System.out.println("Error creating engine. "+e.toString());
					System.exit(1);
				} catch (IOException e) {
					System.out.println("Error creating engine. "+e.toString());
					System.exit(1);
				}
			}
		});
		menuBar.add(engineStart);
		
		JRadioButtonMenuItem joystickRadioButtons[] = new JRadioButtonMenuItem[Joystick.getNumDevices()];
		ButtonGroup joystickButtons = new ButtonGroup();
		
		for(int i=0;i<joystickRadioButtons.length;i++){
			
			final int joyID = i;
			
			joystickRadioButtons[i] = new JRadioButtonMenuItem("Joystick #"+i);
			joystickRadioButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!configPanel.reset())//check that the user does not click cancel
						return;
					try {						
						frmJjoytokey.getContentPane().remove(configPanel);
						
						configPanel = new ConfigurationPanel(joyID);						
						frmJjoytokey.getContentPane().add(configPanel);
						
						configPanel.revalidate();					
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Couldn't load Joystick "+joyID+" configuration.");
					}
				}
			});
			joystickButtons.add(joystickRadioButtons[i]);
			
			if(i==0)
				joystickRadioButtons[0].setSelected(true); //make the Joystick selected by default
			
			menuJoystick.add(joystickRadioButtons[i]);
		}
		
		
		configPanel = new ConfigurationPanel(0);
		frmJjoytokey.getContentPane().add(configPanel);
		
	}

}
