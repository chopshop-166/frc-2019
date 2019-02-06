import gripV2
import cv2
import numpy as np

goalFinder = gripV2.GripiPipeline()
img = cv2.imread('Examples/2.jpg',1)
goalFinder.process(img)


for contour in goalFinder.find_contours_output:
    M = cv2.moments(contour)
    cX = int(M["m10"] / M["m00"])
    cY = int(M["m01"] / M["m00"])
    contourArea = cv2.contourArea(contour)

    def is_contour_bad(contour):
        peri = cv2.arcLength(c, True)
        approx = cv2.approxPolyDP(c, 0.02 * peri, True)

        rect = cv2.minAreaRect(contour)
        box = cv2.boxPoints(rect)
        box = np.int0(box)
        cv2.drawContours(img,[box],0,(0,0,255),2)

        return len(approx) != 4

    print("contour points {},{}".format(cX,cY))
    cv2.circle(img, (cX, cY), 7, (0, 0, 0), -1)
    cv2.drawContours(img, [contour], -1, (255, 0, 0), 2)


    image = cv2.imread("Examples/2.jpg")
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    edged = cv2.Canny(gray, 50, 100)

    # find contours in the image and initialize the mask that will be
    # used to remove the bad contours
    contours, hierarchy = cv2.findContours(edged.copy(), cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
    mask = np.ones(image.shape[:2], dtype="uint8") * 255

    for c in contours:
        # if the contour is bad, draw it on the mask
        if is_contour_bad(c):
            cv2.drawContours(mask, [c], -1, 0, -1)

image = cv2.bitwise_and(image, image, mask=mask)
cv2.imshow("Mask", mask)
cv2.imshow("After", image)

cv2.imshow('image',img)
cv2.imshow('dialated image',goalFinder.cv_dilate_output)
cv2.waitKey(0)
cv2.destroyAllWindows()

