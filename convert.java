import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


public class convert {
	//Path for saveFile
	static String savePath = null;
	
	/** Read in the raw data into an ArrayList **/
	public static void loadData() throws FileNotFoundException{
		ArrayList<String> data = new ArrayList<String>();
		File dataFile = new File(savePath);
		Scanner s = new Scanner(dataFile);
		while(s.hasNextLine()){
			data.add(s.nextLine());
		}
		s.close();
		//Delete the Data.txt file
		/*try {
			Files.delete(dataFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		channelData(data);
	}
	
	/** Separate the data into the different channels. **/
	private static void channelData(ArrayList<String> cData){
		int channelSelect = -1, begin = 0;
		List<String> data1 = new ArrayList<String>(), data2 = new ArrayList<String>(),
				data3 = new ArrayList<String>(),data4 = new ArrayList<String>();
		for(int i = 0; i < cData.size(); i++){
			if(cData.get(i).contains("points") || i == cData.size()-1){
				switch(channelSelect){
				case(-1):
					break;
				case(0):
					data1 = cData.subList(begin,i-1);
					break;
				case(1):
					data2 = cData.subList(begin,i-1);
					break;
				case(2):
					data3 = cData.subList(begin,i-1);
					break;
				case(3):
					data4 = cData.subList(begin,i-1);
					break;
				}
				begin = i;
				channelSelect++;
			}
		}
		processData(data1,data2,data3,data4);
		
	}
	
	
	private static void processData(List<String> data1,List<String> data2,
			List<String> data3,List<String> data4){
		List<Double> values1 = null,values2 = null,values3 = null,values4 = null;
		if(!data1.isEmpty())
			values1 = toVoltage(data1);
		if(!data2.isEmpty())
			values2 = toVoltage(data2);
		if(!data3.isEmpty())
			values3 = toVoltage(data3);
		if(!data4.isEmpty())
			values4 = toVoltage(data4);
		double voltageData[][] = new double[2500][5];
		double timeInc = getTimeInc(data1);
		for(int i = 0; i < 2500; i++){
			voltageData[i][0] = timeInc*i;
			if(values1 != null)
				voltageData[i][1] = values1.get(i);
			if(values2 != null)
				voltageData[i][2] = values2.get(i);
			if(values3 != null)
				voltageData[i][3] = values3.get(i);
			if(values4 != null)
				voltageData[i][4] = values4.get(i);
		}
		writeData(voltageData);
	}
	
	/** Apply the mathematics to the voltage values **/
	private static List<Double> toVoltage(List<String> data){
		double convertData[] = null;
		try{
		convertData = parseStuff(data.get(0));
		}catch(IndexOutOfBoundsException e){
			GUI.printException(e);
			GUI.print("Data is not in correct format");
		}
		
		List<Double> values = new ArrayList<Double>();
		for(int i = 1; i < data.size(); i++){
			List<String> list = Arrays.asList(data.get(i).split(","));
			for(String s : list){
				Double d = Double.valueOf(s);
				d = (d - convertData[1] - convertData[2]) * convertData[3];
				values.add(d);
			}
		}
		return values;
		
	}
	
	/** Get the time increments for the data **/
	private static Double getTimeInc(List<String> data){
		double convertData[] = parseStuff(data.get(0));
		return convertData[0];
	}
	
	/** Parse the waveform preamble into usable values **/
	private static double[] parseStuff(String preamble) throws IndexOutOfBoundsException{
		List<String> buffer = Arrays.asList(preamble.split(";"));
		double yOffset = Double.valueOf(buffer.get(14));
		double yMult = Double.valueOf(buffer.get(12));
		double zero = Double.valueOf(buffer.get(13));
		double timeIncrements = Double.valueOf(buffer.get(8));
		double info[] = {timeIncrements,zero,yOffset,yMult};
		return info;	
	}
	
	/** Convert the data into a CSV format with time in the first column
	 * and voltage in the second column.
	 **/
	private static void writeData(double[][] data){
		try {
			NumberFormat timeFormat = new DecimalFormat("0.000E0");
			NumberFormat numFormat = new DecimalFormat("0.000");
			PrintWriter pw = new PrintWriter(new File("DataVolt.txt"));
			pw.print("Time:\t\t");
			//if(brains.channels.get(0) != null)
				pw.print(/*brains.channels.get(0)*/"CH1"+"\t");
			//if(brains.channels.get(1) != null)
				pw.print(/*brains.channels.get(1)*/"CH2"+"\t");
			//if(brains.channels.get(2) != null)
				pw.print(/*brains.channels.get(2)*/"CH3"+"\t");
			//if(brains.channels.get(3) != null)
				pw.print(/*brains.channels.get(3)*/"CH4"+"\n");
			
			for(int i = 0; i < 2500; i++){
					pw.print(timeFormat.format(data[i][0])+"\t");
					pw.print(numFormat.format(data[i][1]) + "\t");
					pw.print(numFormat.format(data[i][2]) + "\t");
					pw.print(numFormat.format(data[i][3]) + "\t");
					pw.print(numFormat.format(data[i][4]) + "\n");
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		try {
			loadData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
