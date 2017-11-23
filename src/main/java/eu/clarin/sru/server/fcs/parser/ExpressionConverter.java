package eu.clarin.sru.server.fcs.parser;

import java.util.*;
import java.util.stream.Collectors;

import clariah.fcs.mapping.ConversionEngine;
import clariah.fcs.mapping.Feature;
import clariah.fcs.mapping.FeatureConjunction;

import eu.clarin.sru.server.fcs.parser.Expression;
import eu.clarin.sru.server.fcs.parser.ExpressionAnd;
import eu.clarin.sru.server.fcs.parser.ExpressionNot;
import eu.clarin.sru.server.fcs.parser.ExpressionOr;

/**
 *  dit is een hack: 
 *  implementatie van een class waarvan de velden protected zijn, om die toch te kunnen benaderen...
 *   
 * @author jesse
 *
 */


public class ExpressionConverter implements ExpressionRewriter
{
	private ConversionEngine conversion;
	
	
	// ---------------------------------------------------------------------------------
	// constructor
	
	public ExpressionConverter(ConversionEngine conversion)
	{
		this.conversion = conversion;
	}
	
	
	// ---------------------------------------------------------------------------------
	
	
	private QueryNode featureNode(Feature f)
	{
		List<QueryNode> orz = f.getValues().stream().map(v -> new Expression(null, f.getFeatureName(), Operator.EQUALS, v, null)).collect(Collectors.toList());
		if (orz.size() == 1)
			return orz.get(0);
		ExpressionOr eo = new ExpressionOr(orz);
		return eo;
	}
	
	private QueryNode negation(QueryNode n)
	{
		if (n instanceof Expression) // flip if simple expression
    	{
		
    		Expression e1 = (Expression) n;
    		Operator flip = (e1.getOperator() == Operator.NOT_EQUALS)? Operator.EQUALS: Operator.NOT_EQUALS;
    		
    		Expression e2 = new Expression(e1.getLayerQualifier(), e1.getLayerIdentifier(), flip, e1.getRegexValue(), e1.getRegexFlags());
    		// clone e and make negative
    		//System.err.println("Simple negation, flip="  + flip + " input operator = " + e1.getOperator());
    		return e2;
    	}
		return new ExpressionNot(n); // kan je natuurlijk naar binnen proberen te duwen, etc, maar laat maar even
	}
	
	
	// ---------------------------------------------------------------------------------
	
	
	@Override
	public QueryNode rewriteExpression(Expression e) // TODO: if the operator is a NOT_EQUALS, this is too simple
	{
	    final boolean negative = e.getOperator() == Operator.NOT_EQUALS;
		String f = e.getLayerIdentifier();
		String v = e.getRegexValue();
		// System.err.println("Expression: "  + f + "=" + v);
	    Set<FeatureConjunction> fcs = conversion.translateFeature(f, v);
	  
	    List<QueryNode> orz = new ArrayList<>();
	    
	    for (FeatureConjunction fc: fcs)
	    {
	    	List<QueryNode> andz = fc.getFeatures().map(feat -> featureNode(feat)).collect(Collectors.toList());
	    	if (andz.size() == 1)
	    		orz.add(andz.get(0));
	    	else
	    	{
	    		ExpressionAnd ea = new ExpressionAnd(andz);
	    		orz.add(ea);
	    	}
	    }
	    if (orz.size() == 1)
	    {
	    	QueryNode o1 =  orz.get(0);
	    	return negative ? negation(o1): o1;
	    }
	    ExpressionOr orrie = new ExpressionOr(orz);
		return negative?negation(orrie):orrie; 
	}
}
