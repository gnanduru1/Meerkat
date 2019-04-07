import numpy as np
import cv2 as cv
import cv2
import imutils
import os
import face_recognition
import pickle
from random import shuffle

cascPath = "C:/Users/Srikar/AppData/Local/Programs/Python/Python36/Lib/site-packages/cv2/data/haarcascade_frontalface_default.xml"
faceCascade = cv2.CascadeClassifier(cascPath)

print("[INFO] loading encodings...")
data = pickle.loads(open("encodings.pickle", "rb").read())

knownEncodings = data["encodings"]
knownNames = data["names"]
#data = pickle.loads(open("encodings1.pickle", "rb").read())
#knownNames = data["encodings"]
#knownNames = data["names"]
#folders = ['srikar','ritesh','lohit','ganesh','kevin','aryan','aadith','shashank']
folders = ['josh']
maindir = '../facerec_testdata'
for folder in folders:
    print(folder)
    for filename in os.listdir(maindir + "/" + folder):
#        print(filename)
        if '.jpg' not in filename:
            continue
        name = maindir + "/" + folder + "/" + filename
#        print(name)
        img = cv.imread(name)
        img = imutils.resize(img, width=500)
        gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
        rgb = cv.cvtColor(img, cv.COLOR_BGR2RGB)

#        faces = faceCascade.detectMultiScale(
#            gray,
#            scaleFactor=1.1,
#            minNeighbors=5,
#            minSize=(30, 30),
#            flags=cv2.CASCADE_SCALE_IMAGE
#        )
#        if len(faces) < 1:
#            continue
#        (x,y,w,h) = faces[0]
#        cropped = rgb[x:x+w, y:y+h]
#        boxes = []
#        for (x,y,w,h) in faces:
#            boxes.append((x,w,h,y))
        boxes = face_recognition.face_locations(rgb, model = "hog")
#        print(boxes)
#        x = boxes[0][0]
#        w = boxes[0][1]
#        h = boxes[0][2]
#        y = boxes[0][3]

#        cv.rectangle(img,(x,y),(x+w,y+h),(255,0,0),2)
#        cv.imshow('img',img)
#        cv.waitKey(0)
#        cv.destroyAllWindows()
        encodings = face_recognition.face_encodings(rgb, boxes)
#        encodings = face_recognition.api.face_encodings(cropped, known_face_locations=[(0,len(cropped),len(cropped[0]),0)], num_jitters = 1)
#        print(encodings)

        name = folder
#        print(len(encodings))
        for encoding in encodings:
            # add each encoding + name to our set of known names and
            # encodings
            knownEncodings.append(encoding)
            knownNames.append(name)

#        cv.imshow('img',img)
#        cv.waitKey(0)
#        cv.destroyAllWindows()

print("[INFO] serializing encodings...")
data = {"encodings": knownEncodings, "names": knownNames}
f = open("encodings.pickle", "wb")
f.write(pickle.dumps(data))
f.close()
