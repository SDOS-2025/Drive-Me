// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyAMnP7gRw56L2pevvE2wn69ty4hl5kgISs",
  authDomain: "driveme-112b8.firebaseapp.com",
  projectId: "driveme-112b8",
  storageBucket: "driveme-112b8.firebasestorage.app",
  messagingSenderId: "648834346677",
  appId: "1:648834346677:web:4d98381ad9d69de60118fc",
  measurementId: "G-W37VVFDWQS"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);