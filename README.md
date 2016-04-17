# Dynamic_Dialog_System

Based on the Dynamic Dialog System outlined in the following GDC document.

http://www.gdcvault.com/play/1015317/AI-driven-Dynamic-Dialog-through

---

##Setup:

This is a very rough setup guide specifically written for IntelliJ.

* Clone the repo.
* Open the project in IntelliJ.
* Build the project to ensure there are currently no build errors. If one is found, then make an Issue here on GitHub and it will be fixed.
* Open your project in IntelliJ.
* Add the DDS as a module to your project.

Now that the DDS has been sucessfully added as a module to your project, you'll need to create your own ResponeManager and carefully consider the Events and ResponseTypes that you wish to use.

##ResponseManager:

The ResponseManager is in-charge of handling the Responses of any Rule triggered by an Event. You can learn more about how the system works by reading the "System Overview.odt" document, but suffice to say that anytime a Response happens, the ResponseManager is handed the Response object and must perform whatever action the Response says to do.

To create your own ResponseManager, simply create a new class which extends the abstract ResponseManager class and implement whatever Response handling you wish within the respond() method.

As a quick and dirty example, here is the very crude ResponseManager for my Space Invaders test game.

    package core;

    import com.valkryst.java.core.Log;
    import data.Response;
    import data.ResponseManager;
    import data.collection.DataManager;
    import object.Level;

    public class GameResponseManager extends ResponseManager {
        private static final long serialVersionUID = -3411852210090794653L;

        private Level level;

        // todo JavaDoc
        @Override
        public void respond(final DataManager dataManager, final Response response) {
            if(level == null) {
                throw new NullPointerException("No Level has been assigned to the GameResponseManager.");
            }

            switch(response.getResponseType()) {

                case "SCORE_INCREASE": {
                    dataManager.setValue(0, "Score",  String.valueOf((Long)dataManager.getValue(0, "Score") + Integer.valueOf(response.getValue())));
                    break;
                }

                case "SCORE_DECREASE": {
                    dataManager.setValue(0, "Score",  String.valueOf((Long)dataManager.getValue(0, "Score") - Integer.valueOf(response.getValue())));
                    break;
                }

                case "AUDIO": {
                    final String[] responseTokens = response.getValue().split("<<|>>");

                    switch (responseTokens.length) {
                        case 1: {
                            AUDIO_HANDLER.handleAudioResponse(responseTokens[0], null, null, null);
                            break;
                        }

                        case 2: {
                            AUDIO_HANDLER.handleAudioResponse(responseTokens[0], responseTokens[1], null, null);
                            break;
                        }

                        case 3: {
                            AUDIO_HANDLER.handleAudioResponse(responseTokens[0], responseTokens[1], responseTokens[2], null);
                            break;
                        }

                        case 4: {
                            AUDIO_HANDLER.handleAudioResponse(responseTokens[0], responseTokens[1], responseTokens[2], responseTokens[3]);
                            break;
                        }

                        default: {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("The number of parameters parsed from the responseValue is either too low or too high. The parameters are as follows.");

                            for (final String s : responseTokens) {
                                sb.append("\n\t").append(s);
                            }

                            Driver.LOGGER.addLog(Log.LOGTYPE_ERROR, sb.toString());
                        }
                    }

                    break;
                }

                case "PLAYER_ALTER_DX": {
                    level.getPlayer().alterDx(Double.parseDouble(response.getValue()));
                    break;
                }

                case "PLAYER_ALTER_DY": {
                    level.getPlayer().alterDy(Double.parseDouble(response.getValue()));
                    break;
                }

                case "PLAYER_SET_FIRING": {
                    dataManager.setValue(0, "IsPlayerFiring", response.getValue());
                }

                default: {
                    Driver.LOGGER.addLog(Log.LOGTYPE_WARNING, "The response type of the following Response is unknown and cannot be handled.\n" + response.toString());
                }
            }
        }

        public void setLevel(final Level level) {
            this.level = level;
        }
    }
Although the implementation of the respond() method will vary highly from game-to-game, this should give a general idea of what the ResponseManager does.

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

This is a temporary mesure to create the inital "database" of the DDS. In the future the Dynamic Dialog Writer will allow for creation of this "database", but for now you will need to do something along these lines to create the inital "database" that you can load into the Dynamic Dialog Writer and begin working/testing you game with:

    package com.valkryst.dds;

    import com.valkryst.dds.manager.DataManager;
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

            final DataManager dataManager = new DataManager(arrayList_events, arrayList_responseTypes);

            final Context b = new Context("Score", ValueType.LONG, "0");
            final Context c = new Context("Lives", ValueType.BYTE, "3");
            final Context d = new Context("Total Enemies", ValueType.SHORT, "100");
            final Context e = new Context("Chance For Aliens To Not Fire", ValueType.DOUBLE, "1.0");
            final Context f = new Context("IsPlayerFiring", ValueType.BOOLEAN, "TRUE");
            final Context t = new Context("Chance For Powerup To Spawn", ValueType.DOUBLE, "0.075");


            dataManager.addUser(user);

            dataManager.addContext(b);
            dataManager.addContext(c);
            dataManager.addContext(d);
            dataManager.addContext(e);
            dataManager.addContext(f);
            dataManager.addContext(t);

            try {
                FileOutputStream fos = new FileOutputStream(new File("database.ser"));
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(dataManager);

                oos.close();
            } catch(final Exception err) {
                err.printStackTrace();
            }
        }
    }
