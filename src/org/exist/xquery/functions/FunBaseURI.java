/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-06 Wolfgang M. Meier
 *  wolfgang@exist-db.org
 *  http://exist.sourceforge.net
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *  
 *  $Id$
 */
package org.exist.xquery.functions;

import org.exist.dom.NodeProxy;
import org.exist.dom.QName;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.Dependency;
import org.exist.xquery.Function;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.Profiler;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.AnyURIValue;
import org.exist.xquery.value.NodeValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.Type;

/**
 * @author wolf
 */
public class FunBaseURI extends BasicFunction {

	public final static FunctionSignature signatures[] = {
			new FunctionSignature(
				new QName("base-uri", Function.BUILTIN_FUNCTION_NS),
                "This version of the function returns the value of the base-uri property " +
                "from the static context. If the base-uri property is undefined, the " +
                "empty sequence is returned.",
				null,
				new SequenceType(Type.ANY_URI, Cardinality.ZERO_OR_ONE)
			),
            new FunctionSignature(
                new QName("base-uri", Function.BUILTIN_FUNCTION_NS),
                "Returns the value of the base-uri property for $a. If $a is the empty " +
                "sequence, the empty sequence is returned.",
                new SequenceType[] { new SequenceType(Type.NODE, Cardinality.ZERO_OR_ONE) },
                new SequenceType(Type.ANY_URI, Cardinality.ZERO_OR_ONE)
            )
    };
			
	/**
	 * @param name
	 */
	public FunBaseURI(XQueryContext context, FunctionSignature signature) {
		super(context, signature);
	}

    /* (non-Javadoc)
     * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)
     */
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {
        if (context.getProfiler().isEnabled()) {
            context.getProfiler().start(this);       
            context.getProfiler().message(this, Profiler.DEPENDENCIES, "DEPENDENCIES", Dependency.getDependenciesName(this.getDependencies()));
            if (contextSequence != null)
                context.getProfiler().message(this, Profiler.START_SEQUENCES, "CONTEXT SEQUENCE", contextSequence);
        }
        
        Sequence result;
        if (args.length == 0)
            result = new AnyURIValue(context.getBaseURI());
        else if (args[0].getLength() == 0)
            result = Sequence.EMPTY_SEQUENCE;
        else {
            NodeValue node = (NodeValue) args[0].itemAt(0);
            if (node.getImplementationType() == NodeValue.IN_MEMORY_NODE)
                result = new AnyURIValue(context.getBaseURI());
            else {
                NodeProxy proxy = (NodeProxy) node;
                result = new AnyURIValue(proxy.getDocument().getName());
            }
        }
        
        if (context.getProfiler().isEnabled()) 
            context.getProfiler().end(this, "", result);        
        
        return result;        
    }
}
