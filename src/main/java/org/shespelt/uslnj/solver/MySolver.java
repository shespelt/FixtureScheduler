package org.shespelt.uslnj.solver;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.shespelt.uslnj.domain.Division;
import org.shespelt.uslnj.domain.Fixture;
import org.shespelt.uslnj.domain.Season;
import org.shespelt.uslnj.domain.Team;

public class MySolver {
    public static void main( String[] args) {

        // build the solver
        SolverFactory<Season> solverFactory = SolverFactory.createFromXmlResource(
                "org/shespelt/uslnj/seasonSolverConfig.xml");
        Solver<Season> solver = solverFactory.buildSolver();
        // instantiate the uninitialized Season - with our Fixtures, Teams, Divisions,
        Season unsolvedSeason = loadData();

        // solve it
        Season scheduledSeason = solver.solve( unsolvedSeason );
        scheduledSeason.showSchedule();

    }

    private static Season  loadData() {
        Season s1 = new Season();
        // create the divisions and then
        // eventually, this can be done by importing a JSON file
        Division div = new Division();
        div.setName("Div1");
        div.setId( 0L );
        for (int i = 0; i < 6; ++i ) {
            Team t = new Team(div, "Team" + Integer.toString(i) );
            //t.setId( (long) i );
            t.setTeamCode( "tm" + Integer.toString(i+1));
            div.addTeam(t);
            s1.addTeam(t);
        }

        // now the fixtures :-)
        int match=0;
        int numMatchDays = div.getNumberOfTeams()-1; // number of games for team A to play all the others.
        int gamesPerMatchDay= div.getNumberOfTeams()/2; // deal with odd # of teams later...
        s1.setNumMatchDays(  numMatchDays  );
        for (int matchDay = 0; matchDay < numMatchDays; ++matchDay ) {
            for (int j = 0; j< gamesPerMatchDay; ++j, ++match ) {
                Fixture f = new Fixture(match, matchDay+1, null, null );
                s1.addFixture(f);
            }
        }
        s1.addDivision(div);
        System.out.println("MySolver - finished loading Fixtures into Season");
        s1.dataSet();
        return s1;
    }
}
