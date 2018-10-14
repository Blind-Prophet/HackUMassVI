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
//creates player object
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
boolean finished=false;
//takes in data from phone to set variables
bool parseData(String data){
  //Serial.print("hello");
  int data_count = 5;
  int current_data = 0;
  String parsed_data[data_count];
  String temp = "";
  //Serial.print("    "+data.length());
  for(int i =0;i<data.length()-1;i++){
    //Serial.print("    !"+data[i]);
    if(temp==""){
      //Serial.print(data[i]);
      //check for bracket
      if(data[i]!='['){
        //improper formatting
        //Serial.print("false1");
        return false;
      }
      else{
        temp+=data[i+1];
        i++;
      }
    }else{
      //check for end bracket
      if(data[i]==']'){
        //temp is done
        //Serial.print("     "+temp+"   ayyyyyy");
        parsed_data[current_data] = temp;
        current_data++;
        if(current_data==data_count && data[i+1]!='!'){
          //if all the data is entered, the string should be done
          //Serial.print("false2");
          return false;
        }
        temp="";
      }else{
        //add to temp
        temp = temp + data[i];
      }
    }
  }
  
  for(int i=0; i<5; i++){
    //Serial.print(parsed_data[i]+" #");
    if(parsed_data[i]=="")return false;
  }
  red->playing = (parsed_data[0]=="true");
  blue->playing = (parsed_data[1]=="true");
  green->playing = (parsed_data[2]=="true");
  speed = (parsed_data[3].toInt());
  numLaps = (parsed_data[4].toInt());
  //Serial.print("      "+String(numLaps)+"   ayyyyy");
  return true;
}

void setup(){
  Serial.begin(9600);
  lcd.begin(SSD1306_SWITCHCAPVCC, 0x3C); // init LCD
  lcd.clearDisplay();
  lcd.setTextColor(WHITE);
  //lcd.setTextSize(1);
  lcd.setCursor(0,0);
  lcd.display();
  red=new Player();
  green=new Player();
  blue=new Player();
  ring.begin();
  ring.setBrightness(32);
  ring.clear(); // clear all pixels
  ring.show();  // show all pixels
  //defaults first pixel to white to verify connection
  ring.setPixelColor(0, WHITE);
  ring.show();
  speed=475; //default
  numLaps=3; //default
//  if(parseData("[true][true][false][400][4]")) running=true; //only run if the string parses properly
//  else running=false;
  red->timeLeft+=(speed + ((rand()%200)-100));
  blue->timeLeft+=(speed + ((rand()%200)-100));
  green->timeLeft+=(speed + ((rand()%200)-100));
}

void loop(){
  if(running){
    long milli=millis();
    static long timeLapsed=0;
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
    if((red->playing==true&&blue->playing==true&&green->playing==true)&&(red->currLED==blue->currLED&&red->currLED==green->currLED))ring.setPixelColor(red->currLED, WHITE);
    else if((red->playing==true&&blue->playing==true)&&(red->currLED==blue->currLED)){
      ring.setPixelColor(red->currLED, PURPLE);
      if(green->playing==true)ring.setPixelColor(green->currLED, GREEN);
    }
    else if((red->playing==true&&green->playing==true)&&(red->currLED==green->currLED)){
      ring.setPixelColor(red->currLED, YELLOW);
      if(blue->playing==true)ring.setPixelColor(blue->currLED, BLUE);
    }
    else if((green->playing==true&&blue->playing==true)&&(green->currLED==blue->currLED)){
      ring.setPixelColor(green->currLED, CIAN);
      if(red->playing==true)ring.setPixelColor(red->currLED, RED);
    }
    else{
      if(red->playing==true)ring.setPixelColor(red->currLED, RED);
      if(green->playing==true)ring.setPixelColor(green->currLED, GREEN);
      if(blue->playing==true)ring.setPixelColor(blue->currLED, BLUE);
    }
    ring.show();
    if(red->playing&&numLaps==red->lapsCompleted){
      running=false;
      red->winner=true;
      finished=true;
    }
    else if(green->playing&&numLaps==green->lapsCompleted){
      running=false;
      green->winner=true;
      finished=true;
    }
    else if(blue->playing&&numLaps==blue->lapsCompleted){
      running=false;
      blue->winner=true;
      finished=true;
    }
  }
  else if(finished){ 
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
  else{
    static String content="";
    char c;
    while(Serial.available()){
      c=Serial.read();
      //Serial.println(c);
      content.concat(c);
    }
    if(c=='!'){
      //Serial.println("    "+content+"  end of string");
      running=parseData(content);
      content="";
    }
  }
}
