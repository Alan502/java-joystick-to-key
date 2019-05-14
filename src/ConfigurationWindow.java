import java.awt.BorderLayout;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class ConfigurationWindow extends JFrame {

	private JPanel contentPane;	
	private JButton setKeyButton;
	
	private JComboBox setProfileBox;
	private JCheckBox chckbxChangeToPrevious;
	
	private String buttonName;
	private ConfigurationPanel configPanel;
	
	private String currentKey;
	
	/**
	 * Create the frame.
	 */
	public ConfigurationWindow(ConfigurationPanel cfgPanel, String btnName) {
		setTitle("Set action for "+btnName);
		
		buttonName = btnName;
		configPanel = cfgPanel;
		currentKey = null;
		currentKey = configPanel.getKey(buttonName);
	
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 401, 150);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		setContentPane(contentPane);
		
		JLabel explanationLabel = new JLabel("When this button is pressed:");
		contentPane.add(explanationLabel, BorderLayout.NORTH);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		//set the keys to current configuration
		try{
			setKeyButton = new JButton("Key: "+KeyEvent.getKeyText(Integer.parseInt(currentKey)));
		}catch(NumberFormatException n){
			setKeyButton = new JButton("Key:");
		}
		
		setKeyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				setKeyButton.setText("Press a keyboard key..");
				setKeyButton.setFocusTraversalKeysEnabled(false); //Disable focus transversal keys, so TAB is captured
				
				//Disable space as a key that clicks the button, so it can be captured
				InputMap im = setKeyButton.getInputMap();
				im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
				im.put(KeyStroke.getKeyStroke("released SPACE"), "none");
				
				setKeyButton.addKeyListener(new KeyListener() {
					
					@Override
					public void keyTyped(KeyEvent key) {
						
					}
					
					@Override
					public void keyReleased(KeyEvent key) {
						//After we capture the key, we will no longer need the listener
						setKeyButton.removeKeyListener(this);
						
					}
					
					@Override
					public void keyPressed(KeyEvent key) {
						currentKey = key.getKeyCode()+"";
						//Set the text of the button to the selected key
						setKeyButton.setText("Key: "+KeyEvent.getKeyText(key.getKeyCode()));
					}
				});
			}
		});
		tabbedPane.addTab("Press on keyboard...", null, setKeyButton, null);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Change to Profile...", null, panel, null);
		
		setProfileBox = new JComboBox();
		setProfileBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				currentKey = "PROFILE:"+setProfileBox.getSelectedIndex()+":"+chckbxChangeToPrevious.isSelected();
			}
		});
		panel.add(setProfileBox);
		setProfileBox.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
		
		chckbxChangeToPrevious = new JCheckBox("Change to previous profile on release");
		chckbxChangeToPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				currentKey = "PROFILE:"+setProfileBox.getSelectedIndex()+":"+chckbxChangeToPrevious.isSelected();
			}
		});
		chckbxChangeToPrevious.setToolTipText("If checked, JJoy2Key will change to the selected profile only while this button is pressed. nIf not checked, JJoy2Key will change to the previous profile automatically after the button is pressed. This is useful for things like button combos.");
		panel.add(chckbxChangeToPrevious);
		
		//set the profile and Checkbox to current configuration
		if(currentKey!=null && currentKey.startsWith("PROFILE")){
			String config[] = currentKey.split(":");			
			setProfileBox.setSelectedIndex(Integer.parseInt(config[1]));
			chckbxChangeToPrevious.setSelected(Boolean.parseBoolean(config[2]));
		}
			
		
		
		JButton okButton = new JButton("OK");
		final ConfigurationWindow window = this;
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(currentKey!=null)
					configPanel.setKey(buttonName, currentKey); //save the key
					window.dispose();
			}
		});
		contentPane.add(okButton, BorderLayout.SOUTH);
		
		setVisible(true);
	}	

}
