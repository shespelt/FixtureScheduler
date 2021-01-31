package org.shespelt.uslnj.solver.move;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.shespelt.uslnj.domain.Fixture;
import org.shespelt.uslnj.domain.Season;

public class DifferentFixtureSwapMoveFilter implements SelectionFilter<Season, SwapMove> {
    public boolean accept(ScoreDirector<Season> scoreDirector, SwapMove swapMove) {
        //TODO - is swapMove.getLeftEntity() -> based on curriculumcourse example, a PlanningEntity
        Fixture fixture1 = (Fixture) swapMove.getLeftEntity();
        Fixture fixture2 = (Fixture) swapMove.getRightEntity();
        return false;
    }
}
