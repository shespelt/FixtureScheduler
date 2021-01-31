package org.shespelt.uslnj.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Team { // extends AbstractPersistable {
    private String name= "unassigned";
    private String teamCode = "";  // eg. SPV or SPO
    private Field homeField = new Field(); // generic field -> no possible conflict
    private Division ourDivision;
    // eventually just the matchDay#
    private List<Integer> unableToPlay = new ArrayList<Integer>();

    public Team( Division d, String nm ) {
        this.ourDivision = d;
        this.ourDivision.addTeam( this );
        this.setName( nm );
        // TODO we should get rid of the setters for division, name - set by ctor only
    }

    public boolean inOurDivision( Team b ) {
        return ourDivision.isTeamInOurDivision( b );
    }

    public boolean isUnavailable( int i ) {
        for ( Integer matchDay : this.getUnableToPlay() ) {
            if ( matchDay == i )
                return true;
        }
        return false;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public Division getOurDivision() {
        return ourDivision;
    }

    public void setOurDivision(Division ourDivision) {
        this.ourDivision = ourDivision;
    }

    public String getName() {
        return name;
    }

    public Field getHomeField() {
        return homeField;
    }

    public List<Integer> getUnableToPlay() {
        return unableToPlay;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHomeField(Field homeField) {
        this.homeField = homeField;
        homeField.setHomeFieldForTeam(this);
    }

    public void setUnableToPlay(List<Integer> unableToPlay) {
        this.unableToPlay = unableToPlay;
    }

    public Team clone() {
        Team newTeam = new Team( this.ourDivision, this.name);
        newTeam.setTeamCode( this.teamCode);
        newTeam.setUnableToPlay( this.unableToPlay);
        newTeam.setHomeField( this.homeField);
        return newTeam;
    }

    public String toString() {
        StringBuilder badMatchDays = new StringBuilder();
        for ( Integer match : this.getUnableToPlay()) {
            badMatchDays.append(match.intValue());
            badMatchDays.append(',');
        }
        if ( badMatchDays.length() > 0 )
            badMatchDays.deleteCharAt( badMatchDays.length()-1); // chop off trailing ,
        return "Team Name: " + this.getName() + ", " + this.getHomeField().getName() + ", shared with " +
                (this.getHomeField().getNumTeams()-1) + ", unable to play on match days: " + badMatchDays;
    }
}
