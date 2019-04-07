import os

loops = int(input("Number of folders? "))
for x in range(loops):
    directory = "C:/Users/Srikar/Documents/Hackathons/facerec_testdata/"+ input("Folder name? ") +"/"
    i = 1;
    for filename in os.listdir(directory):
        os.rename(directory+filename,directory+str(i)+".jpg")
        i += 1
