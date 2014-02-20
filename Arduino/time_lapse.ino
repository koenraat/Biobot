#include <Adafruit_VC0706.h>
#include <SoftwareSerial.h>

/* ==== ERRORS ==== */
/* 2 = Camera Error
 3 = Failed to Snap
 */
 
char filename[14];

Adafruit_VC0706 cam = Adafruit_VC0706(&Serial2);
void takepic();

uint8_t hello = 0;

void setup() {
  Serial1.begin(38400);
  Serial.begin(38400);
  Serial.println("Time Lapse Maker GO!");

  // Find camera
  if (cam.begin()) {
    cam.getVersion();
    //Serial.println("Camera Found");
    cam.setImageSize(VC0706_320x240);  // 320x240 // 160x120
    cam.setCompression(250);
    uint8_t imgsize = cam.getImageSize();
    cam.resumeVideo();
    //Serial.print("Image resolution set to: ");
    //if (imgsize == VC0706_640x480) Serial.println("640x480");
    //if (imgsize == VC0706_320x240) Serial.println("320x240");
    //if (imgsize == VC0706_160x120) Serial.println("160x120");
  } 
  else {
    Serial.println("Camera NOT Found");
  }
}

void loop() 
{
  if(Serial1.available() > 0) {
    hello = Serial1.read();
    Serial.print("I received: ");
    Serial.println(hello);
    if(hello == 1)
    {
      Serial.println("Hello!");
      takepic();
    }
    else
    {
      Serial.println("Bye!");
    }
  }
}
void takepic() {
  cam.resumeVideo();
    if (! cam.takePicture()) {
      Serial.println("Failed to snap!");
    }
    //Serial.println("I'm waiting!");
    //while(btSerial.available() == 0)
    //{ }
    uint8_t hello = 0xA;//btSerial.read();
    if(hello == 0xA)
    {
      //Serial.println("Hi!");
      // Get the size of the image (frame) taken  
      uint16_t jpglen = cam.frameLength();
      //Serial.println("Will write now");
      //Serial.print("Storing ");
      //Serial.println(jpglen, DEC);
      //Serial.print(" byte image.");
      
      uint8_t bytesToSend[2];
      bytesToSend[0]=jpglen & 0xff;
      bytesToSend[1]=(jpglen >> 8);
      //Serial.println(jpglen);
      uint16_t test = bytesToSend[0] + (bytesToSend[1] << 8);
      //Serial.println(test, DEC);
      Serial1.write(bytesToSend, 2);
      while (jpglen > 0) {
        uint8_t *buffer;
        uint8_t bytesToRead = min(32, jpglen); // change 32 to 64 for a speedup but may not work with all setups!
        buffer = cam.readPicture(bytesToRead);
        Serial1.write(buffer, bytesToRead);
        //Serial.print("Read ");  Serial.print(bytesToRead, DEC); Serial.println(" bytes");
        jpglen -= bytesToRead;
      }
      cam.resumeVideo();
  }
}
