package org.shespelt.uslnj.score;


import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.shespelt.uslnj.domain.Division;
import org.shespelt.uslnj.domain.Fixture;
import org.shespelt.uslnj.domain.Season;
import org.shespelt.uslnj.domain.Team;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * NOT yet dealing with mirror schedule. Currently, this would be the Spring. Since, unless field availability is
 * an issue in the fall dates, the Fall is just the mirror of the Spring. And there are usually less known issues
 * with fall field availability since we're not coming out of Winter field closures.
 */

public class SeasonEasyScoreCalculator implements EasyScoreCalculator<Season> {
    // can't have team playing itself
    private static int SAME_OPPONENT = -1000;
    private static int OUTSIDE_DIVISION = -1000;
    // home side can't play the same team > 1 time at home.
    private static int MultipleHomeMatches = -1000;
    private static int UNSET_PLANNING_VARIABLE = Integer.MIN_VALUE/10; // since we might be accumulating sever of these
    private static int[] consecutivePenalty;
    static {
        consecutivePenalty = new int[ Season.MAX_NUM_MATCH_DAYS  + 1 ];
        consecutivePenalty[0] = 0;
        consecutivePenalty[1] = 0;
        for ( int i = 2; i < consecutivePenalty.length; ++i ) {
            if ( i < 10 )
                consecutivePenalty[i] = (int) (100 * Math.pow(2.0d, (double) i ));
            else
                consecutivePenalty[i] = (int) (100 * Math.pow(2.0d, (double) 10 )); // plenty costly enough
        }
    }

    public Score calculateScore(Season season) {
        int hardScore = 0;
        int softScore = 0;
        // here's we the checks come into it.
        // do the hard score checks first
        // delegate to this private methods checking if the PlanningVariables (eg. home & away team) have been set.
        // if not, return very bad score.
        hardScore += checkInvalidOpponent(season);
        hardScore += checkInvalidMatchUp(season);
        hardScore += checkFieldAvailable(season);

        // soft scores
        softScore += checkIdealSchedule( season, SeasonEasyScoreCalculator.consecutivePenalty );

        System.out.println("SCORE:  hard:"+Integer.toString(hardScore)+ ", soft:"+Integer.toString(softScore));

        return HardSoftScore.of( hardScore, softScore );
    }

    private int checkInvalidOpponent( Season season ) {
        int score = 0;
        List<Fixture> l = season.getFixtureList();
        Iterator<Fixture> iter = l.iterator();
        while ( iter.hasNext() ) {
            Fixture f = iter.next();
            if ( unsetPlanningVariable(f))
                return UNSET_PLANNING_VARIABLE;
            if ( f.getHomeTeam().getName().equals( f.getAwayTeam().getName() ) ) {
                score += SAME_OPPONENT;
                System.err.println("OOPS: can't play self. Fixture:" + f.toString());
            } else {
                System.out.println(">>>: fixture has different teams: " + f.toString() );
            }
            if ( f.getHomeTeam().getOurDivision().getId() != f.getAwayTeam().getOurDivision().getId() ) {
                score += OUTSIDE_DIVISION;
            }
        }
        return score;
    }

    private int checkInvalidMatchUp( Season season ) {
        int score = 0;
        // home team can't play at home twice against same opponent
        List<Fixture> l = season.getFixtureList();
        ListIterator<Fixture> iter = l.listIterator();
        while ( iter.hasNext() ) {
            Fixture f = iter.next();
            if ( unsetPlanningVariable(f))
                return UNSET_PLANNING_VARIABLE;
            // quick, cheap test first
            // if any fixture has team A playing itself, not allowed.
            if ( f.getHomeTeam().getName().equals( f.getAwayTeam().getName() ) ) {
                score += SAME_OPPONENT;
            }
            // Team A can't play team outside A's division
            if ( f.getHomeTeam().getOurDivision().getId() != f.getAwayTeam().getOurDivision().getId() ) {
                score += OUTSIDE_DIVISION;
            }
            // if the score here is so bad, don't bother with more expensive tests
            if ( score <= SAME_OPPONENT || score <= OUTSIDE_DIVISION ) {
                return score;
            }
            // check against every other team - can start with outter Iter because earlier teams already checked
            // A vs B is same as B v A with regard to this rule.
            ListIterator<Fixture> otherTeamIter = l.listIterator( iter.nextIndex());
            while ( otherTeamIter.hasNext() ) {
                Fixture otherMatch = otherTeamIter.next();
                if ( unsetPlanningVariable(otherMatch))
                    return UNSET_PLANNING_VARIABLE;
                if ( ! f.getId().equals( otherMatch.getId())) {
                    // do we have another match with the same teams as home, away sides?
                    if ( (f.getHomeTeam().getTeamCode().equals(otherMatch.getHomeTeam().getTeamCode())) &&
                            (f.getAwayTeam().getTeamCode().equals(otherMatch.getAwayTeam().getTeamCode()))) {
                        score += MultipleHomeMatches; // same matchup multiple times -> not good.
                    }
                }
            }
        }
        return score;
    }

    private int checkFieldAvailable( Season season ) {
        int score = 0;

        return score;
    }

    // Soft score calculations

    /**
     * In an ideal schedule each team plays an alternating schedule
     * @param season
     * @return
     */
    private int checkIdealSchedule( Season season, int[] consecutiveMatchPenaltyWeight ) {
        int score = 0;
        // don't do the other checks as they are handled by other methods.
        // check 1: for each Team, see how many consecutive games are home or away.
        Iterator<String> itrNames = season.getDivisionsIter();
        while (itrNames.hasNext() ) {
            String divName = itrNames.next();
            Division div = season.getDivision( divName );
            // ideally we have a means to get the set of Fixtures for a given Division but until we do...
            Iterator<Team> itrTeam = div.getTeamsIter();
            while (itrTeam.hasNext() ) {
                Team team1 = itrTeam.next();
                int numConsecutiveHome = 1; // max # of home games in a row
                int numConsecutiveAway = 1; // max # of away games in a row
                boolean priorHome = false;
                boolean priorAway = false;

                List<Fixture> l = season.getFixtureList();
                ListIterator<Fixture> iter = l.listIterator();
                while (iter.hasNext()) {
                    Fixture f = iter.next();
                    if ( unsetPlanningVariable(f))
                        return UNSET_PLANNING_VARIABLE;
                    String teamId = f.getHomeTeam().getTeamCode();
                    if (!f.getHomeTeam().getOurDivision().getId().equals(div.getId())) {
                        continue;  // not in the division we're checking the teams of
                    }
                    if (f.getHomeTeam().getTeamCode().equals(team1.getTeamCode())) {
                        priorAway = false;
                        if (priorHome)
                            ++numConsecutiveHome;
                        priorHome = true;
                    }
                    if (f.getAwayTeam().getTeamCode().equals(team1.getTeamCode())) {
                        priorHome = false;
                        if (priorAway)
                            ++numConsecutiveAway;
                        priorAway = true;
                    }
                } // while doing through all of the season's fixtures
                // take into account the penalty for each team in this solution
                // Note that the value numConsecutiveAway/numConsecutiveHome should not exceed # of match days or the
                // above logic is really wrong. (how does a team have more matches than num of match days?
                // if a 'lot' of teams have serious # of consecutive matches, this solutions score far from ideal.
                score -= consecutiveMatchPenaltyWeight[ numConsecutiveAway ];
                score -= consecutiveMatchPenaltyWeight[ numConsecutiveHome ];
            } // going through all the teams within a given division
        } // for each division
        return score;
    }

    protected boolean  unsetPlanningVariable( Fixture f ) {
        if (f.getAwayTeam() == null )
            return true;
        if ( f.getHomeTeam() == null )
            return true;
        if ( f.getMatchDay() == null )
            return true;
        return false;

    }
}
