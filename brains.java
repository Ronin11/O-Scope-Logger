import java.io.FileNotFoundException;
import java.util.Vector;


public class brains {
	//Collected channels from the GUI
	static Vector<String> channels;
	
	/** Add the channels from the GUI, and set the scope to a 2-Byte data format for more accuracy **/
	public brains(Vector<String> availableChannels) throws FileNotFoundException, InterruptedException{
		channels = availableChannels;
		SerialStuff.getInfo();
		Thread.sleep(250);
		SerialStuff.setDataTo2Byte();
		GUI.enableGetData();
		
	}
	
	/** Get the waveform data from the scope on the listed channels.
	 * The data will appear in the following order:
	 * 	The Horizontal Offset from the ASCII values
	 * 	The Waveform Preamble, which includes the time difference between values.
	 *  The ASCII data for the waveform.
	 *  
	 *  For more information about converting this to times and voltages, look at the convert class. 
	 **/
	public static void getData(){
		SerialStuff.setDataToASCII();
		
		for(String chn : channels){
			try {
				Thread.sleep(250);
				GUI.pw.append('\n');
				SerialStuff.changeChannel(chn);
				Thread.sleep(250);
				GUI.pw.append('\n');
				SerialStuff.setDataSource();
				Thread.sleep(250);
				GUI.pw.append('\n');
				SerialStuff.getDataParams();
				Thread.sleep(250);
				GUI.pw.append('\n');
				SerialStuff.getData();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
