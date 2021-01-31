package org.shespelt.uslnj.domain;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * a USLNJ Division has at least 2 teams. A given team only plays teams within its own
 * division (most of the time!), but can share its home field with any team within the league
 * which obviously can & does affect schedule home fixtures.
 */
public class Division  extends AbstractPersistable {
    private List<Team> divisionalTeams = new ArrayList<Team>();
    private String name;

    public Division( long id, String name, List<Team> teams ) {
        super(id);
        this.setName( name );
        this.setDivisionalTeams(teams);
    }
    public Division() { }

    public void addTeam( Team a ) {
        for ( Team t : this.getDivisionalTeams() ) {
            if ( t.getName().equals( a.getName() ))
                return; // already in our list
        }
        this.divisionalTeams.add(a);
    }

    public void setName( String nm ) {
        this.name = nm;
    }
    public String getName() {
        return name;
    }

    public void setDivisionalTeams( List<Team> theTeams ) {
        this.divisionalTeams = theTeams;
    }
    public List<Team> getDivisionalTeams() {
        return this.divisionalTeams;
    }

    public boolean isTeamInOurDivision( Team a ) {
        return ( a.getOurDivision().getName().equals( this.getName() ));
    }

    public Iterator<Team> getTeamsIter() {
        return this.divisionalTeams.iterator();
    }

    public int getNumberOfTeams() {
        return this.getDivisionalTeams().size();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "DivId: ");
        sb.append( this.getName() );
        sb.append(',');
        for ( Team tm : this.getDivisionalTeams() ) {
            sb.append( tm.getName() );
            sb.append( ',');
        }
        sb.deleteCharAt( sb.length()-1 ); // chop off the last ,
        return sb.toString();
    }
}
