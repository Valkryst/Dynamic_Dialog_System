# Dynamic Dialog System

Based on the Dynamic Dialog System outlined in the following GDC document.

http://www.gdcvault.com/play/1015317/AI-driven-Dynamic-Dialog-through



##Setup:

This is a very rough setup guide using IntelliJ.

* Clone the repo.
* Open the project in IntelliJ.
* Build the project to ensure there are currently no build errors. If one is found, then make an Issue here on GitHub and it will be fixed.
* Add the DDS as a module to your project.

##Dispatching & Handling Events:

An object that is able to handle the Response of an Event should implement Notifiable and should be added to the 
Publisher as a subscriber to any and all EventTypes which the object can handle.

Whenever the DDS determines a Response or Responses to an Event, the Publisher is used to tell/notify all subscribers
that they should handle the Response(s). Only those subscribers who have subscribed to the response type of the Response
will be told/notified to handle the Response(s).

###Example:

For example, let's say we have both a Cat and a Dog object which both implement Notifiable. Using the Publisher, the Cat
subscribes to the "Eat", "Sleep", and "Meow" response types whereas the Dog subscribes to the "Eat", "Sleep", and "Bark"
response types.

If the DDS determines that a response of the response type "Eat" should be handled, it will use the Publisher to notify 
any subscribers to the "Eat" response type. Both the Cat and Dog subscribe to "Eat", so they are both notified.

If the DDS determines that a response of the response type "Sleep" should be handled, the same situation occurs where
both the Cat and Dog are notified.

If the DDS determines that a response of the response type "Meow" should be handled, then only the Cat is notified.

If the DDS determines that a response of the response type "Bark" should be handled, then only the Dog is notified.


##Events & ResponseTypes:

For information on what an Event or ResponseType is, please refer to the "System Overview.odt" document.

When creating a DataManager, you must pass in a list containing all of the Events that can happen within your game and a list of ResponseTypes. These can be as detailed or as general as you want. 

For an example of the Events used by my Space Invaders test game, see the following:

    final ArrayList<String> arrayList_events = new ArrayList<>();
    arrayList_events.add("ON_PLAYER_HIT_BY_MISSILE");
    arrayList_events.add("ON_ALIEN_HIT_BY_MISSILE");
    arrayList_events.add("ON_MISSILE_FIRED");
    arrayList_events.add("ON_POWERUP");
    arrayList_events.add("ON_ALIEN_DEATH");
    arrayList_events.add("ON_PLAYER_DEATH");
    
For an example of the ResponseTypes used by my Space Invaders test game, see the following:

    final ArrayList<String> arrayList_responseTypes = new ArrayList<>();
    arrayList_responseTypes.add("AUDIO");
    arrayList_responseTypes.add("SCORE_INCREASE");
    arrayList_responseTypes.add("SCORE_DECREASE");
    arrayList_responseTypes.add("PLAYER_ALTER_DX");
    arrayList_responseTypes.add("PLAYER_ALTER_DY");
    arrayList_responseTypes.add("PLAYER_SET_FIRING");

These will be visible to anyone working through the Dynamic Dialogue Writer (a supplemental program for easy editing of the DDS' "database"), so please try to keep the names descriptive and formatted in some standardized way as I have.

##Bootstrapping a DDS:

This is a temporary mesure to create the inital "database" of the DDS. In the future the Dynamic Dialog Writer will 
allow for creation of this "database", but for now you will need to do something along these lines to create the initial
"database" that you can load into the Dynamic Dialog Writer and begin working/testing you game with:

    package com.valkryst.dds;

    import com.valkryst.dds.manager.DDSManager;
    import com.valkryst.dds.object.*;

    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.ObjectOutputStream;
    import java.util.ArrayList;

    public class Driver {
        public static void main(final String[] args) {
            final User user = new User(0);

            final ArrayList<String> arrayList_events = new ArrayList<>();
            arrayList_events.add("ON_PLAYER_HIT_BY_MISSILE");
            arrayList_events.add("ON_ALIEN_HIT_BY_MISSILE");
            arrayList_events.add("ON_MISSILE_FIRED");
            arrayList_events.add("ON_POWERUP");
            arrayList_events.add("ON_ALIEN_DEATH");
            arrayList_events.add("ON_PLAYER_DEATH");

            final ArrayList<String> arrayList_responseTypes = new ArrayList<>();
            arrayList_responseTypes.add("AUDIO");
            arrayList_responseTypes.add("SCORE_INCREASE");
            arrayList_responseTypes.add("SCORE_DECREASE");
            arrayList_responseTypes.add("PLAYER_ALTER_DX");
            arrayList_responseTypes.add("PLAYER_ALTER_DY");
            arrayList_responseTypes.add("PLAYER_SET_FIRING");

            final DataManager ddsManager = new DataManager(arrayList_events, arrayList_responseTypes);

            final Context b = new Context("Score", ValueType.LONG, "0");
            final Context c = new Context("Lives", ValueType.BYTE, "3");
            final Context d = new Context("Total Enemies", ValueType.SHORT, "100");
            final Context e = new Context("Chance For Aliens To Not Fire", ValueType.DOUBLE, "1.0");
            final Context f = new Context("IsPlayerFiring", ValueType.BOOLEAN, "TRUE");
            final Context t = new Context("Chance For Powerup To Spawn", ValueType.DOUBLE, "0.075");


            ddsManager.addUser(user);

            ddsManager.addContext(b);
            ddsManager.addContext(c);
            ddsManager.addContext(d);
            ddsManager.addContext(e);
            ddsManager.addContext(f);
            ddsManager.addContext(t);

            try {
                FileOutputStream fos = new FileOutputStream(new File("database.ser"));
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(ddsManager);

                oos.close();
            } catch(final Exception err) {
                err.printStackTrace();
            }
        }
    }
