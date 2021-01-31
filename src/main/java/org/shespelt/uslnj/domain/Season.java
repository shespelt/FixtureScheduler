package org.shespelt.uslnj.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;


import java.util.*;

/**
 * A 'Season' is a collection of fixtures along with the definition of our Divisions, Teams, Fields, constraints for
 * that Season (eg. (dates+field available differ from Season to Season)
 */
@PlanningSolution
public class Season extends AbstractPersistable {
    public static int MAX_NUM_MATCH_DAYS=30;
    // Per StackOverflow question 51597744, only use this annotation before the getter method.
    //@PlanningEntityCollectionProperty
    private Set<Fixture> matches = new HashSet<Fixture>();

    private Set<Team> teams = new HashSet<Team>();
    private Map<String, Division> divisions = new HashMap<String, Division>();


    private List<Fixture> matchList = null;

    private List<Team> homeTeamList = null;

    //@ValueRangeProvider(id = "awayTeamRange")
    //@ProblemFactCollectionProperty
    private List<Team> awayTeamList = null;

    private Integer[] matchDays = null;

    private int numMatchDays = 0;

    //@PlanningScore
    private HardSoftScore ourScore;

    public Season() {
    }

    /**
     * call this once all the Problem Facts are set (data all loaded)
     */
    public void dataSet() {
        matchList = new ArrayList<Fixture>(matches);
        ArrayList<Team> homeTeams = new ArrayList<Team>( teams );
        ArrayList<Team> away = new ArrayList<Team>( homeTeams.size() );
        // now duplicate each Team as a possible away team (so our Planning Entities are unique objects?)
        // use differnt id values - we later use the teamCode to see if the same team
        int j = homeTeams.size()-1; // going to reverse the home team list by iterating backwards from homeTeams
        while ( j >= 0 ) {
            Team h = homeTeams.get(j--);
            Team b = h.clone();
            away.add( b );
        }
        awayTeamList = away;
        homeTeamList = homeTeams;
     }

    @PlanningEntityCollectionProperty
    public List<Fixture> getFixtureList() {
        // need to see if this is too expensive - might be cheaper to use an ArrayList  to keep the Fixtures in
        // rather than constructing one
        // if multi-thread, we will need to use the double guard pattern with a mutex.
        return matchList;
    }

    public void setFixtureList( List<Fixture> newList ) {
        this.matches = new HashSet<Fixture>( newList );
    }

    @PlanningScore
    public HardSoftScore getScore() {
        return ourScore;
    }

    public void setScore( HardSoftScore ascore) {
        ourScore = ascore;
    }

    @ValueRangeProvider(id = "homeTeamRange")
    @ProblemFactCollectionProperty
    public List<Team> getHomeTeam() {
          return homeTeamList;
    }

    /**
     * seems like we get this exception:
     * Exception in thread "main" java.lang.IllegalStateException:
     * The workingObjects (Fixture: 5, matchDay: 2, homeTeam: null, awayTeam: null,
     * Fixture: 5, matchDay: 2, homeTeam: null, awayTeam: null) have the same planningId ((class org.shespelt.uslnj.domain.Fixture,5)).
     * Working objects must be unique.
     *
     * probably because I am using 2 differents lists (home & away teams) with the same Team (planning Variable instances) ?
     * So use the same list for both home & away ?
     * @return
     */
    @ValueRangeProvider(id="awayTeamRange")
    @ProblemFactCollectionProperty
    public List<Team> getAwayTeam() {
        return awayTeamList;  // I think we can't use the same object twice  (see docs 18.5.1) - A team instance is a ProjectFact - so don't use awayTeamList
    }

    public void setNumMatchDays( int n ) {
        this.numMatchDays = n;
        this.matchDays = new Integer[this.numMatchDays];
        for (int i = 1; i <= matchDays.length; ++i ) {
            matchDays[i-1] = i;  // match days are base 1
        }
    }

    //@ValueRangeProvider( id="matchDayRange")
    public Integer[] getMatchDayValues() {
        return matchDays;
    }

    /**
     * just send to stdout
     */
    public void showSchedule() {
        System.out.println("Season:showSchedule");
        for ( Fixture f : getFixtureList() ) {
            System.out.println("fixture: " + f.toString() );
        }
    }

    // data management methods
    public void addTeam( Team t ) {
        this.teams.add(t);
    }

    public void addFixture( Fixture match ) {
        this.matches.add(match);
    }

    public void addDivision( Division theDivision ) {
        this.divisions.put( theDivision.getName(), theDivision );
    }

    // no adding a team to a division, done in the Team Ctor. Otherwise, double addition to the container.

    public Iterator<String> getDivisionsIter() {
        return this.divisions.keySet().iterator();
    }
    public Division getDivision( String divName ) {
        return this.divisions.get( divName );
    }
}
