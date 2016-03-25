package data.collection;

import com.google.common.collect.ArrayListMultimap;
import data.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManager implements Serializable {
    private static final long serialVersionUID = -6339946788075815071L;

    /** The Events that can be used by the Dynamic Dialog System. */
    private ArrayList<String> arrayList_events;

    /** The ResponseTypes that can be used by the Dynamic Dialog System. */
    private ArrayList<String> arrayList_responseTypes;

    /** The ResponseManager used to handle all events of the Dynamic Dialog System. */
    private ResponseManager responseManager;



    /** The SplayTree containing all currently loaded Contexts, with their Names as Keys. */
    private SplayTree<String, Context> splayTree_context = new SplayTree<>();
    /** The ArrayList containing all currently loaded Criterion, with their IDs as Keys. */
    private ArrayList<Criterion> arrayList_criterion = new ArrayList<>();
    /** The ArrayList containing all currently loaded Responses, with their IDs as Keys. */
    private ArrayList<Response> arrayList_response = new ArrayList<>();
    /** The ArrayList containing all currently loaded Rules, with their IDs as Keys. */
    private ArrayList<Rule> arrayList_rules = new ArrayList<>();

    /** The ArrayList containing all currently loaded Context names. */
    private ArrayList<String> arrayList_contextNames = new ArrayList<>();

    /** The ArrayListMultimap containing all associations between each Event and the Rules that it triggers. */
    private ArrayListMultimap<String, Rule> arrayListMultimap_ruleEventAssociations = ArrayListMultimap.create();
    /** The ArrayListMultimap containing all associations between each Rule and it's Responses. */
    private ArrayListMultimap<Rule, Response> arrayListMultimap_ruleResponseAssociations = ArrayListMultimap.create();
    /** The ArrayListMultimap containing all associations between each Rule and it's Criterion. */
    private ArrayListMultimap<Rule, Criterion> arrayListMultimap_ruleCriterionAssociations = ArrayListMultimap.create();

    /** The HashMap containing Context Namess and the time at which they were last used. */
    private HashMap<Context, Long> hashMap_context_lastUsedTime = new HashMap<>();
    /** The HashMap containing Criterion IDs and the time at which they were last used. */
    private HashMap<Criterion, Long> hashMap_criterion_lastUsedTime = new HashMap<>();
    /** The HashMap containing Response IDs and the time at which they were last used. */
    private HashMap<Response, Long> hashMap_response_lastUsedTime = new HashMap<>();
    /** The HashMap containing Rule IDs and the time at which they were last used. */
    private HashMap<Rule, Long> hashMap_rules_lastUsedTime = new HashMap<>();

    /**
     * Construct a new DataManager.
     *
     * @param arrayList_events
     *         The Events that can be used by the Dynamic Dialog System.
     *
     * @param arrayList_responseTypes
     *         The ResponseTypes that can be used by the Dynamic Dialog system.
     */
    public DataManager(final ArrayList<String> arrayList_events, final ArrayList<String> arrayList_responseTypes) {
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

        for(final Criterion criterion : arrayListMultimap_ruleCriterionAssociations.get(rule)) {
            criterion.update();

            isTrueCounter += (criterion.getIsTrue() ? 1 : 0);
        }

        return isTrueCounter;
    }

    public void determineResponse(final String event) {
        // Determine the Triggered Rules and their Scores:
        final List<Rule> set_triggeredRules = arrayListMultimap_ruleEventAssociations.get(event);
        final ArrayList<Integer> arrayList_scores = new ArrayList<>(set_triggeredRules.size());

        set_triggeredRules.parallelStream()
                .forEachOrdered(rule -> arrayList_scores.add(updateRuleCriterion(rule)));


        final int totalScoredRules = set_triggeredRules.size();

        if (totalScoredRules == 0) {
            // Do Nothing
        } else if (totalScoredRules == 1) {
            // If only one Rule is found, then respond to it.
            handleResponse(arrayListMultimap_ruleResponseAssociations.get(set_triggeredRules.get(0)));

        } else if (set_triggeredRules.parallelStream().anyMatch(rule -> rule.getLastUsedTime() == 0)) {
            /*
             * If there are multiple Rules found and any of them has never been used before,
             * then use one of the Rules which hasn't been used before.
             */
            final Rule rule = set_triggeredRules.parallelStream().filter(r -> r.getLastUsedTime() == 0).findAny().get();

            rule.updateLastUsedTime();
            handleResponse(arrayListMultimap_ruleResponseAssociations.get(rule));

        } else if(set_triggeredRules.parallelStream().allMatch(r -> arrayListMultimap_ruleCriterionAssociations.get(r).size() == 0)) {
            /*
             * If none of the Rules have any Criterion, then find the Rule that was least recently
             * used and use it.
             */
            final Rule rule = set_triggeredRules.parallelStream()
                                                .sorted((ruleA, ruleB) -> Long.compare(ruleA.getLastUsedTime(), ruleB.getLastUsedTime()))
                                                .findFirst()
                                                .get();

            rule.updateLastUsedTime();
            handleResponse(arrayListMultimap_ruleResponseAssociations.get(rule));
        } else {
                /*
                 * The next step is to determine which Response to use.
                 *
                 * The responses are already ordered from highest to lowest score,
                 * so the chance to be used will be calculated by the following formula
                 * because we can't just keep using whichever Response has the highest score
                 * or else the game will become repetitive.
                 *
                 * Step #1:
                 *      First we must use proportional scoring, so that the Response with the highest
                 *      score is "100%" and the Response with the lowest score is "0%". Because the
                 *      array of Responses is already sorted in order from highest score to lowest score,
                 *      this part is simple.
                 *      To determine the score of all Responses between these two, the following formula
                 *      must be used.
                 *
                 *              ((currScore - lowestScore) / (highestScore - lowestScore)) * 100 = Score_Percentage
                 *
                 * Step #2:
                 *      Same reasoning as above, but for the LastUsed_UnixTime values. But instead of just using
                 *      the LUUT values, we'll use the distances from the current time.
                 *
                 *              currLUUT = System.currentTimeMillis() - array_lastUsed_unixTime[i]
                 *
                 *              ((currLUUT - lowestLUUT) / (highestLUUT - lowestLUUT)) * 100 = LUUT_Percentage
                 *
                 * Step #3:
                 *      We must weight our two criteria from the first two steps. The score is important and having
                 *      a response which hasn't recently been used is less important.
                 *
                 *      So, the weights are worth:
                 *          Score_Percentage = 60% = 0.6
                 *          LUUT_Percentage = 40% = 0.4
                 *
                 * Step #4:
                 *      The final score of each Response is determined as follows:
                 *
                 *          (Score_Percentage * 0.6) + (LUUT_Percentage * 0.4)
                 *
                 * Step #5:
                 *      Use whichever Response has the highest final score.
                 */


            int indexOfRuleToUse = 0;
            double highestScore = 0;
            final long currentTime = System.currentTimeMillis();
            int counter = 0;

            final double score_minimum = arrayList_scores.get(set_triggeredRules.size() - 1);
            final double score_maximum = arrayList_scores.get(0);

            final Rule[] array_sortedRulesByLUUT = set_triggeredRules.parallelStream()
                                                                     .sorted((ruleA, ruleB) -> Long.compare(ruleA.getLastUsedTime(), ruleB.getLastUsedTime()))
                                                                     .toArray(Rule[]::new);
            final double luut_minimum = array_sortedRulesByLUUT[0].getLastUsedTime();
            final double luut_maximum = array_sortedRulesByLUUT[array_sortedRulesByLUUT.length - 1].getLastUsedTime();

            for(final Rule rule : set_triggeredRules) {
                /*
                 * If there are Criterion associated with the Rule and at-least one of them evaluates
                 * to TRUE, then continue.
                 *
                 * If there are no Criterion associated with the Rule, then continue.
                 */
                if(arrayList_scores.get(counter) > 0 && getAssociatedCriterions(rule).size() != 0) {
                    final double normalizedScore = normalize(arrayList_scores.get(counter), score_minimum, score_maximum);
                    final double normalizedLUUT = normalize(rule.getLastUsedTime(), luut_minimum, luut_maximum) * (currentTime - rule.getLastUsedTime())/1000;

                    double finalScore = (normalizedScore * 0.6f) + (normalizedLUUT * 0.4f);

                    if (finalScore > highestScore) {
                        indexOfRuleToUse = counter;
                        highestScore = finalScore;
                    }
                }

                counter ++;
            }

            set_triggeredRules.get(indexOfRuleToUse).updateLastUsedTime();
            handleResponse(arrayListMultimap_ruleResponseAssociations.get(set_triggeredRules.get(indexOfRuleToUse)));
        }
    }

    private double normalize(final double current, final double minimum, final double maximum) {
        double numerator = current - minimum;
        double denominator = maximum - minimum;

        if(denominator == 0) {
            denominator = 1;
        }

        return numerator / denominator;
    }


    private void handleResponse(final List<Response> responses) throws IllegalStateException {
        if(responseManager == null) {
            throw new IllegalStateException("A ResponseManager has not been set, the Dynamic Dialog System cannot handle any responses.");
        } else {
            responses.parallelStream()
                     .forEach(response -> responseManager.respond(this, response));
        }
    }





    /**
     * Attempts to retrieve the Value associated with the specified Key
     * from the splayTree_context.
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
    public Object getValue(final String key) {
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
     * @param key
     *        The Key associated with the Value to retrieve.
     *
     * @param newValue
     *        The new Value to place into the Context.
     */
    public void setValue(final String key, final String newValue) {
        splayTree_context.get(key).setValue(newValue);
    }





    /**
     * Adds the specified Context into the Dynamic Dialog System.
     *
     * @param context
     *         The Context to add into the Dynamic Dialog System.
     */
    public void addContext(final Context context) {
        splayTree_context.put(context.getName(), context);
        arrayList_contextNames.add(context.getName());
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

        hashMap_context_lastUsedTime.remove(context.getName());
        splayTree_context.remove(context.getName());
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

        hashMap_criterion_lastUsedTime.remove(criterion);
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

        hashMap_response_lastUsedTime.remove(response);
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

        hashMap_rules_lastUsedTime.remove(rule);
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
    public void removeRuleCriterionAssociations(final Rule rule) {
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
    public void removeRuleEventAssociations(final Rule rule) {
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
    public void removeRuleResponseAssociations(final Rule rule) {
        arrayListMultimap_ruleResponseAssociations.removeAll(rule);
    }

    /** @return The ArrayList containing all currently loaded Rules, with their IDs as Keys. */
    public List<Rule> getRules() {
        return arrayList_rules;
    }

    /** @return The ResponseTypes that can be used by the Dynamic Dialog System. */
    public List<String> getEvents() {
        return arrayList_events;
    }

    /** @return The ResponseManager used to handle all events of the Dynamic Dialog System. */
    public List<String> getResponseTypes() {
        return arrayList_responseTypes;
    }

    /** @return The ArrayList containing all currently loaded Context names. */
    public List<String> getContextNames() {
        return arrayList_contextNames;
    }


    /**
     * Attempts to locate, and return, a Context by it's name.
     *
     * @param contextName
     *         The Context name to search for.
     *
     * @return
     *         If a context using the specified name exists, then it is returned.
     *         Else NULL is returned.
     */
    public Context getContextByName(final String contextName) {
        return splayTree_context.get(contextName);
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





    /**
     * Set a new set of Events that can be used by the Dynamic Dialog System.
     *
     * @param arrayList_events
     *         The Events that can be used by the Dynamic Dialog system.
     */
    public void setEvents(final ArrayList<String> arrayList_events) {
        this.arrayList_events = arrayList_events;
    }

    /**
     * Set a new set of ResponseTypes that can be used by the Dynamic Dialog System.
     *
     * @param arrayList_events
     *         The ResponseTypes that can be used by the Dynamic Dialog system.
     */
    public void setResponseTypes(final ArrayList<String> arrayList_events) {
        this.arrayList_events = arrayList_events;
    }

    /**
     * Set a new ResponseManager to use when handling responses.
     *
     * @param responseManager
     *         The new ResponseManager used to handle all events of the Dynamic Dialog System.
     */
    public void setResponseManager(final ResponseManager responseManager) {
        this.responseManager = responseManager;
    }
}
