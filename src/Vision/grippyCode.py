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
def findAngle(M):
    cX = int(M["m10"] / M["m00"])
    cY = int(M["m01"] / M["m00"])
    mU_20 = int(M["m20"] / M["m00"] - (cX * cX))
    mU_02 = int(M["m02"] / M["m00"] - (cY * cY))
    mU_11 = int(M["m11"] / M["m00"] - (cX * cY))
    if mU_20 != mU_02:
        theta = 0.5 * math.atan((2 * mU_11) / (mU_20 - mU_02))
    return theta
        
        
cap = cv2.VideoCapture(1)

cap.set(10,30)
goalFinder = gripV2.GripiPipeline()

#frame = cv2.imread('Examples/2.jpg',1)
#goalFinder.process(cap)

while(True):



    ret, frame = cap.read()
    cv2.waitKey(300)

    goalFinder.process(frame)

    filteredContours=[]
    for contour in goalFinder.find_contours_output:

        area=cv2.contourArea(contour)
        if area>10:
            filteredContours.append(contour)

    if len(filteredContours) == 0:
        cv2.imshow('image',frame)
        continue 


    totalX, totalY = 0, 0

    for contour in filteredContours:
        M = cv2.moments(contour)
        cX = int(M["m10"] / M["m00"])
        cY = int(M["m01"] / M["m00"])
        contourArea = cv2.contourArea(contour)
        theta = findAngle(M)
        print ("Give me the angle{}".format(math.degrees(theta)))
        vX = int(math.cos(theta) * 80 + cX)
        vY = int(math.sin(theta) * 80 + cY)
        cv2.line(frame, (cX,cY), (vX,vY), (255,0,255), 2)

        rect = cv2.minAreaRect(contour)
        box = cv2.boxPoints(rect)
        box = np.int0(box)
        cv2.drawContours(frame,[box],0,(0,0,255),2)

        totalX += cX
        totalY += cY

        #print("contour points {},{}".format(cX,cY))
        cv2.circle(frame, (cX, cY), 7, (255, 0, 0), -1)

        cv2.drawContours(frame, [contour], -1, (255, 255, 0), 2)
    #cv2.drawContours(frame, filteredContours, -1, (255, 0, 0), 2)


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




