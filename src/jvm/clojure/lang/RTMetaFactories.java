/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package clojure.lang;

import static java.lang.invoke.MethodHandles.constant;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.regex.Pattern;

class RTMetaFactories{
		
static CallSite bsm_constant(Lookup lookup, String kind, MethodType type, Object data) {
	Class<?> expectedType = type.returnType();
	Object constant = decodeConstant(kind, data);
	return new ConstantCallSite(constant(expectedType, constant).asType(type));
}

private static Object decodeConstant(String kind, Object data) {
	switch(kind) {
	case "integer":
	case "long":
	case "double": // already boxed when calling the bsm
		return data;
	case "character":  
		return (char)(int)(Integer)data;
	case "class":
		return RT.classForName((String)data);
	case "keyword":  
		return Keyword.intern((String)data);
	case "symbol":  
		return Symbol.intern((String)data);
	case "pattern":
		return Pattern.compile((String)data);
  default:
  	throw new LinkageError("invalid kind " + kind);
	}
}

static CallSite bsm_var(Lookup lookup, String kind, MethodType type, String ns, String name) {
	Class<?> expectedType = type.returnType();
	Var var = RT.var(ns, name);
	return new ConstantCallSite(constant(expectedType, var).asType(type));
}


}