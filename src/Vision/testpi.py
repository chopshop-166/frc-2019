#!/usr/bin/env python3
# ----------------------------------------------------------------------------
# Copyright (c) 2018 FIRST. All Rights Reserved.
# Open Source Software - may be modified and shared by FRC teams. The code
# must be accompanied by the FIRST BSD license file in the root directory of
# the project.
# ----------------------------------------------------------------------------
import cv2
import json
import time
import sys
import numpy as np

from cscore import CameraServer, VideoSource, UsbCamera, MjpegServer
from networktables import NetworkTablesInstance


#   JSON format:
#   {
#       "team": <team number>,
#       "ntmode": <"client" or "server", "client" if unspecified>
#       "cameras": [
#           {
#               "name": <camera name>
#               "path": <path, e.g. "/dev/video0">
#               "pixel format": <"MJPEG", "YUYV", etc>   // optional
#               "width": <video mode width>              // optional
#               "height": <video mode height>            // optional
#               "fps": <video mode fps>                  // optional
#               "brightness": <percentage brightness>    // optional
#               "white balance": <"auto", "hold", value> // optional
#               "exposure": <"auto", "hold", value>      // optional
#               "properties": [                          // optional
#                   {
#                       "name": <property name>
#                       "value": <property value>
#                   }
#               ],
#               "stream": {                              // optional
#                   "properties": [
#                       {
#                           "name": <stream property name>
#                           "value": <stream property value>
#                       }
#                   ]
#               }
#           }
#       ]
#   }
class gripV2:

    """

    An OpenCV pipeline generated by GRIP.
    """

    def __init__(self):
        """initializes all values to presets or None if need to be set
        """

        self.__hsl_threshold_hue = [70, 101]
        self.__hsl_threshold_saturation = [147, 255.0]
        self.__hsl_threshold_luminance = [37, 255.0]

        self.hsl_threshold_output = None

        self.__find_contours_input = self.hsl_threshold_output
        self.__find_contours_external_only = False

        self.find_contours_output = None

    def process(self, source0):
        """
        Runs the pipeline and sets all outputs to new values.
        """
        # Step HSL_Threshold0:
        self.__hsl_threshold_input = source0
        (self.hsl_threshold_output) = self.__hsl_threshold(self.__hsl_threshold_input,
                                                           self.__hsl_threshold_hue, self.__hsl_threshold_saturation, self.__hsl_threshold_luminance)

        # Step Find_Contours0:
        self.__find_contours_input = self.hsl_threshold_output
        (self.find_contours_output) = self.__find_contours(
            self.__find_contours_input, self.__find_contours_external_only)

    @staticmethod
    def __hsl_threshold(input, hue, sat, lum):
        """Segment an image based on hue, saturation, and luminance ranges.
        Args:
            input: A BGR numpy.ndarray.
            hue: A list of two numbers the are the min and max hue.
            sat: A list of two numbers the are the min and max saturation.
            lum: A list of two numbers the are the min and max luminance.
        Returns:
            A black and white numpy.ndarray.
        """
        out = cv2.cvtColor(input, cv2.COLOR_BGR2HLS)
        return cv2.inRange(out, (hue[0], lum[0], sat[0]), (hue[1], lum[1], sat[1]))

    @staticmethod
    def __find_contours(input, external_only):
        """Sets the values of pixels in a binary image to their distance to the nearest black pixel.
        Args:
            input: A numpy.ndarray.
            external_only: A boolean. If true only external contours are found.
        Return:
            A list of numpy.ndarray where each one represents a contour.
        """
        if(external_only):
            mode = cv2.RETR_EXTERNAL
        else:
            mode = cv2.RETR_LIST
        method = cv2.CHAIN_APPROX_SIMPLE
        im2, contours, hierarchy = cv2.findContours(
            input, mode=mode, method=method)
        return contours


configFile = "/boot/frc.json"


class CameraConfig:
    pass


rightAngle = -15
leftAngle = -75
deadzone = 15

team = None
server = False
width = None
height = None
imageMidpoint = None

cameraConfigs = []

"""Report parse error."""


def parseError(mode):
    print("config error in '" + configFile + "': " + mode, file=sys.stderr)


"""Read single camera configuration."""


def readCameraConfig(config):
    cam = CameraConfig()
    global width
    global height
    global imageMidpoint

    # name
    try:
        cam.name = config["name"]
    except KeyError:
        parseError("could not read camera name")
        return False

    # path
    try:
        cam.path = config["path"]
    except KeyError:
        parseError("camera '{}': could not read path".format(cam.name))
        return False

    try:
        width = config["width"]
        imageMidpoint = (width / 2)
    except KeyError:
        parseError("could not read width")
        return False

    try:
        height = config["height"]
    except KeyError:
        parseError("could not read height")
        return False

    # stream properties
    cam.streamConfig = config.get("stream")

    cam.config = config

    cameraConfigs.append(cam)
    return True


"""Read configuration file."""


def readConfig():
    global team
    global server

    # parse file
    try:
        with open(configFile, "rt") as f:
            j = json.load(f)
    except OSError as err:
        print("could not open '{}': {}".format(
            configFile, err), file=sys.stderr)
        return False

    # top level must be an object
    if not isinstance(j, dict):
        parseError("must be JSON object")
        return False

    # team number
    try:
        team = j["team"]
    except KeyError:
        parseError("could not read team number")
        return False

    # ntmode (optional)
    if "ntmode" in j:
        mode = j["ntmode"]
        if mode.lower() == "client":
            server = False
        elif mode.lower() == "server":
            server = True
        else:
            parseError("could not understand ntmode value '{}'".format(mode))

    # cameras
    try:
        cameras = j["cameras"]
    except KeyError:
        parseError("could not read cameras")
        return False
    for camera in cameras:
        if not readCameraConfig(camera):
            return False

    return True


"""Start running the camera."""


def startCamera(config):
    print("Starting camera '{}' on {}".format(config.name, config.path))
    inst = CameraServer.getInstance()
    camera = UsbCamera(config.name, config.path)
    server = inst.startAutomaticCapture(camera=camera, return_server=True)

    camera.setConfigJson(json.dumps(config.config))
    camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen)

    if config.streamConfig is not None:
        server.setConfigJson(json.dumps(config.streamConfig))

    return camera


def findPairOffset(pair):
    leftcX = pair[0][0][0]
    rightcX = pair[1][0][0]
    pairMidpoint = (leftcX + rightcX) / 2
    pairOffset = abs(imageMidpoint - pairMidpoint)
    return pairOffset


def findPairs(contourList):
    rightRectList = []
    leftRectList = []
    rectPairList = []

    for contour in contourList:
        rectangleBox = cv2.minAreaRect(contour)
        if rectangleBox[2] > rightAngle - deadzone and rectangleBox[2] < rightAngle + deadzone:
            rightRectList.append(rectangleBox)

        if rectangleBox[2] > leftAngle - deadzone and rectangleBox[2] < leftAngle + deadzone:
            leftRectList.append(rectangleBox)

    for leftRect in leftRectList:
        pair = (leftRect, None)
        for rightRect in rightRectList:
            # 0 is center and 0 is x coordinate
            if rightRect[0][0] > leftRect[0][0]:
                if pair[1] is None or rightRect[0][0] < pair[1][0][0]:
                    pair = (leftRect, rightRect)
        if pair[1] != None:
            rectPairList.append(pair)

    closestCenterPair = None
    for pair in rectPairList:
        if closestCenterPair is None or findPairOffset(pair) < findPairOffset(closestCenterPair):
            closestCenterPair = pair

    return closestCenterPair


def normalizeImage(pairMidpoint):
    pairPointOrient = (pairMidpoint - imageMidpoint)
    fullPairMidpoint = (pairPointOrient / imageMidpoint)
    # prints after ewww math THIS IS THE CONTOUR MIDPOINT from...
    # Contour Midpoint Scale (-1 to 1)
    return fullPairMidpoint 


if __name__ == "__main__":
    if len(sys.argv) >= 2:
        configFile = sys.argv[1]

    # read configuration
    if not readConfig():
        sys.exit(1)

    print("width: {} height: {} Midpoint: {}".format(
        width, height, imageMidpoint))
    # start NetworkTables
    ntinst = NetworkTablesInstance.getDefault()
    if server:
        print("Setting up NetworkTables server")
        ntinst.startServer()
    else:
        print("Setting up NetworkTables client for team {}".format(team))
        ntinst.startClientTeam(team)

    table = ntinst.getTable('Vision Correction Table')

    # start cameras
    cameras = []
    for cameraConfig in cameraConfigs:
        cameras.append(startCamera(cameraConfig))

    cvSink = CameraServer.getInstance().getVideo()
    outputstream = CameraServer.getInstance().putVideo("Front Camera", width, height)

    # Preallocating the frame object
    frame = np.zeros(shape=(height, width, 3), dtype=np.uint8)

    goalFinder = gripV2()

    # loop forever
    # BIG LOOP!!!!!!!!
    while True:
        # capture image
        cvSink.grabFrame(frame)

        width = frame.shape[1]
        height = frame.shape[0]
        # process with GRIP stuff
        goalFinder.process(frame)

        # filter returned contours
        filteredContours = []
        for contour in goalFinder.find_contours_output:
            area = cv2.contourArea(contour)
            # Need to adjust area value based on distance
            if area > 100:
                filteredContours.append(contour)

        # find best pair, if we found contours
        if len(filteredContours) >= 2:
            bestPair = findPairs(filteredContours)
            if bestPair != None:
                table.putBoolean("Vision Found", True)
                rectangleMidpoint = (int((bestPair[1][0][0] + bestPair[0][0][0]) / 2), int(
                    (bestPair[1][0][1] + bestPair[0][0][1]) / 2))
                table.putNumber("Vision Correction",
                                normalizeImage(rectangleMidpoint[0]))
                cv2.circle(frame, (rectangleMidpoint), 3, (0, 255, 0), -1)
            else:
                table.putBoolean("Vision Found", False)
        else:
            table.putBoolean("Vision Found", False)

        outputstream.putFrame(frame)

# therealvisioncode
#         insert vision
#  = hatch panel placement
#  = work well
# +ballo vision
#  +that good code
#     +even better code
# reflective tape
# +LEDs
#  = reflection

# reflection
# +camera
#  = feedback to computer

# feedback to computer
# +software
#  = computer tells robot to do thing

# computer tells robot to do thing
# +robot
# +having game piece
#  = game piece PLACED !
