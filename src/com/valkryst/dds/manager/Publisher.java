package com.valkryst.dds.manager;

import com.google.common.collect.ArrayListMultimap;
import com.valkryst.dds.object.Response;

import java.io.Serializable;
import java.util.List;

public class Publisher implements Serializable {
    private static final long serialVersionUID = 9075221020153629524L;

    /** The ArrayList of all objects to be notified. */
    private ArrayListMultimap<String, Notifiable> arrayListMultimap_responseSubscribers = ArrayListMultimap.create();

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
                    final String responseType = response.getResponseType();

                    arrayListMultimap_responseSubscribers.get(responseType)
                                                         .parallelStream()
                                                         .forEach(subscriber -> subscriber.handleResponse(ddsManager, response));
                });
    }

    /**
     * Adds the specified Notifiable subscriber to the Dynamic Dialog System.
     *
     * Duplicate entries will be ignored.
     *
     * @param responseType
     *         The type of response to subscribe to.
     *
     * @param subscriber
     *         The Notifiable subscriber to add into the Dynamic Dialog System.
     */
    public void addResponseSubscriber(final String responseType, final Notifiable subscriber) {
        /*
         * If the subscriber has not previously subscribed to
         * the specified response type, then allow it to
         * subscribe.
         */
        boolean isAlreadySubscribed = arrayListMultimap_responseSubscribers.get(responseType)
                                                                           .contains(subscriber);

        if(! isAlreadySubscribed) {
            arrayListMultimap_responseSubscribers.put(responseType, subscriber);
        }
    }

    /**
     * Removes the specified Notifable subscriber from the Dynamic Dialog System.
     *
     * @param responseType
     *         The type of response that the subscriber is subscribed to to.
     *
     * @param subscriber
     *         The Notifiable subscriber to remove from the Dynamic Dialog System.
     */
    public void removeResponseSubscriber(final String responseType, final Notifiable subscriber) {
        arrayListMultimap_responseSubscribers.remove(responseType, subscriber);
    }
}
