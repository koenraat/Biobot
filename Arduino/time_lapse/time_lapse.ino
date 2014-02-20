#include <Adafruit_VC0706.h>
#include <SoftwareSerial.h>
#include <Servo.h>

#define MOTOR_A 0
#define MOTOR_B 1
#define CW 0
#define CCW 1
#define DIRA 12
#define PWMA 3
#define DIRB 13
#define PWMB 11
#define SERVO1 5
#define SERVO2 6
#define SERVO3 9
#define SERVO4 10

Servo servo1;
Servo servo2;
Servo servo3;
Servo servo4;

Adafruit_VC0706 cam = Adafruit_VC0706(&Serial2);
void takepic();

uint8_t left = 0;
uint8_t right = 0;
uint8_t s1 = 0;
uint8_t s2 = 0;
uint8_t s3 = 0;
uint8_t s4 = 0;
uint8_t magic = 0;

uint8_t cam1 = 0;
uint8_t cam2 = 0;
uint8_t cam3 = 0;
uint8_t cumin = 0;



void setup() {
  Serial1.begin(38400);
  Serial.begin(38400);
  setupArdumoto();
  servo1.attach(SERVO1);
  servo2.attach(SERVO2);
  servo3.attach(SERVO3);
  servo4.attach(SERVO4);

  if (cam.begin()) {
    cam.setCompression(250);
    cam.resumeVideo();
  } 
  else {
    Serial.println("Camera NOT Found");
  }
}

void loop() 
{
  if(Serial1.available() > 0) {
    left = Serial1.read();
    right = Serial1.read();
    s1 = Serial1.read();
    s2 = Serial1.read();
    s3 = Serial1.read();
    s4 = Serial1.read();
    magic = Serial1.read();
    
    cam1 = magic & 0x3;
    cam2 = (magic >> 2) & 0x3;
    cam3 = (magic >> 4) & 0x3;
    cumin = (magic >> 6) & 0x3;
    
    velocityToArdu(MOTOR_A, left);
    velocityToArdu(MOTOR_B, right);
    
    servo1.write(s1);
    servo2.write(s2);
    servo3.write(s3);
    servo4.write(s4);
    
    if(cam1 || cam2 || cam3)
    {
      Serial.println("I will take a picture!");
      takepic();
    }
    else
    {
      Serial.println("No pix! See you!");
    }
  }
}

void velocityToArdu(uint8_t motor, uint8_t v)
{
  if(v > 0)
  {
    driveArdumoto(motor, CW, v);
  } else {
    driveArdumoto(motor, CCW, (-1)*v);
  }
}

void takepic() {
    if(cam1)
    {
      cam.setImageSize(VC0706_160x120);
    }
    
    if(cam2)
    {
       cam.setImageSize(VC0706_320x240);
    }
    
    if(cam3)
    {
      cam.setImageSize(VC0706_640x480);
    }
    
    cam.resumeVideo();
  
    if (! cam.takePicture())
    {
      Serial.println("Failed to snap!");
    }

    uint16_t jpglen = cam.frameLength();
    
    uint8_t bytesToSend[2];
    bytesToSend[0]=jpglen & 0xff;
    bytesToSend[1]=(jpglen >> 8);
    uint16_t test = bytesToSend[0] + (bytesToSend[1] << 8);
    Serial1.write(bytesToSend, 2);
    while (jpglen > 0) {
      uint8_t *buffer;
      uint8_t bytesToRead = min(32, jpglen); // change 32 to 64 for a speedup but may not work with all setups!
      buffer = cam.readPicture(bytesToRead);
      Serial1.write(buffer, bytesToRead);
      jpglen -= bytesToRead;
    }
    cam.resumeVideo();
}

void driveArdumoto(byte motor, byte dir, byte spd) {


  if (motor == MOTOR_A)
  {
    digitalWrite(DIRA, dir);
    analogWrite(PWMA, spd);
  }
  else if (motor == MOTOR_B)
  {
    digitalWrite(DIRB, dir);
    analogWrite(PWMB, spd);
  }  
}

// stopArdumoto makes a motor stop
void stopArdumoto(byte motor)
{
  driveArdumoto(motor, 0, 0);
}

// setupArdumoto initialize all pins
void setupArdumoto()
{
  // All pins should be setup as outputs:
  pinMode(PWMA, OUTPUT);
  pinMode(PWMB, OUTPUT);
  pinMode(DIRA, OUTPUT);
  pinMode(DIRB, OUTPUT);

  // Initialize all pins as low:
  digitalWrite(PWMA, LOW);
  digitalWrite(PWMB, LOW);
  digitalWrite(DIRA, LOW);
  digitalWrite(DIRB, LOW);
}
