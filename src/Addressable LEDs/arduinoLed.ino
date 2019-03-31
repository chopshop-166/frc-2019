#include "FastLED.h"
#define NUM_LEDS 42
CRGB leds[NUM_LEDS];
#define PIN 4

void setup()
{
    FastLED.addLeds<WS2812, PIN, GRB>(leds, NUM_LEDS).setCorrection(TypicalLEDStrip);
}

void loop()
{
    CylonBounce(0, 0, 0xff, 6, 8, 30);
}

void CylonBounce(byte red, byte green, byte blue, int EyeSize, int SpeedDelay, int ReturnDelay)
{

    for (int i = 0; i < NUM_LEDS - EyeSize - 2; i++)
    {
        setAll(255, 255, 255);
        setPixel(i, red / 10, green / 10, blue / 10);
        for (int j = 1; j <= EyeSize; j++)
        {
            setPixel(i + j, red, green, blue);
        }
        setPixel(i + EyeSize + 1, red / 10, green / 10, blue / 10);
        showStrip();
        delay(SpeedDelay);
    }

    delay(ReturnDelay);

    for (int i = NUM_LEDS - EyeSize - 2; i > 0; i--)
    {
        setAll(255, 255, 255);
        setPixel(i, red / 10, green / 10, blue / 10);
        for (int j = 1; j <= EyeSize; j++)
        {
            setPixel(i + j, red, green, blue);
        }
        setPixel(i + EyeSize + 1, red / 10, green / 10, blue / 10);
        showStrip();
        delay(SpeedDelay);
    }

    delay(ReturnDelay);
}
void showStrip()
{
#ifdef ADAFRUIT_NEOPIXEL_H
    // NeoPixel
    strip.show();
#endif
#ifndef ADAFRUIT_NEOPIXEL_H
    // FastLED
    FastLED.show();
#endif
}

void setPixel(int Pixel, byte red, byte green, byte blue)
{
#ifdef ADAFRUIT_NEOPIXEL_H
    // NeoPixel
    strip.setPixelColor(Pixel, strip.Color(red, green, blue));
#endif
#ifndef ADAFRUIT_NEOPIXEL_H
    // FastLED
    leds[Pixel].r = red;
    leds[Pixel].g = green;
    leds[Pixel].b = blue;
#endif
}

void setAll(byte red, byte green, byte blue)
{
    for (int i = 0; i < NUM_LEDS; i++)
    {
        setPixel(i, red, green, blue);
    }
    showStrip();
}
