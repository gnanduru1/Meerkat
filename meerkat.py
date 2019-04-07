import cv2
#from newface_test import findface
import pickle
import time
import face_recognition
import imutils
import socket
cascPath = "C:/Users/ganes/AppData/Local/Programs/Python/Python36/Lib/site-packages/cv2/data/haarcascade_frontalface_default.xml"
faceCascade = cv2.CascadeClassifier(cascPath)
x,y = 0,0
video_capture = cv2.VideoCapture(0)

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect(('192.168.137.28',6000))


print("[INFO] loading encodings...")
data = pickle.loads(open("new_encodings.pickle", "rb").read())

while True:
    # Capture frame-by-frame
    ret, frame = video_capture.read()
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = faceCascade.detectMultiScale(
        gray,
        scaleFactor=1.1,
        minNeighbors=5,
        minSize=(30, 30),
        flags=cv2.CASCADE_SCALE_IMAGE
    )

    for (x, y, w, h) in faces:
        cv2.rectangle(frame, (x, y), (x+w, y+h), (0, 255, 0), 2)

    cv2.imshow('Video', frame)
    key = cv2.waitKey(1) & 0xFF
    if key == ord('q'):
        print("Quitting...")
        break
    elif key == ord(' '):
        print("Detecting contours...")
        rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        boxes = face_recognition.face_locations(rgb, model = "hog")
        encodings = face_recognition.face_encodings(rgb, boxes)
        names = []
        for encoding in encodings:
            matches = face_recognition.compare_faces(data["encodings"], encoding)
            name = "Unknown"

            if True in matches:
                matchedIdxs = [i for (i, b) in enumerate(matches) if b]
                counts = {}
                for i in matchedIdxs:
                    name = data["names"][i]
                    counts[name] = counts.get(name, 0) + 1

            name = max(counts, key= counts.get)
            names.append(name)
        if len(names) == 0:
            print("No face detected! Please try again.")
        else:
            #cv2.putText(frame, str(names), (x, y), cv2.FONT_HERSHEY_SIMPLEX, 0.75, (0, 255, 0), 2)
            for stud_name in names:
                print("Welcome to class, " + stud_name[0].upper()+stud_name[1:] + ".")
                sock.send((stud_name[0].upper()+stud_name[1:] + "\n").encode())

video_capture.release()
cv2.destroyAllWindows()
