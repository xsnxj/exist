package org.exist.xquery.functions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.exist.dom.ElementImpl;
import org.exist.dom.NodeProxy;
import org.exist.dom.NodeSet;
import org.exist.dom.QName;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.AnyURIValue;
import org.exist.xquery.value.NodeValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.Type;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class FunNamespaceURIForPrefix extends BasicFunction {

	public final static FunctionSignature signature =
		new FunctionSignature(
			new QName("namespace-uri-for-prefix", ModuleImpl.NAMESPACE_URI, ModuleImpl.PREFIX),
			"Returns the namespace URI of one of the in-scope namespaces for $b, identified by its namespace prefix. " +
			"If $b has an in-scope namespace whose namespace prefix is equal to $a, it returns the namespace " +
			"URI of that namespace. If $b is the zero-length string or the empty sequence, " +
			"it returns the namespace URI of the default (unnamed) namespace. Otherwise, " +
			"it returns the empty sequence.",
			new SequenceType[] { 
				new SequenceType(Type.STRING, Cardinality.ZERO_OR_ONE),
				new SequenceType(Type.ELEMENT, Cardinality.EXACTLY_ONE)
			},
			new SequenceType(Type.ANY_URI, Cardinality.ZERO_OR_ONE));
	
	public FunNamespaceURIForPrefix(XQueryContext context) {
		super(context, signature);
	}

	public Sequence eval(Sequence[] args, Sequence contextSequence)
			throws XPathException {
		String prefix;
		if (args[0].getLength() == 0)
			prefix = "";
		else
			prefix = args[0].itemAt(0).getStringValue();
		NodeValue node = (NodeValue) args[1].itemAt(0);
		
		Map prefixes = new HashMap();
		if (node.getImplementationType() == NodeValue.PERSISTENT_NODE) {
			NodeProxy proxy = (NodeProxy) node;
			NodeSet ancestors = proxy.getAncestors(false, true);
			for (Iterator i = ancestors.iterator(); i.hasNext(); ) {
				proxy = (NodeProxy) i.next();
				FunInScopePrefixes.collectNamespacePrefixes((ElementImpl) proxy.getNode(), prefixes);
			}
		} else {
			Node next = (Node) node;
			do {
				FunInScopePrefixes.collectNamespacePrefixes((Element) next, prefixes);
				next = next.getParentNode();
			} while (next != null && next.getNodeType() == Node.ELEMENT_NODE);
		}
		String namespace = (String) prefixes.get(prefix);
		if (namespace != null)
			return new AnyURIValue(namespace);
		return Sequence.EMPTY_SEQUENCE;
	}
}
