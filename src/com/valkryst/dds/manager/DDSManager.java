package com.valkryst.dds.manager;

import com.google.common.collect.ArrayListMultimap;
import com.valkryst.dds.collection.SplayTree;
import com.valkryst.dds.object.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DDSManager implements Serializable {
    private static final long serialVersionUID = 6158503022877874004L;

    /** The Random to use where necessary. */
    private final Random random = new Random(System.nanoTime());
    /** The Publisher to use when handling Responses. */
    @Getter private final Publisher publisher = new Publisher();


    /** The ConcurrentHashMap containing User IDs and the Users that they corrospond to. */
    private ConcurrentHashMap<Long, User> hashMap_users = new ConcurrentHashMap<>();

    /** The Events that can be used by the Dynamic Dialog System. */
    @Getter @Setter @NonNull private ArrayList<String> arrayList_events;

    /** The ResponseTypes that can be used by the Dynamic Dialog System. */
    @Getter @Setter @NonNull private ArrayList<String> arrayList_responseTypes;



    /** The ArrayList containing all Criterion, with their IDs as Keys. */
    private ArrayList<Criterion> arrayList_criterion = new ArrayList<>();
    /** The ArrayList containing all Responses, with their IDs as Keys. */
    private ArrayList<Response> arrayList_response = new ArrayList<>();
    /** The ArrayList containing all Rules, with their IDs as Keys. */
    @Getter private ArrayList<Rule> arrayList_rules = new ArrayList<>();

    /** The ArrayList containing all possible Context names. */
    @Getter private ArrayList<String> arrayList_contextNames = new ArrayList<>();

    /** The ArrayListMultimap containing all associations between each Event and the Rules that it triggers. */
    private ArrayListMultimap<String, Rule> arrayListMultimap_ruleEventAssociations = ArrayListMultimap.create();
    /** The ArrayListMultimap containing all associations between each Rule and it's Responses. */
    private ArrayListMultimap<Rule, Response> arrayListMultimap_ruleResponseAssociations = ArrayListMultimap.create();
    /** The ArrayListMultimap containing all associations between each Rule and it's Criterion. */
    private ArrayListMultimap<Rule, Criterion> arrayListMultimap_ruleCriterionAssociations = ArrayListMultimap.create();

    /** The ConcurrentHashMap containing Context Namess and the time at which they were last used. */
    private ConcurrentHashMap<Context, Long> hashMap_context_lastUsedTime =  new ConcurrentHashMap<>();
    /** The ConcurrentHashMap containing Criterion IDs and the time at which they were last used. */
    private ConcurrentHashMap<Criterion, Long> hashMap_criterion_lastUsedTime =  new ConcurrentHashMap<>();
    /** The ConcurrentHashMap containing Response IDs and the time at which they were last used. */
    private ConcurrentHashMap<Response, Long> hashMap_response_lastUsedTime =  new ConcurrentHashMap<>();
    /** The ConcurrentHashMap containing Rule IDs and the time at which they were last used. */
    private ConcurrentHashMap<Rule, Long> hashMap_rules_lastUsedTime = new ConcurrentHashMap<>();

    /**
     * Construct a new DDSManager.
     *
     * @param arrayList_events
     *         The Events that can be used by the Dynamic Dialog System.
     *
     * @param arrayList_responseTypes
     *         The ResponseTypes that can be used by the Dynamic Dialog system.
     */
    public DDSManager(final ArrayList<String> arrayList_events, final ArrayList<String> arrayList_responseTypes) {
        this.arrayList_events = arrayList_events;
        this.arrayList_responseTypes = arrayList_responseTypes;
    }


    /**
     * Updates all of the Criterions associated with the specified Rule.
     *
     * @param rule
     *         The Rule whose associated Criterions are to be updated.
     *
     * @return
     *         The total number of Criterions who, after being updated, evaluate
     *         to TRUE.
     */
    private int updateRuleCriterion(final Rule rule) {
        int isTrueCounter = 0;

        // Update the Data:
        for(final Criterion criterion : arrayListMultimap_ruleCriterionAssociations.get(rule)) {
            criterion.update();

            isTrueCounter += (criterion.isTrue() ? 1 : 0);
        }

        return isTrueCounter;
    }

    public void determineResponse(final String event) {
        // Determine the Triggered Rules and their Scores:
        final List<Rule> set_triggeredRules = arrayListMultimap_ruleEventAssociations.get(event);
        final ConcurrentHashMap<Rule, Double> hashMap_scores = new ConcurrentHashMap<>();

        set_triggeredRules.parallelStream()
                .forEachOrdered(rule -> {
                    updateRuleCriterion(rule);
                    hashMap_scores.put(rule, determineCriterionWeight(rule));
                });


        final int totalScoredRules = set_triggeredRules.size();

        if (totalScoredRules == 0) { // If no Rules were found
            return;
        } else if (totalScoredRules == 1) { // If one Rule was found
            determineResponseCaseB(set_triggeredRules);

        } else if (set_triggeredRules.parallelStream().anyMatch(rule -> rule.getLastUsedTime() == 0)) { // Multiple rules found, some not used before
            determineResponseCaseC(set_triggeredRules, hashMap_scores);

        } else if(set_triggeredRules.parallelStream().allMatch(r -> arrayListMultimap_ruleCriterionAssociations.get(r).size() == 0)) { // Multiple rules found, none have Criterion
            determineResponseCaseD(set_triggeredRules);

        } else {
            determineResponseCaseE(set_triggeredRules, hashMap_scores);
        }
    }

    /**
     * If only one Rule was found in the set of triggered rules,
     * then respond to it.
     *
     * @param list_triggeredRules
     *         The Rules of which one will be responded to.
     */
    private void determineResponseCaseB(final List<Rule> list_triggeredRules) {
        // If only one Rule is found, then respond to it.
        list_triggeredRules.get(0).updateLastUsedTime();
        publisher.publishResponses(this, arrayListMultimap_ruleResponseAssociations.get(list_triggeredRules.get(0)));
    }

    /**
     * If multiple Rules were found in the set of triggered rules,
     * then the Rule with the highest Criterion weight and which has
     * never been run before will be used.
     *
     * @param list_triggeredRules
     *         The Rules of which one will be responded to.
     */
    private void determineResponseCaseC(final List<Rule> list_triggeredRules, final ConcurrentHashMap<Rule, Double> hashMap_scores) {
        double highestWeight = 0;
        double currentWeight = 0;

        Rule ruleWithHighestWeight = null;

        for(final Rule rule : list_triggeredRules) {
            currentWeight = hashMap_scores.get(rule);

            if(currentWeight > highestWeight) {
                highestWeight = currentWeight;
                ruleWithHighestWeight = rule;
            }
        }



        if(ruleWithHighestWeight == null) {
            throw new IllegalStateException("The algorithm to determine which Rule to use, when there exists " +
                    " a Rule that has not been run before, has not chosen a Rule to " +
                    "respond to.");
        } else {
            ruleWithHighestWeight.updateLastUsedTime();
            publisher.publishResponses(this, arrayListMultimap_ruleResponseAssociations.get(ruleWithHighestWeight));
        }
    }

    /**
     * If multiple Rules were found in the set of triggered rules
     * and none of them have any associated Criterion, then the
     * Rule that was least recently used will be used.
     *
     * @param list_triggeredRules
     *         The Rules of which one will be responded to.
     */
    private void determineResponseCaseD(final List<Rule> list_triggeredRules) {
        final Rule rule = list_triggeredRules.parallelStream()
                                             .sorted(Comparator.comparingLong(Rule::getLastUsedTime))
                                             .findFirst()
                                             .get();

        rule.updateLastUsedTime();
        publisher.publishResponses(this, arrayListMultimap_ruleResponseAssociations.get(rule));
    }

    /**
     * If multiple Rules were found in the set of triggered rules
     * and no other case applies, then a weighted average is done
     * taking into account both the scores and the last used times
     * of each Rule to determine which Rule to use.
     *
     * All Rules with no Criterion will be ignored.
     *
     * @param list_triggeredRules
     *         The Rules of which one or more will be responded to.
     *
     * @param hashMap_scores
     *         todo JavaDoc
     */
    private void determineResponseCaseE(final List<Rule> list_triggeredRules, final ConcurrentHashMap<Rule, Double> hashMap_scores) {
        final ArrayList<Integer> arrayList_incidesToUse = new ArrayList<>();


        double highestScore = 0;
        final long currentTime = System.currentTimeMillis();
        int counter = 0;

        final double lowestCriterionScore = getLowestCriterionScore(list_triggeredRules, hashMap_scores);
        final double highestCriterionScore = getHighestCriterionScore(list_triggeredRules, hashMap_scores);

        final double oldestLastUsedTime = getOldestLastUsedTime(list_triggeredRules);
        final double newestLastUsedTime = getNewestLastUsedTime(list_triggeredRules);

        double normalizedScore;
        double normalizedLUUT;
        double finalScore;

        for(final Rule rule : list_triggeredRules) {
                /*
                 * If there are Criterion associated with the Rule and at-least one of them evaluates
                 * to TRUE, then continue.
                 *
                 * AND
                 *
                 * If there are no Criterion associated with the Rule, then continue.
                 */
            if(hashMap_scores.get(rule) > 0 && getAssociatedCriterions(rule).size() != 0) {
                normalizedScore = normalize(hashMap_scores.get(rule), lowestCriterionScore, highestCriterionScore);
                normalizedLUUT = normalize(rule.getLastUsedTime(), oldestLastUsedTime, newestLastUsedTime) * (currentTime - rule.getLastUsedTime())/1000;

                finalScore = (normalizedScore * 0.6f) + (normalizedLUUT * 0.4f);

                if(finalScore > highestScore) {
                    arrayList_incidesToUse.clear();
                    highestScore = finalScore;
                } else if(finalScore == highestScore) {
                    arrayList_incidesToUse.add(counter);
                }
            }

            counter ++;
        }


        /*
         * If there is only one Rule with the highest score, then use
         * it.
         *
         * If there are multiple Rules that share the highest score,
         * then randomly pick one to use.
         */
        final int indexToUse = random.nextInt(arrayList_incidesToUse.size());

        list_triggeredRules.get(indexToUse).updateLastUsedTime();
        publisher.publishResponses(this, arrayListMultimap_ruleResponseAssociations.get(list_triggeredRules.get(indexToUse)));
    }

    /**
     * Determines the lowest Criterion score from the specified
     * Rule<->Score associations.
     *
     * @param list_triggeredRules
     *         todo JavaDoc
     *
     * @param hashMap_scores
     *         todo JavaDoc
     *
     * @return
     *         The lowest Criterion score from the specified
     *         Rule<->Score associations.
     */
    private double getLowestCriterionScore(final List<Rule> list_triggeredRules, final ConcurrentHashMap<Rule, Double> hashMap_scores) {
        double lowest = Double.MAX_VALUE;
        double currentVal;

        for(final Rule rule : list_triggeredRules) {
            currentVal = hashMap_scores.get(rule);

            if(currentVal < lowest) {
                lowest = currentVal;
            }
        }

        return lowest;
    }

    /**
     * Determines the lowest Criterion score from the specified
     * Rule<->Score associations.
     *
     * @param list_triggeredRules
     *         todo JavaDoc
     *
     * @param hashMap_scores
     *         todo JavaDoc
     *
     * @return
     *         The lowest Criterion score from the specified
     *         Rule<->Score associations.
     */
    private double getHighestCriterionScore(final List<Rule> list_triggeredRules, final ConcurrentHashMap<Rule, Double> hashMap_scores) {
        double highest = Double.MIN_VALUE;
        double currentVal;

        for(final Rule rule : list_triggeredRules) {
            currentVal = hashMap_scores.get(rule);

            if(currentVal > highest) {
                highest = currentVal;
            }
        }

        return highest;
    }

    /**
     * Determines the oldest last used time of any Rule
     * from the specified rules.
     *
     * @param list_triggeredRules
     *         todo JavaDoc
     *
     * @return
     *         The oldest last used time of any Rule
     *         from the specified Rules.
     */
    private long getOldestLastUsedTime(final List<Rule> list_triggeredRules) {
        long oldest = Long.MAX_VALUE;

        for(final Rule rule : list_triggeredRules) {
            if(rule.getLastUsedTime() < oldest) {
                oldest = rule.getLastUsedTime();
            }
        }

        return oldest;
    }

    /**
     * Determines the newest last used time of any Rule
     * from the specified rules.
     *
     * @param set_triggeredRules
     *         todo JavaDoc
     *
     * @return
     *         The newest last used time of any Rule
     *         from the specified Rules.
     */
    private long getNewestLastUsedTime(final List<Rule> set_triggeredRules) {
        long newest = Long.MIN_VALUE;

        for(final Rule rule : set_triggeredRules) {
            if(rule.getLastUsedTime() > newest) {
                newest = rule.getLastUsedTime();
            }
        }

        return newest;
    }

    /**
     * Determines the weight of all Criterion that evaluate to TRUE
     * for the specified Rule.
     *
     * @param rule
     *         The Rule whose Criterion weight is to be evaluated.
     *
     * @return
     *         The combined weight of all TRUE Criterion divided by the
     *         weight of all Criterion combined.
     */
    private double determineCriterionWeight(final Rule rule) {
        double totalWeight = 0;
        double trueWeight = 0;

        for(final Criterion criterion : arrayListMultimap_ruleCriterionAssociations.get(rule)) {
            totalWeight += criterion.getWeight();

            if(criterion.isTrue()) {
                trueWeight += criterion.getWeight();
            }
        }

        return trueWeight / totalWeight;
    }

    /**
     * Normalizes the specified value to a percentage between the specified
     * minimum and maximum values.
     *
     * @param value
     *         The value to normalize.
     *
     * @param minimum
     *         The minimum value.
     *
     * @param maximum
     *         The maximum value.
     *
     * @return
     *         The normalized value.
     */
    private double normalize(final double value, final double minimum, final double maximum) {
        double numerator = value - minimum;
        double denominator = maximum - minimum;

        if(denominator == 0) {
            denominator = 1;
        }

        return numerator / denominator;
    }





    /**
     * Attempts to retrieve the Value associated with the specified Key
     * from the splayTree_context of the specified user.
     *
     * @param userId
     *         The ID of the user to retrieve the value from.
     *
     * @param key
     *        The Key associated with the Value to retrieve.
     *
     * @return
     *        Returns the Value as an Object. The Object type returned
     *        is specified through the ValueType field of the Context
     *        and is automatically dealt with before the return.
     *
     *        So, if the key "Total enemies" is used, the Value retrieved
     *        might have a ValueType of Integer. If this is the case, then
     *        the Object returned will be an integer.
     *
     *        If anything goes wrong, String of data itself is returned.
     */
    public Object getValue(final long userId, final String key) {
        final User user = hashMap_users.get(userId);

        // Get Data:
        final SplayTree<String, Context> splayTree_context = user.getSplayTree_context();

        final ValueType valueType = splayTree_context.get(key).getValueType();
        final String value = splayTree_context.get(key).getValue();


        switch(valueType) {
            case BYTE: {
                return Byte.valueOf(value);
            }
            case SHORT: {
                return Short.valueOf(value);
            }
            case INTEGER: {
                return Integer.valueOf(value);
            }
            case LONG: {
                return Long.valueOf(value);
            }
            case FLOAT: {
                return Float.valueOf(value);
            }
            case DOUBLE: {
                return Double.valueOf(value);
            }
            case BOOLEAN: {
                return Boolean.valueOf(value);
            }
            default: {
                return value;
            }
        }
    }

    /**
     * Attempts to set the Value associated with the specified Key to
     * the specified Value within the splayTree_context.
     *
     * @param userId
     *         The ID of the user whose value is to be set.
     *
     * @param key
     *        The Key associated with the Value to retrieve.
     *
     * @param newValue
     *        The new Value to place into the Context.
     */
    public void setValue(final long userId, final String key, final String newValue) {
        hashMap_users.get(userId)
                .getSplayTree_context()
                .get(key)
                .setValue(newValue);
    }


    /**
     * Adds the specified User into the Dynamic Dialog System.
     * If a User with the same ID as the specified User already
     * exists, then no duplicate is added.
     *
     * @param user
     *         The User to add.
     *
     * @throws
     *         If a User with the same ID as the specified
     *         User already exists.
     */
    public void addUser(final User user) throws IllegalArgumentException {
        if(! hashMap_users.containsKey(user.getId())) {
            hashMap_users.put(user.getId(), user);
        } else {
            throw new IllegalArgumentException("A user with the ID " + user.getId() + " already exists.");
        }
    }

    /**
     * Adds the specified Event into the Dynamic Dialog System.
     * If the Event already exists, then no duplicate is added.
     *
     * @param event
     *         The Event to add.
     */
    public void addEvent(final String event) {
        if(! arrayList_events.contains(event)) {
            arrayList_events.add(event);
        }
    }

    /**
     * Adds the specified Context into the Dynamic Dialog System.
     *
     * @param context
     *         The Context to add into the Dynamic Dialog System.
     */
    public void addContext(final Context context) {
        for (Map.Entry<Long, User> user : hashMap_users.entrySet()) {
            user.getValue()
                    .getSplayTree_context()
                    .put(context.getName(), context);
        }

        arrayList_contextNames.add(context.getName());
    }

    /**
     * Removes the specified User from the Dynamic Dialog System.
     *
     * If no User matching the exact ID and User object of the
     * specified User is found, then nothing happens.
     *
     * @param user
     *         The User to remove.
     */
    public void removeUser(final User user) {
        hashMap_users.remove(user.getId(), user);
    }

    /**
     * Removes the specified Context from the Dynamic Dialog System.
     *
     * @param context
     *         The Context to remove from the Dynamic Dialog System.
     */
    public void removeContext(final Context context) throws UnsupportedOperationException {
        // If the Context to be removed is still in-use by some Criterion in the System,
        // then throw an exception to prevent it from being removed.
        arrayList_criterion.parallelStream()
                .filter(criterion -> criterion != null)
                .filter(criterion -> criterion.getContext().equals(context))
                .forEach(criterion -> {
                    throw new UnsupportedOperationException("The following Context is still in-use by the following Criterion" +
                            ", it cannot be removed from the Dynamic Dialog System.\n" +
                            context.toString() + "\n\n" + criterion.toString());
                });


        // Remove the Context from all users:
        for (Map.Entry<Long, User> user : hashMap_users.entrySet()) {
            user.getValue()
                    .getSplayTree_context()
                    .remove(context.getName());

        }


        // Remove the Context's last used time from the DDS:
        hashMap_context_lastUsedTime.remove(context);


        // Remove the Context's name from the DDS:
        arrayList_contextNames.remove(context.getName());
    }

    /**
     * Adds the specified Criterion into the Dynamic Dialog System.
     *
     * @param criterion
     *         The Criterion to add into the Dynamic Dialog System.
     */
    public void addCriterion(final Criterion criterion) {
        arrayList_criterion.add(criterion);
    }

    /**
     * Removes the specified Criterion from the Dynamic Dialog System.
     *
     * @param criterion
     *         The Criterion to remove from the Dynamic Dialog System.
     */
    public void removeCriterion(final Criterion criterion) throws UnsupportedOperationException {
        // If the Criterion to be removed is still in-use by some Rule in the System,
        // then throw an exception to prevent it from being removed.
        arrayList_rules.parallelStream()
                .filter(rule -> rule != null)
                .filter(rule -> arrayListMultimap_ruleCriterionAssociations.get(rule).contains(criterion))
                .forEach(rule -> {
                    throw new UnsupportedOperationException("The following Criterion is still in-use by the following Rule" +
                            ", it cannot be removed from the Dynamic Dialog System.\n" +
                            criterion.toString() + "\n\n" + rule.toString());
                });


        // Remove the Rule's last used time from the DDS:
        hashMap_criterion_lastUsedTime.remove(criterion);


        // Remove the Rule's name from the DDS:
        arrayList_criterion.remove(criterion);
    }

    /**
     * Adds the specified Response into the Dynamic Dialog System.
     *
     * @param response
     *         The Response to add into the Dynamic Dialog System.
     */
    public void addResponse(final Response response) {
        arrayList_response.add(response);
    }

    /**
     * Removes the specified Response from the Dynamic Dialog System.
     *
     * @param response
     *         The Response to remove from the Dynamic Dialog System.
     */
    public void removeResponse(final Response response) throws UnsupportedOperationException {
        // If the Response to be removed is still in-use by some Rule in the System,
        // then throw an exception to prevent it from being removed.
        arrayList_rules.parallelStream()
                .filter(rule -> rule != null)
                .filter(rule -> arrayListMultimap_ruleResponseAssociations.get(rule).contains(response))
                .forEach(rule -> {
                    throw new UnsupportedOperationException("The following Response is still in-use by the following Rule" +
                            ", it cannot be removed from the Dynamic Dialog System.\n" +
                            response.toString() + "\n\n" + rule.toString());
                });


        // Remove the Response's last used time from the DDS:
        hashMap_response_lastUsedTime.remove(response);


        // Remove the Response's name from the DDS:
        arrayList_response.remove(response);
    }

    /**
     * Adds the specified Rule into the Dynamic Dialog System.
     *
     * @param rule
     *         The Rule to add into the Dynamic Dialog System.
     */
    public void addRule(final Rule rule) {
        arrayList_rules.add(rule);
    }

    /**
     * Removes the specified Rule from the Dynamic Dialog System.
     *
     * @param rule
     *         The Rule to remove from the Dynamic Dialog System.
     */
    public void removeRule(final Rule rule) {
        removeRuleCriterionAssociations(rule);
        removeRuleEventAssociations(rule);
        removeRuleResponseAssociations(rule);


        // Remove the Rule's last used time from the DDS:
        hashMap_rules_lastUsedTime.remove(rule);


        // Remove the Rule from the DDS:
        arrayList_rules.remove(rule);
    }

    /**
     * Adds the specified Rule<->Criterion Association to the Dynamic Dialog system.
     *
     * @param rule
     *         The rule to use in the association.
     *
     * @param criterion
     *         The criterion to use in the association.
     */
    public void addRuleCriterionAssociation(final Rule rule, final Criterion criterion) {
        arrayListMultimap_ruleCriterionAssociations.put(rule, criterion);
    }

    /**
     * Removes all Rule<->Criterion Associations, that use the specified rule,
     * from the Dynamic Dialog System.
     *
     * @param rule
     *         The Rule whose associations are to be removed.
     */
    private void removeRuleCriterionAssociations(final Rule rule) {
        arrayListMultimap_ruleCriterionAssociations.removeAll(rule);
    }

    /**
     * Adds the specified Event<->Rule Association to the Dynamic Dialog system.
     *
     * @param event
     *         The event to use in the association.
     *
     * @param rule
     *         The rule to use in the association.
     */
    public void addRuleEventAssociation(final String event, final Rule rule) {
        arrayListMultimap_ruleEventAssociations.put(event, rule);
    }

    /**
     * Removes all Event<->Rule Associations, that use the specified rule,
     * from the Dynamic Dialog System.
     *
     * @param rule
     *         The Rule whose associations are to be removed.
     */
    private void removeRuleEventAssociations(final Rule rule) {
        arrayListMultimap_ruleEventAssociations.entries()
                                               .removeIf(entry -> entry.getValue().equals(rule));
    }

    /**
     * Adds the specified Rule<->Response Association to the Dynamic Dialog system.
     *
     * @param rule
     *         The rule to use in the association.
     *
     * @param response
     *         The response to use in the association.
     */
    public void addRuleResponseAssociation(final Rule rule, final Response response) {
        arrayListMultimap_ruleResponseAssociations.put(rule, response);
    }

    /**
     * Removes all Rule<->Response Associations, that use the specified rule,
     * from the Dynamic Dialog System.
     *
     * @param rule
     *         The Rule whose associations are to be removed.
     */
    private void removeRuleResponseAssociations(final Rule rule) {
        arrayListMultimap_ruleResponseAssociations.removeAll(rule);
    }





    /**
     * Locates all Criterions associated with the specified Rule.
     *
     * @param rule
     *         The Rule to search with.
     *
     * @return
     *         A list containing all Criterions associated with the specified Rule.
     */
    public List<Criterion> getAssociatedCriterions(final Rule rule) {
        return arrayListMultimap_ruleCriterionAssociations.get(rule);
    }

    /**
     * Locates all Events associated with the specified Rule.
     *
     * @param rule
     *         The Rule to search with.
     *
     * @return
     *         A list containing all Events associated with the specified Rule.
     */
    public List<String> getAssociatedEvents(final Rule rule) {
        final List<String> list_associatedEvents = new ArrayList<>();

        arrayListMultimap_ruleEventAssociations.entries()
                                               .parallelStream()
                                               .filter(entry -> entry.getValue().equals(rule))
                                               .forEach(entry -> list_associatedEvents.add(entry.getKey()));

        return list_associatedEvents;
    }

    /**
     * Locates all Responses associated with the specified Rule.
     *
     * @param rule
     *         The Rule to search with.
     *
     * @return
     *         A list containing all Responses associated with the specified Rule.
     */
    public List<Response> getAssociatedResponses(final Rule rule) {
        return arrayListMultimap_ruleResponseAssociations.get(rule);
    }

    /**
     * Locates all Rules associated with the specified Event.
     *
     * @param event
     *         The Event to search with.
     *
     * @return
     *         A list containing all Rules associated with the specified Event.
     */
    public List<Rule> getAssociatedRules(final String event) {
        return arrayListMultimap_ruleEventAssociations.get(event);
    }
}
