package org.shespelt.uslnj.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * see section 18.4.1 regarding making a PlanningEntity immovable (ie. a match must be defined by the current
 * home & away teams for the given PlanningEntity (Fixture.class). Might be useful? along with the PlanningPin
 */

@PlanningEntity
public class Fixture extends AbstractPersistable {

    private Integer matchDay = null;

    @PlanningVariable( valueRangeProviderRefs = {"homeTeamRange"})
    private Team homeTeam=null;

    @PlanningVariable( valueRangeProviderRefs = {"awayTeamRange"})
    private Team awayTeam=null;

    public Fixture( long id, int matchDay, Team homeTeam, Team awayTeam ) {
        super.setId(id);
        this.matchDay = matchDay;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        System.out.println("Fixture() -> just created via non-empty CTOR. ID: " + this.getId().toString());
    }
    // the no-arg Ctor only needed by Optaplanner framework - when cloning so it ought to handle, through
    // reflection, the setting of all of the properties.
    public Fixture() {
        super();
        System.out.println("Fixture() - no-arg CTOR invoked. ID: " +
                ( this.getId() != null ? this.getId().toString():"NULL") );
    }

    //@PlanningVariable( valueRangeProviderRefs = { "matchDayRange"})
    public Integer getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(Integer theMatchDay) {
        this.matchDay = theMatchDay;
    }


    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }


    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    @Override
    public String toString() {
        return "Fixture: " + this.getId() + ", matchDay: " + this.getMatchDay() +
                ", homeTeam: " + this.getHomeTeam() + ", awayTeam: " + this.getAwayTeam();
    }
}
