// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
import "mx-global";
import { Big } from "big.js";

// BEGIN EXTRA CODE
async function wait(timeout) {
    return new Promise(resolve => setTimeout(resolve, timeout))
}

async function attemptWithRetries(retries, timeout, action) {
    for (let trial = 0; trial < retries; trial++) {
        try {
            return await action();
        } catch (err) {
            await wait(timeout);
        }
    }
    return await action();
}
// END EXTRA CODE

/**
 * @param {string} vapidKey
 * @returns {Promise.<string>}
 */
export async function RegisterForPushNotificationsWeb(vapidKey) {
    // BEGIN USER CODE
    if ("Notification" in window !== true)
        throw new Error("Notification API not available.");
    
    if ("firebase" in window !== true)
        throw new Error("Firebase API not available.");

    if (vapidKey === undefined || vapidKey === "")
        throw new Error("VAPID (Voluntary Application Server Identification) key not available.")
    
    // Request permission
    const permission = await Notification.requestPermission();
    if (permission !== "granted")
        throw new Error("Permission to receive push notifications not granted.")

    // Get token
    const messaging = firebase.messaging();
    const token = await attemptWithRetries(3, 1000, async () => await messaging.getToken({ vapidKey }));

    console.info("Registered for push notifications with token: " + token);
    return token;
    // END USER CODE
}