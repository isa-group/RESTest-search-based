package es.us.isa.restest.searchbased.terminationcriteria;

import es.us.isa.restest.searchbased.algorithms.SearchBasedAlgorithm;

import java.util.function.Predicate;

public interface TerminationCriterion extends Predicate<SearchBasedAlgorithm>{

}
