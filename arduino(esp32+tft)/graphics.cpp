#include "graphics.h"

void drawHeader(TFT_eSPI &tft, String direction, String distance)
{
    uint16_t mapsGreen = tft.color565(24, 84, 52);

    tft.fillRoundRect(0, 0, 320, 40, 8, mapsGreen);
    tft.setTextColor(TFT_WHITE, mapsGreen);

    tft.setTextDatum(TL_DATUM);
    tft.drawString(direction, 12, 10, 2);

    tft.setTextDatum(TR_DATUM);
    tft.drawString(distance, 308, 10, 2);

    tft.setTextDatum(TL_DATUM);
}

void drawFooter(TFT_eSPI &tft, String eta, String remaining)
{
    tft.drawFastHLine(0, 205, 320, TFT_DARKGREY);

    tft.setTextColor(TFT_LIGHTGREY, TFT_BLACK);

    tft.setTextDatum(TL_DATUM);
    tft.drawString(eta, 10, 214, 2);

    tft.setTextDatum(TR_DATUM);
    tft.drawString(remaining, 308, 214, 2);

    tft.setTextDatum(TL_DATUM);
}

void drawLeftArrow(TFT_eSPI &tft)
{
    tft.fillRoundRect(115, 105, 130, 20, 10, TFT_WHITE);
    tft.fillTriangle(55,115,120,70,120,160,TFT_WHITE);
}

void drawRightArrow(TFT_eSPI &tft)
{
    tft.fillRoundRect(95,105,130,20,10,TFT_WHITE);
    tft.fillTriangle(255,115,200,70,200,160,TFT_WHITE);
}

void drawStraightArrow(TFT_eSPI &tft)
{
    tft.fillRoundRect(150,80,20,90,10,TFT_WHITE);
    tft.fillTriangle(160,50,120,100,200,100,TFT_WHITE);
}

void drawUTurnArrow(TFT_eSPI &tft)
{
    tft.fillRoundRect(110, 60, 20, 90, 10, TFT_WHITE);
    tft.fillRoundRect(110, 60, 80, 20, 10, TFT_WHITE);
    tft.fillRoundRect(170, 60, 20, 90, 10, TFT_WHITE);

    tft.fillTriangle(
    180, 155,   // bottom point
    150, 110,   // top left
    210, 110,   // top right
    TFT_WHITE
);
}

void drawFirstExitArrow(TFT_eSPI &tft)
{
    // straight shaft
    tft.fillRoundRect(150, 70, 20, 90, 10, TFT_WHITE);

    // straight head
    tft.fillTriangle(
        160, 40,
        130, 80,
        190, 80,
        TFT_WHITE
    );

    // exit branch
    tft.fillRoundRect(160, 100, 70, 20, 10, TFT_WHITE);

    // exit head
    tft.fillTriangle(
        250, 110,
        210, 80,
        210, 140,
        TFT_WHITE
    );
}

void drawSmallStatus(TFT_eSPI &tft, String message, uint16_t color)
{
    tft.fillScreen(TFT_BLACK);

    tft.setTextDatum(MC_DATUM);
    tft.setTextColor(color, TFT_BLACK);

    tft.drawString(message, 160, 120, 2);

    tft.setTextDatum(TL_DATUM);
}

void drawGreeting(TFT_eSPI &tft, String greeting)
{
    tft.fillScreen(TFT_BLACK);

    tft.setTextColor(TFT_GREEN, TFT_BLACK);
    tft.setTextDatum(MC_DATUM);

    tft.drawString(greeting, 160, 90, 2);
    // tft.drawString("How are you?", 160, 120, 2);
    tft.setTextColor(TFT_WHITE, TFT_BLACK);
    tft.drawString("Where to?", 160, 150, 4);

    tft.setTextDatum(TL_DATUM);
}

void drawNoNavigation(TFT_eSPI &tft)
{
    tft.fillScreen(TFT_BLACK);

    tft.setTextColor(TFT_YELLOW, TFT_BLACK);
    tft.setTextDatum(MC_DATUM);

    tft.drawString("Keep Google Maps open", 160, 100, 2);
    tft.drawString("for live updates", 160, 130, 2);

    tft.setTextDatum(TL_DATUM);
}