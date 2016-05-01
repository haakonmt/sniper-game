package no.social.snipergame.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @author Håkon Meyer Tørnquist <haakon.t@gmail.com>
 *         Date: 30.04.2016 14.56.
 */
public abstract class MqttHandler implements MqttCallback {

    final Gson gson = new GsonBuilder().create();

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Lost connection. " + cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Message arrived on " + topic + ": " + message.toString());
        internalMessageArrived(topic, message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.println("Delivery complete.");
    }

    public abstract void internalMessageArrived(String topic, MqttMessage message);
}
