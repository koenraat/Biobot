#include <Adafruit_VC0706.h>
#include <SoftwareSerial.h>

/* ==== ERRORS ==== */
/* 2 = Camera Error
 3 = Failed to Snap
 */

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

  if (cam.begin()) {
    cam.getVersion();
    cam.setImageSize(VC0706_320x240);
    cam.setCompression(250);
    uint8_t imgsize = cam.getImageSize();
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
    cumin = (magic >> 6) 0x3;
    
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
