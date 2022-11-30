package nut.goals;

import nut.goals.GoalException;
import nut.model.Project;

public interface Goal
{
   public void execute(Project project) throws GoalException;
}
