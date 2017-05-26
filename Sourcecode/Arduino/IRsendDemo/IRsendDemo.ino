/*
 * IRremote: IRsendDemo - demonstrates sending IR codes with IRsend
 * An IR LED must be connected to Arduino PWM pin 3.
 * Version 0.1 July, 2009
 * Copyright 2009 Ken Shirriff
 * http://arcfn.com
 */


#include <IRremote.h>
#include <SoftwareSerial.h>
IRsend irsend;
int blueTx=4;   //Tx (보내는핀 설정)at
int blueRx=5;   //Rx (받는핀 설정)
SoftwareSerial mySerial(blueTx, blueRx);
int bufferPosition; 
void setup()
{
  Serial.begin(9600);
  mySerial.begin(9600);
  bufferPosition = 0; 
}

void loop() {
  if (mySerial.available()) {       
    //Serial.write(mySerial.read());  //블루투스측 내용을 시리얼모니터에 출력
    byte data = 0;
    data = mySerial.read();
    if(data > 0){
      for (int i = 0; i < 1; i++) {
        if(data=='1') {
            irsend.sendNEC(0x616A817E, 32);//onoff
        } else if(data=='2') {
            irsend.sendNEC(0x616AE916, 32);//down
        } else if(data=='3') {
            irsend.sendNEC(0x616A6996, 32);//up
        } else if(data=='4') {
            irsend.sendNEC(0x616A9966, 32);//power
        }
        delay(40);
      }
      data = 0;
    }
  }
}
