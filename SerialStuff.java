import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
 
public class SerialStuff {
	// Stream for sending commands: 
	 static OutputStream out;
	// COM port for the O-Scope
	 String portName ="COM1";
	// Selected channel for the commands. Channel is changed in 
	 // the brains class for each channel selected by the user. 
	 private static String channel = "ch1";
	 
	 /** Connect to the COM port using the rxtx library **/
  void connect() throws Exception {
    CommPortIdentifier portIdentifier = CommPortIdentifier
        .getPortIdentifier( portName );
    if( portIdentifier.isCurrentlyOwned() ) {
      GUI.print( "Error: Port is currently in use" );
    } else {
      int timeout = 2000;
      CommPort commPort = portIdentifier.open( this.getClass().getName(), timeout );
 
      if( commPort instanceof SerialPort ) {
        SerialPort serialPort = ( SerialPort )commPort;
        //Serial port parameters may change from scope to scope, this is for a TDS2014 TEKTRONICS
        serialPort.setSerialPortParams( 9600,
                                        SerialPort.DATABITS_8,
                                        SerialPort.STOPBITS_1,
                                        SerialPort.PARITY_NONE );
 
        //Create the inputStream for data from the scope
        InputStream in = serialPort.getInputStream();
        //Initialize the output command stream
        out = serialPort.getOutputStream();
 
        //Start the thread for the input stream to listen for data
        ( new Thread( new SerialReader( in ) ) ).start();
      } else {
        GUI.print( "Error: Only serial ports are handled by this program." );
      }
    }
  }
 
  /** Using the InputStream, create the reader for incoming data **/
  public static class SerialReader implements Runnable {
	static boolean isReading = false;
    InputStream in;
 
    public SerialReader( InputStream in ) {
      this.in = in;
    }
    
    public static boolean hasData(){return isReading;}
 
    public void run() {
      byte[] buffer = new byte[ 1024 ];
      int len = -1;
      try {
    	//Look for data, and store it in the buffer. Send the data to the file for storage
        while( ( len = this.in.read( buffer ) ) > -1 ) {
        	isReading = true;
          GUI.printRx( new String( buffer, 0, len ) );
        }
        isReading = false;
      } catch( IOException e ) {
    	  GUI.printException(e);
      }
    }
  }

  /** Send the command to the scope with a newline & carriage return as dictated in the Programmer Manual.
   * Link is listed just above the command set.
   * sendCommand and getCommand are very similar, but getCommand is used for commands that receive data
   * after being processed by the scope.
  **/
    private static void sendCommand(String s) {
    	try {
    		s = s+"\r\n";
        	out.write(s.getBytes(Charset.forName("US-ASCII")));
        	//GUI.print(s + "Command sent.\n");
      	} catch( IOException e ) {
      		GUI.printException(e);
      }
    }
    
    private static void getCommand(String s) {
    	try {
    		s = s+"\r\n";
    		for(int i = 0; i < 5; i++){
    			out.write(s.getBytes(Charset.forName("US-ASCII")));
    			//GUI.print(s + " Command Sent.\n");
        		Thread.sleep(500);
        		if(SerialReader.hasData())
        			break;
        		if(i == 4){
        			GUI.print(s + " Command would not send, please check connections.");
        		}
    		}
      	} catch( IOException e ) {
      		GUI.printException(e);
      } catch (InterruptedException e) {
			GUI.printException(e);
		}
    }
    
    /** Commands for the oscilloscope, found here: 
     * http://physics.ucsd.edu/neurophysics/Manuals/Tektronix/TDS%20200,%20TDS1000_TDS2000%20Manual.pdf 
     * Methods using getCommand expect to receive data from the scope, while methods using setCommand
     * are generally setting parameters on the scope, and return no data.
     * **/
    public static void changeChannel(String s){channel = s;};
    
    public static void getInfo(){getCommand("*idn?");};
    
    public static void getVoltPerDiv(){getCommand(channel+":volt?");};
    public static void setVoltPerDiv(double volts){sendCommand(channel+":volt "+volts);};
    
    public static void getSecPerDiv(){getCommand("horizontal:scale?");};
    public static void setSecPerDiv(double time){sendCommand("horizontal:scale "+time);};
    
    public static void getData(){getCommand("curve?");};
    public static void setDataToASCII(){sendCommand("data:encdg ascii");};
    public static void getDataParams(){getCommand("wfmpre?");};
    public static void getYOffset(){getCommand("wfmpre:ymult?");};
    public static void setDataTo2Byte(){getCommand("wfmpre:byt_nr 2");};
    public static void setDataSource(){sendCommand("data:source "+channel);};
    public static void getVerticalOffset(){sendCommand("WFMPre:YOFf");};
    
    public static void getTriggerLevel(){getCommand("trigger:main:level?");};
    public static void setTriggerLevel(double level){sendCommand("trigger:main:level "+level);};
    public static void setTriggerToEdge(){sendCommand("trigger:main:type edge");};
    public static void getTriggerType(){getCommand("trigger:main:type?");};
    public static void setTriggerChannel(String channel){sendCommand("trigger:main:edge:source "+channel);};
    public static void getTriggerChannel(){getCommand("trigger:main:edge:source?");};
    
    
 
    
  
  
  }
