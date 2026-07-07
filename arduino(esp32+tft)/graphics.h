#ifndef GRAPHICS_H
#define GRAPHICS_H

#include <TFT_eSPI.h>

void drawHeader(TFT_eSPI &tft, String direction, String distance);
void drawFooter(TFT_eSPI &tft, String eta, String remaining);

void drawLeftArrow(TFT_eSPI &tft);
void drawRightArrow(TFT_eSPI &tft);
void drawStraightArrow(TFT_eSPI &tft);
void drawUTurnArrow(TFT_eSPI &tft);
void drawFirstExitArrow(TFT_eSPI &tft);

void drawSmallStatus(TFT_eSPI &tft, String message, uint16_t color);
void drawGreeting(TFT_eSPI &tft, String greeting);
void drawNoNavigation(TFT_eSPI &tft);

#endif