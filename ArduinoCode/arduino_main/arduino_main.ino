#include <Adafruit_SSD1306.h>
Adafruit_SSD1306 lcd(-1); // create display object
#include <Adafruit_NeoPixel.h>
#define RED 0xFF0000
#define BLUE 0x0000FF
#define GREEN 0x00FF00
#define WHITE 0xFFFFFF
#define PURPLE 0xFF00FF
#define YELLOW 0xFFFF00
#define CIAN 0x00FFFF
#define PIN 4
#define POT 6

Adafruit_NeoPixel ring = Adafruit_NeoPixel(16, PIN, NEO_GRB + NEO_KHZ800);

class Player{
  public:
  long speed;
  boolean playing=true;
  int currLED=0;
  long timeLeft;
  int lapsCompleted=0;
  boolean winner;
};
Player *red, *green, *blue;
long speed;
int numLaps;
boolean running;
void setup(){
  Serial.begin(9600);
  lcd.begin(SSD1306_SWITCHCAPVCC, 0x3C); // init LCD
  lcd.clearDisplay();
  lcd.setTextColor(WHITE);
  lcd.setTextSize(1);
  lcd.display();
  red=new Player();
  green=new Player();
  blue=new Player();
  ring.begin();
  ring.setBrightness(32);
  ring.clear(); // clear all pixels
  ring.show();  // show all pixels
  speed=475;
  numLaps=3;
  running=true;
  red->timeLeft+=(speed + ((rand()%200)-100));
  blue->timeLeft+=(speed + ((rand()%200)-100));
  green->timeLeft+=(speed + ((rand()%200)-100));
}

void loop(){
  if(running){
    long milli=millis();
    static long timeLapsed=0;
    lcd.clearDisplay();
    lcd.setCursor(0,0);
    red->timeLeft-=(milli-timeLapsed);
    blue->timeLeft-=(milli-timeLapsed);
    green->timeLeft-=(milli-timeLapsed);
    if(red->timeLeft<=0){
      red->currLED++;
      if((red->currLED)>=ring.numPixels()){
        red->currLED=0;
        red->lapsCompleted++;
      }
      red->timeLeft+=(speed + ((rand()%200)-100));
    }
    if(blue->timeLeft<=0){
      blue->currLED++;
      if((blue->currLED)>=ring.numPixels()){
        blue->currLED=0;
        blue->lapsCompleted++;
      }
      blue->timeLeft+=(speed + ((rand()%200)-100));
    }
    if(green->timeLeft<=0){
      green->currLED++;
      if((green->currLED)>=ring.numPixels()){
        green->currLED=0;
        blue->lapsCompleted;
      }
      green->timeLeft+=(speed + ((rand()%200)-100));
    }
    timeLapsed=milli;
    ring.clear();
    if(red->currLED==blue->currLED&&red->currLED==green->currLED)ring.setPixelColor(red->currLED, WHITE);
    else if(red->currLED==blue->currLED){
      ring.setPixelColor(red->currLED, PURPLE);
      ring.setPixelColor(green->currLED, GREEN);
    }
    else if(red->currLED==green->currLED){
      ring.setPixelColor(red->currLED, YELLOW);
      ring.setPixelColor(blue->currLED, BLUE);
    }
    else if(green->currLED==blue->currLED){
      ring.setPixelColor(green->currLED, CIAN);
      ring.setPixelColor(red->currLED, RED);
    }
    else{
      ring.setPixelColor(red->currLED, RED);
      ring.setPixelColor(green->currLED, GREEN);
      ring.setPixelColor(blue->currLED, BLUE);
    }
    ring.show();
    if(numLaps==red->lapsCompleted){
      running=false;
      red->winner=true;
    }
    else if(numLaps==green->lapsCompleted){
      running=false;
      green->winner=true;
    }
    else if(numLaps==blue->lapsCompleted){
      running=false;
      blue->winner=true;
    }
  }
  else{ 
    if(blue->winner==true){
      for(int i=0; i<ring.numPixels(); i++){
        ring.setPixelColor(i, BLUE);
        ring.show();
        delay(100);
      }
    }
    else if(red->winner==true){
      for(int i=0; i<ring.numPixels(); i++){
        ring.setPixelColor(i, RED);
        ring.show();
        delay(100);
      }
    }
    else{
      for(int i=0; i<ring.numPixels(); i++){
        ring.setPixelColor(i, GREEN);
        ring.show();
        delay(100);
      }
    }
    ring.clear();
  }
}
