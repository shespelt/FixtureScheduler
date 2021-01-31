package org.shespelt.uslnj.domain;

import java.util.ArrayList;
import java.util.List;

public class Field  extends AbstractPersistable  {
    private  String name;
    private List<Team> homeFieldOf = new ArrayList<Team>();

    public void setName( String nm ) {
        this.name = nm;
    }
    public String getName() {
        return name;
    }

    public void setHomeFieldOf( List<Team> teams ) {
        this.homeFieldOf = teams;
    }
    public List<Team> getHomeFieldOf() {
        return homeFieldOf;
    }

    public int getNumTeams() {
        return this.getHomeFieldOf().size();
    }

    /**
     * since we're only setting fields during data read, object generation, the overhead just to make sure
     * we're not adding duplicates is not an issue.
     * @param a
     */
    public void setHomeFieldForTeam( Team a ) {
        for ( Team aTeam : this.getHomeFieldOf() ) {
            if ( aTeam.getName().equals( a.getName() ) )
                return;
        }
        this.getHomeFieldOf().add( a );
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "Field Name: " );
        sb.append( this.getName());
        sb.append(',');
        sb.append(" home field for: ");
        for ( Team tm : this.getHomeFieldOf() ) {
            sb.append( tm.getName() );
            sb.append(',');
        }
        sb.deleteCharAt( sb.length()-1); // chop off last , as we don't need it.
        return sb.toString();
    }
}
