import gripV2
import cv2
import numpy as np
import threading
from networktables import NetworkTables


cond = threading.Condition()
notified = [False]

def connectionListener(connected, info):
    print(info, '; Connected=%s' % connected)
    with cond:
        notified[0] = True
        cond.notify()

NetworkTables.initialize(server='10.1.66.2')
NetworkTables.addConnectionListener(connectionListener, immediateNotify=True)
table = NetworkTables.getTable('Vision Correction Table')
with cond:
    if not notified[0]:
        cond.wait()

        
        

goalFinder = gripV2.GripiPipeline()
img = cv2.imread('Examples/2.jpg',1)
goalFinder.process(img)
filteredContours=[]


for contour in goalFinder.find_contours_output:

    area=cv2.contourArea(contour)
    if area>10000:
        filteredContours.append(contour)

totalX, totalY = 0, 0

for contour in filteredContours:
    M = cv2.moments(contour)
    cX = int(M["m10"] / M["m00"])
    cY = int(M["m01"] / M["m00"])
    contourArea = cv2.contourArea(contour)

    rect = cv2.minAreaRect(contour)
    box = cv2.boxPoints(rect)
    box = np.int0(box)
    cv2.drawContours(img,[box],0,(0,0,255),2)

    totalX += cX
    totalY += cY

    print("contour points {},{}".format(cX,cY))
    cv2.circle(img, (cX, cY), 7, (0, 0, 0), -1)

    # cv2.drawContours(img, [contour], -1, (255, 0, 0), 2)
cv2.drawContours(img, filteredContours, -1, (255, 0, 0), 2)


avgPoint = (int(totalX / len(filteredContours)), int(totalY / len(filteredContours)))

dimensions = img.shape
width = img.shape[1]



#coordinate i have then subtract center in pixels to find midpoint
imageMidpoint = (width / 2)
avgPointOrient = (avgPoint[0] - imageMidpoint)
fullContourMidpoint = (avgPointOrient / imageMidpoint)
# prints after ewww math THIS IS THE CONTOUR MIDPOINT from...
# Contour Midpoint Scale (-1 to 1)
print('Final Contour Midpoint: ',fullContourMidpoint)

table.putNumber("Vision Correction", fullContourMidpoint)

print("Average Contour Point {},{}".format(avgPoint[0],avgPoint[1]))
cv2.circle(img, (avgPoint), 7, (0, 0, 0), -1)

cv2.imshow('image',img)
cv2.imshow('dialated image',goalFinder.cv_dilate_output)
cv2.waitKey(0)
cv2.destroyAllWindows()

