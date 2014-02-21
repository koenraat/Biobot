package pcRemote;

import ch.aplu.xboxcontroller.*;
import java.lang.Math;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.awt.BorderLayout;
import java.awt.Image;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class TurtleDemo
{
	public class AddShutdownHook{
		public void attachShutDownHook(){
			Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
				public void run()
				{
					arduino.closePort();
					System.out.println("Shutdown works");
				}
			});
			System.out.println("Shut Down Hook Attached.");
		}
	}
	private class Arduino
	{
		private byte leftMotor = 0;
		private byte rightMotor = 0;
		private byte servo1 = 0;
		private byte servo2 = 0;
		private byte servo3 = 60;
		private byte servo4 = 60;
		private byte cumin = 0;
		private byte[] cam;
		private String com;
		private byte toByte(boolean a)
		{
			if(a)
			{
				return 1;
			} else{
				return 0;
			}
		}
		public boolean toBool(byte a)
		{
			if(a > 0)
			{
				System.out.println("true");
				return true;
			}
			else 
			{
				return false;
			}
		}
		public void deserialize(byte[] des)
		{
			leftMotor = des[0];
			rightMotor = des[1];
			servo1 = des[2];
			servo2 = des[3];
			servo3 = des[4];
			servo4 = des[5];
			cumin = des[6];
			cam[0] = des[7];
			cam[1] = des[8];
			cam[2] = des[9];
		}
		public byte[] serialize()
		{
			byte[] ret = {leftMotor, rightMotor, servo1, servo2, servo3, servo4, cumin, cam[0], cam[1], cam[2]};
			return ret;
		}
		public boolean takePic()
		{
			return (cam[0] == 1 || cam[1] == 1 || cam[2] == 1);
		}
		public void run()
		{
			//send stuffs to arduinos;
			System.out.println("I am in arduino");
			
			/*BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Gimme 10 nums:\n >>");
			String cmnd = "0 0 0 0 0 0 0 0 0 0";
	    	try { cmnd = br.readLine();} catch(Exception e) {}
	    	if(cmnd.equals(""))
	    	{
	    		closePort();
	    		return;
	    	}
	    	String[] com_ = cmnd.split(" ");
	    	byte[] ret = new byte[10];
	    	for(int i = 0; i < 10; i++)
	    	{
	    		System.out.println(com_[i]);
	    		ret[i] = (byte) Integer.parseInt(com_[i]);
	    	}
	    	
	    	System.out.println("???");
	    	
	    	deserialize(ret);*/
	    	
	    	System.out.println("???");
			
			setMotorSpeeds(leftThumbMagnitude, leftThumbDirection);
			setFingerServosMagnitude(leftTrigger, rightTrigger);
			setLampServosMagnitude(rightThumbMagnitude, rightThumbDirection);
			try {
				
				/*
				 * handshake:
				 * 1B - left
				 * 1B - right
				 * 1B - s1
				 * 1B - s2
				 * 1B - s3
				 * 1B - s4
				 * 1B - reszta
				 */
				System.out.println("Bede slaw");
				if(takePic())
				{
					System.out.println("Jezdę w pętlę");
					serialPort.writeByte((byte) 0);
					serialPort.writeByte((byte) 0);	
					//timerStop();
				}
				else{
					serialPort.writeByte((byte) leftMotor);
					System.out.println((byte) leftMotor);
					serialPort.writeByte((byte) rightMotor);
					System.out.println((byte) rightMotor);
				}
				
				serialPort.writeByte((byte) servo1);
				System.out.println((byte) servo1);
				serialPort.writeByte((byte) servo2);
				System.out.println((byte) servo2);
				serialPort.writeByte((byte) servo3);
				System.out.println((byte) servo3);
				serialPort.writeByte((byte) servo4);
				System.out.println((byte) servo4);
				
				//lastbyte:
				if(cumin == 1)
				{
					serialPort.writeByte((byte) 255);
				}
				else
				{
					serialPort.writeByte((byte) 0);
				}
				
				//System.out.println((byte) lastbyte);
				//System.out.println("Magic stuff:");
				for(int i = 0; i < 3; i++)
				{
					//if(cam[i] == 1)
					//{
					//	serialPort.writeByte((byte) 255);
					//	System.out.print(i);
					////	System.out.println(" 1");
					//}
					//else {
						serialPort.writeByte((byte) cam[i]);
						System.out.println((byte) cam[i]);
					//}
				}
				
				System.out.println("Przywitalem sie!");
				
				serialPort.purgePort(SerialPort.PURGE_RXCLEAR | SerialPort.PURGE_TXCLEAR);
				
				if(takePic())
				{
					System.out.println("TakePic!");
					long now = System.currentTimeMillis();
					while(System.currentTimeMillis()-now < 300){}
					int[] dataToWrite = serialPort.readIntArray(2);
					
					System.out.println(dataToWrite[0]);
					System.out.println(dataToWrite[1]);
					System.out.println((int)dataToWrite[0]);
					System.out.println((int)dataToWrite[1]);
					System.out.println((byte)dataToWrite[1] << 8);
					System.out.println((int)dataToWrite[1] << 8);
					//byte length = (byte) ( dataToWrite[0] | ( dataToWrite[1] << 8));
					int length = ( dataToWrite[0] | ( dataToWrite[1] << 8));
					System.out.println((byte) length);
					System.out.println((int) length);
					//System.out.println((byte) length2);
					//System.out.println((int) length2);
					
//					-116
//					-78
//					-116
//					-78
//					-19968
//					-19968
//					-116
//					-116
//					-116
//					-116
//					Zczytano-116
					
//					-56
//					-61
//					-56
//					-61
//					-15616
//					-15616
//					-56
//					-56
//					-56
//					-56
//					Zczytano-56
					
					System.out.println("Zczytano" + length);
					
					if(length > 0)
					{
						byte[] dataFromArdu = new byte[length];
						int packages = (int) Math.ceil((double)length/(double)32);
						int len = 32;
						byte[][] dfa = new byte[packages][32];
						for(int i = 0; i < packages; i++) {
							if(i == packages-1) {
								len = length % 32;
							}
//							now = System.currentTimeMillis();
//							while(System.currentTimeMillis()-now < 10){}
							System.out.println("Teraz bede zczytywal:"+i+" z "+packages+ " o dlugosci "+len);
							dfa[i] = serialPort.readBytes(len);
						}
						len = 32;
						for(int i = 0; i < packages; i++) {
							if(i == packages-1) {
								len = length % 32;
							}
							for(int j = 0; j < len; j++){
								dataFromArdu[32*i+j] = dfa[i][j];
							}
						}			
						try {
							Random rnd = new Random();
							int n = rnd.nextInt(25000);
							FileOutputStream out = new FileOutputStream("file"+n+".jpg");
							out.write(dataFromArdu);
							out.close();
							
							System.out.println("Koniec.");
							frame("file"+n+".jpg");
						} catch(Exception e) {
							System.out.println("Error");
						}
						
					}
					camOff();
					//timerResume();
				}
			} 
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		public byte getLeftMotor() {
			return leftMotor;
		}
		public void setLeftMotor(byte leftMotor) {
			this.leftMotor = leftMotor;
		}
		public byte getRightMotor() {
			return rightMotor;
		}
		public void setRightMotor(byte rightMotor) {
			this.rightMotor = rightMotor;
		}
		public byte getServo1() {
			return servo1;
		}
		public void setServo1(byte servo1) {
			this.servo1 = servo1;
		}
		public byte getServo2() {
			return servo2;
		}
		public void setServo2(byte servo2) {
			this.servo2 = servo2;
		}
		public byte getServo3() {
			return servo3;
		}
		public void setServo3(byte servo3) {
			this.servo3 = servo3;
		}
		public byte getServo4() {
			return servo4;
		}
		public void setServo4(byte servo4) {
			this.servo4 = servo4;
		}
		public void cuminOn() {
			cumin = 1;
		}
		public void cuminOff() {
			cumin = 0;
		}
		public void camOn(int i) {
			for(int j = 0; j < 3; j++) {
				if(j == i) {
					cam[j] = 1;
				}
				else {
					cam[j] = 0;
				}
			}
		}
		public void camOff() {
			for(int i = 0; i < 3; i++) {
				cam[i] = 0;
			}
		}
		public Arduino(String com) {
			cam = new byte[3];
			cam[0] = 0;
			cam[1] = 0;
			cam[2] = 0;
			System.out.println("Arduino opened!");
			this.com = com;
			openPort();
		}
		public void openPort()
		{
			try {
				serialPort = new SerialPort(com);
				serialPort.openPort();
				serialPort.setParams(38400, 8, 1, 0);
			} catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		public void closePort()
		{
			try {
				serialPort.closePort();
			} catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	private class MyXboxControllerAdapter extends XboxControllerAdapter
	{	
		public void leftThumbMagnitude(double magnitude)
	    {
			leftThumbMagnitude = magnitude;
	    }

	    public void leftThumbDirection(double direction)
	    {
	    	leftThumbDirection = direction;
	    }
	    
	    public void leftTrigger(double value)
	    {
			leftTrigger = value;
	    }
	    
	    public void rightTrigger(double value)
	    {
			rightTrigger = value;
	    }
	    
	    public void rightThumbMagnitude(double magnitude)
	    {
			rightThumbMagnitude = magnitude;
	    }

	    public void rightThumbDirection(double direction)
	    {
	    	rightThumbDirection = direction;
	    }
	    
	    public void buttonX(boolean pressed)
	    {
	    	if(pressed){
	    		arduino.cuminOn();
	    	}else{
	    		arduino.cuminOff();
	    	}
	    }
	    
	    public void buttonA(boolean pressed)
	    {
	    	if(pressed){
	    		System.out.println("Pressed A");
	    		arduino.camOn(2);
	    	}
	    }
	    
	    public void buttonB(boolean pressed)
	    {
	    	if(pressed){
	    		System.out.println("Pressed B");
	    		arduino.camOn(1);
	    	}
	    }
	    
	    public void buttonY(boolean pressed)
	    {
	    	if(pressed){
	    		System.out.println("Pressed Y");
	    		arduino.camOn(0);
	    	}
	    }
	    
	    public void start(boolean pressed)
	    {
	    	if(pressed) {
				//arduinoReset();
	    	} else {
	    		
	    	}
	    }
	    
	    public void back(boolean pressed)
	    {
	    	if(pressed){
	    		arduino.closePort();
	    		System.out.println("arduino.closePort()");
	    		System.exit(0);
	    	}
	    }
	}
	
	private Arduino arduino;
	public TurtleDemo(String com)
	{
		xc = new XboxController();
		xc.addXboxControllerListener(new MyXboxControllerAdapter());
		xc.setLeftThumbDeadZone(0.2);
		xc.setRightThumbDeadZone(0.2);
		
		this.com = com;
		arduino = new Arduino(com);
		arduino.run();
		long now = System.currentTimeMillis();
		while(true)
		{
			if(System.currentTimeMillis()-now > 500)
			{
				arduino.run();
				now = System.currentTimeMillis();
			}
		}
	}
	
	private double leftThumbMagnitude, leftThumbDirection;
	private void setMotorSpeeds(double leftThumbMagnitudeTemp, double leftThumbDirectionTemp)
	{
		double leftMotor = 127 * leftThumbMagnitudeTemp;
		double rightMotor = 127 * leftThumbMagnitudeTemp;
		
		if(leftThumbDirectionTemp < 80) {
			rightMotor *= Math.cos(Math.toRadians(this.leftThumbDirection));
		}
		else if (80 >= leftThumbDirectionTemp || leftThumbDirectionTemp < 100) {
			rightMotor *= -1;
		}
		else if (100 >= leftThumbDirectionTemp || leftThumbDirectionTemp < 180) {
			rightMotor *= Math.cos(Math.toRadians(leftThumbDirectionTemp));
			leftMotor *= -1;
		}
		else if (180 >= leftThumbDirectionTemp || leftThumbDirectionTemp < 260) {
			rightMotor *= -1;
			leftMotor *= Math.cos(Math.toRadians(leftThumbDirectionTemp));
		}
		else if (260 >= leftThumbDirectionTemp || leftThumbDirectionTemp < 280) {
			leftMotor *= -1;
		}
		else if (280 >= leftThumbDirectionTemp || leftThumbDirectionTemp <= 360) {
			leftMotor *= Math.cos(Math.toRadians(leftThumbDirectionTemp));
		}
		else {
			System.err.print("leftThumbDirection > 360 degrees");
		}
		
		arduino.setLeftMotor((byte) Math.round(leftMotor));
		arduino.setRightMotor((byte) Math.round(rightMotor));
	}
	
	private double leftTrigger, rightTrigger;
	private void setFingerServosMagnitude(double leftTriggerTemp, double rightTriggerTemp)
	{
		if(leftTriggerTemp > 0){
			arduino.setServo1((byte) Math.round(leftTriggerTemp * (127 - 0) + 127));
			arduino.setServo2((byte) Math.round(leftTriggerTemp * (127 - 0) + 127));
		} else if (leftTriggerTemp == 0){
			arduino.setServo1((byte) 127);
			arduino.setServo2((byte) Math.round(-rightTriggerTemp * (127 - 0) + 127));
		} else {
			System.err.println("leftTrigger < 0");
		}
	}
	
	private double rightThumbMagnitude, rightThumbDirection;
	private void setLampServosMagnitude(double rightThumbMagnitudeTemp, double rightThumbDirectionTemp)
	{
		double x = rightThumbMagnitudeTemp*Math.cos(Math.toRadians(rightThumbDirectionTemp));
		double y = rightThumbMagnitudeTemp*Math.sin(Math.toRadians(rightThumbDirectionTemp));
		
		int servo3 = (int) ((int)arduino.getServo3() + (int)Math.round(x*18.0));
		if (servo3 > 120){
			arduino.setServo3((byte) 120);
		} else if (servo3 < 0){
			arduino.setServo3((byte) 0);
		} else {
			arduino.setServo3((byte)servo3);
		}
		
		int servo4 = (int) (arduino.getServo4() + Math.round(y*(double)18));
		if (servo4 > 120){
			arduino.setServo4((byte) 120);
		} else if (servo4 < 0){
			arduino.setServo4((byte) 0);
		} else {
			arduino.setServo4((byte) servo4);
		}
	}
	
	private XboxController xc;
	private SerialPort serialPort;
	private String com;
	public static void frame(String name)
	{
		JFrame frame = new JFrame("Arduino Pix");
		try{
			Image image = ImageIO.read(new File(name));
			JLabel lblimage = new JLabel(new ImageIcon(image));
			frame.getContentPane().add(lblimage, BorderLayout.CENTER);
			frame.setSize(300, 400);
			frame.setVisible(true);
		}
		catch(Exception e)
		{}
	}
  
	public static void main(String[] args)
	{
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    	System.out.print("Enter com name:\n >>");
	    	String com = br.readLine();
			TurtleDemo demo = new TurtleDemo(com);
			AddShutdownHook sample = demo.new AddShutdownHook();
			sample.attachShutDownHook();
		} catch(Exception e)
		{}
	}
}