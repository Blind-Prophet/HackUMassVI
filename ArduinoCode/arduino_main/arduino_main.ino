#include <Adafruit_SSD1306.h>
Adafruit_SSD1306 lcd(-1); // create display object

void setup(){
  Serial.begin(9600);
  lcd.begin(SSD1306_SWITCHCAPVCC, 0x3C); // init LCD
  lcd.clearDisplay();
  lcd.setTextColor(WHITE);
  lcd.setTextSize(1);
  lcd.display();
}

void loop(){
  lcd.clearDisplay();
  lcd.setCursor(0,0);
  
  String content = "";
  char character;

  while(Serial.available()) {
      character = Serial.read();
      content.concat(character);
  }

  if (content != "") {
    lcd.print(content);
  }

  lcd.display();
}
