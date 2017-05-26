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
byte buffer[1024];
int bufferPosition; 
void setup()
{
  Serial.begin(9600);
  mySerial.begin(9600);
  bufferPosition = 0; 
}

void loop() {
	for (int i = 0; i < 3; i++) {
		irsend.sendNEC(0x616A817E, 32);
		delay(40);
	}
  if (mySerial.available()) {       
    //Serial.write(mySerial.read());  //블루투스측 내용을 시리얼모니터에 출력
    byte data = mySerial.read();
    Serial.write(data);
    buffer[bufferPosition++] = data;
    if(data=='\n'){
        //buffer[bufferPosition]='\0';
        if(!strcmp(buffer,"")){
          
        }
        bufferPosition=0;
    }
  }
  if (Serial.available()) {         
    mySerial.write(Serial.read());  //시리얼 모니터 내용을 블루추스 측에 WRITE
  } //5 second delay between each signal burst
}
