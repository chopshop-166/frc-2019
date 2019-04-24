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
    // NewKITT(0, 0, 0xff, 8, 10, 50);
    colorWipe(0x00, 0x00, 0xff, 20);
    colorWipe(0x00, 0x00, 0x00, 20);
    // meteorRain(0xff, 0xff, 0xff, 10, 64, true, 15);
    // Fire(30, 200, 0);
    // rainbowCycle(5);
    // RunningLights(0xff, 0xff, 0xff, 50);
    // theaterChase(0, 0, 0xff, 50);
}

void NewKITT(byte red, byte green, byte blue, int EyeSize, int SpeedDelay, int ReturnDelay)
{
    OutsideToCenter(red, green, blue, EyeSize, SpeedDelay, ReturnDelay);
    CenterToOutside(red, green, blue, EyeSize, SpeedDelay, ReturnDelay);
}

void CenterToOutside(byte red, byte green, byte blue, int EyeSize, int SpeedDelay, int ReturnDelay)
{
    for (int i = ((NUM_LEDS - EyeSize) / 2); i >= 0; i--)
    {
        setAll(0, 0, 0);

        setPixel(i, red / 10, green / 10, blue / 10);
        for (int j = 1; j <= EyeSize; j++)
        {
            setPixel(i + j, red, green, blue);
        }
        setPixel(i + EyeSize + 1, red / 10, green / 10, blue / 10);

        setPixel(NUM_LEDS - i, red / 10, green / 10, blue / 10);
        for (int j = 1; j <= EyeSize; j++)
        {
            setPixel(NUM_LEDS - i - j, red, green, blue);
        }
        setPixel(NUM_LEDS - i - EyeSize - 1, red / 10, green / 10, blue / 10);

        showStrip();
        delay(SpeedDelay);
    }
    delay(ReturnDelay);
}

void OutsideToCenter(byte red, byte green, byte blue, int EyeSize, int SpeedDelay, int ReturnDelay)
{
    for (int i = 0; i <= ((NUM_LEDS - EyeSize) / 2); i++)
    {
        setAll(0, 0, 0);

        setPixel(i, red / 10, green / 10, blue / 10);
        for (int j = 1; j <= EyeSize; j++)
        {
            setPixel(i + j, red, green, blue);
        }
        setPixel(i + EyeSize + 1, red / 10, green / 10, blue / 10);

        setPixel(NUM_LEDS - i, red / 10, green / 10, blue / 10);
        for (int j = 1; j <= EyeSize; j++)
        {
            setPixel(NUM_LEDS - i - j, red, green, blue);
        }
        setPixel(NUM_LEDS - i - EyeSize - 1, red / 10, green / 10, blue / 10);

        showStrip();
        delay(SpeedDelay);
    }
    delay(ReturnDelay);
}

void LeftToRight(byte red, byte green, byte blue, int EyeSize, int SpeedDelay, int ReturnDelay)
{
    for (int i = 0; i < NUM_LEDS - EyeSize - 2; i++)
    {
        setAll(0, 0, 0);
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

void RightToLeft(byte red, byte green, byte blue, int EyeSize, int SpeedDelay, int ReturnDelay)
{
    for (int i = NUM_LEDS - EyeSize - 2; i > 0; i--)
    {
        setAll(0, 0, 0);
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
void colorWipe(byte red, byte green, byte blue, int SpeedDelay)
{
    for (uint16_t i = 0; i < NUM_LEDS; i++)
    {
        setPixel(i, red, green, blue);
        showStrip();
        delay(SpeedDelay);
    }
}

void meteorRain(byte red, byte green, byte blue, byte meteorSize, byte meteorTrailDecay, boolean meteorRandomDecay, int SpeedDelay)
{
    setAll(0, 0, 255);

    for (int i = 0; i < NUM_LEDS + NUM_LEDS; i++)
    {

        // fade brightness all LEDs one step
        for (int j = 0; j < NUM_LEDS; j++)
        {
            if ((!meteorRandomDecay) || (random(10) > 5))
            {
                fadeToBlack(j, meteorTrailDecay);
            }
        }

        // draw meteor
        for (int j = 0; j < meteorSize; j++)
        {
            if ((i - j < NUM_LEDS) && (i - j >= 0))
            {
                setPixel(i - j, red, green, blue);
            }
        }

        showStrip();
        delay(SpeedDelay);
    }
}
void Fire(int Cooling, int Sparking, int SpeedDelay)
{
    static byte heat[NUM_LEDS];
    int cooldown;

    // Step 1.  Cool down every cell a little
    for (int i = 0; i < NUM_LEDS; i++)
    {
        cooldown = random(0, ((Cooling * 10) / NUM_LEDS) + 2);

        if (cooldown > heat[i])
        {
            heat[i] = 0;
        }
        else
        {
            heat[i] = heat[i] - cooldown;
        }
    }

    // Step 2.  Heat from each cell drifts 'up' and diffuses a little
    for (int k = NUM_LEDS - 1; k >= 2; k--)
    {
        heat[k] = (heat[k - 1] + heat[k - 2] + heat[k - 2]) / 3;
    }

    // Step 3.  Randomly ignite new 'sparks' near the bottom
    if (random(255) < Sparking)
    {
        int y = random(7);
        heat[y] = heat[y] + random(160, 255);
        //heat[y] = random(160,255);
    }

    // Step 4.  Convert heat to LED colors
    for (int j = 0; j < NUM_LEDS; j++)
    {
        setPixelHeatColor(j, heat[j]);
    }

    showStrip();
    delay(SpeedDelay);
}

void setPixelHeatColor(int Pixel, byte temperature)
{
    // Scale 'heat' down from 0-255 to 0-191
    byte t192 = round((temperature / 255.0) * 191);

    // calculate ramp up from
    byte heatramp = t192 & 0x3F; // 0..63
    heatramp <<= 2;              // scale up to 0..252

    // figure out which third of the spectrum we're in:
    if (t192 > 0x80)
    { // hottest
        setPixel(Pixel, 255, 255, heatramp);
    }
    else if (t192 > 0x40)
    { // middle
        setPixel(Pixel, 255, heatramp, 0);
    }
    else
    { // coolest
        setPixel(Pixel, heatramp, 0, 0);
    }
}
void rainbowCycle(int SpeedDelay)
{
    byte *c;
    uint16_t i, j;

    for (j = 0; j < 256 * 5; j++)
    { // 5 cycles of all colors on wheel
        for (i = 0; i < NUM_LEDS; i++)
        {
            c = Wheel(((i * 256 / NUM_LEDS) + j) & 255);
            setPixel(i, *c, *(c + 1), *(c + 2));
        }
        showStrip();
        delay(SpeedDelay);
    }
}

byte *Wheel(byte WheelPos)
{
    static byte c[3];

    if (WheelPos < 85)
    {
        c[0] = WheelPos * 3;
        c[1] = 255 - WheelPos * 3;
        c[2] = 0;
    }
    else if (WheelPos < 170)
    {
        WheelPos -= 85;
        c[0] = 255 - WheelPos * 3;
        c[1] = 0;
        c[2] = WheelPos * 3;
    }
    else
    {
        WheelPos -= 170;
        c[0] = 0;
        c[1] = WheelPos * 3;
        c[2] = 255 - WheelPos * 3;
    }

    return c;
}
void fadeToBlack(int ledNo, byte fadeValue)
{
#ifdef ADAFRUIT_NEOPIXEL_H
    // NeoPixel
    uint32_t oldColor;
    uint8_t r, g, b;
    int value;

    oldColor = strip.getPixelColor(ledNo);
    r = (oldColor & 0x00ff0000UL) >> 16;
    g = (oldColor & 0x0000ff00UL) >> 8;
    b = (oldColor & 0x000000ffUL);

    r = (r <= 10) ? 0 : (int)r - (r * fadeValue / 256);
    g = (g <= 10) ? 0 : (int)g - (g * fadeValue / 256);
    b = (b <= 10) ? 0 : (int)b - (b * fadeValue / 256);

    strip.setPixelColor(ledNo, r, g, b);
#endif
#ifndef ADAFRUIT_NEOPIXEL_H
    // FastLED
    leds[ledNo].fadeToBlackBy(fadeValue);
#endif
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
