import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
 

//Set up GUI
public class GUI {
	/** Member Variables **/
    final static boolean shouldFill = true;
    private static JButton getData, run;
    private static JTextArea logs,logs2;
    private static Vector<String> availableChannels=new Vector<String>();
    private static JComboBox triggerChannel;
    static PrintWriter pw; 
    static JFileChooser path = new JFileChooser();
    
    
    /** Set up the GUI **/
    public static void addComponentsToPane(final Container pane) {
    pane.setPreferredSize(new Dimension(800,800));
    pane.setLayout(new GridBagLayout());
    pane.setBackground(Color.black);
    GridBagConstraints c = new GridBagConstraints();
    if (shouldFill) {
    //natural height, maximum width
    c.fill = GridBagConstraints.HORIZONTAL;
    }
 
    JLabel ComPort = new JLabel("COM Port: ");
    JLabel COM = new JLabel("COM1");
    JLabel Connected = new JLabel("Connected: ");
    JLabel isConnected = new JLabel("False");
    JLabel channels = new JLabel("Select Channels:");
    final JCheckBox ch1 = new JCheckBox("Ch1");
    final JCheckBox ch2 = new JCheckBox("Ch2");
    final JCheckBox ch3 = new JCheckBox("Ch3");
    final JCheckBox ch4 = new JCheckBox("Ch4");
    
    
    run = new JButton("Run");
    getData = new JButton("Get Data");
    JButton choosePath = new JButton("Save Location");
     

    logs = new JTextArea();
    logs2 = new JTextArea();
    c.insets= new Insets(5,35,5,35);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    pane.add(ComPort, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 0;
    pane.add(COM, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 1;
    pane.add(Connected, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 1;
    pane.add(isConnected, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 5;
    c.gridwidth = 2;
    pane.add(run, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 5;
    c.gridwidth = 2;
    pane.add(choosePath, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 1;
    c.gridy = 6;
    c.gridwidth = 2;
    pane.add(getData, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 0;
    pane.add(channels, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 1;
    c.gridwidth = 1;
    pane.add(ch1, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 2;
    c.gridwidth = 1;
    pane.add(ch2, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 3;
    c.gridwidth = 1;
    pane.add(ch3, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 2;
    c.gridy = 4;
    c.gridwidth = 1;
    pane.add(ch4, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 8;
    c.gridwidth = 4;
    pane.add(logs, c);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 9;
    c.gridwidth = 4;
    pane.add(logs2, c);
    
    for(Component comp : pane.getComponents()){
    	comp.setBackground(Color.black);
    	comp.setForeground(Color.white);
    }
    
    run.setForeground(Color.blue);
    run.setBackground(Color.white);
    getData.setForeground(Color.blue);
    getData.setBackground(Color.white);
    getData.setEnabled(false);
    
    //Initialize the connection, and set to 2 byte data stream
    run.addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				run.setEnabled(false);
				new brains(availableChannels);
			} catch (FileNotFoundException e) {
				printException(e);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    });
    
    choosePath.addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
			path.setDialogTitle("Specify a file to save");  
			int userSelection = path.showSaveDialog(pane);
			if (userSelection == JFileChooser.APPROVE_OPTION) {
			    File fileToSave = path.getSelectedFile();
			    //System.out.println("Save as file: " + fileToSave.getAbsolutePath());
			    convert.savePath = fileToSave.getAbsolutePath();
			}
			
		}
    });

    
    //Get the data from the O-Scope from the specific channels
    getData.addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent arg0) {
		    try {
				pw = new PrintWriter(new FileWriter("Data.txt", true));
				brains.getData();
				Thread.sleep(1000);
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    });
 
    /** Add the channels to the list when they're being checked. **/
   ch1.addActionListener(new ActionListener(){
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(ch1.isSelected()){
			availableChannels.add("ch1");
		}
		else
			availableChannels.remove("ch1");
		Collections.sort(availableChannels);
		}   
   });
   ch2.addActionListener(new ActionListener(){
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(ch2.isSelected()){
			availableChannels.add("ch2");
		}
		else
			availableChannels.remove("ch2");
		Collections.sort(availableChannels);
		}   
   });
   ch3.addActionListener(new ActionListener(){
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(ch3.isSelected()){
			availableChannels.add("ch3");
		}
		else
			availableChannels.remove("ch3");
		Collections.sort(availableChannels);
		}   
   });
   ch4.addActionListener(new ActionListener(){
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(ch4.isSelected()){
			availableChannels.add("ch4");
		}
		else
			availableChannels.remove("ch4");
		Collections.sort(availableChannels);
		}   
   });
   }
    
    //Setters and getters for the main program

   /** Print errors to the on-screen logs **/
   public static void printException(Exception e){
	   Writer writer = new StringWriter();
	   PrintWriter printWriter = new PrintWriter(writer);
	   e.printStackTrace(printWriter);
	   String s = writer.toString();
	   logs.append(s + "\n");
   }
   
   /** Print the data to the on-screen logs **/
   public static void print(String s){
	   if(!s.isEmpty())
		   logs.append(s + "\n");
   }
   
   /** Print the data to the file **/
   public static void printRx(String s){
	   if(!s.isEmpty()){
		    pw.flush();
			pw.append(s);
	   }
	    
   }
   
   //Enable the getData button
   public static void enableGetData(){getData.setEnabled(true);}
   
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Automated Oscilloscope Program");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    /** Program start **/
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
    		    try {
    		        ( new SerialStuff() ).connect();
    		      } catch( Exception e ) {
    		        printException(e);
    		      }
            }
        });
    }
}