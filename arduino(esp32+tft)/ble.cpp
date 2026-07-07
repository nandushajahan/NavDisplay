#include "ble.h"
#include "graphics.h"

#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

#define SERVICE_UUID           "6E400001-B5A3-F393-E0A9-E50E24DCCA9E"
#define CHARACTERISTIC_UUID_RX "6E400002-B5A3-F393-E0A9-E50E24DCCA9E"
#define CHARACTERISTIC_UUID_TX "6E400003-B5A3-F393-E0A9-E50E24DCCA9E"

static TFT_eSPI *display = nullptr;

BLECharacteristic *pTxCharacteristic = nullptr;
BLECharacteristic *pRxCharacteristic = nullptr;

String lastMsg = "";

String getGreeting()
{
    int h = (__TIME__[0] - '0') * 10 + (__TIME__[1] - '0');

    if (h < 12) return "Good Morning";
    if (h < 17) return "Good Afternoon";
    return "Good Evening";
}

class ServerCallbacks : public BLEServerCallbacks
{
    void onConnect(BLEServer *server)
    {
        Serial.println("BLE Client Connected");

        if (display != nullptr)
        {
            drawGreeting(*display, getGreeting());
        }
    }

    void onDisconnect(BLEServer *server)
    {
        Serial.println("BLE Client Disconnected");

        if (display != nullptr)
        {
            drawSmallStatus(
                *display,
                "Bluetooth failed. Open app.",
                TFT_RED
            );
        }

        delay(500);
        server->startAdvertising();

        Serial.println("BLE Advertising Restarted");
    }
};

class MyCallbacks : public BLECharacteristicCallbacks
{
    void onWrite(BLECharacteristic *pCharacteristic)
    {
        String msg = pCharacteristic->getValue().c_str();

        Serial.println("ESP RX: " + msg);

        if (msg == lastMsg)
        {
            if (pTxCharacteristic != nullptr)
            {
                pTxCharacteristic->setValue("OK");
                pTxCharacteristic->notify();
            }
            return;
        }

        lastMsg = msg;

        String direction = "";
        String distance = "";
        String road = "";
        String eta = "";
        String remaining = "";

        int p1 = msg.indexOf('|');
        int p2 = msg.indexOf('|', p1 + 1);
        int p3 = msg.indexOf('|', p2 + 1);
        int p4 = msg.indexOf('|', p3 + 1);

        if (p1 > 0 && p2 > p1 && p3 > p2 && p4 > p3)
        {
            direction = msg.substring(0, p1);
            distance = msg.substring(p1 + 1, p2);
            road = msg.substring(p2 + 1, p3);
            eta = msg.substring(p3 + 1, p4);
            remaining = msg.substring(p4 + 1);
        }
        else
        {
            Serial.println("Invalid message format");

            if (pTxCharacteristic != nullptr)
            {
                pTxCharacteristic->setValue("ERR");
                pTxCharacteristic->notify();
            }

            return;
        }

        direction.trim();
        distance.trim();
        road.trim();
        eta.trim();
        remaining.trim();

        if (display == nullptr)
        {
            Serial.println("Display is NULL");
            return;
        }

        if (direction == "STATUS")
        {
            drawSmallStatus(*display, road, TFT_YELLOW);
            return;
        }

        if (direction == "NONAV")
        {
            drawGreeting(*display, road);
            return;
        }

        display->fillScreen(TFT_BLACK);

        drawHeader(*display, direction, distance);

        if (direction == "LEFT")
            drawLeftArrow(*display);
        else if (direction == "RIGHT")
            drawRightArrow(*display);
        else if (direction == "UTURN")
            drawUTurnArrow(*display);
        else if (direction == "FIRSTEXIT")
            drawFirstExitArrow(*display);
        else
            drawStraightArrow(*display);

        display->setTextDatum(MC_DATUM);
        display->setTextColor(TFT_CYAN, TFT_BLACK);
        display->drawString(road, 160, 175, 4);
        display->setTextDatum(TL_DATUM);

        drawFooter(*display, eta, remaining);

        if (pTxCharacteristic != nullptr)
        {
            pTxCharacteristic->setValue("OK");
            pTxCharacteristic->notify();
            Serial.println("ESP ACK: OK");
        }
    }
};

void bleInit(TFT_eSPI *tftDisplay)
{
    display = tftDisplay;

    drawSmallStatus(*display, "Ready to pair. Open app.", TFT_YELLOW);

    BLEDevice::init("NavDisplay");

    BLEServer *server = BLEDevice::createServer();
    server->setCallbacks(new ServerCallbacks());

    BLEService *service = server->createService(SERVICE_UUID);

    pRxCharacteristic = service->createCharacteristic(
        CHARACTERISTIC_UUID_RX,
        BLECharacteristic::PROPERTY_WRITE
    );

    pRxCharacteristic->setCallbacks(new MyCallbacks());

    pTxCharacteristic = service->createCharacteristic(
        CHARACTERISTIC_UUID_TX,
        BLECharacteristic::PROPERTY_NOTIFY
    );

    pTxCharacteristic->addDescriptor(new BLE2902());

    service->start();

    BLEAdvertising *advertising = BLEDevice::getAdvertising();
    advertising->addServiceUUID(SERVICE_UUID);
    advertising->start();

    Serial.println("BLE Ready");
}

void bleLoop()
{
    delay(5);
}