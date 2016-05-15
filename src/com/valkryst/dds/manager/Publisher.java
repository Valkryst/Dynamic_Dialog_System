package com.valkryst.dds.manager;

import com.valkryst.dds.object.Response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Publisher implements Serializable {
    private static final long serialVersionUID = 9075221020153629524L;

    /** The ArrayList of all objects to be notified. */
    private ArrayList<Notifiable> arrayList_responseSubscribers = new ArrayList<>();

    /**
     * Publishes the specified Responses to all Response subscribers.
     *
     * @param ddsManager
     *         todo JavaDoc
     *
     * @param responses
     *         The Responses to publish.
     */
    void publishResponses(final DDSManager ddsManager, final List<Response> responses) {
        responses.parallelStream()
                .forEach(response -> {
                    arrayList_responseSubscribers.parallelStream()
                            .forEach(subscriber -> subscriber.handleResponse(ddsManager, response));
                });
    }

    /**
     * Adds the specified Notifiable subscriber to the Dynamic Dialog System.
     *
     * Duplicate entries will be ignored.
     *
     *
     * @param subscriber
     *         The Notifiable subscriber to add into the Dynamic Dialog System.
     */
    public void addResponseSubscriber(final Notifiable subscriber) {
        if(! arrayList_responseSubscribers.contains(subscriber)) {
            arrayList_responseSubscribers.add(subscriber);
        }
    }

    /**
     * Removes the specified Notifable subscriber from the Dynamic Dialog System.
     *
     *
     * @param subscriber
     *         The Notifiable subscriber to remove from the Dynamic Dialog System.
     */
    public void removeResponseSubscriber(final Notifiable subscriber) {
        arrayList_responseSubscribers.remove(subscriber);
    }
}
