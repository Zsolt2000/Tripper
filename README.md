# Tripper
## Tripper is a Android application in which a user can create events, modify their information and communicate with other users. It was developed in Java using Firebase as a back end service, Retrofit for networking and Google Maps Platform for implementing a map inside the application.
### Steps to set up the project:
1. Clone the repository using your prefered IDE
2.  Create a project in Firebase
3.  Enable Firebase Authentication, Realtime Database, Cloud Storage and Cloud MessagingI
4.  n the project settings, download the google-services.json and put it inside the /app directory
5.  On the Cloud Messaging, enable Cloud Messaging API and copy the server key
6.  Inside the local.properties, copy the key under the name FCM_AUTH_KEY = YOUR_KEY_HERE
7.  Go to the Google Cloud Console and enable the Places API and Maps SDK services
8.  Create a key for the Maps API key and copy it inside local.properties under MAPS_API_KEY = YOUR_KEY_HERE
