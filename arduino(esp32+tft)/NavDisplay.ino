#include <TFT_eSPI.h>
#include "graphics.h"
#include "ble.h"

TFT_eSPI tft;

void setup()
{
    Serial.begin(115200);

    tft.init();
    tft.setRotation(1);
    tft.fillScreen(TFT_BLACK);

    drawHeader(tft, "READY", "--");
    drawStraightArrow(tft);
    drawFooter(tft, "", "");

    bleInit(&tft);
}

void loop()
{
    bleLoop();
}