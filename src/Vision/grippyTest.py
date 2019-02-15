import gripV2
import cv2

img = cv2.imread('Examples/2.jpg',1)
cv2.imshow("Please show",img)

cap = cv2.VideoCapture(1)
while(True):
    ret, frame = cap.read()
    cv2.imshow('image stream',frame)
    cv2.waitKey(30)

cap.release()
cv2.waitKey(0)
cv2.destroyAllWindows()





