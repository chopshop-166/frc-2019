import gripV2
import cv2
import numpy as np
import threading
import math
# from networktables import NetworkTables


# cond = threading.Condition()
# notified = [False]

# def connectionListener(connected, info):
#     print(info, '; Connected=%s' % connected)
#     with cond:
#         notified[0] = True
#         cond.notify()

# NetworkTables.initialize(server='10.1.66.2')
# NetworkTables.addConnectionListener(connectionListener, immediateNotify=True)
# table = NetworkTables.getTable('Vision Correction Table')
# with cond:
#     if not notified[0]:
#         cond.wait()

cap = cv2.VideoCapture(1)
cap.set(10,30)

goalFinder = gripV2.GripiPipeline()

rightAngle = -15
leftAngle = -75

deadzone = 5
#frame = cv2.imread('Examples/2.jpg',1)
#goalFinder.process(cap)

#First Search for the -15 degree contour and add to first part of pair,
#then search for -75 degree contour, put in pair, assure the -75 contour is to the right of the -15 degree contour,
#then assure the -75 contour is the closest contour to the -15,
#by searching for other -75 contours that are either closer or farther
#DONEEEEEEEE!!!!!!!!

#find center of each pair then identify which pair is closest to image center
def findPairs(contourList):
    #right box is -15 degrees
    rightRectList = []
    #left box is -75 degrees
    leftRectList = []

    rectPairList = []
    for contour in contourList:
        rectangleBox = cv2.minAreaRect(contour)

        
        if rectangleBox[2] > rightAngle - deadzone and rectangleBox[2] < rightAngle + deadzone:
            rightRectList.append(rectangleBox)
       
        if rectangleBox[2] > leftAngle - deadzone and rectangleBox[2] < leftAngle + deadzone:
            leftRectList.append(rectangleBox) 

    for leftRect in leftRectList:
        pair = (leftRect,None)
        for rightRect in rightRectList:
            #0 is center and 0 is x coordinate
            if rightRect[0][0] > leftRect[0][0]:
                if pair[1] is None or rightRect[0][0] < pair[1][0][0]:
                    pair = (leftRect,rightRect)
        if pair[1] != None:
            rectPairList.append(pair)

    for pair in rectPairList:
        print ("rectangle pair coordinates: {}, {}".format(pair[0][0][0],pair[1][0][0]))

while(True):



    ret, frame = cap.read()
    cv2.waitKey(300)

    goalFinder.process(frame)

    filteredContours=[]
    for contour in goalFinder.find_contours_output:

        area=cv2.contourArea(contour)
        if area>1000:
            filteredContours.append(contour)

    if len(filteredContours) == 0:
        cv2.imshow('image',frame)
        continue 


    totalX, totalY = 0, 0

    for contour in filteredContours:
        rect = cv2.minAreaRect(contour)
        box = cv2.boxPoints(rect)
        box = np.int0(box)
        cv2.drawContours(frame,[box],0,(0,0,255),2)

        cX,cY = rect[0]
        cX = int(cX)
        cY = int(cY)
        theta = rect[2]
        #print ("center and angles: ({},{}), {}".format(cX,cY, (theta)))

        vX = int(math.cos(math.radians(theta)) * 70 + cX)
        vY = int(math.sin(math.radians(theta)) * 70 + cY)
        wX = int(math.cos(math.radians(theta)) * -70 + cX)
        wY = int(math.sin(math.radians(theta)) * -70 + cY)
        #Draws contour lines based off angles of contours
        cv2.line(frame, (cX,cY), (vX,vY), (255,0,255), 2)
        cv2.line(frame, (cX,cY), (wX,wY), (255,0,255), 2)



        totalX += cX
        totalY += cY

        #print("contour points {},{}".format(cX,cY))
        cv2.circle(frame, (cX, cY), 7, (255, 0, 0), -1)

        #cv2.drawContours(frame, [contour], -1, (255, 255, 0), 2)

    findPairs(filteredContours)
    avgPoint = (int(totalX / len(filteredContours)), int(totalY / len(filteredContours)))

    dimensions = frame.shape
    width = frame.shape[1]



    #coordinate i have then subtract center in pixels to find midpoint
    imageMidpoint = (width / 2)
    avgPointOrient = (avgPoint[0] - imageMidpoint)
    fullContourMidpoint = (avgPointOrient / imageMidpoint)
    # prints after ewww math THIS IS THE CONTOUR MIDPOINT from...
    # Contour Midpoint Scale (-1 to 1)
    #print('Final Contour Midpoint: ',fullContourMidpoint)

    #table.putNumber("Vision Correction", fullContourMidpoint)

    #print("Average Contour Point {},{}".format(avgPoint[0],avgPoint[1]))
    cv2.circle(frame, (avgPoint), 7, (255, 255, 255), -1)

    cv2.imshow('image',frame)
    #filteredContours.clear()


cv2.waitKey(0)
cap.release()
cv2.destroyAllWindows()




