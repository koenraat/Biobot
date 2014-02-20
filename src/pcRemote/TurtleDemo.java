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

import java.io.File;
import java.io.FileOutputStream;

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
					try {
						serialPort.closePort();
					} catch (SerialPortException e) {
						e.printStackTrace();
					}
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
		private int servo1;
		private int servo2;
		private int servo3;
		private int servo4;
		private boolean cumin;
		private boolean[] cam;
		private String com = "COM6";
		public boolean takePic()
		{
			return (cam[0] || cam[1] || cam[2]);
		}
		public void run()
		{
			//send stuffs to arduinos;
			try {
				serialPort = new SerialPort(com);
				serialPort.openPort();
				serialPort.setParams(38400, 8, 1, 0);
				
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
						serialPort.closePort();			
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
					serialPort.closePort();
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
		public int getServo1() {
			return servo1;
		}
		public void setServo1(int servo1) {
			this.servo1 = servo1;
		}
		public int getServo2() {
			return servo2;
		}
		public void setServo2(int servo2) {
			this.servo2 = servo2;
		}
		public int getServo3() {
			return servo3;
		}
		public void setServo3(int servo3) {
			this.servo3 = servo3;
		}
		public int getServo4() {
			return servo4;
		}
		public void setServo4(int servo4) {
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
		public Arduino() {
			cam = new boolean[3];
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
	public TurtleDemo()
	{
		Timer t = new Timer();
		arduino = new Arduino();
		t.schedule(arduino, 0, 100);
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
	
	private void setFingerServosMagnitude(double leftTriggerTemp, double rightTriggerTemp)
	{
		if(leftTriggerTemp > 0){
			smallFingerAngle = (byte) Math.round(leftTriggerTemp * (127 - 0) + 127);
			bigFingerAngle = (byte) Math.round(leftTriggerTemp * (127 - 0) + 127);
		} else if (leftTriggerTemp == 0){
			bigFingerAngle = (byte) Math.round(-rightTriggerTemp * (255 - 0) + 127);
		} else {
			System.err.println("leftTrigger < 0");
		}
	}
	
	private double leftTrigger, rightTrigger;
	private byte smallFingerAngle, bigFingerAngle;
	
	private double rightThumbMagnitude, rightThumbDirection;
	private byte horizontalLampServoAngle, verticalLampServoAngle;
	
	private void setLampServosMagnitude()
	{
		
	}
	
	private XboxController xc;
	private SerialPort serialPort;
	
	public void execute()
	{
		xc = new XboxController();
		xc.addXboxControllerListener(new MyXboxControllerAdapter());
		xc.setLeftThumbDeadZone(0.2);
		xc.setRightThumbDeadZone(0.2);
		
		System.out.println("Turtledemo");
		
		
	}
	
	public static void frame(String name)
	{
		JFrame frame = new JFrame("Arduino Pix");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		TurtleDemo demo = new TurtleDemo();
		AddShutdownHook sample = demo.new AddShutdownHook();
		sample.attachShutDownHook();
		demo.execute();
	}
}