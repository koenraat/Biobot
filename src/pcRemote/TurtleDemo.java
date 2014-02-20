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
	private class Arduino extends TimerTask
	{
		private byte leftMotor;
		private byte rightMotor;
		private byte servo1;
		private byte servo2;
		private byte servo3;
		private byte servo4;
		private boolean cumin;
		private boolean[] cam;
		private String com;
		public boolean takePic()
		{
			return (cam[0] || cam[1] || cam[2]);
		}
		public void run()
		{
			//send stuffs to arduinos;
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
				
				if(takePic())
				{
					serialPort.writeByte((byte) 0);
					serialPort.writeByte((byte) 0);	
				}
				else{
					serialPort.writeByte((byte) leftMotor);
					serialPort.writeByte((byte) rightMotor);
				}
				
				serialPort.writeByte((byte) servo1);
				serialPort.writeByte((byte) servo2);
				serialPort.writeByte((byte) servo3);
				serialPort.writeByte((byte) servo4);
				
				//lastbyte:
				byte lastbyte = 0;
				
				for(int i = 0; i < 0; i++)
				{
					if(cam[i])
					{
						lastbyte = (byte) (lastbyte | (0x3 << i));
					}
				}
				
				if(cumin)
				{
					lastbyte = (byte) (lastbyte | 0xC);
				}
				
				serialPort.writeByte(lastbyte);
				
				System.out.println("Przywitalem sie!");
				
				if(takePic())
				{
					byte[] dataToWrite = serialPort.readBytes(2);
					System.out.println(dataToWrite[0]);
					System.out.println(dataToWrite[1]);
					int length = dataToWrite[0] + (dataToWrite[1] << 8);
					
					System.out.println("Zczytano" + length);
					
					if(length > 0)
					{
						byte[] dataFromArdu = new byte[length];
						int packages = (int) Math.ceil(length/(32));
						int len = 32;
						byte[][] dfa = new byte[packages][32];
						for(int i = 0; i < packages; i++) {
							if(i == packages-1) {
								len = length % 32;
							}
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
				}
			} 
			catch(Exception e)
			{
				
			}
			try{
				serialPort.closePort();
			} catch(Exception e) {
				
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
			cumin = true;
		}
		public void cuminOff() {
			cumin = false;
		}
		public void camOn(int i) {
			for(int j = 0; j < 3; j++) {
				if(j == i) {
					cam[i] = true;
				}
				else {
					cam[i] = false;
				}
			}
		}
		public void camOff() {
			for(int i = 0; i < 3; i++) {
				cam[i] = false;
			}
		}
		public Arduino(String com) {
			cam = new boolean[3];
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
	    		System.exit(0);
	    	}
	    }
	}
	
	private Arduino arduino;
<<<<<<< HEAD
	public TurtleDemo(String com)
=======
	
	public TurtleDemo()
>>>>>>> Change
	{
		Timer t = new Timer();
		arduino = new Arduino(com);
		t.schedule(arduino, 0, 100);
		
		xc = new XboxController();
		xc.addXboxControllerListener(new MyXboxControllerAdapter());
		xc.setLeftThumbDeadZone(0.2);
		xc.setRightThumbDeadZone(0.2);
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
			arduino.setServo2((byte) Math.round(-rightTriggerTemp * (255 - 0) + 127));
		} else {
			System.err.println("leftTrigger < 0");
		}
	}
	
	private double rightThumbMagnitude, rightThumbDirection;
	private void setLampServosMagnitude(double rightThumbMagnitudeTemp, double rightThumbDirectionTemp)
	{
		double x = rightThumbMagnitudeTemp*Math.cos(Math.toRadians(rightThumbDirectionTemp));
		double y = rightThumbMagnitudeTemp*Math.sin(Math.toRadians(rightThumbDirectionTemp));
		
		byte servo3 = arduino.getServo3() + (byte) Math.round(x*18);
		if (servo3 > 180){
			arduino.setServo3(180);
		} else if (servo3 < 0){
			arduino.setServo3(0);
		} else {
			arduino.setServo3(servo3);
		}
		
		byte servo4 = arduino.getServo4() + (byte) Math.round(y*18);
		if (servo4 > 180){
			arduino.setServo4(180);
		} else if (servo4 < 0){
			arduino.setServo4(0);
		} else {
			arduino.setServo4(servo4);
		}
	}
	
	private XboxController xc;
	private SerialPort serialPort;
	
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
			demo.execute();
		} catch(Exception e)
		{}
	}
}